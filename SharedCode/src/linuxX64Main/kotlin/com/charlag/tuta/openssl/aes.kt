package com.charlag.tuta.openssl

import kotlinx.cinterop.*
import org.openssl.*

object AES {
    fun encrypt(
        plaintext: ByteArray,
        key: ByteArray,
        iv: ByteArray,
        usePadding: Boolean
    ): ByteArray {
        val ctx = EVP_CIPHER_CTX_new() ?: error("failed to init evp context")

        try {
            var len = 0

            val keyBytes = key.asUByteArray()
            val ivBytes = iv.asUByteArray()
            // Plaintext + padding
            val ciphertextBytes = UByteArray(plaintext.size + 16)
            val plaintextBytes = plaintext.toUByteArray()

            /*
             * Initialise the encryption operation. IMPORTANT - ensure you use a key
             * and IV size appropriate for your cipher
             * In this example we are using 256 bit com.charlag.tuta.openssl.AES (i.e. a 256 bit key). The
             * IV size for *most* modes is the same as the block size. For com.charlag.tuta.openssl.AES this
             * is 128 bits
             */
            if (1 != EVP_EncryptInit_ex(
                    ctx,
                    EVP_aes_128_cbc(),
                    null,
                    keyBytes.refTo(0),
                    ivBytes.refTo(0)
                )
            ) {
                error("Failed to init encrypt")
            }
            if (!usePadding) EVP_CIPHER_CTX_set_padding(ctx, 0)


            memScoped {
                val ciphertextLenVal = alloc<IntVar>()

                /*
                 * Provide the message to be encrypted, and obtain the encrypted output.
                 *
                 * It is important to use cValues instead of refTo(0) because refTo(0) does length
                 * checks and fails if array is empty. It makes a copy but doesn't seem like
                 * there's a way around it at the moment.
                 *
                 * For ciphertext we must a pointer and not a copy, otherwise nothing is written.
                 */
                if (1 != EVP_EncryptUpdate(
                        ctx,
                        ciphertextBytes.refTo(0),
                        ciphertextLenVal.ptr,
                        plaintextBytes.toCValues(),
                        plaintextBytes.size
                    )
                ) {
                    error("Failed to update encrypt")
                }
                len += ciphertextLenVal.value

                if (1 != EVP_EncryptFinal_ex(
                        ctx,
                        ciphertextBytes.refTo(len),
                        ciphertextLenVal.ptr
                    )
                ) {
                    error("Failed to final encrypt")
                }

                len += ciphertextLenVal.value

                return ciphertextBytes.asByteArray().copyOfRange(0, len)
            }
        } finally {
            EVP_CIPHER_CTX_free(ctx)
        }
    }


    fun decrypt(
        ciphertext: ByteArray,
        key: ByteArray,
        iv: ByteArray,
        usePadding: Boolean
    ): ByteArray {
        val ctx = EVP_CIPHER_CTX_new() ?: error("Error init context in decrypt")
        /*
         * Initialise the decryption operation. IMPORTANT - ensure you use a key
         * and IV size appropriate for your cipher
         * In this example we are using 256 bit com.charlag.tuta.openssl.AES (i.e. a 256 bit key). The
         * IV size for *most* modes is the same as the block size. For AES this is 128 bits
         */
        if (1 != EVP_DecryptInit_ex(
                ctx,
                EVP_aes_128_cbc(),
                null,
                key.asUByteArray().refTo(0),
                iv.asUByteArray().refTo(0)
            )
        ) error("Error init decrypt")
        if (!usePadding) EVP_CIPHER_CTX_set_padding(ctx, 0)

        try {
            // AES does not change the size but it might be padded to make a full 16 byte block. If
            // it's exact multiplier it will get a full padding block.
            // It is very important to have big enough buffer here as OpenSSL will just silently
            // corrupt our memory otherwise
            val plaintextGuessedLength = ciphertext.size + 16
            val plaintext = UByteArray(plaintextGuessedLength)
            val plaintextPin = plaintext.pin()
            var plaintextActualLen = 0

            val ciphtertextPin = ciphertext.pin()
            memScoped {
                val outLenVal = alloc<IntVar>()
                val lenPin = outLenVal.pin()

                /*
                 * Provide the message to be decrypted, and obtain the plaintext output.
                 * EVP_DecryptUpdate can be called multiple times if necessary.
                 */
                val ciphertextU = ciphertext.asUByteArray()
                if (1 != EVP_DecryptUpdate(
                        ctx,
                        plaintext.refTo(0),
                        outLenVal.ptr,
                        ciphertextU.refTo(0),
                        ciphertextU.size
                    )
                ) {
                    error("Error during decryptUpdate")
                }

                plaintextActualLen = outLenVal.value
                /*
                 * Finalise the decryption. Further plaintext bytes may be written at
                 * this stage.
                 */
                if (1 != EVP_DecryptFinal_ex(
                        ctx,
                        plaintext.refTo(outLenVal.value),
                        outLenVal.ptr
                    )
                ) {
                    error("Error during final")
                }
                plaintextActualLen += outLenVal.value;
                lenPin.unpin()
            }
            plaintextPin.unpin()
            ciphtertextPin.unpin()
            return plaintext.toByteArray().copyOfRange(0, plaintextActualLen)
        } finally {
            EVP_CIPHER_CTX_free(ctx)
        }
    }

    const val AES_KEY_LENGTH = 128
    const val AES_KEY_LENGTH_BYTES = AES_KEY_LENGTH / 8
}


inline fun Int.checkOne(message: () -> String): Int {
    if (this != 1) error(message())
    return this
}