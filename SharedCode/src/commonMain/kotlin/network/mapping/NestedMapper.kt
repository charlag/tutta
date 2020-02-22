package com.charlag.tuta.network.mapping

import com.charlag.tuta.entities.ByteArraySerializer
import kotlinx.serialization.*
import kotlinx.serialization.modules.EmptyModule
import kotlinx.serialization.modules.SerialModule

/**
 * This class maps is a "serial format" in a two-pass serialization mechanism. It serializes things
 * into maps and lists
 */
internal class NestedMapper(context: SerialModule = EmptyModule) : AbstractSerialFormat(context) {

    fun <T : Any?> map(serializer: SerializationStrategy<T>, value: T): Map<String, Any?> {
        lateinit var result: Map<String, Any?>
        return MapperStructureOutput {
            result = it
        }
            .encode(serializer, value)
            .let { result }
    }

    fun <T : Any?> unmap(deserializer: DeserializationStrategy<T>, map: Map<String, Any?>): T {
        return MapperStructureInput(map).decode(deserializer)
    }

    private abstract inner class Mapperinput(val obj: Any?) : NamedValueDecoder() {
        override val context: SerialModule
            get() = this@NestedMapper.context

        override fun composeName(parentName: String, childName: String): String = childName


        override fun decodeTaggedValue(tag: String): Any {
            return currentElement(tag) ?: throw MissingFieldException(tag)
        }

        override fun decodeTaggedNotNullMark(tag: String): Boolean {
            return currentElement(tag) != null
        }

        override fun <T> decodeSerializableValue(deserializer: DeserializationStrategy<T>): T {
            val tag = currentTagOrNull
            if (deserializer === ByteArraySerializer) {
                if (tag == null) error("value is null")
                @Suppress("UNCHECKED_CAST")
                return currentElement(tag) as? T ?: error("It's not bytearray")
            }
            return super.decodeSerializableValue(deserializer)
        }

        override fun beginStructure(
            desc: SerialDescriptor,
            vararg typeParams: KSerializer<*>
        ): CompositeDecoder {
            val curObj = currentTagOrNull?.let { currentElement(it) } ?: obj
            @Suppress("UNCHECKED_CAST")
            return when (desc.kind) {
                StructureKind.LIST -> {
                    if (curObj is List<*>) {
                        MapperListInput(curObj as List<Any?>)
                    } else {
                        error("Expected list for tag $currentTagOrNull, got $curObj")
                    }

                }
                StructureKind.CLASS -> MapperStructureInput(curObj as Map<String, Any?>)
                StructureKind.MAP -> MapperMapInput(curObj as Map<String, Any?>)
                else -> error("Unsupported strcture kind ${desc.kind}")
            }
        }

        protected abstract fun currentElement(tag: String): Any?
    }

    /**
     * Descriptor knows the keys and we just need to find the index.
     */
    private inner class MapperStructureInput(val map: Map<String, Any?>) : Mapperinput(map) {
        private var position = 0

        override fun decodeElementIndex(desc: SerialDescriptor): Int {
            while (position < desc.elementsCount) {
                val name = desc.getTag(position++)
                if (name in map) {
                    return position - 1
                }
            }
            return CompositeDecoder.READ_DONE
        }

        override fun decodeTaggedValue(tag: String): Any {
            return currentElement(tag) ?: throw MissingFieldException(tag)
        }

        override fun currentElement(tag: String): Any? {
            return map[tag]
        }
    }

    /**
     * Descriptor has no idea about the index and we just need to return all indices.
     */
    private inner class MapperMapInput(val map: Map<String, Any?>) : Mapperinput(map) {
        private val keys = map.keys.toList()
        private val size: Int = keys.size * 2
        private var position = -1

        override fun elementName(desc: SerialDescriptor, index: Int): String {
            val i = index / 2
            return keys[i]
        }

        override fun decodeElementIndex(desc: SerialDescriptor): Int {
            while (position < size - 1) {
                position++
                return position
            }
            return CompositeDecoder.READ_DONE
        }

        override fun currentElement(tag: String): Any? {
            return if (position.isEven) tag else map[tag]
        }
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

        override fun currentElement(tag: String): Any? {
            return list[tag.toInt()]
        }
    }

    /**
     * For the case when the keys are known in advance
     */
    private open inner class MapperStructureOutput(
        val andAfter: (Map<String, Any?>) -> Unit = { Unit }
    ) : NamedValueEncoder() {
        override val context: SerialModule
            get() = this@NestedMapper.context

        protected val map = mutableMapOf<String, Any?>()

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
                StructureKind.MAP -> MapperMapOutput { map[currentTag] = it }
                StructureKind.CLASS ->
                    if (currentTagOrNull == null) this else MapperStructureOutput {
                        map[currentTag] = it
                    }
                else -> error("Unsupported strcture kind ${desc.kind}")
            }
        }

        override fun endEncode(desc: SerialDescriptor) {
            andAfter(map)
        }
    }

    /**
     * For serializing unknown (to us) keys. Serializer must provide them first.
     */
    private inner class MapperMapOutput(andAfter: (Map<String, Any?>) -> Unit) :
        MapperStructureOutput(andAfter) {
        private var currentMapKey: String? = null

        override fun encodeTaggedNull(tag: String) {
            if (isTagKey(tag)) {
                throw IllegalStateException("Trying to write null as key $tag")
            } else {
                map[getMapKey()] = null
            }
        }

        override fun encodeTaggedValue(tag: String, value: Any) {
            if (isTagKey(tag)) {
                this.currentMapKey = value as String
            } else {
                map[getMapKey()] = value
            }
        }

        private fun getMapKey(): String = currentMapKey
            ?: throw IllegalStateException("Key was not encoded first")

        private fun isTagKey(tag: String) = tag.toInt().isEven
    }

    private inner class MapperListOutput(val andAfter: (List<Any?>) -> Unit) :
        TaggedEncoder<Int>() {

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
                    MapperStructureOutput {
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


    val Int.isEven
        get() = this % 2 == 0
}