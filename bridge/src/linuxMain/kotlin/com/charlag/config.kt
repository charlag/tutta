package com.charlag

import com.charlag.tuta.libsecret.SecretSchema
import com.charlag.tuta.libsecret.lookupSecretPasswordSync
import com.charlag.tuta.libsecret.storeSecretPasswordSync
import com.charlag.tuta.posix.*
import kotlinx.cinterop.MemScope
import kotlinx.cinterop.memScoped
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import org.libsecret.SECRET_COLLECTION_DEFAULT
import org.libsecret.SecretSchema
import org.libsecret.SecretSchemaAttributeType
import org.libsecret.SecretSchemaFlags
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

const val APP_FOLDER = "tuta-bridge"

fun getUserConfigDir(): Path {
    val xdg = getEnvironmentVariable("XDG_CONFIG_HOME")
    return if (xdg.isNullOrBlank()) {
        val home = getEnvironmentVariable("HOME") ?: error("No home?")
        Path(home).append(".config")
    } else {
        Path(xdg)
    }
}

fun getAppConfigDir(): Path {
    return getUserConfigDir().append(APP_FOLDER)
}

fun ensureAppConfigDir(): Path {
    return getAppConfigDir().also { ensureDir(it) }
}

@Serializable
class Credentials(
    val mailAddress: String,
    val accessToken: String,
    val encryptedPassword: ByteArray,
)

fun tryToLoadCredentials(): Credentials? {
    val json = Json { }

    return try {
        val pw = memScoped {
            lookupSecretPasswordSync(secretSchema(), mapOf("id" to "bridge"))
        }

        return pw?.let {
            json.decodeFromString<Credentials>(it)
        }
    } catch (e: Throwable) {
        println("Error while trying to read credentials: $e")
        null
    }
}

private fun getConfigPath(): Path {
    val configDir = ensureAppConfigDir()
    return configDir.append("config.json")
}

fun writeCredentials(credentials: Credentials) {
    val json = Json { }
    val jsonData = json.encodeToString(credentials)
    memScoped {
        storeSecretPasswordSync(
            secretSchema(),
            SECRET_COLLECTION_DEFAULT,
            label = "Tutanota bridge credentials",
            password = jsonData,
            attrs = mapOf("id" to "bridge")
        )
    }
}

private fun MemScope.secretSchema(): SecretSchema {
    return SecretSchema(
        "com.charlag.tuta-bridge",
        SecretSchemaFlags.SECRET_SCHEMA_NONE,
        mapOf("id" to SecretSchemaAttributeType.SECRET_SCHEMA_ATTRIBUTE_STRING)
    )
}

private fun readConfig(): JsonObject? {
    val json = Json { }

    val configPath = getConfigPath()
    return if (configPath.exists()) {
        val readFileContent = readFile(configPath)
        json.parseToJsonElement(readFileContent).jsonObject
    } else {
        null
    }
}

private fun saveConfig(config: JsonObject) {
    val json = Json { }
    val configPath = getConfigPath()
    val jsonData = json.encodeToString(config)
    writeFile(configPath, jsonData)
}

private const val LAST_ENTITY_EVENT_BATCH_ID = "lastEntityEventBatchId"

fun loadLastEntityEventBatchId(): String? {
    return readConfig()?.get(LAST_ENTITY_EVENT_BATCH_ID)?.jsonPrimitive?.content
}

fun writeLastEntityEventBatchId(lastId: String) {
    val config = readConfig() ?: JsonObject(mapOf())
    val newMap = config.mutableCopy()
    newMap[LAST_ENTITY_EVENT_BATCH_ID] = JsonPrimitive(lastId)
    saveConfig(JsonObject(newMap))
}

private fun <K, V> Map<K, V>.mutableCopy(): MutableMap<K, V> {
    val newMap = mutableMapOf<K, V>()
    for ((k, v) in this.entries) {
        newMap[k] = v
    }
    return newMap
}