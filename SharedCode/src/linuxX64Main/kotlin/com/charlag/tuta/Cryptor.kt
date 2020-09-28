package com.charlag.tuta

actual class Cryptor {
    /**
     * Symmetric encryption using AES. IV must be prepended.
     */
    actual suspend fun aesEncrypt(
        value: ByteArray,
        iv: ByteArray,
        key: ByteArray,
        usePadding: Boolean, // should default to true but it doesn't work
        useMac: Boolean // this should default to true too
    ): ByteArray {
        TODO()
    }

    /**
     * Symmetric decryption using AES.
     */
    actual suspend fun aesDecrypt(value: ByteArray, key: ByteArray, usePadding: Boolean): DecryptResult {
        TODO()
    }

    /**
     * Decrypt RSA with symmetric encryption
     */
    actual suspend fun decryptRsaKey(value: ByteArray, key: ByteArray): PrivateKey {
        TODO()
    }

    /**
     * Asymmetric encryption using RSA public key
     */
    actual suspend fun rsaEncrypt(value: ByteArray, publicKey: PublicKey): ByteArray {
        TODO()
    }

    /**
     * Asymmetric decryption using RSA private key
     */
    actual suspend fun rsaDecrypt(value: ByteArray, privateKey: PrivateKey): ByteArray {
        TODO()
    }

    actual fun generateRandomData(byteSize: Int): ByteArray {
        TODO()
    }
    actual suspend fun sha256hash(bytes: ByteArray): ByteArray {
        TODO()
    }
    /**
     * Raw BCrypt hash without null terminator, 24 byte out.
     */
    actual fun bcrypt(rounds: Int, passphrase: ByteArray, salt: ByteArray): ByteArray {
        TODO()
    }
}