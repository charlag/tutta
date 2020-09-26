@file:JvmMultifileClass
@file:JvmName("httpClient")

package com.charlag.tuta.network

import io.ktor.client.engine.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import kotlinx.serialization.json.Json

actual fun platformEngine(): HttpClientEngine {
    return OkHttp.create { }
}

actual fun platformJsonSerializer(): JsonSerializer {
    return KotlinxSerializer(Json).apply {
        // TODO: check if we still have to do this
//        typeInfo.forEach { (klass, _, _, serializer) ->
//            @Suppress("UNCHECKED_CAST")
//            setMapper(klass as KClass<Any>, serializer as KSerializer<Any>)
//        }
    }
}