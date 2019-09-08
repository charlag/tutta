package com.charlag.tuta.entities

import kotlinx.serialization.*
import kotlin.reflect.KClass

enum class MetamodelType {
    ELEMENT_TYPE, LIST_ELEMENT_TYPE, AGGREGATED_TYPE, DATA_TRANSFER_TYPE;
}

enum class ValueType {
    BooleanType, NumberType, StringType, DateType, GeneratedIdType, CustomIdType, BytesType;
}

enum class AssociationType {
    ELEMENT_ASSOCIATION, LIST_ELEMENT_ASSOCIATION, LIST_ASSOCIATION, AGGREGATION;
}

enum class Cardinality {
    One, ZeroOrOne, Any;
}

data class Value(
    val type: ValueType,
    val cardinality: Cardinality,
    val encrypted: Boolean,
    val final: Boolean
)

data class Association(
    val type: AssociationType,
    val cardinality: Cardinality,
    val refType: String,
    val final: Boolean,
    val external: Boolean
)

data class TypeModel(
    val name: String,
    val encrypted: Boolean,
    val type: MetamodelType,
    val id: Long,
    val rootId: String,
    val values: Map<String, Value> = mapOf(),
    val associations: Map<String, Association> = mapOf()
)

data class TypeInfo<T : Any>(
    val klass: KClass<T>,
    val typemodel: TypeModel,
    val serializer: KSerializer<T>
)

@Serializable(with = Id.IdSerilizer::class)
abstract class Id {
    abstract fun asString(): String

    @Serializer(forClass = Id::class)
    companion object IdSerilizer: KSerializer<Id> {
        override fun serialize(encoder: Encoder, obj: Id) {
            encoder.encodeString(obj.asString())
        }

        override fun deserialize(decoder: Decoder): Id {
            return GeneratedId(decoder.decodeString())
        }
    }
}

@Serializable(with = Id.IdSerilizer::class)
data class GeneratedId(val stringData: String) : Id() {
    // TODO
    override fun asString(): String = stringData
}

@Serializable(with = CustomId.IdSerilizer::class)
data class CustomId(val stringData: String) : Id() {
    // TODO
    override fun asString(): String = stringData

    @Serializer(forClass = Id::class)
    companion object IdSerilizer: KSerializer<Id> {
        override fun serialize(encoder: Encoder, obj: Id) {
            encoder.encodeString(obj.asString())
        }

        override fun deserialize(decoder: Decoder): Id {
            return CustomId(decoder.decodeString())
        }
    }
}
