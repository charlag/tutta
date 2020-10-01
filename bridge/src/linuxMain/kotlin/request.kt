import com.charlag.tuta.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.openssl.*
import kotlin.test.assertEquals

// That's like the only secure RSA padding. Don't use anything else.
const val RSA_PADDING = RSA_PKCS1_OAEP_PADDING

fun main() {
    /*
     * Set up the key and iv. Do I need to say to not hard code these in a
     * real application? :-)
     */

//    /* A 256 bit key */
//    val aesKey = "01234567890123456789012345678901".toByteArray()

    val cryptor = Cryptor()
//
//    // 16 * 8 = 128 bit key because cryptor doesn't like other key sizes
//    val aesKey = cryptor.generateRandomData(16)
//
//    /* A 128 bit IV */
//    val iv = "0123456789012345".toByteArray()
//
//    /* Message to be encrypted */
//    val plaintext = "The quick brown fox jumps over the lazy dog"
//
//    val ciphertext = encrypt(plaintext.toByteArray(), aesKey, iv)
//
//    val decrypted = decrypt(ciphertext, aesKey, iv).decodeToString()
//    assert(plaintext == decrypted)
//    println("✅ com.charlag.tuta.openssl.AES Matches!")
//
//    runBlocking {
//        val decryptedByCryptor = cryptor.aesDecrypt(iv + ciphertext, aesKey, true).data.decodeToString()
//        println("Decrypted with cryptor: $decryptedByCryptor")
//        assert(decrypted == decryptedByCryptor)
//    }
//
//    val rsaKey = generateRsaKey()
//    val rsaEncrypted = rsaEncrypt(aesKey, rsaKey)
//    println("Encrypted using RSA")
//    val rsaDecrypted = rsaDecrypt(rsaEncrypted, rsaKey)
//
////    BN_free(e)
//    RSA_free(rsaKey)
//
//    assert(rsaDecrypted.contentEquals(aesKey))
//    println("✅ RSA Matches!")
//
//    val bytes = byteArrayOf(1, 2, 3, 4, 5, 6)
//    val encodedAsB64 = bytes.toBase64()
//    println(encodedAsB64)
//    val decodedBytes = base64ToBytes(encodedAsB64)
//    println("Decoded: ${decodedBytes.joinToString()}, ${decodedBytes.contentEquals(bytes)}")
//
}