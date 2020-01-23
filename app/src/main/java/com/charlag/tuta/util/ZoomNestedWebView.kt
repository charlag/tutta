package com.charlag.tuta.util

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.webkit.WebView

/**
 * WebView which does not let parent intercept its multi-pointer events. This lets it zoom smoothly.
 */
class ZoomNestedWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : WebView(context, attrs, defStyleAttr) {

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.pointerCount >= 2) {
            requestDisallowInterceptTouchEvent(true)
        } else {
            requestDisallowInterceptTouchEvent(false)
        }

        return super.onTouchEvent(event)
    }
}