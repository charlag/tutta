package com.charlag.tuta

import android.util.Base64
import com.charlag.tuta.entities.sys.typeInfos
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.json.JsonSerializer
import io.ktor.client.features.json.serializer.KotlinxSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.math.BigInteger
import java.util.*
import kotlin.reflect.KClass

actual fun platformName(): String {
    return "Android"
}

actual fun platformEngine(): HttpClientEngine {
    return OkHttp.create { }
}

actual fun base64ToBytes(base64: String): ByteArray =
    Base64.decode(base64, 0)

actual fun ByteArray.toBase64(): String =
    Base64.encodeToString(this, Base64.NO_WRAP)

@UnstableDefault
actual fun platformJsonSerializer(): JsonSerializer {
    return KotlinxSerializer(Json(JsonConfiguration.Default)).apply {
        typeInfos.forEach { (klass, _, _, serializer) ->
            @Suppress("UNCHECKED_CAST")
            setMapper(klass as KClass<Any>, serializer as KSerializer<Any>)
        }
    }
}

actual val KClass<*>.noReflectionName: String
    get() = this.java.simpleName

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

fun hexToBytes(s: String): ByteArray {
    val len = s.length
    val data = ByteArray(len / 2)
    var i = 0
    while (i < len) {
        data[i / 2] = ((Character.digit(s[i], 16) shl 4)
                + Character.digit(s[i + 1], 16)).toByte()
        i += 2
    }
    return data
}