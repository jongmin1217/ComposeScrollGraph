package com.bellmin.scrollablegraph.utils

import android.content.Context
import androidx.annotation.ColorRes
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat

fun colorFromRes(context: Context, @ColorRes resId: Int): Color {
    val colorInt = ContextCompat.getColor(context, resId)
    return Color(colorInt)
}