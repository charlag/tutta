package com.charlag.tuta.util

import kotlin.native.concurrent.AtomicReference
import kotlin.native.concurrent.freeze

actual class PlatformAtomicRef<T> actual constructor(value: T) {
    private val ref: AtomicReference<T> = AtomicReference(value.freeze())

    actual fun get(): T {
        return ref.value
    }

    actual fun set(value: T) {
        ref.value = value.freeze()
    }
}