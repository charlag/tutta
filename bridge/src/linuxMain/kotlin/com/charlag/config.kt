package com.charlag

import com.charlag.tuta.CreateSessionResult
import com.charlag.tuta.posix.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*

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

fun tryToLoadCredentials(): CreateSessionResult? {
    val credentialsPath = getCredentialsPath()
    println("Credentials $credentialsPath exists ${credentialsPath.exists()}")
    if (!credentialsPath.exists()) {
        return null
    }

    val json = Json { }

    val readFileContent = readFile(credentialsPath)

    json.parseToJsonElement(readFileContent)
    return json.decodeFromString<CreateSessionResult>(readFileContent)
}

private fun getCredentialsPath(): Path {
    val configDir = ensureAppConfigDir()
    return configDir.append("credentials.json")
}

private fun getConfigPath(): Path {
    val configDir = ensureAppConfigDir()
    return configDir.append("config.json")
}

fun writeCredentials(createSessionResult: CreateSessionResult) {
    val json = Json { }
    val jsonData = json.encodeToString(createSessionResult)
    writeFile(getCredentialsPath(), jsonData)
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
    val json = Json { }
    val config = readConfig() ?: JsonObject(mapOf())
    val newMap = config.mutableCopy()
    newMap[LAST_ENTITY_EVENT_BATCH_ID] = JsonPrimitive(lastId)
    writeFile(getConfigPath(), json.encodeToString(JsonObject(newMap)))
}

private fun <K, V> Map<K, V>.mutableCopy(): MutableMap<K, V> {
    val newMap = mutableMapOf<K, V>()
    for ((k, v) in this.entries) {
        newMap.set(k, v)
    }
    return newMap
}