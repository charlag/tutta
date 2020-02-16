/* Copyright 2019 Tusky Contributors
 *
 * This file is a part of Tusky.
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * Tusky is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Tusky; if not,
 * see <http://www.gnu.org/licenses>. */
package com.charlag.tuta.util

import androidx.lifecycle.*

inline fun <X> LiveData<X>.filter(crossinline predicate: (X) -> Boolean): LiveData<X> {
    val liveData = MediatorLiveData<X>()
    liveData.addSource(this) { value ->
        if (predicate(value)) {
            liveData.value = value
        }
    }
    return liveData
}

fun LifecycleOwner.withLifecycleContext(body: LifecycleContext.() -> Unit) =
    LifecycleContext(this).apply(body)

class LifecycleContext(val lifecycleOwner: LifecycleOwner) {
    inline fun <T> LiveData<T>.observe(crossinline observer: (T) -> Unit) =
        this.observe(lifecycleOwner, Observer { observer(it) })

    /**
     * Just hold a subscription,
     */
    fun <T> LiveData<T>.subscribe() =
        this.observe(lifecycleOwner, Observer { })
}

/**
 * Invokes @param [combiner] when value of both @param [a] and @param [b] are not null. Returns
 * [LiveData] with value set to the result of calling [combiner] with value of both.
 * Important! You still need to observe to the returned [LiveData] for [combiner] to be invoked.
 */
fun <A, B, R> combineLiveData(a: LiveData<A>, b: LiveData<B>, combiner: (A, B) -> R): LiveData<R> {
    val liveData = MediatorLiveData<R>()
    liveData.addSource(a) {
        if (a.value != null && b.value != null) {
            liveData.value = combiner(a.value!!, b.value!!)
        }
    }
    liveData.addSource(b) {
        if (a.value != null && b.value != null) {
            liveData.value = combiner(a.value!!, b.value!!)
        }
    }
    return liveData
}

/**
 * Returns [LiveData] with value set to the result of calling [combiner] with value of [a] and [b]
 * after either changes. Doesn't check if either has value.
 * Important! You still need to observe to the returned [LiveData] for [combiner] to be invoked.
 */
fun <A, B, R> combineOptionalLiveData(
    a: LiveData<A>,
    b: LiveData<B>,
    combiner: (A?, B?) -> R
): LiveData<R> {
    val liveData = MediatorLiveData<R>()
    liveData.addSource(a) {
        liveData.value = combiner(a.value, b.value)
    }
    liveData.addSource(b) {
        liveData.value = combiner(a.value, b.value)
    }
    return liveData
}


inline fun <T> MutableLiveData<T>.mutate(block: (T?) -> T) {
    this.value = block(this.value)
}

inline fun <T> FilledMutableLiveData<T>.mutate(block: (T) -> T) {
    this.value = block(this.value)
}