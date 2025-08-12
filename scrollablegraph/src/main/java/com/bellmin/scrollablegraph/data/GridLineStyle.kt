package com.bellmin.scrollablegraph.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bellmin.scrollablegraph.utils.colorFromRes
import com.bellmin.scrollablegraph.utils.pxToDp

interface GridLineStyle{
    fun gridLineColor(): Color
    fun gridLineThickness(): Dp
    fun gridLinePattern(): LinePattern
}

fun GridLineStyle.color() = gridLineColor()
fun GridLineStyle.thickness() = gridLineThickness()
fun GridLineStyle.pattern() = gridLinePattern()

data class GridLineStyleDp(
    val color: Color = Color.Black,
    val thickness: Dp = 2.dp,
    val pattern: LinePattern = LinePattern.DashedDp()
): GridLineStyle{
    override fun gridLineColor() = color
    override fun gridLineThickness() = thickness
    override fun gridLinePattern() = pattern
}

data class GridLineStylePx(
    val color: Int,
    val thickness: Float,
    val pattern: LinePattern = LinePattern.DashedPx(),
): GridLineStyle{
    override fun gridLineColor() = colorFromRes(color)
    override fun gridLineThickness() = thickness.pxToDp().dp
    override fun gridLinePattern() = pattern
}