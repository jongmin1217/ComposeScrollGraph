package com.bellmin.scrollablegraph.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class GridLineStyle(
    val color: Color = Color.Black,
    val thickness: Dp = 2.dp,
    val pattern: LinePattern = LinePattern.Dashed()
)