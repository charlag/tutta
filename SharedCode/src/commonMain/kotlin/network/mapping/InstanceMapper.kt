package com.charlag.tuta.network.mapping

import com.charlag.tuta.*
import com.charlag.tuta.entities.*
import kotlinx.serialization.json.*
import kotlin.reflect.KClass

/**
 * This class provides transformations for communication with the API. This includes:
 *  - walking type model
 *  - transforming fields into their Json (string) form
 *  - encryption
 *  - compression
 *  - assigning default values
 *
 * We use maps for transformations because Json cannot represent binary data and we need
 * that quite often. Transformation JSON->JSON would require encoding/decoding binary fields as
 * Base64 multiple times.
 *
 * Each entity (Instances, Aggregates, DataTransfterTypes) has description of it's structure -
 * [TypeModel].
 */
class InstanceMapper(
    private val cryptor: Cryptor,
    private val compressor: Compressor,
    private val typeModelByName: Map<String, TypeInfo<*>>
) {
    /**
     * Does transformation map -> json
     */
    suspend fun encryptAndMapToLiteral(
        map: Map<String, Any?>,
        typeModel: TypeModel,
        sessionKey: ByteArray?
    ): JsonObject {
        val encrypted = mutableMapOf<String, JsonElement>()
        for ((fieldName, valueModel) in typeModel.values) {
            encrypted[fieldName] =
                if (typeModel.type == MetamodelType.AGGREGATED_TYPE &&
                    fieldName == ID_FIELD_NAME &&
                    map[fieldName] == null
                ) {
                    // We must give aggregates an IDs
                    JsonPrimitive(generateAggregateId())
                } else if (typeModel.type == MetamodelType.LIST_ELEMENT_TYPE
                    && fieldName == ID_FIELD_NAME
                ) {
                    val idFieldValue = map[fieldName]
                    if (idFieldValue != null) {
                        @Suppress("UNCHECKED_CAST")
                        (JsonArray((map[fieldName] as List<String>).map(::JsonPrimitive)))
                    } else {
                        JsonNull
                    }
                } else {
                    // If we update entity, we must get the same final fields. So we use IVs for
                    // that.
                    @Suppress("UNCHECKED_CAST")
                    val finalIvs = map["finalIvs"] as Map<String, ByteArray?>?
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
                val refTypeModel = typeModelByName.getValue(assocModel.refType).typemodel
                @Suppress("UNCHECKED_CAST")
                when (assocModel.type) {
                    AssociationType.AGGREGATION -> when (assocModel.cardinality) {
                        Cardinality.Any -> (value as List<Map<String, Any?>>).map {
                            encryptAndMapToLiteral(it, refTypeModel, sessionKey)
                        }.let(::JsonArray)
                        else -> encryptAndMapToLiteral(
                            value as Map<String, Any?>,
                            refTypeModel,
                            sessionKey
                        )
                    }
                    AssociationType.LIST_ELEMENT_ASSOCIATION -> when (assocModel.cardinality) {
                        Cardinality.Any -> (value as List<List<String>>).map { idTuple ->
                            JsonArray((idTuple).map(::JsonPrimitive))
                        }.let(::JsonArray)
                        Cardinality.One,
                        Cardinality.ZeroOrOne -> // null is checked above
                            JsonArray((value as List<String>).map { JsonPrimitive(it) }).let(::JsonArray)
                    }

                    else -> when (value) {
                        is String -> JsonPrimitive(value)
                        is List<*> -> JsonArray(value.map { JsonPrimitive(value as String) })
                        else -> error("Unknown association value")
                    }
                }
            }
        }
        return JsonObject(encrypted)
    }

    /**
     * Does transformation json -> map
     */
    suspend fun decryptAndMapToMap(
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
                if (typeModel.type == MetamodelType.LIST_ELEMENT_TYPE
                    && fieldName == ID_FIELD_NAME
                ) {
                    listOf(fieldValue.jsonArray[0].jsonPrimitive.content,
                        fieldValue.jsonArray[1].jsonPrimitive.content
                    )
                } else {
                    decryptValue(fieldName, valueModel, fieldValue, sessionKey, finalIvs)
                }

        }
        for ((fieldName, assocModel) in typeModel.associations) {
            val value = map[fieldName]
            decrypted[fieldName] = if (value == null) {
                if (assocModel.cardinality == Cardinality.ZeroOrOne) {
                    null
                } else {
                    error("No association $fieldName")
                }
            } else {
                @Suppress("UNCHECKED_CAST")
                when (assocModel.type) {
                    AssociationType.AGGREGATION -> {
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
                    AssociationType.ELEMENT_ASSOCIATION,
                    AssociationType.LIST_ASSOCIATION -> when (assocModel.cardinality) {
                        Cardinality.One -> value.jsonPrimitive.contentOrNull
                            ?: error("association $fieldName is null even though it's cardinality One")
                        Cardinality.ZeroOrOne -> value.jsonPrimitive.contentOrNull
                        Cardinality.Any -> error("Cannot have ANY element association")
                    }
                    AssociationType.LIST_ELEMENT_ASSOCIATION -> when (assocModel.cardinality) {
                        Cardinality.One -> value.asIdTuple()
                        Cardinality.ZeroOrOne -> {
                            if (value is JsonNull) null
                            else value.asIdTuple()
                        }
                        Cardinality.Any -> {
                            value.jsonArray.map { item -> item.asIdTuple() }
                        }
                    }
                }
            }
        }
        decrypted["finalIvs"] = finalIvs
        return decrypted
    }

    private fun JsonElement.asIdTuple(): List<String> {
        return listOf(jsonArray[0].jsonPrimitive.content, jsonArray[1].jsonPrimitive.content)
    }

    private suspend fun encryptValue(
        name: String,
        valueModel: Value,
        value: Any?,
        sessionKey: ByteArray?,
        iv: ByteArray?
    ): JsonPrimitive {
        if (value == null) {
            if (name.startsWith("_")
                || valueModel.cardinality == Cardinality.ZeroOrOne
            ) {
                return JsonNull
            } else {
                throw Error("Value $name with cardinality ONE cannot be null")
            }
        } else if (valueModel.encrypted) {
            if (iv != null && iv.isEmpty()) {
                // We use empty byte array to indicate that it's final default value
                return JsonPrimitive("")
            }
            val bytes = if (valueModel.type === ValueType.BytesType) value as ByteArray
            else valueToString(value, valueModel).toBytes()
            return JsonPrimitive(
                cryptor.aesEncrypt(
                    bytes,
                    iv ?: cryptor.generateIV(),
                    sessionKey!!,
                    usePadding = true,
                    useMac = true
                ).toBase64()
            )
        } else {
            return JsonPrimitive(valueToString(value, valueModel))
        }
    }

    private suspend fun decryptValue(
        name: String,
        valueModel: Value,
        encryptedValue: JsonElement,
        sessionKey: ByteArray?,
        finalIvs: MutableMap<String, ByteArray?>
    ): Any? {
        return if (encryptedValue is JsonNull) {
            if (valueModel.cardinality === Cardinality.ZeroOrOne) {
                null
            } else {
                error("Value $name is null")
            }
        } else if (valueModel.encrypted) {
            val bytes = base64ToBytes(encryptedValue.jsonPrimitive.content)

            val decryptedBytes =
                if (bytes.isEmpty()) {
                    if (valueModel.final) {
                        // We use empty byte array to indicate that it's final default value
                        finalIvs[name] = byteArrayOf()
                    }
                    bytes
                } else {
                    val decryptResult = cryptor.aesDecrypt(bytes, sessionKey!!, true)
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
            valueFromString(encryptedValue.jsonPrimitive.content, valueModel)
        }
    }

    private fun defaultValue(type: ValueType): Any {
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

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getTypeInfoByClass(klass: KClass<T>): TypeInfo<T> =
        typeModelByName.getValue(klass.noReflectionName) as TypeInfo<T>

    fun getTypeModelByClass(klass: KClass<*>): TypeModel =
        typeModelByName.getValue(klass.noReflectionName).typemodel

    private fun getTypeModelForName(name: String): TypeModel {
        val typeInfo = typeModelByName[name] ?: error("No type info for $name")
        return typeInfo.typemodel
    }

    private fun generateAggregateId(): String {
        return base64ToBase64Url(cryptor.generateRandomData(4).toBase64())
    }

    companion object {
        private const val ID_FIELD_NAME = "_id"
    }
}