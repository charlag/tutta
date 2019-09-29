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
        val (_, model, typeModel) = typeModelByName.getValue(klass.noReflectionName)
        return loadAndMapToEntity(
            "${model}/${typeModel.name.toLowerCase()}/${id.asString()}",
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
        }.map { deserializeEntitity(it as JsonObject, klass) }
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
        val (_, _, typeModel, serializer) = typeModelByName[entity::class.noReflectionName]!!
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
        @Suppress("UNCHECKED_CAST")
        return NestedMapper().unmap(serializer as KSerializer<T>, processedMap)
    }

    private fun Any?.stringOrJsonContent() =
        this as? String ?: (this as JsonElement).contentOrNull

    private suspend fun resolveSessionKey(
        typeModel: TypeModel,
        instance: Map<String, Any?>,
        loaders: SessionKeyLoaders
    ): ByteArray? {
        if (!typeModel.encrypted) return null
        val key = instance["_ownerEncSessionKey"]
        val ownerGroup = instance["_ownerGroup"].stringOrJsonContent()
        val groupKey = ownerGroup?.let { groupKeysCache.getGroupKey(ownerGroup) }
        if (key != null && groupKey != null) {
            return cryptor.decryptKey(
                key.stringOrJsonContent()?.let(::base64ToBytes) ?: key as ByteArray,
                groupKey
            )
        }
        val permissionsString =
            if (instance["_permissions"] is String) instance["_permissions"] as String
            else (instance["_permissions"] as JsonLiteral).content
        val listPermissions =
            loaders.loadPermissions(GeneratedId(permissionsString))
        val p =
            listPermissions.find { p ->
                (p.type == PermissionType.PublicSymemtric.value ||
                        p.type == PermissionType.Symmetric.value) &&
                        p._ownerGroup != null &&
                        groupKeysCache.getGroupKey(p._ownerGroup.asString()) != null
            }
        if (p != null) {
            val gk = groupKeysCache.getGroupKey(p._ownerGroup!!.asString())
            return cryptor.decryptKey(p._ownerEncSessionKey!!, gk!!)
        }
        val pp =
            listPermissions.find { lp ->
                lp.type == PermissionType.Public.value || lp.type == PermissionType.External.value
            }
        if (pp == null) {
            error("Could not find permission $typeModel $instance")
        }

        val bucketPermissions = loadBuckerPermissions(pp.bucket!!.bucketPermissions)
        // find the bucket permission with the same group as the permission and public type
        val bp = bucketPermissions.find { bp ->
            (bp.type == BucketPermissionType.Public.value || bp.type == BucketPermissionType.External.value) &&
                    pp._ownerGroup === bp._ownerGroup
        }

        if (bp == null) {
            error("no corresponding bucket permission found");
        }

        if (bp.type == BucketPermissionType.External.value) {
            error("External permissions are not implemented")
        } else {
            val group = loadGroup(bp.group)
            val keypair = group.keys[0]
            // decrypt RSA keys
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
            val fieldValue = map[fieldName]!!
            decrypted[fieldName] =
                    // Kind of a hack because model only defines type of the element id
                if (typeModel.type == MetamodelType.LIST_ELEMENT_TYPE && fieldName == "_id") {
                    listOf(fieldValue.jsonArray[0].content, fieldValue.jsonArray[1].content)
                } else {
                    decryptValue(fieldName, valueModel, fieldValue, sessionKey)
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
                    ELEMENT_ASSOCIATION, LIST_ASSOCIATION -> value.content
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
            val bytes = base64ToBytes(encryptedValue.primitive.content)

            val decryptedBytes =
                if (bytes.isEmpty()) bytes else cryptor.decrypt(bytes, sessionKey!!, true)
            when (valueModel.type) {
                ValueType.BytesType -> decryptedBytes
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
            ValueType.CompressedStringType -> TODO()
        }
    }

    private fun valueToString(value: Any?, valueModel: Value): String {
        return when (valueModel.type) {
            ValueType.BooleanType -> if (value as Boolean) "1" else "0"
            ValueType.NumberType, ValueType.DateType -> (value as Long).toString()
            ValueType.StringType, ValueType.BytesType,
            ValueType.CustomIdType, ValueType.GeneratedIdType -> value as String
            ValueType.CompressedStringType -> TODO()
        }
    }
}