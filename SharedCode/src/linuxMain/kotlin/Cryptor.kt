package com.charlag.tuta

actual class Cryptor {
    actual suspend fun encrypt(
        value: ByteArray,
        iv: ByteArray,
        key: ByteArray,
        usePadding: Boolean,
        useMac: Boolean
    ): ByteArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual suspend fun decrypt(
        value: ByteArray,
        key: ByteArray,
        usePadding: Boolean
    ): DecryptResult {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual suspend fun decryptRsaKey(
        value: ByteArray,
        key: ByteArray
    ): PrivateKey {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual suspend fun rsaEncrypt(
        value: ByteArray,
        publicKey: PublicKey
    ): ByteArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual suspend fun rsaDecrypt(
        value: ByteArray,
        privateKey: PrivateKey
    ): ByteArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun generateRandomData(byteSize: Int): ByteArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual suspend fun hash(bytes: ByteArray): ByteArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun bcrypt(
        rounds: Int,
        passphrase: ByteArray,
        salt: ByteArray
    ): ByteArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}