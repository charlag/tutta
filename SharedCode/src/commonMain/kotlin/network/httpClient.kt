package com.charlag.tuta.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.features.json.JsonFeature

inline fun makeHttpClient(
    crossinline additionalConfig: HttpClientConfig<*>.() -> Unit = {}
): HttpClient = HttpClient(platformEngine()) {
    install(JsonFeature) {
        serializer = platformJsonSerializer()
    }
    additionalConfig()
}