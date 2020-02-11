package com.charlag.tuta.util

import android.content.Context
import android.content.res.ColorStateList
import android.view.MenuItem
import androidx.core.view.MenuItemCompat


fun dpToPx(context: Context, dp: Int): Float {
    return dp * context.resources.displayMetrics.density
}

fun dpToPx(context: Context, dp: Float): Float {
    return dp * context.resources.displayMetrics.density
}

fun MenuItem.setIconTintListCompat(tint: ColorStateList) = this.apply {
    MenuItemCompat.setIconTintList(this, tint)
}
