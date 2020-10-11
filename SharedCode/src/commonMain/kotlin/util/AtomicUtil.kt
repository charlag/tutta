package com.charlag.tuta.util

/**
 * This is an atomic reference implemented with platform code. It is safe to modify it when frozen.
 * It automatically freezes everything you pass into it.
 * It is not efficient re-boxing and is only needed until kotlinx.atomicfu can be used.
 */
expect class PlatformAtomicRef<T>(value: T) {
    fun get(): T
    fun set(value: T)
}