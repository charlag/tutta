package com.charlag.tuta.openssl

import kotlinx.cinterop.CPointer
import com.charlag.tuta.PrivateKey
import com.charlag.tuta.PublicKey
import com.charlag.tuta.base64ToBytes
import kotlinx.cinterop.*
import org.openssl.*
import platform.posix.stdout

// That's like the only secure RSA padding. Don't use anything else.
private const val RSA_PADDING = RSA_PKCS1_OAEP_PADDING

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
    val pkey = EVP_PKEY_new()
    EVP_PKEY_set1_RSA(pkey, key).checkOne { "pkey set rsa" }
    val ctx = EVP_PKEY_CTX_new(pkey, null)
    if (EVP_PKEY_encrypt_init(ctx) <= 0) {
        error("EVP_PKEY_encrypt_init")
    }
    setRSAPadding(ctx)

    memScoped {
        val outlen = alloc<ULongVar>()
        outlen.value = rsaEncrypted.size.toULong()

        if (EVP_PKEY_encrypt(
                ctx,
                rsaEncrypted.refTo(0),
                outlen.ptr,
                plaintext.toUByteArray().toCValues(),
                plaintext.size.toULong()
            ) <= 0
        ) {
            ERR_print_errors_fp(stdout)
            error("Could not do encryption")
        }
    }
    return rsaEncrypted.asByteArray()
}

/**
 * The version of the padding we want is RSA_PKCS1_OAEP with SHA256 for both digest and mgf1 digest.
 * These are two independent operations and openssl defaults to SHA1 for OAEP digest.
 * For most Java providers is corresponds to RSA/ECB/OAEPWithSHA-256AndMGF1Padding which usually
 * defaults to SHA256 for mgf1 but some (like Android keychain) do not and require explicit
 * OAEP parameter spec.
 */
private fun setRSAPadding(ctx: CPointer<EVP_PKEY_CTX>?) {
    if (evp_PKEY_CTX_set_rsa_padding(ctx, RSA_PKCS1_OAEP_PADDING) <= 0) {
        error("Could not set PKCS1 padding")
    }
    if (evp_PKEY_CTX_set_rsa_oaep_md(ctx, EVP_sha256()) <= 0) {
        error("Could not set oaep padding")
    }
    // mgf1 should take digest anyway but do it to be sure
    if (evp_PKEY_CTX_set_rsa_mgf1_md(ctx, EVP_sha256()) <= 0) {
        error("Could not set mgf padding")
    }
}

fun rsaDecrypt(ciphertext: ByteArray, key: CPointer<RSA>): ByteArray {
    val rsaDecryptedBuffer = UByteArray(RSA_size(key))

    val pkey = EVP_PKEY_new()
    EVP_PKEY_set1_RSA(pkey, key).checkOne { "pkey set rsa" }
    val ctx = EVP_PKEY_CTX_new(pkey, null)
    if (EVP_PKEY_decrypt_init(ctx) <= 0) {
        error("EVP_PKEY_decrypt_init")
    }
    setRSAPadding(ctx)

    memScoped {
        val outlen = alloc<ULongVar>()

        /* Determine buffer length */
        if (EVP_PKEY_decrypt(
                ctx,
                null,
                outlen.ptr,
                ciphertext.toUByteArray().toCValues(),
                ciphertext.size.toULong()
            ) <= 0
        ) {
            ERR_print_errors_fp(stdout)
            error("Could not determine buffer size for decryption")
        }
        println("buffer size is  ${rsaDecryptedBuffer.size} and it should be ${outlen.value}")

        outlen.value = rsaDecryptedBuffer.size.toULong()

        val outPtr = rsaDecryptedBuffer.refTo(0)
        val outlenPtr = outlen.ptr
        val cipthertextBuffer = ciphertext.toUByteArray().toCValues()
        val ciphertextLength = ciphertext.size.toULong()
        val decResult = EVP_PKEY_decrypt(
            ctx,
            outPtr,
            outlenPtr,
            cipthertextBuffer,
            ciphertextLength
        )
        if (decResult <= 0) {
            ERR_print_errors_fp(stdout)
            error("Could not do decryption")
        }
        println("after decryption outLen in ${outlen.value}")
        return rsaDecryptedBuffer.asByteArray().copyOfRange(0, outlen.value.toInt())
    }
}

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

fun arrayToPublicKey(keyArray: List<String>): PublicKey {
    return PublicKey(
        version = 0,
        modulus = hexToBnBinary(keyArray[0])
    )
}

fun hexToBnBinary(hex: String): ByteArray {
    val bn = BN_new()!!
    try {
        memScoped {
            val bnPointer = alloc<CPointerVar<BIGNUM>> { value = bn }
            if (BN_hex2bn(bnPointer.ptr, hex) == 0) {
                error("Failed hex2bn $hex")
            }

            val output = ByteArray(bn.byteNum)
            BN_bn2bin(bn, output.asUByteArray().refTo(0))

            return output
        }
    } finally {
        BN_free(bn)
    }
}

fun base64toBn(base64: String): CPointer<BIGNUM> {
    val bn = BN_new()!!
    val data = base64ToBytes(base64).toUByteArray()
    BN_bin2bn(data.refTo(0), data.size, bn)
    return bn
}

fun PublicKey.toOpenssl(): CPointer<RSA> {

    val rsaKey = RSA_new()!!

    val publicExponent = BN_new()
    memScoped {
        val publicExponentPointer = alloc<CPointerVar<BIGNUM>> { value = publicExponent }
        BN_dec2bn(publicExponentPointer.ptr, "65537")
    }
    val modulus = BN_bin2bn(this.modulus.toUByteArray().toCValues(), this.modulus.size, null)
    RSA_set0_key(rsaKey, modulus, publicExponent, null)
    return rsaKey
}

fun PrivateKey.toOpenssl(): CPointer<RSA> {
    val rsaKey = RSA_new()!!

    val publicExponent = BN_new()
    memScoped {
        val publicExponentPointer = alloc<CPointerVar<BIGNUM>> { value = publicExponent }
        BN_dec2bn(publicExponentPointer.ptr, "65537")
    }
    fun paramToBn(param: ByteArray): CPointer<BIGNUM> {
        return BN_bin2bn(param.asUByteArray().toCValues(), param.size, null)!!
    }

    val modulus = paramToBn(this.modulus)
    val privateExponent = paramToBn(this.privateExponent)

    RSA_set0_key(rsaKey, modulus, publicExponent, privateExponent).checkOne { "set0_key" }
    RSA_set0_factors(
        rsaKey,
        paramToBn(this.primeP),
        paramToBn(this.primeQ)
    ).checkOne { "RSA_set0_factors" }
    RSA_set0_crt_params(
        rsaKey,
        paramToBn(this.primeExponentP),
        paramToBn(this.primeExponentQ),
        paramToBn(this.crtCoefficient)
    ).checkOne { "RSA_set0_crt_params" }
    return rsaKey
}

fun PrivateKey.toPublicOpenssl(): CPointer<RSA> {

    val rsaKey = RSA_new()!!

    val publicExponent = BN_new()
    memScoped {
        val publicExponentPointer = alloc<CPointerVar<BIGNUM>> { value = publicExponent }
        BN_dec2bn(publicExponentPointer.ptr, "65537")
    }
    val modulus = BN_bin2bn(this.modulus.toUByteArray().toCValues(), this.modulus.size, null)
    RSA_set0_key(rsaKey, modulus, publicExponent, null)
    return rsaKey
}

val CValuesRef<BIGNUM>.byteNum: Int
    // define BN_num_bytes(a) ((BN_num_bits(a)+7)/8)
    get() = (BN_num_bits(this) + 7) / 8