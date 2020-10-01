package com.charlag.tuta

import kotlinx.cinterop.*
import kotlinx.serialization.Serializable
import org.openssl.*
import platform.posix.memcpy

//char *base64(const unsigned char *input, int length)
//{
//BIO *bmem, *b64;
//BUF_MEM *bptr;
//
//b64 = BIO_new(BIO_f_base64());
//bmem = BIO_new(BIO_s_mem());
//b64 = BIO_push(b64, bmem);
//BIO_write(b64, input, length);
//BIO_flush(b64);
//BIO_get_mem_ptr(b64, &bptr);
//
//char *buff = (char *)malloc(bptr->length);
//memcpy(buff, bptr->data, bptr->length-1);
//buff[bptr->length-1] = 0;
//
//BIO_free_all(b64);
//
//return buff;
//}
//
//char *unbase64(unsigned char *input, int length)
//{
//BIO *b64, *bmem;
//
//char *buffer = (char *)malloc(length);
//memset(buffer, 0, length);
//
//b64 = BIO_new(BIO_f_base64());
//bmem = BIO_new_mem_buf(input, length);
//bmem = BIO_push(b64, bmem);
//
//BIO_read(bmem, buffer, length);
//
//BIO_free_all(bmem);
//
//return buffer;
//}

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
    BIO_write(b64, this.asUByteArray().refTo(0), this.size)
    bio_flush(b64)
    memScoped {
        val ptr = alloc<CPointerVar<BUF_MEM>>()
        bio_get_mem_ptr(b64, ptr.ptr)

        val output = ByteArray(ptr.pointed!!.length.toInt())

        val bufMem = ptr.pointed!!
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
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}