package com.charlag.tuta.util

import android.content.Context


fun dpToPx(context: Context, dp: Int): Float {
    return dp * context.resources.displayMetrics.density
}

fun dpToPx(context: Context, dp: Float): Float {
    return dp * context.resources.displayMetrics.density
}