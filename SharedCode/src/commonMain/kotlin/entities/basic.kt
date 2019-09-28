package com.charlag.tuta.entities

import kotlinx.serialization.*
import kotlinx.serialization.internal.*

interface Entity

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