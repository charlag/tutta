package com.charlag.tuta.entities

import com.charlag.tuta.entities.sys.IdTuple
import kotlinx.serialization.*
import kotlinx.serialization.internal.*


object IvSerializer : KSerializer<Map<String, ByteArray?>> by HashMapSerializer(
    String.serializer(),
    ByteArraySerializer.nullable
)

@Serializable
abstract class Entity(
    @Serializable(with = IvSerializer::class)
    var finalIvs: Map<String, ByteArray?>? = null
)

@Serializable
abstract class ElementEntity : Entity() {
    abstract val _id: Id
}

@Serializable
abstract class ListElementEntity : Entity() {
    abstract val _id: IdTuple
}

data class Date(val millis: Long)


@Serializer(forClass = Date::class)
object DateSerializer : KSerializer<Date> {
    override fun serialize(encoder: Encoder, obj: Date) {
        encoder.encodeLong(obj.millis)
    }

    override fun deserialize(decoder: Decoder): Date {
        return Date(decoder.decodeLong())
    }
}

@Serializer(forClass = ByteArray::class)
object ByteArraySerializer : KSerializer<ByteArray> {
    override val descriptor: SerialDescriptor = SerialClassDescImpl("ByteArray")

    override fun serialize(encoder: Encoder, obj: ByteArray) {
        error("Should not be invoked")
    }

    override fun deserialize(decoder: Decoder): ByteArray {
        error("Should not be invoked")
    }
}

const val GENERATED_ID_BYTES_LENGTH = 9

val GENERATED_MIN_ID = GeneratedId("------------")
val GENERATED_MAX_ID = GeneratedId("zzzzzzzzzzzz")