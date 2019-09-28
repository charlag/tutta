package com.charlag.tuta

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.AssociationType.*
import com.charlag.tuta.entities.sys.*
import io.ktor.client.HttpClient
import io.ktor.client.features.feature
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.*
import io.ktor.http.Url
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

typealias GroupKeysCache = MutableMap<Id, ByteArray>

class API(
    private val httpClient: HttpClient,
    val baseUrl: String,
    val cryptor: Cryptor,
    val typeModelByName: Map<String, TypeInfo<*>>,
    val groupKeysCache: GroupKeysCache,
    var accessToken: String?
) : SessionKeyLoaders {
    private val json = Json(JsonConfiguration.Stable)

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

    suspend fun <T : Entity> loadElementEntity(klass: KClass<T>, id: Id): T {
        val typeModel = typeModelByName.getValue(klass.noReflectionName).typemodel
        return loadAndMapToEntity(
            "sys/${typeModel.name.toLowerCase()}/${id.asString()}",
            klass
        )!!
    }

    suspend inline fun <reified T : Entity> loadElementEntity(id: Id): T {
        return loadElementEntity(T::class, id)
    }

    suspend fun <T : Entity> loadAll(klass: KClass<T>, listId: Id): List<T> {
        // TODO load in chunks until all is loaded
        return loadRange(klass, listId, GENERATED_MIN_ID, 1000, false)
    }

    suspend fun <T : Entity> loadRange(
        klass: KClass<T>, listId: Id, start: Id, count: Int, reverse: Boolean
    ): List<T> {
        val typeModel = getTypeModelByClass(klass)
        if (typeModel.type != MetamodelType.LIST_ELEMENT_TYPE) error("Only list elements are allowed to be loaded in range")
        return httpClient.get<JsonArray> {
            commonHeaders()
            parameter("start", start)
            parameter("count", count)
            parameter("reverse", reverse.toString())
            url(baseUrl + "sys/${typeModel.name.toLowerCase()}/${listId}")
        }.map { deserializeEntitity(it as JsonObject, klass) }
    }

    fun HttpRequestBuilder.commonHeaders() {
        header("cv", "3.59.16")
        header("v", "49")
        accessToken?.let {
            header("accessToken", it)
        }
    }

    fun getTypeModelByClass(klass: KClass<*>): TypeModel =
        typeModelByName[klass.noReflectionName]!!.typemodel

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
            url(address)
        }?.let { deserializeEntitity(it, responseClass) }
    }

    private suspend fun <R : Entity> post(
        path: String,
        requestEntity: Entity?,
        responseClass: KClass<R>
    ): R? {
        val serializedEntity = requestEntity?.let { serializeEntity(it) }
        val serializer = httpClient.feature(JsonFeature)!!.serializer
        val address = Url(baseUrl + path)
        return httpClient.post<JsonObject?> {
            commonHeaders()
            url(address)

            if (serializedEntity != null) body = serializer.write(serializedEntity)
        }?.let { deserializeEntitity(it, responseClass) }
    }

    private suspend fun <T : Entity> serializeEntity(entity: T): JsonObject {
        val (_, typeModel, serializer) = typeModelByName[entity::class.noReflectionName]!!
        @Suppress("UNCHECKED_CAST")
        return NestedMapper().map(serializer as KSerializer<T>, entity)
            .let { map ->
                val withFormat = map + ("_format" to 0L)
                val sessionKey = resolveSessionKey(typeModel, withFormat, this)
                encryptAndMapToLiteral(withFormat, typeModel, sessionKey)
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
        println("processedMap $processedMap")
        @Suppress("UNCHECKED_CAST")
        return NestedMapper().unmap(serializer as KSerializer<T>, processedMap)
    }

    private suspend fun resolveSessionKey(
        typeModel: TypeModel,
        instance: Map<String, Any?>,
        loaders: SessionKeyLoaders
    ): ByteArray? {
        if (!typeModel.encrypted) return null
        val key = instance["_owenrEncSessionKey"]
        val groupKey = groupKeysCache[instance["_ownerGroup"]]
        if (key != null && groupKey != null) {
            return cryptor.decryptKey(
                if (key is String) base64ToBytes(key) else key as ByteArray,
                groupKey
            )
        }
        val listPermissions =
            loaders.loadPermissions(GeneratedId(instance["_permissions"] as String))
        val p =
            listPermissions.find { p -> p._ownerGroup != null && groupKeysCache.containsKey(p._ownerGroup) }
        if (p != null) {
            val gk = groupKeysCache[p._ownerGroup]
            return cryptor.decryptKey(p._ownerEncSessionKey!!, gk!!)
        }
        val pp =
            listPermissions.find { lp ->
                lp.type == PermissionType.Public.value || lp.type == PermissionType.External.value
            }
        if (pp == null) {
            error("Could not find permission")
        }
        error("Public permissions are not implemented")
    }

    private suspend fun encryptAndMapToLiteral(
        map: Map<String, Any?>,
        typeModel: TypeModel,
        sessionKey: ByteArray?
    ): JsonObject {
        val encrypted = mutableMapOf<String, JsonElement>()
        for ((fieldName, valueModel) in typeModel.values) {
            encrypted[fieldName] = encryptValue(fieldName, valueModel, map[fieldName], sessionKey)
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
                    else -> when (value) {
                        is String -> JsonLiteral(value)
                        is List<*> -> JsonArray(value.map { JsonLiteral(it as String) })
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
        for ((fieldName, valueModel) in typeModel.values) {
            decrypted[fieldName] =
                decryptValue(fieldName, valueModel, map[fieldName]!!, sessionKey)
        }
        for ((fieldName, assocModel) in typeModel.associations) {
            val value = map[fieldName]
            decrypted[fieldName] = if (value == null) {
                if (assocModel.cardinality == Cardinality.ZeroOrOne) null else error("No association $fieldName")
            } else {
                val refTypeModel = typeModelByName[assocModel.refType]!!.typemodel
                @Suppress("UNCHECKED_CAST")
                when (assocModel.type) {
                    AGGREGATION -> when (assocModel.cardinality) {
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
                    ELEMENT_ASSOCIATION, LIST_ASSOCIATION -> value.content
                    LIST_ELEMENT_ASSOCIATION -> listOf(
                        value.jsonArray[0].content,
                        value.jsonArray[1].content
                    )
                }
            }
        }
        return decrypted
    }

    private suspend fun encryptValue(
        name: String,
        valueModel: Value,
        value: Any?,
        sessionKey: ByteArray?
    ): JsonPrimitive {
        if (value == null && name !== "_id" && name !== "_permissions") {
            if (valueModel.cardinality == Cardinality.ZeroOrOne) {
                return JsonNull
            } else {
                throw Error("Value $name with cardinality ONE cannot be null")
            }
        } else if (valueModel.encrypted) {
            val bytes = if (valueModel.type === ValueType.BytesType) value as ByteArray
            else base64ToBytes(valueToString(value, valueModel))
            return JsonLiteral(
                cryptor.encrypt(
                    bytes,
                    cryptor.generateIV(),
                    sessionKey!!,
                    true,
                    true
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
        sessionKey: ByteArray?
    ): Any? {
        return if (encryptedValue.isNull) {
            if (valueModel.cardinality === Cardinality.ZeroOrOne) {
                null
            } else {
                error("Value $name is null")
            }
        } else if (valueModel.encrypted) {
            val decryptedBytes =
                cryptor.decrypt(base64ToBytes(encryptedValue.primitive.content), sessionKey!!, true)
            when (valueModel.type) {
                ValueType.BytesType -> decryptedBytes
                else -> valueFromString(encryptedValue.primitive.content, valueModel)
            }
        } else {
            valueFromString(encryptedValue.primitive.content, valueModel)
        }
    }

    private fun valueFromString(value: String, valueModel: Value): Any? {
        return when (valueModel.type) {
            ValueType.BooleanType -> value != "0"
            ValueType.NumberType -> value.toLong()
            ValueType.DateType -> Date((value.toLong()))
            ValueType.BytesType -> base64ToBytes(value)
            ValueType.StringType,
            ValueType.CustomIdType,
            ValueType.GeneratedIdType -> value
        }
    }

    private fun valueToString(value: Any?, valueModel: Value): String {
        return when (valueModel.type) {
            ValueType.BooleanType -> if (value as Boolean) "1" else "0"
            ValueType.NumberType, ValueType.DateType -> (value as Long).toString()
            ValueType.StringType, ValueType.BytesType,
            ValueType.CustomIdType, ValueType.GeneratedIdType -> value as String
        }
    }
}