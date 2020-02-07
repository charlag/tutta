package com.charlag.tuta.util

typealias AsyncProvider<T> = suspend () -> T

inline fun <T> lazyAsync(crossinline provider: suspend () -> T): suspend () -> T {
    var value: T? = null
    return {
        value ?: provider().also {
            value = it
        }
    }
}