package com.charlag.tuta

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.AssociationType.*
import com.charlag.tuta.entities.sys.*
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
import kotlin.collections.set
import kotlin.reflect.KClass


interface SessionKeyLoaders {
    suspend fun loadPermissions(listId: Id): List<Permission>

    suspend fun loadBuckerPermissions(listId: Id): List<BucketPermission>

    suspend fun loadGroup(groupId: Id): Group
}

class GroupKeysCache(private val cryptor: Cryptor) {
    var user: User? = null
    val cachedKeys = mutableMapOf<String, ByteArray>()

    suspend fun getGroupKey(groupId: String): ByteArray? {
        val user = this.user
        return cachedKeys[groupId]
            ?: if (user != null) {
                val membership = user.memberships.find { it.group.asString() == groupId }
                if (membership != null) {
                    val userGroupKey = cachedKeys[user.userGroup.group.asString()]!!
                    cryptor.decryptKey(membership.symEncGKey, userGroupKey)
                } else {
                    null
                }
            } else {
                null
            }
    }
}

class API(
    private val httpClient: HttpClient,
    val baseUrl: String,
    val cryptor: Cryptor,
    val typeModelByName: Map<String, TypeInfo<*>>,
    val compressor: Compressor,
    val groupKeysCache: GroupKeysCache,
    var accessToken: String?,
    val wsUrl: String
) : SessionKeyLoaders {
    private val json = Json(JsonConfiguration.Stable)
    private val mailBodySessionKeyCache = mutableMapOf<String, ByteArray>()

    suspend fun getSalt(mailAddress: String): SaltReturn {
        val address = Url(baseUrl + "sys/saltservice")
        val postData = SaltData(mailAddress = mailAddress)
        val serializedEntity = serializeEntity(postData)
        val response = httpClient.get<JsonObject> {
            url(address)
            parameter("_body", json.stringify(JsonObjectSerializer, serializedEntity))
        }
        return deserializeEntitity(response, SaltReturn::class)
    }

    suspend fun createSession(mailAddress: String, authVerifier: String): CreateSessionReturn {
        val postData = CreateSessionData(
            mailAddress = mailAddress,
            authVerifier = authVerifier,
            clientIdentifier = "Multiplatform test"
        )
        return post("sys/sessionservice", postData, CreateSessionReturn::class)!!
    }

    suspend fun <T : Entity> serviceRequest(
        app: String,
        name: String,
        method: HttpMethod,
        requestEntity: Entity?,
        responseClass: KClass<T>,
        queryParams: Map<String, String>? = null,
        sk: ByteArray? = null
    ): T {
        val path = "${app}/${name}"
        return if (method == HttpMethod.Get) {
            val serializedEntity = requestEntity?.let { serializeEntity(it, sk) }
            val address = Url(baseUrl + path)
            httpClient.get<JsonObject?> {
                commonHeaders()
                url(address)
                if (serializedEntity != null) {
                    parameter("_body", json.stringify(JsonObjectSerializer, serializedEntity))
                }
            }!!.let { deserializeEntitity(it, responseClass) }
        } else {
            post(path, requestEntity, responseClass, sk)!!
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
        post<Nothing>("${app}/${name}", requestEntity, null, sk)
    }

    suspend fun <T : Entity> loadElementEntity(klass: KClass<T>, id: Id): T {
        val (_, model, typeModel) = typeModelByName.getValue(klass.noReflectionName)
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

    suspend fun <T : Entity> loadListElementEntity(klass: KClass<T>, id: IdTuple): T {
        val (_, model, typeModel) = typeModelByName.getValue(klass.noReflectionName)
        val name = typeModel.name.toLowerCase()
        val url = "${model}/${name}/${id.listId.asString()}/${id.elementId.asString()}"
        return loadAndMapToEntity(url, klass)!!
    }

    suspend fun <T : ListElementEntity> loadAll(klass: KClass<T>, listId: Id): List<T> {
        return loadRangeInBetween(klass, listId)
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
            result += range.dropLastWhile { it._id.elementId > endId }
            if (range.isEmpty() || range.last()._id.elementId > endId) {
                break
            }
        }
        return result
    }

    suspend fun <T : Entity> loadRange(
        klass: KClass<T>, listId: Id, start: Id, count: Int, reverse: Boolean
    ): List<T> {
        val (_, model, typeModel) = typeModelByName[klass.noReflectionName]
            ?: error("no type model ${klass.noReflectionName}")
        if (typeModel.type != MetamodelType.LIST_ELEMENT_TYPE) error("Only list elements are allowed to be loaded in range")
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
                httpClient.feature(Logging)!!.logger.log("Failed to decrypt entity, skipping $e")
                null
            }
        }
    }

    suspend fun <T : Entity> updateEntity(entity: T) {
        val (_, model, typeModel) = typeModelByName[entity::class.noReflectionName]!!

        val idPart = if (entity is ListElementEntity) {
            "${entity._id.listId.asString()}/${entity._id.elementId.asString()}"
        } else {
            (entity as ElementEntity)._id.asString()
        }
        val address = Url(baseUrl + "${model}/${typeModel.name.toLowerCase()}/${idPart}")
        val serializer = httpClient.feature(JsonFeature)!!.serializer
        val serializedEntity = serializeEntity(entity)
        return httpClient.put {
            commonHeaders()
            url(address)
            body = serializer.write(serializedEntity)
        }
    }


    suspend fun <T : ListElementEntity> deleteListElementEntity(klass: KClass<T>, id: IdTuple) {
        val (_, model, typeModel) = typeModelByName[klass.noReflectionName]!!
        val typeName = typeModel.name.toLowerCase()
        val address =
            Url("$baseUrl$model/$typeName/${id.listId.asString()}/${id.elementId.asString()}")
        return httpClient.delete {
            commonHeaders()
            header("v", typeModel.version)
            url(address)
        }
    }

    fun HttpRequestBuilder.commonHeaders() {
        header("cv", "3.59.16")
        accessToken?.let {
            header("accessToken", it)
        }
    }

    fun HttpRequestBuilder.entityHeaders(typeModel: TypeModel) {
        header("v", typeModel.version.toString())
    }

    fun getTypeModelByClass(klass: KClass<*>): TypeModel =
        typeModelByName[klass.noReflectionName]!!.typemodel

    fun getTypeModelForName(name: String): TypeModel {
        val typeInfo = typeModelByName[name] ?: error("No type info for $name")
        return typeInfo.typemodel
    }

    sealed class WSEvent {
        // TODO
        object CounterUpdate : WSEvent()

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
                    accessToken?.let { token -> url.parameters["accessToken"] = token }
                }) {

                    for (frame in incoming) {
                        val frameString = bytesToString(frame.data)
                        httpClient.feature(Logging)!!.logger.log("Got frame $frameString")
                        val (type, value) = frameString.split(";")
                        val wsEvent = when (type) {
                            "entityUpdate" -> {
                                val jsonElement = json.parseJson(value).jsonObject
                                val data =
                                    deserializeEntitity(jsonElement, WebsocketEntityData::class)
                                WSEvent.EntityUpdate(data)
                            }
                            "unreadCounterUpdate" -> {
                                WSEvent.CounterUpdate
                            }
                            else -> WSEvent.Unknown(frame.data)
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

    private suspend fun <R : Entity> loadAndMapToEntity(
        path: String,
        responseClass: KClass<R>
    ): R? {
        val address = Url(baseUrl + path)

        return httpClient.get<JsonObject?> {
            commonHeaders()
            entityHeaders(typeModelByName.getValue(responseClass.noReflectionName).typemodel)
            url(address)
        }?.let { deserializeEntitity(it, responseClass) }
    }

    private suspend fun <R : Entity> post(
        path: String,
        requestEntity: Entity?,
        responseClass: KClass<R>?,
        sessionKey: ByteArray? = null
    ): R? {
        val serializedEntity = requestEntity?.let { serializeEntity(it, sessionKey) }
        val serializer = httpClient.feature(JsonFeature)!!.serializer
        val address = Url(baseUrl + path)

        return if (responseClass != null) {
            httpClient.post<JsonObject?> {
                commonHeaders()
                url(address)

                if (serializedEntity != null) body = serializer.write(serializedEntity)
            }?.let { deserializeEntitity(it, responseClass) }

        } else {
            httpClient.post<Unit> {
                commonHeaders()
                url(address)

                if (serializedEntity != null) body = serializer.write(serializedEntity)
            }
            return null
        }

    }

    private suspend fun <T : Entity> serializeEntity(
        entity: T,
        sessionKey: ByteArray? = null
    ): JsonObject {
        val (_, _, typeModel, serializer) = typeModelByName[entity::class.noReflectionName]!!
        @Suppress("UNCHECKED_CAST")
        return NestedMapper().map(serializer as KSerializer<T>, entity)
            .let { map ->
                val withFormat = map + ("_format" to 0L)
                val resolvedSessionKey =
                    sessionKey ?: resolveSessionKey(typeModel, withFormat, this)
                encryptAndMapToLiteral(withFormat, typeModel, resolvedSessionKey)
            }
    }

    private suspend fun <T : Entity> deserializeEntitity(
        map: JsonObject,
        klass: KClass<T>
    ): T {
        val typeModel = getTypeModelByClass(klass)
        val sessionKey = resolveSessionKey(typeModel, map, this)
        val processedMap = decryptAndMapToMap(map, typeModel, sessionKey)
        val serializer = typeModelByName.getValue(klass.noReflectionName).serializer
        @Suppress("UNCHECKED_CAST")
        return NestedMapper().unmap(serializer as KSerializer<T>, processedMap)
    }

    private fun Any?.stringOrJsonContent() =
        this as? String ?: (this as? JsonElement)?.contentOrNull

    suspend fun resolveSessionKey(
        typeModel: TypeModel,
        ownerEncSessionKey: ByteArray?,
        ownerGroup: String?,
        permissions: String?,
        loaders: SessionKeyLoaders
    ): ByteArray? {
        if (!typeModel.encrypted) return null
        val groupKey = ownerGroup?.let { groupKeysCache.getGroupKey(ownerGroup) }
        if (ownerEncSessionKey != null && groupKey != null) {
            return cryptor.decryptKey(ownerEncSessionKey, groupKey)
        }
        permissions ?: error("No symmetric key and also no permissions")
        val listPermissions =
            loaders.loadPermissions(GeneratedId(permissions))
        val symmetricPermission = listPermissions.find { p ->
            (p.type == PermissionType.PublicSymemtric.value ||
                    p.type == PermissionType.Symmetric.value) &&
                    p._ownerGroup != null &&
                    groupKeysCache.getGroupKey(p._ownerGroup.asString()) != null
        }
        if (symmetricPermission != null) {
            val gk = groupKeysCache.getGroupKey(symmetricPermission._ownerGroup!!.asString())
            return cryptor.decryptKey(symmetricPermission._ownerEncSessionKey!!, gk!!)
        }
        val publicPermission =
            listPermissions.find { lp ->
                lp.type == PermissionType.Public.value || lp.type == PermissionType.External.value
            } ?: error("Could not find permission")

        val bucketPermissions = loadBuckerPermissions(publicPermission.bucket!!.bucketPermissions)
        // find the bucket permission with the same group as the permission and public type
        val bucketPermission = bucketPermissions.find { bp ->
            (bp.type == BucketPermissionType.Public.value || bp.type == BucketPermissionType.External.value) &&
                    publicPermission._ownerGroup == bp._ownerGroup
        } ?: error("no corresponding bucket permission found")

        if (bucketPermission.type == BucketPermissionType.External.value) {
            error("External permissions are not implemented")
        } else {
            bucketPermission.pubEncBucketKey
                ?: error("PubEncBucketKey is not defined for BucketPermission $bucketPermissions")
            publicPermission.bucketEncSessionKey
                ?: error("BucketEncSessionKey is not defined for $symmetricPermission")
            val group = loadGroup(bucketPermission.group)
            val keypair = group.keys[0]
            // decrypt RSA keys
            val bucketPermissionGroupKey = groupKeysCache.getGroupKey(group._id.asString())
                ?: error("No key for ${group._id} ")

            val privKey = cryptor.decryptRsaKey(keypair.symEncPrivKey, bucketPermissionGroupKey)
            val bucketKey = cryptor.rsaDecrypt(bucketPermission.pubEncBucketKey, privKey)
            val sessionKey = cryptor.decryptKey(publicPermission.bucketEncSessionKey, bucketKey)
            return sessionKey
            // TODO: _updateWithSymPermissionKey
//            val bucketPermissionOwnerGroupKey =
//                groupKeysCache.getGroupKey(bucketPermission._ownerGroup!!.asString())
//            val buckePermissionGroupKey =
//                groupKeysCache.getGroupKey(bucketPermission.group.asString())

        }
    }

    suspend fun resolveSessionKey(
        typeModel: TypeModel,
        instance: Map<String, Any?>,
        loaders: SessionKeyLoaders
    ): ByteArray? {
        return this.resolveSessionKey(
            typeModel,
            instance["_ownerEncSessionKey"].stringOrJsonContent()?.let(::base64ToBytes),
            instance["_ownerGroup"].stringOrJsonContent(),
            instance["_permissions"].stringOrJsonContent(),
            loaders
        )
    }

    private suspend fun encryptAndMapToLiteral(
        map: Map<String, Any?>,
        typeModel: TypeModel,
        sessionKey: ByteArray?
    ): JsonObject {
        val encrypted = mutableMapOf<String, JsonElement>()
        @Suppress("UNCHECKED_CAST")
        val finalIvs = map["finalIvs"] as Map<String, ByteArray?>?
        for ((fieldName, valueModel) in typeModel.values) {
            encrypted[fieldName] =
                if (typeModel.type == MetamodelType.AGGREGATED_TYPE &&
                    fieldName == "_id" &&
                    map[fieldName] == null
                ) {
                    // We must give aggregates IDs
                    JsonLiteral(base64ToBase64Url(cryptor.generateRandomData(4).toBase64()))
                } else if (typeModel.type == MetamodelType.LIST_ELEMENT_TYPE && fieldName == "_id") {
                    @Suppress("UNCHECKED_CAST")
                    JsonArray((map[fieldName] as List<String>).map(::JsonPrimitive))
                } else {
                    // If we update entity, we must get the same final fields. So we useIVs for
                    // that.
                    val iv = if (valueModel.final) finalIvs?.get(fieldName) else null
                    encryptValue(fieldName, valueModel, map[fieldName], sessionKey, iv)
                }
        }
        for ((fieldName, assocModel) in typeModel.associations) {
            val value = map[fieldName]
            encrypted[fieldName] = if (value == null) {
                if (assocModel.cardinality == Cardinality.ZeroOrOne) JsonNull
                else error("No association $fieldName")
            } else {
                val refTypeModel = typeModelByName[assocModel.refType]!!.typemodel
                @Suppress("UNCHECKED_CAST")
                when (assocModel.type) {
                    AGGREGATION -> when (assocModel.cardinality) {
                        Cardinality.Any -> (value as List<Map<String, Any?>>).map {
                            encryptAndMapToLiteral(it, refTypeModel, sessionKey)
                        }.let(::JsonArray)
                        else -> encryptAndMapToLiteral(
                            value as Map<String, Any?>,
                            refTypeModel,
                            sessionKey
                        )
                    }
                    LIST_ELEMENT_ASSOCIATION -> when (assocModel.cardinality) {
                        Cardinality.Any -> (value as List<List<String>>).map { idTuple ->
                            JsonArray((idTuple).map(::JsonLiteral))
                        }.let(::JsonArray)
                        Cardinality.One,
                        Cardinality.ZeroOrOne -> // null is checked above
                            JsonArray((value as List<String>).map(::JsonLiteral)).let(::JsonArray)
                    }

                    else -> when (value) {
                        is String -> JsonLiteral(value)
                        is List<*> -> JsonArray(value.map { JsonLiteral(value as String) })
                        else -> error("Unknown association value")
                    }
                }
            }
        }
        return JsonObject(encrypted)
    }

    private suspend fun decryptAndMapToMap(
        map: JsonObject,
        typeModel: TypeModel,
        sessionKey: ByteArray?
    ): Map<String, Any?> {
        val decrypted = mutableMapOf<String, Any?>()
        val finalIvs = mutableMapOf<String, ByteArray?>()
        for ((fieldName, valueModel) in typeModel.values) {
            val fieldValue = map[fieldName]!!
            decrypted[fieldName] =
                    // Kind of a hack because model only defines type of the element id
                if (typeModel.type == MetamodelType.LIST_ELEMENT_TYPE && fieldName == "_id") {
                    listOf(fieldValue.jsonArray[0].content, fieldValue.jsonArray[1].content)
                } else {
                    decryptValue(fieldName, valueModel, fieldValue, sessionKey, finalIvs)
                }

        }
        for ((fieldName, assocModel) in typeModel.associations) {
            val value = map[fieldName]
            decrypted[fieldName] = if (value == null) {
                if (assocModel.cardinality == Cardinality.ZeroOrOne) null else error("No association $fieldName")
            } else {
                @Suppress("UNCHECKED_CAST")
                when (assocModel.type) {
                    AGGREGATION -> {
                        val refTypeModel = getTypeModelForName(assocModel.refType)
                        when (assocModel.cardinality) {
                            Cardinality.Any -> (value as JsonArray).map {
                                decryptAndMapToMap(it as JsonObject, refTypeModel, sessionKey)
                            }
                            Cardinality.ZeroOrOne ->
                                if (value is JsonNull) null
                                else decryptAndMapToMap(
                                    value as JsonObject,
                                    refTypeModel,
                                    sessionKey
                                )
                            else -> decryptAndMapToMap(
                                value as JsonObject,
                                refTypeModel,
                                sessionKey
                            )
                        }
                    }
                    ELEMENT_ASSOCIATION, LIST_ASSOCIATION -> when (assocModel.cardinality) {
                        Cardinality.One -> value.contentOrNull
                            ?: error("association $fieldName is null even though it's cardinality One")
                        Cardinality.ZeroOrOne -> value.contentOrNull
                        Cardinality.Any -> error("Cannot have ANY element assciation")
                    }
                    LIST_ELEMENT_ASSOCIATION -> when (assocModel.cardinality) {
                        Cardinality.One -> listOf(
                            value.jsonArray[0].content,
                            value.jsonArray[1].content
                        )
                        Cardinality.ZeroOrOne -> {
                            if (value is JsonNull) null
                            else listOf(
                                value.jsonArray[0].content,
                                value.jsonArray[1].content
                            )
                        }
                        Cardinality.Any -> {
                            value.jsonArray.map { item ->
                                listOf(
                                    item.jsonArray[0].content,
                                    item.jsonArray[1].content
                                )
                            }
                        }
                    }
                }
            }
        }
        decrypted["finalIvs"] = finalIvs
        return decrypted
    }

    private suspend fun encryptValue(
        name: String,
        valueModel: Value,
        value: Any?,
        sessionKey: ByteArray?,
        iv: ByteArray?
    ): JsonPrimitive {
        if (value == null) {
            if (name == "_id"
                || name == "_permissions"
                || valueModel.cardinality == Cardinality.ZeroOrOne
            ) {
                return JsonNull
            } else {
                throw Error("Value $name with cardinality ONE cannot be null")
            }
        } else if (valueModel.encrypted) {
            if (value == defaultValue(valueModel.type) && iv == null) {
                // if iv is there, we need to re-encrypt it back tp get the same value
                // but if not we should just restore empty placeholder for default value
                return JsonLiteral("")
            }
            val bytes = if (valueModel.type === ValueType.BytesType) value as ByteArray
            else valueToString(value, valueModel).toBytes()
            return JsonLiteral(
                cryptor.encrypt(
                    bytes,
                    iv ?: cryptor.generateIV(),
                    sessionKey!!,
                    /*usePadding=*/true,
                    /*useMac=*/true
                ).toBase64()
            )
        } else {
            return JsonLiteral(valueToString(value, valueModel))
        }
    }

    private suspend fun decryptValue(
        name: String,
        valueModel: Value,
        encryptedValue: JsonElement,
        sessionKey: ByteArray?,
        finalIvs: MutableMap<String, ByteArray?>
    ): Any? {
        return if (encryptedValue.isNull) {
            if (valueModel.cardinality === Cardinality.ZeroOrOne) {
                null
            } else {
                error("Value $name is null")
            }
        } else if (valueModel.encrypted) {
            val bytes = base64ToBytes(encryptedValue.primitive.content)

            val decryptedBytes =
                if (bytes.isEmpty()) bytes else {
                    val decryptResult = cryptor.decrypt(bytes, sessionKey!!, true)
                    if (valueModel.final) {
                        finalIvs[name] = decryptResult.iv
                    }
                    decryptResult.data
                }

            when (valueModel.type) {
                ValueType.BytesType -> decryptedBytes
                ValueType.CompressedStringType -> compressor.decompressString(decryptedBytes)
                else -> valueFromString(bytesToString(decryptedBytes), valueModel)
            }
        } else {
            valueFromString(encryptedValue.primitive.content, valueModel)
        }
    }

    fun defaultValue(type: ValueType): Any {
        return when (type) {
            ValueType.StringType, ValueType.CompressedStringType -> ""
            ValueType.NumberType -> 0L
            ValueType.BytesType -> byteArrayOf()
            ValueType.DateType -> 0L
            ValueType.BooleanType -> false
            else -> error("No default value for $type")
        }
    }

    private fun valueFromString(value: String, valueModel: Value): Any? {
        if (value == "" && valueModel.cardinality == Cardinality.One) {
            return defaultValue(valueModel.type)
        }

        return when (valueModel.type) {
            ValueType.BooleanType -> value != "0"
            ValueType.NumberType -> value.toLong()
            ValueType.DateType -> value.toLong()
            ValueType.BytesType -> base64ToBytes(value)
            ValueType.StringType,
            ValueType.CustomIdType,
            ValueType.GeneratedIdType -> value
            ValueType.CompressedStringType ->
                error("CompressedStringType must be converted from bytes, not from string")
        }
    }

    private fun valueToString(value: Any?, valueModel: Value): String {
        return when (valueModel.type) {
            ValueType.BooleanType -> if (value as Boolean) "1" else "0"
            ValueType.NumberType, ValueType.DateType -> (value as Long).toString()
            ValueType.BytesType -> (value as ByteArray).toBase64()
            ValueType.StringType,
            ValueType.CustomIdType,
            ValueType.GeneratedIdType -> value as String
            ValueType.CompressedStringType -> compressor.compressString(value as String)
        }
    }
}