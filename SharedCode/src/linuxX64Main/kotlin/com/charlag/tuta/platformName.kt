package com.charlag.tuta

import kotlin.reflect.KClass

actual fun platformName(): String = "LinuxX64"

actual val KClass<*>.noReflectionName: String
    get() = this.simpleName ?: "[Anonymous]"
