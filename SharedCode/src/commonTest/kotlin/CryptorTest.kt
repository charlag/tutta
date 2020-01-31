import com.charlag.tuta.Cryptor
import com.charlag.tuta.decryptKey
import com.charlag.tuta.encryptKey
import kotlin.test.Test

class CryptorTest {

    var cryptor = Cryptor()

    @Test
    fun testRoundtrip() {
//        val value = "Test string!".toByteArray()
//        val key = ByteArray(16)
//        val iv = ByteArray(16) { 0 }
//        val cryptor = Cryptor()
//        val encrypted = cryptor.encrypt(value, iv, key)
//        val decrypted = cryptor.decrypt(encrypted, key)
//        println(stringFromUtf8Bytes(decrypted))
//        assertArrayEquals(value, decrypted)
    }

    @Test
    fun testKeyEncryptionRoundtrip() = runTest {
        val plaintextKey = cryptor.generateRandomData(16)
        val encryptionKey = cryptor.generateRandomData(16)
        val encryptedKey = cryptor.encryptKey(plaintextKey, encryptionKey)
        val decryptedKey = cryptor.decryptKey(encryptedKey, encryptionKey)
        assertArrayEquals(plaintextKey, decryptedKey)
    }
}