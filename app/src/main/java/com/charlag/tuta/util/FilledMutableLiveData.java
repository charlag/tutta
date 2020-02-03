package com.charlag.tuta.util;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;


/**
 * LiveData version which is guaranteed to have a value.
 *
 * Implemented in Java because we cannot override Java function with Kotlin property.
 */
public class FilledMutableLiveData<T> extends MutableLiveData<T> {
    // No constructor which does not use value
    public FilledMutableLiveData(T value) {
        super(value);
    }

    @SuppressWarnings("ConstantConditions")
    @NonNull
    @Override
    public T getValue() {
        return super.getValue();
    }
}
