package com.charlag.tuta

import kotlin.math.max

actual class Compressor {
    actual fun compressString(string: String): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    actual fun decompressString(bytes: ByteArray): String {
        if (bytes.isEmpty()) return ""
        val output = LZ4.uncompress(bytes.toUByteArray())
        return bytesToString(output)
    }
}

object LZ4 {
    /**
     * Decode a block. Assumptions: input contains all sequences of a chunk.
     * @param input {Buffer} input data
     * @throws on invalid offset
     * @return {Uint8Array} decoded data
     */
    @OptIn(ExperimentalUnsignedTypes::class)
    fun uncompress(input: UByteArray): ByteArray {
        val endIndex = input.size
        var output = UByteArray(input.size * 6)
        var j = 0
        // Process each sequence in the incoming data
        var i = 0
        val n = endIndex
        while (i < n) {
            val token = input[i++]

            // Literals
            var literals_length = (token.toInt() shr 4)
            if (literals_length > 0) {
                // length of literals
                var l = literals_length + 240
                while (l == 255) {
                    l = input[i++].toInt()
                    literals_length += l
                }

                // Copy the literals
                val end = i + literals_length
                val sizeNeeded = j + (end - i)
                if (output.size < sizeNeeded) {
                    val newSize = max(output.size * 2, sizeNeeded)
                    val newOutput = UByteArray(newSize)
                    output.copyInto(newOutput)
                    output = newOutput
                }
                while (i < end) output[j++] = input[i++]

                // End of buffer?
                if (i == n) break // return j
            }

            // Match copy
            // 2 bytes offset (little endian)
            val offset = input[i++].toInt() or (input[i++].toInt() shl 8)

            // 0 is an invalid offset value
            if (offset == 0 || offset > j) {
                // was:
                // return -(i - 2)
                throw Error("Invalid offset value. i: ${i}, -(i-2): ${-(i - 2)}")
            }

            // length of match copy
            var match_length = (token.toInt() and 0xf)
            var l = match_length + 240
            while (l == 255) {
                l = input[i++].toInt()
                match_length += l
            }

            // Copy the match
            var pos = j - offset // position of the match copy in the current output
            val end = j + match_length + 4 // minmatch = 4
            val sizeNeeded = end
            if (output.size < sizeNeeded) {
                val newSize = max (output.size * 2, sizeNeeded)
                val newOutput = UByteArray(newSize)
                output.copyInto(newOutput)
                output = newOutput
            }
            while (j < end) output[j++] = output[pos++]
        }
        return output.copyOfRange(0, j).toByteArray()
    }
}