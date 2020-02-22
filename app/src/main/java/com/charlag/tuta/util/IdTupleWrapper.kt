package com.charlag.tuta.util

import android.os.Parcelable
import com.charlag.tuta.entities.IdTuple
import kotlinx.android.parcel.Parcelize

/**
 * Little class needed because we can't implement Parcelable for IdTuple easily.
 */
@Parcelize
internal data class IdTupleWrapper(val listId: String, val elementId: String) : Parcelable

internal fun IdTuple.toWrapper() =
    IdTupleWrapper(listId.asString(), elementId.asString())

internal fun IdTupleWrapper.unwrap() =
    IdTuple.fromRawValues(
        listId,
        elementId
    )