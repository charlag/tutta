package com.charlag

import com.charlag.tuta.CreateSessionResult
import com.charlag.tuta.posix.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

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

fun tryToLoadCredentials(): CreateSessionResult? {
    val credentialsPath = getConfigPath()
    println("Credentials $credentialsPath exists ${credentialsPath.exists()}")
    if (!credentialsPath.exists()) {
        return null
    }

    val json = Json { }

    val readFileContent = readFile(credentialsPath)

    json.parseToJsonElement(readFileContent)
    return json.decodeFromString<CreateSessionResult>(readFileContent)
}

private fun getConfigPath(): Path {
    val configDir = getAppConfigDir()
    println("Config dir: $configDir ${configDir.exists()}")
    ensureDir(configDir)

    return configDir.append("credentials.json")
}

fun writeCredentials(createSessionResult: CreateSessionResult) {
    val json = Json {  }
    val jsonData = json.encodeToString(createSessionResult)
    writeFile(getConfigPath(), jsonData)
}