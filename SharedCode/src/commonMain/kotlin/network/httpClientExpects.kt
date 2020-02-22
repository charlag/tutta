// Should not be a separate file, workaround for a compiler bug
// see https://youtrack.jetbrains.com/issue/KT-21186

@file:JvmMultifileClass
@file:JvmName("httpClient")

package com.charlag.tuta.network

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.features.json.JsonSerializer
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

expect fun platformEngine(): HttpClientEngine
expect fun platformJsonSerializer(): JsonSerializer