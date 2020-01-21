package com.charlag.tuta

import net.jpountz.lz4.LZ4Factory

actual class Compressor {
    actual fun compressString(string: String): String {
        return String(
            LZ4Factory.fastestJavaInstance().fastCompressor()
                .compress(string.toBytes())
        )
    }

    actual fun decompressString(bytes: ByteArray): String {
        val mb20InBytes = 20 * 1024 * 1024
        return String(
            LZ4Factory.safeInstance().safeDecompressor()
                .decompress(bytes, mb20InBytes)
        )
    }
}