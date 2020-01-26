package com.charlag.tuta

import com.charlag.tuta.entities.ByteArraySerializer
import kotlinx.serialization.*
import kotlinx.serialization.modules.EmptyModule
import kotlinx.serialization.modules.SerialModule


class NestedMapper(context: SerialModule = EmptyModule) : AbstractSerialFormat(context) {

    fun <T : Any?> map(serializer: SerializationStrategy<T>, value: T): Map<String, Any?> {
        lateinit var result: Map<String, Any?>
        return MapperMapOutput {
            result = it
        }
            .encode(serializer, value)
            .let { result }
    }

    fun <T : Any?> unmap(deserializer: DeserializationStrategy<T>, map: Map<String, Any?>): T {
        return MapperMapInput(map).decode(deserializer)
    }

    private abstract inner class Mapperinput(val obj: Any?) : NamedValueDecoder() {
        override val context: SerialModule
            get() = this@NestedMapper.context

        override fun composeName(parentName: String, childName: String): String = childName

        abstract fun getByTag(tag: String): Any?

        override fun decodeTaggedValue(tag: String): Any {
            val o = getByTag(tag) ?: throw MissingFieldException(tag)
            return o
        }

        override fun decodeTaggedNotNullMark(tag: String): Boolean {
            return getByTag(tag) != null
        }

        override fun <T> decodeSerializableValue(deserializer: DeserializationStrategy<T>): T {
            val tag = currentTagOrNull
            if (deserializer === ByteArraySerializer) {
                if (tag == null) error("value is null")
                @Suppress("UNCHECKED_CAST")
                return getByTag(tag) as? T ?: error("It's not bytearray")
            }
            return super.decodeSerializableValue(deserializer)
        }

        override fun beginStructure(
            desc: SerialDescriptor,
            vararg typeParams: KSerializer<*>
        ): CompositeDecoder {
            val curObj = currentTagOrNull?.let { getByTag(it) } ?: obj
            @Suppress("UNCHECKED_CAST")
            return when (desc.kind) {
                StructureKind.LIST -> {
                    if (curObj is List<*>) {
                        MapperListInput(curObj as List<Any?>)
                    } else {
                        error("Expected list for tag $currentTagOrNull, got $curObj")
                    }

                }
                StructureKind.MAP, StructureKind.CLASS -> MapperMapInput(curObj as Map<String, Any?>)
                else -> error("Unsupported strcture kind ${desc.kind}")
            }
        }
    }

    private inner class MapperMapInput(val map: Map<String, Any?>) : Mapperinput(map) {
        private var pos = 0

        override fun decodeElementIndex(desc: SerialDescriptor): Int {
            while (pos < desc.elementsCount) {
                val name = desc.getTag(pos++)
                if (map.containsKey(name)) return pos - 1
            }
            return CompositeDecoder.READ_DONE
        }

        override fun decodeTaggedValue(tag: String): Any {
            val o = getByTag(tag) ?: throw MissingFieldException(tag)
            return o
        }

        override fun getByTag(tag: String): Any? = map[tag]

    }

    private inner class MapperListInput(val list: List<Any?>) : Mapperinput(list) {
        private val size = list.size
        private var pos = -1

        override fun elementName(desc: SerialDescriptor, index: Int): String = (index).toString()

        override fun decodeElementIndex(desc: SerialDescriptor): Int {
            while (pos < size - 1) {
                val o = list[++pos]
                if (o != null) return pos
            }
            return CompositeDecoder.READ_DONE
        }

        override fun getByTag(tag: String): Any? {
            return list[tag.toInt()]
        }
    }

    private inner class MapperMapOutput(
        val andAfter: (Map<String, Any?>) -> Unit = { Unit }
    ) : NamedValueEncoder() {
        override val context: SerialModule
            get() = this@NestedMapper.context

        private val map = mutableMapOf<String, Any?>()

        override fun composeName(parentName: String, childName: String): String = childName

        override fun encodeTaggedValue(tag: String, value: Any) {
            map[tag] = value
        }

        override fun encodeTaggedNull(tag: String) {
            map[tag] = null
        }

        override fun <T : Any?> encodeSerializableValue(
            serializer: SerializationStrategy<T>,
            value: T
        ) {
            if (serializer == ByteArraySerializer) {
                if (value == null) {
                    if (serializer.descriptor.isNullable) {
                        encodeTaggedNull(currentTag)
                    } else
                        throw SerializationException(
                            "null is not allowed by the descriptor ${serializer.descriptor}"
                        )
                } else {
                    encodeTaggedValue(currentTag, value)
                }
            } else {
                super.encodeSerializableValue(serializer, value)
            }
        }

        override fun beginStructure(
            desc: SerialDescriptor,
            vararg typeParams: KSerializer<*>
        ): CompositeEncoder {
            @Suppress("UNCHECKED_CAST")
            return when (desc.kind) {
                StructureKind.LIST -> MapperListOutput { map[currentTag] = it }
                StructureKind.MAP, StructureKind.CLASS ->
                    if (currentTagOrNull == null) this else MapperMapOutput { map[currentTag] = it }
                else -> error("Unsupported strcture kind ${desc.kind}")
            }
        }

        override fun endEncode(desc: SerialDescriptor) {
            andAfter(map)
        }
    }

    private inner class MapperListOutput(val andAfter: (List<Any?>) -> Unit) : TaggedEncoder<Int>() {

        val items = mutableListOf<Any?>()

        override fun beginStructure(
            desc: SerialDescriptor,
            vararg typeParams: KSerializer<*>
        ): CompositeEncoder {
            @Suppress("UNCHECKED_CAST")
            return when (desc.kind) {
                StructureKind.LIST -> MapperListOutput {
                    items.add(currentTag, it)
                }
                StructureKind.MAP, StructureKind.CLASS ->
                    MapperMapOutput {
                        items.add(currentTag, it)
                    }
                else -> error("Unsupported strcture kind ${desc.kind}")
            }
        }

        override val context: SerialModule
            get() = this@NestedMapper.context

        override fun encodeTaggedValue(tag: Int, value: Any) {
            items.add(tag, value)
        }

        override fun endEncode(desc: SerialDescriptor) {
            andAfter(items)
        }

        override fun SerialDescriptor.getTag(index: Int): Int = index
    }
}