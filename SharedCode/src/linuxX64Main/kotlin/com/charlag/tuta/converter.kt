package com.charlag.tuta

import com.charlag.tuta.openssl.arrayToPrivateKey
import com.charlag.tuta.openssl.arrayToPublicKey
import com.charlag.tuta.openssl.hexToKeyArray
import kotlinx.cinterop.*
import org.openssl.*
import platform.posix.memcpy

@OptIn(ExperimentalUnsignedTypes::class)
actual fun base64ToBytes(base64: String): ByteArray {
    if (base64 == "") return byteArrayOf()
    val b64 = BIO_new(BIO_f_base64())
    BIO_set_flags(b64, BIO_FLAGS_BASE64_NO_NL)
    val dataBuf = BIO_new_mem_buf(base64.toBytes().refTo(0), base64.length)
    val bmem = BIO_push(b64, dataBuf)

    val buffer = ByteArray(base64.length)
    memScoped {
        val read = alloc<ULongVar>()
        if (BIO_read_ex(bmem, buffer.refTo(0), buffer.size.toULong(), read.ptr) != 1) {
            error("Base 64 decode failed")
        }

        BIO_free_all(bmem)

        return buffer.copyOfRange(0, read.value.toInt())
    }
}

@OptIn(ExperimentalUnsignedTypes::class)
actual fun ByteArray.toBase64(): String {
    var b64 = BIO_new(BIO_f_base64())
    BIO_set_flags(b64, BIO_FLAGS_BASE64_NO_NL)
    val bmem = BIO_new(BIO_s_mem())
    b64 = BIO_push(b64, bmem)
    BIO_write(b64, this.toCValues(), this.size)
    bio_flush(b64)
    memScoped {
        val ptr = alloc<CPointerVar<BUF_MEM>>()
        bio_get_mem_ptr(b64, ptr.ptr)
        val bufMem = ptr.pointed!!
        if (bufMem.length == 0.toULong()) {
            return ""
        }

        val output = ByteArray(bufMem.length.toInt())


        memcpy(output.refTo(0), bufMem.data, bufMem.length)

        return output.decodeToString()
    }
}

actual fun bytesToString(bytes: ByteArray): String {
    return bytes.decodeToString()
}

actual fun String.toBytes(): ByteArray {
    return this.encodeToByteArray()
}


actual fun hexToPublicKey(hex: String): PublicKey {
    return arrayToPublicKey(hexToKeyArray(hex))
}

actual fun hexToPrivateKey(hex: String): PrivateKey {
    return arrayToPrivateKey(hexToKeyArray(hex))
}