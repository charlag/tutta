@file:JvmMultifileClass
@file:JvmName("httpClient")

package com.charlag.tuta.network

import com.charlag.tuta.entities.sys.typeInfos
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.json.JsonSerializer
import io.ktor.client.features.json.serializer.KotlinxSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlin.reflect.KClass

actual fun platformEngine(): HttpClientEngine {
    return OkHttp.create { }
}

@UnstableDefault
actual fun platformJsonSerializer(): JsonSerializer {
    return KotlinxSerializer(Json(JsonConfiguration.Default)).apply {
        // TODO: check if we still have to do this
        typeInfos.forEach { (klass, _, _, serializer) ->
            @Suppress("UNCHECKED_CAST")
            setMapper(klass as KClass<Any>, serializer as KSerializer<Any>)
        }
    }
}