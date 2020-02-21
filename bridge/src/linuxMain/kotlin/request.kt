import io.ktor.utils.io.core.toByteArray
import kotlinx.cinterop.*
import org.openssl.*

// That's like the only secure RSA padding. Don't use anything else.
const val RSA_PADDING = RSA_PKCS1_OAEP_PADDING

fun main() {
    /*
     * Set up the key and iv. Do I need to say to not hard code these in a
     * real application? :-)
     */

    /* A 256 bit key */
    val aesKey = "01234567890123456789012345678901".toByteArray()

    /* A 128 bit IV */
    val iv = "0123456789012345".toByteArray()

    /* Message to be encrypted */
    val plaintext = "The quick brown fox jumps over the lazy dog"

    val ciphertext = encrypt(plaintext.toByteArray(), aesKey, iv)

    val decrypted = decrypt(ciphertext, aesKey, iv).decodeToString()
    assert(plaintext == decrypted)
    println("✅ AES Matches!")

    val rsaKey = generateRsaKey()
    val rsaEncrypted = rsaEncrypt(aesKey, rsaKey)
    println("Encrypted using RSA")
    val rsaDecrypted = rsaDecrypt(rsaEncrypted, rsaKey)

//    BN_free(e)
    RSA_free(rsaKey)

    assert(rsaDecrypted.contentEquals(aesKey))
    println("✅ RSA Matches!")

}

fun encrypt(
    plaintext: ByteArray,
    key: ByteArray,
    iv: ByteArray
): ByteArray {
    val ctx = EVP_CIPHER_CTX_new() ?: error("failed to init evp context")
    try {
        var len = 0

        val keyBytes = key.asUByteArray()
        val ivBytes = iv.asUByteArray()
        val ciphertextBytes = UByteArray(128)
        val plaintextBytes = plaintext.toUByteArray()

        /*
         * Initialise the encryption operation. IMPORTANT - ensure you use a key
         * and IV size appropriate for your cipher
         * In this example we are using 256 bit AES (i.e. a 256 bit key). The
         * IV size for *most* modes is the same as the block size. For AES this
         * is 128 bits
         */
        if (1 != EVP_EncryptInit_ex(
                ctx,
                EVP_aes_256_cbc(),
                null,
                keyBytes.refTo(0),
                ivBytes.refTo(0)
            )
        ) {
            error("Failed to init encrypt")
        }


        memScoped {
            val ciphertextLenVal = alloc<IntVar>()

            /*
             * Provide the message to be encrypted, and obtain the encrypted output.
             * EVP_EncryptUpdate can be called multiple times if necessary
             */
            if (1 != EVP_EncryptUpdate(
                    ctx,
                    ciphertextBytes.refTo(0),
                    ciphertextLenVal.ptr,
                    plaintextBytes.refTo(0),
                    plaintextBytes.size
                )
            ) {
                error("Failed to update encrypt")
            }
            len += ciphertextLenVal.value

            if (1 != EVP_EncryptFinal_ex(ctx, ciphertextBytes.refTo(len), ciphertextLenVal.ptr)) {
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
    ciphertext: ByteArray, key: ByteArray,
    iv: ByteArray
): ByteArray {
    val ctx = EVP_CIPHER_CTX_new() ?: error("Error init context in decrypt")

    /*
     * Initialise the decryption operation. IMPORTANT - ensure you use a key
     * and IV size appropriate for your cipher
     * In this example we are using 256 bit AES (i.e. a 256 bit key). The
     * IV size for *most* modes is the same as the block size. For AES this
     * is 128 bits
     */
    if (1 != EVP_DecryptInit_ex(
            ctx,
            EVP_aes_256_cbc(),
            null,
            key.asUByteArray().refTo(0),
            iv.asUByteArray().refTo(0)
        )
    ) error("Error init decrypt")

    try {
        val plaintext = UByteArray(128)
        var plaintext_len = 0

        memScoped {
            val outLenVal = alloc<IntVar>()

            /*
             * Provide the message to be decrypted, and obtain the plaintext output.
             * EVP_DecryptUpdate can be called multiple times if necessary.
             */
            if (1 != EVP_DecryptUpdate(
                    ctx,
                    plaintext.refTo(0),
                    outLenVal.ptr,
                    ciphertext.asUByteArray().refTo(0),
                    ciphertext.size
                )
            ) error("Error during decryptUpdate")

            plaintext_len = outLenVal.value

            /*
             * Finalise the decryption. Further plaintext bytes may be written at
             * this stage.
             */
            if (1 != EVP_DecryptFinal_ex(ctx, plaintext.refTo(outLenVal.value), outLenVal.ptr))
                error("Error during final")
            plaintext_len += outLenVal.value;
        }
        return plaintext.toByteArray().copyOfRange(0, plaintext_len)
    } finally {
        EVP_CIPHER_CTX_free(ctx)
    }
}

inline fun Int.checkOne(message: () -> String) {
    if (this != 1) error(message())
}