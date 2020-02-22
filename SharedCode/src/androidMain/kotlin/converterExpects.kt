package com.charlag.tuta

import java.math.BigInteger
import java.util.ArrayList

actual fun bytesToString(bytes: ByteArray): String = String(bytes)
actual fun String.toBytes(): ByteArray = this.toByteArray()

fun hexToPrivateKey(hex: String): PrivateKey {
    return arrayToPrivateKey(hexToKeyArray(hex))
}

actual fun hexToPublicKey(hex: String): PublicKey {
    return arrayToPublicKey(hexToKeyArray(hex))
}

fun hexToKeyArray(hex: String): Array<BigInteger> {
    val key: ArrayList<BigInteger> = ArrayList()
    var pos = 0
    while (pos < hex.length) {
        val nextParamLen = hex.substring(pos, pos + 4).toInt(16)
        pos += 4
        key.add(BigInteger(hex.substring(pos, pos + nextParamLen), 16))
        pos += nextParamLen
    }
    return key.toArray(arrayOf<BigInteger>())
}

fun arrayToPrivateKey(keyArray: Array<BigInteger>): PrivateKey {
    return PrivateKey(
        version = 0,
        modulus = keyArray[0].toByteArray(),
        privateExponent = keyArray[1].toByteArray(),
        primeP = keyArray[2].toByteArray(),
        primeQ = keyArray[3].toByteArray(),
        primeExponentP = keyArray[4].toByteArray(),
        primeExponentQ = keyArray[5].toByteArray(),
        crtCoefficient = keyArray[6].toByteArray()
    )
}

fun arrayToPublicKey(keyArray: Array<BigInteger>): PublicKey {
    return PublicKey(
        version = 0,
        modulus = keyArray[0].toByteArray()
    )
}