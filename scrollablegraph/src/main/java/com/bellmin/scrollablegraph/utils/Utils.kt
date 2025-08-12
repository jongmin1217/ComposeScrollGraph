package com.bellmin.scrollablegraph.utils

import androidx.annotation.ColorRes
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import com.bellmin.scrollablegraph.core.AppCtx

fun colorFromRes(@ColorRes resId: Int): Color {
    val colorInt = ContextCompat.getColor(AppCtx.app, resId)
    return Color(colorInt)
}

fun Float.pxToDp(): Float {
    val density = AppCtx.app.resources.displayMetrics.density
    return this / density
}