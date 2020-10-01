import com.charlag.tuta.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CryptorTest {

    var cryptor = Cryptor()

    @BeforeTest
    fun before() {

    }

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

    @Test
    fun aes128() = runTest {
        val json = Json { }
        val jsonData = json.parseToJsonElement(aes128Tests)
        val aesTests = jsonData.jsonArray
        for (aesTest in aesTests) {
            val aesTestCase = json.decodeFromJsonElement(Aes128TestData.serializer(), aesTest)
            try {
                runAes128Test(aesTestCase)
            } catch (e: Exception) {
                println("Failed on $aesTest")
                throw e
            }
        }
    }

    @Test
    fun bcrypt128() = runTest {
        val json = Json { }
        val jsonData = json.parseToJsonElement(bcrypt128Tests)
        val bcryptTests = jsonData.jsonArray
        for (aesTest in bcryptTests) {
            val testCase = json.decodeFromJsonElement(Bcrypt128TestData.serializer(), aesTest)
            try {
                runBcrypt128Test(testCase)
            } catch (e: Throwable) {
                println("Failed on $aesTest")
                throw e
            }
        }
    }

    @Test
    fun sha256() = runTest {
        assertArrayEquals(
            hexToBytes("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"),
            cryptor.sha256hash(byteArrayOf())
        )
        assertArrayEquals(
            hexToBytes("ef537f25c895bfa782526529a9b63d97aa631564d5d789c2b765448c8635fb6c"),
            cryptor.sha256hash("The quick brown fox jumps over the lazy dog.".toBytes())
        )
    }

    private suspend fun runAes128Test(data: Aes128TestData) {
        val key = hexToBytes(data.hexKey)
        val plaintextBytes = base64ToBytes(data.plainTextBase64)
        val encryptedBytes = cryptor.aesEncrypt(
            plaintextBytes,
            base64ToBytes(data.ivBase64),
            key,
            usePadding = true,
            useMac = false
        )
        assertEquals(data.cipherTextBase64, encryptedBytes.toBase64())
    }

    private suspend fun runBcrypt128Test(data: Bcrypt128TestData) {
        val key = cryptor.generateKeyFromPassphrase(data.password, hexToBytes(data.saltHex))
        assertEquals(data.keyHex, bytesToHex(key))
    }
}

@Serializable
private data class Aes128TestData(
    val plainTextBase64: String,
    val ivBase64: String,
    val cipherTextBase64: String,
    val hexKey: String,
    val keyToEncrypt256: String,
    val keyToEncrypt128: String,
    val encryptedKey256: String,
    val encryptedKey128: String,
)

@Serializable
private data class Bcrypt128TestData(
    val password: String,
    val keyHex: String,
    val saltHex: String,
)