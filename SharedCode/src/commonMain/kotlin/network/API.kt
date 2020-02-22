package com.charlag.tuta.network

import com.charlag.tuta.*
import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.sys.*
import com.charlag.tuta.network.mapping.InstanceMapper
import com.charlag.tuta.network.mapping.NestedMapper
import io.ktor.client.HttpClient
import io.ktor.client.features.feature
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.logging.Logging
import io.ktor.client.features.websocket.wss
import io.ktor.client.request.*
import io.ktor.http.HttpMethod
import io.ktor.http.Url
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.*
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.reflect.KClass

class API(
    private val httpClient: HttpClient,
    val baseUrl: String,
    val cryptor: Cryptor,
    val instanceMapper: InstanceMapper,
    val sessionDataProvider: SessionDataProvider,
    val keyResolver: SessionKeyResolver,
    val wsUrl: String
) : SessionKeyLoader {
    private val json = Json(JsonConfiguration.Stable)
    private val jsonSerializer = httpClient.feature(JsonFeature)!!.serializer

    suspend fun <T : Entity> serviceRequest(
        app: String,
        name: String,
        method: HttpMethod,
        requestEntity: Entity?,
        responseClass: KClass<T>,
        queryParams: Map<String, String>? = null,
        sk: ByteArray? = null
    ): T {
        val serializedEntity = requestEntity?.let { serializeEntity(it, sk) }
        return if (method == HttpMethod.Get) {
            httpClient.get<JsonObject?> {
                commonHeaders()
                url(serviceUrl(app, name))
                addQueryParams(queryParams)
                if (serializedEntity != null) {
                    parameter("_body", json.stringify(JsonObjectSerializer, serializedEntity))
                }
            }!!.let { deserializeEntitity(it, responseClass, sk) }
        } else {
            httpClient.post<JsonObject?> {
                commonHeaders()
                url(serviceUrl(app, name))
                addQueryParams(queryParams)

                if (serializedEntity != null) body = jsonSerializer.write(serializedEntity)
            }!!.let { deserializeEntitity(it, responseClass, sk) }
        }
    }

    suspend fun serviceRequestBinaryGet(
        app: String,
        name: String,
        method: HttpMethod,
        requestEntity: Entity,
        sessionKey: ByteArray? = null
    ): ByteArray {
        val path = "${app}/${name}"
        val serializedEntity = serializeEntity(requestEntity, sessionKey)
        val address = Url(baseUrl + path)
        return if (method == HttpMethod.Get) {
            httpClient.get<ByteArray> {
                commonHeaders()
                url(address)
                parameter("_body", json.stringify(JsonObjectSerializer, serializedEntity))
            }
        } else {
            httpClient.post<ByteArray>() {
                commonHeaders()
                url(address)
                body = json.stringify(JsonObjectSerializer, serializedEntity)
            }
        }.let { data ->
            if (sessionKey != null) {
                cryptor.aesDecrypt(data, sessionKey, true).data
            } else {
                data
            }
        }
    }

    suspend fun serviceRequestBinaryPost(
        app: String,
        name: String,
        method: HttpMethod,
        data: ByteArray,
        queryParams: Map<String, String>? = null,
        sessionKey: ByteArray
    ) {
        val encData = cryptor.aesEncrypt(data, cryptor.generateIV(), sessionKey, true, true)
        httpClient.request<Unit> {
            this.method = method
            commonHeaders()
            url(serviceUrl(app, name))
            body = encData
            addQueryParams(queryParams)
        }
    }

    private fun serviceUrl(app: String, name: String): Url {
        val path = "${app}/${name}"
        val address = Url(baseUrl + path)
        return address
    }

    private fun HttpRequestBuilder.addQueryParams(queryParams: Map<String, String>?) {
        if (queryParams != null) {
            for ((key, value) in queryParams) {
                parameter(key, value)
            }
        }
    }

    suspend fun serviceRequestVoid(
        app: String,
        name: String,
        method: HttpMethod,
        requestEntity: Entity,
        queryParams: Map<String, String>? = null,
        sk: ByteArray? = null
    ) {
        httpClient.request<Unit> {
            this.method = method
            commonHeaders()
            url(serviceUrl(app, name))
            addQueryParams(queryParams)
            body = jsonSerializer.write(serializeEntity(requestEntity, sk))
        }
    }

    suspend fun <T : Entity> loadElementEntity(klass: KClass<T>, id: Id): T {
        instanceMapper.getTypeModelByClass(klass)
        val (_, model, typeModel) = instanceMapper.getTypeInfoByClass(klass)
        return loadAndMapToEntity(
            "${model}/${typeModel.name.toLowerCase()}/${id.asString()}",
            klass
        )!!
    }

    suspend inline fun <reified T : ElementEntity> loadElementEntity(id: Id): T {
        return loadElementEntity(T::class, id)
    }

    suspend inline fun <reified T : ListElementEntity> loadListElementEntity(id: IdTuple): T {
        return loadListElementEntity(T::class, id)
    }

    suspend fun <T : ListElementEntity> loadListElementEntity(klass: KClass<T>, id: IdTuple): T {
        val (_, model, typeModel) = instanceMapper.getTypeInfoByClass(klass)
        val name = typeModel.name.toLowerCase()
        val url = "${model}/${name}/${id.listId.asString()}/${id.elementId.asString()}"
        return loadAndMapToEntity(url, klass)!!
    }

    suspend fun <T : ListElementEntity> loadRange(
        klass: KClass<T>, listId: Id, start: Id, count: Int, reverse: Boolean
    ): List<T> {
        val (_, model, typeModel) = instanceMapper.getTypeInfoByClass(klass)
        if (typeModel.type != MetamodelType.LIST_ELEMENT_TYPE)
            error("Only list elements are allowed to be loaded in range")
        return httpClient.get<JsonArray> {
            commonHeaders()
            entityHeaders(typeModel)
            parameter("start", start.asString())
            parameter("count", count)
            parameter("reverse", reverse.toString())
            url(baseUrl + "${model}/${typeModel.name.toLowerCase()}/${listId.asString()}")
        }.mapNotNull {
            try {
                deserializeEntitity(it as JsonObject, klass)
            } catch (e: CryptoException) {
                println("Failed to decrypt entity, skipping $e")
                null
            }
        }
    }

    suspend fun <T : ListElementEntity> loadRangeInBetween(
        klass: KClass<T>, listId: Id,
        startId: Id = GENERATED_MIN_ID, endId: Id = GENERATED_MAX_ID
    ): List<T> {
        val result = mutableListOf<T>()
        while (true) {
            // Load chunk from the oldest to the newest
            val loadFrom = result.lastOrNull()?._id?.elementId ?: startId
            val range = loadRange(klass, listId, loadFrom, 1000, false)
            // Each chunk is newer so we append it to the end
            result += range.dropLastWhile { it._id!!.elementId > endId }
            if (range.isEmpty() || range.last()._id!!.elementId > endId) {
                break
            }
        }
        return result
    }

    suspend fun <T : ListElementEntity> loadAll(klass: KClass<T>, listId: Id): List<T> {
        return loadRangeInBetween(klass, listId)
    }

    suspend fun <T : Entity> loadRoot(klass: KClass<T>, groupId: Id): T {
        val rootIdTuple = IdTuple(
            groupId,
            GeneratedId(instanceMapper.getTypeModelByClass(klass).rootId)
        )
        val rootInstance = loadListElementEntity<RootInstance>(rootIdTuple)
        return loadElementEntity(klass, rootInstance.reference)
    }

    /**
     * Returns generatedId if it's a generatedId instance
     *
     * Currently ownerEncSessionKey must be explicitly set if needed but this can be fixed with
     * better Entity definition.
     */
    suspend fun <T : ListElementEntity> createListElementEntity(listId: Id, entity: T): Id? {
        return createEntity(entity, listId.asString())
    }

    /**
     * Returns generatedId if it's a generatedId instance
     *
     * Currently ownerEncSessionKey must be explicitly set if needed but this can be fixed with
     * better Entity definition.
     */
    suspend fun <T : ElementEntity> createElementEntity(entity: T): Id? {
        return createEntity(entity, null)
    }

    private suspend fun createEntity(entity: Entity, listId: String?): Id? {
        val (_, model, typeModel) = instanceMapper.getTypeInfoByClass(entity::class)

        val idPart = listId?.let { "/${it}" } ?: ""
        val address = Url(baseUrl + "${model}/${typeModel.name.toLowerCase()}${idPart}")
        val serializedEntity = serializeEntity(entity)
        val response = httpClient.post<JsonObject> {
            commonHeaders()
            entityHeaders(typeModel)
            url(address)
            body = jsonSerializer.write(serializedEntity)
        }
        return response["generatedId"]?.contentOrNull?.let(::GeneratedId)
    }

    suspend fun <T : Entity> updateEntity(entity: T) {
        val (_, model, typeModel) = instanceMapper.getTypeInfoByClass(entity::class)

        val idPart = if (entity is ListElementEntity) {
            val id = entity._id ?: throw IllegalArgumentException("Entity id is not set")
            "${id.listId.asString()}/${id.elementId.asString()}"
        } else {
            (entity as ElementEntity)._id.asString()
        }
        val address = Url(baseUrl + "${model}/${typeModel.name.toLowerCase()}/${idPart}")
        val serializedEntity = serializeEntity(entity)
        return httpClient.put {
            commonHeaders()
            entityHeaders(typeModel)
            url(address)
            body = jsonSerializer.write(serializedEntity)
        }
    }

    suspend fun <T : ListElementEntity> deleteListElementEntity(klass: KClass<T>, id: IdTuple) {
        val (_, model, typeModel) = instanceMapper.getTypeInfoByClass(klass)
        val typeName = typeModel.name.toLowerCase()
        val address =
            Url("$baseUrl$model/$typeName/${id.listId.asString()}/${id.elementId.asString()}")
        return httpClient.delete {
            commonHeaders()
            entityHeaders(typeModel)
            url(address)
        }
    }

    sealed class WSEvent {
        // TODO
        data class CounterUpdate(val counterData: WebsocketCounterData) : WSEvent()

        data class EntityUpdate(val entityData: WebsocketEntityData) : WSEvent()
        data class Unknown(val data: ByteArray) : WSEvent()
    }

    fun getEvents(userId: String): Channel<WSEvent> {
        val channel = Channel<WSEvent>(capacity = Channel.UNLIMITED)
        GlobalScope.launch {
            try {
                httpClient.wss(wsUrl, {
                    url.parameters["modelVersions"] = "49.36"
                    url.parameters["clientVersion"] = "3.59.7"
                    url.parameters["userId"] = userId
                    sessionDataProvider.accessToken?.let { token ->
                        url.parameters["accessToken"] = token
                    }
                }) {

                    for (frame in incoming) {
                        val frameString =
                            bytesToString(frame.data)
                        httpClient.feature(Logging)!!.logger.log("Got frame $frameString")
                        val (type, value) = frameString.split(";")
                        val wsEvent = when (type) {
                            "entityUpdate" -> {
                                val jsonElement = json.parseJson(value).jsonObject
                                val data =
                                    deserializeEntitity(jsonElement, WebsocketEntityData::class)
                                WSEvent.EntityUpdate(
                                    data
                                )
                            }
                            "unreadCounterUpdate" -> {
                                val jsonElement = json.parseJson(value).jsonObject
                                val data =
                                    deserializeEntitity(jsonElement, WebsocketCounterData::class)
                                WSEvent.CounterUpdate(data)
                            }
                            else -> WSEvent.Unknown(
                                frame.data
                            )
                        }
                        channel.send(wsEvent)
                    }
                    channel.close()
                }
            } catch (e: Throwable) {
                channel.close(e)
            }
        }
        return channel
    }

    override suspend fun loadPermissions(listId: Id): List<Permission> {
        return loadAll(Permission::class, listId)
    }

    override suspend fun loadBuckerPermissions(listId: Id): List<BucketPermission> {
        return loadAll(BucketPermission::class, listId)
    }

    override suspend fun loadGroup(groupId: Id): Group {
        return loadElementEntity(Group::class, groupId)
    }

    override suspend fun updatePermission(
        permissionId: IdTuple,
        bucketPermissionId: IdTuple,
        bucketPermissionOwnerEncSessionKey: ByteArray,
        bucketPermissionEncSessionKey: ByteArray
    ) {
        val updateData = UpdatePermissionKeyData(
            permission = permissionId,
            bucketPermission = bucketPermissionId,
            ownerEncSessionKey = bucketPermissionOwnerEncSessionKey,
            symEncSessionKey = bucketPermissionEncSessionKey // legacy
        )
        serviceRequestVoid("sys", "updatepermissionkeyservice", HttpMethod.Post, updateData)
    }

    private fun HttpRequestBuilder.commonHeaders() {
        header("cv", "3.59.16")
        sessionDataProvider.accessToken?.let {
            header("accessToken", it)
        }
    }

    private fun HttpRequestBuilder.entityHeaders(typeModel: TypeModel) {
        header("v", typeModel.version.toString())
    }

    private suspend fun <R : Entity> loadAndMapToEntity(
        path: String,
        responseClass: KClass<R>
    ): R? {
        val address = Url(baseUrl + path)

        return httpClient.get<JsonObject?> {
            commonHeaders()
            entityHeaders(instanceMapper.getTypeModelByClass(responseClass))
            url(address)
        }?.let { deserializeEntitity(it, responseClass) }
    }

    private suspend fun <T : Entity> serializeEntity(
        entity: T,
        sessionKey: ByteArray? = null
    ): JsonObject {
        val (_, _, typeModel, serializer) = instanceMapper.getTypeInfoByClass(entity::class)
        @Suppress("UNCHECKED_CAST")
        return NestedMapper()
            .map(serializer as KSerializer<T>, entity)
            .let { map ->
                val withFormat = map + ("_format" to 0L)
                val resolvedSessionKey =
                    sessionKey ?: keyResolver.resolveSessionKey(typeModel, withFormat, this)
                instanceMapper.encryptAndMapToLiteral(withFormat, typeModel, resolvedSessionKey)
            }
    }

    private suspend fun <T : Any> deserializeEntitity(
        map: JsonObject,
        klass: KClass<T>,
        serviceSessionKey: ByteArray? = null
    ): T {
        val (_, _, typeModel, serializer) = instanceMapper.getTypeInfoByClass(klass)
        val sessionKey = keyResolver.resolveSessionKey(typeModel, map, this) ?: serviceSessionKey
        val processedMap = instanceMapper.decryptAndMapToMap(map, typeModel, sessionKey)
        @Suppress("UNCHECKED_CAST")
        return NestedMapper()
            .unmap(serializer, processedMap)
    }
}