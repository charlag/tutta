package com.charlag.tuta.entities

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.native.concurrent.SharedImmutable


object IvSerializer : KSerializer<Map<String, ByteArray?>> by MapSerializer(
    String.serializer(),
    ByteArraySerializer.nullable
)

@Serializable
abstract class Entity {
    @Serializable(with = IvSerializer::class)
    var finalIvs: Map<String, ByteArray?>? = null
}

@Serializable
abstract class ElementEntity : Entity() {
    abstract val _id: Id?
}

@Serializable
abstract class ListElementEntity : Entity() {
    abstract val _id: IdTuple?
}

data class Date(val millis: Long)


@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = Date::class)
object DateSerializer : KSerializer<Date> {
    override fun serialize(encoder: Encoder, value: Date) {
        encoder.encodeLong(value.millis)
    }

    override fun deserialize(decoder: Decoder): Date {
        return Date(decoder.decodeLong())
    }
}

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = ByteArray::class)
object ByteArraySerializer : KSerializer<ByteArray> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ByteArray")

    override fun serialize(encoder: Encoder, value: ByteArray) {
        error("Should not be invoked")
    }

    override fun deserialize(decoder: Decoder): ByteArray {
        error("Should not be invoked")
    }
}

@SharedImmutable
const val GENERATED_ID_BYTES_LENGTH = 9

@SharedImmutable
val GENERATED_MIN_ID = GeneratedId("------------")
@SharedImmutable
val GENERATED_MAX_ID = GeneratedId("zzzzzzzzzzzz")