package com.bellmin.scrollablegraph.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class LabelStyle(
    val color: Color = Color.Black,
    val fontSize: Dp = 20.dp,
    val fontFamily: FontFamily? = null,
    val fontWeight: Int = 700      // 400=normal, 700=bold 등
)