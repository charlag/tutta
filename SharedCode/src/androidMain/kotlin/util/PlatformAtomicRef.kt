package com.charlag.tuta.util

import java.util.concurrent.atomic.AtomicReference

actual class PlatformAtomicRef<T> actual constructor(value: T) {
    private val ref: AtomicReference<T> = AtomicReference(value)

    actual fun get(): T {
        return this.ref.get()
    }

    actual fun set(value: T) {
        this.ref.set(value)
    }
}