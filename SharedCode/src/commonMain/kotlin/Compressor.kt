package com.charlag.tuta

expect class Compressor {
    fun compressString(string: String): String
    fun decompressString(bytes: ByteArray): String
}