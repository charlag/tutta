package com.charlag.tuta

import net.jpountz.lz4.LZ4Compressor
import net.jpountz.lz4.LZ4Factory
import net.jpountz.lz4.LZ4SafeDecompressor

actual class Compressor {
    private val compressor: LZ4Compressor
    private val decompressor: LZ4SafeDecompressor

    init {
        // TODO: check if we can use fast instances for all of this
        val lz4 = LZ4Factory.safeInstance()
        compressor = lz4.fastCompressor()
        decompressor = lz4.safeDecompressor()
    }

    actual fun compressString(string: String): String {
        return String(compressor.compress(string.toBytes()))
    }

    actual fun decompressString(bytes: ByteArray): String {
        return if (bytes.isEmpty()) {
            ""
        } else {
            String(decompressor.decompress(bytes, mb20InBytes))
        }
    }

    companion object {
        private const val mb20InBytes = 20 * 1024 * 1024
    }
}