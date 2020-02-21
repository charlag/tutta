import com.charlag.tuta.PrivateKey
import kotlinx.cinterop.*
import org.openssl.*

fun hexToKeyArray(hex: String): List<String> {
    val key = mutableListOf<String>()
    var pos = 0
    while (pos < hex.length) {
        val nextParamLen = hex.substring(pos, pos + 4).toInt(16)
        pos += 4
        key.add(hex.substring(pos, pos + nextParamLen))
        pos += nextParamLen
    }
    return key
}

fun arrayToPrivateKey(keyArray: List<String>): PrivateKey {
    return PrivateKey(
        version = 0,
        modulus = hexToBnBinary(keyArray[0]),
        privateExponent = hexToBnBinary(keyArray[1]),
        primeP = hexToBnBinary(keyArray[2]),
        primeQ = hexToBnBinary(keyArray[3]),
        primeExponentP = hexToBnBinary(keyArray[4]),
        primeExponentQ = hexToBnBinary(keyArray[5]),
        crtCoefficient = hexToBnBinary(keyArray[6])
    )
}

fun hexToBnBinary(hex: String): ByteArray {
    val bn = BN_new()!!
    try {
        memScoped {
            val bnPointer = alloc<CPointerVar<BIGNUM>> { value = bn }
            BN_hex2bn(bnPointer.ptr, hex).checkIs(0) { "Failed hex2bn $it" }

            val output = ByteArray(bn.byteNum)
            BN_bn2bin(bn, output.asUByteArray().refTo(0))

            return output
        }
    } finally {
        BN_free(bn)
    }
}

fun hexToPrivateKey(hex: String): PrivateKey {
    return arrayToPrivateKey(hexToKeyArray(hex))
}

var CValuesRef<BIGNUM>.byteNum: Int
    // define BN_num_bytes(a) ((BN_num_bits(a)+7)/8)
    get() = (BN_num_bits(this) + 7) / 8

// mostly for testing now because we want to abstract it away
fun generateRsaKey(): CPointer<RSA> {
    val rsaKey = RSA_new() ?: error("Could create RSA key")
    // don't free e as rsa key somehow uses it internally
    val e = BN_new()
    // RSA_F4 is 65537, go figure
    BN_set_word(e, RSA_F4.toULong()).checkOne { "set_word" }

    RSA_generate_key_ex(rsaKey, 2048, e, null).checkOne { "generateKey" }

    return rsaKey
}


fun rsaEncrypt(plaintext: ByteArray, key: CPointer<RSA>): ByteArray {
    val rsaEncrypted = UByteArray(RSA_size(key))
    RSA_public_encrypt(
        plaintext.size,
        plaintext.asUByteArray().refTo(0),
        rsaEncrypted.refTo(0),
        key,
        RSA_PADDING
    ).also { if (it == -1) error("Error during encrypt") }
    return rsaEncrypted.asByteArray()
}

fun rsaDecrypt(ciphertext: ByteArray, key: CPointer<RSA>): ByteArray {
    // https://www.openssl.org/docs/man1.1.1/man3/RSA_private_decrypt.html
    // > to must point to a memory section large enough to hold the maximal possible decrypted data
    // > (which is equal to RSA_size(rsa) for RSA_NO_PADDING, RSA_size(rsa) - 11 for the PKCS #1
    // > v1.5 based padding modes and RSA_size(rsa) - 42 for RSA_PKCS1_OAEP_PADDING)
    val rsaDecryptedBuffer = UByteArray(RSA_size(key) - 42)
    val decryptedSize = RSA_private_decrypt(
        ciphertext.size,
        ciphertext.asUByteArray().refTo(0),
        rsaDecryptedBuffer.refTo(0),
        key,
        RSA_PADDING
    ).also { if (it == -1) error("Error during decrypt") }
    return rsaDecryptedBuffer.copyOfRange(0, decryptedSize).toByteArray()
}

inline fun Int.checkIs(value: Int, message: (Int) -> String): Int {
    if (this != value) {
        error(message(this))
    }
    return this
}