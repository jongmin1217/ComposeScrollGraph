package com.bellmin.scrollablegraph.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bellmin.scrollablegraph.utils.colorFromRes
import com.bellmin.scrollablegraph.utils.pxToDp

data class ChartLine(
    val label: String,
    val points: List<Pair<Float, Float>>,
    val style: LineStyle
)

interface LineStyle{
    fun lineStyleColor(): Color
    fun lineStyleThickness(): Dp
    fun lineStyleIsRoundCap(): Boolean
    fun lineStylePattern(): LinePattern
}

fun LineStyle.color() = lineStyleColor()
fun LineStyle.thickness() = lineStyleThickness()
fun LineStyle.isRoundCap() = lineStyleIsRoundCap()
fun LineStyle.pattern() = lineStylePattern()


data class LineStyleDp(
    val color: Color = Color.Black,
    val thickness: Dp = 2.dp,
    val isRoundCap: Boolean = true,
    val pattern: LinePattern = LinePattern.Solid
): LineStyle{
    override fun lineStyleColor() = color
    override fun lineStyleThickness() = thickness
    override fun lineStyleIsRoundCap() = isRoundCap
    override fun lineStylePattern() = pattern
}



sealed class LinePattern {

    interface Dashed{
        fun lineDashLength(): Dp
        fun lineGapLength(): Dp
    }

    object Solid : LinePattern()
    data class DashedDp(val dashLength: Dp = 6.dp, val gapLength: Dp = 4.dp) : LinePattern(), Dashed{
        override fun lineDashLength() = dashLength
        override fun lineGapLength() = gapLength
    }

    data class DashedPx(val dashLength: Float = 6f, val gapLength: Float = 4f) : LinePattern(), Dashed{
        override fun lineDashLength() = dashLength.pxToDp().dp
        override fun lineGapLength() = gapLength.pxToDp().dp
    }
}


data class LineStylePx(
    val color: Int,
    val thickness: Float,
    val isRoundCap: Boolean,
    val pattern: LinePattern = LinePattern.Solid,
): LineStyle{
    override fun lineStyleColor() = colorFromRes(color)
    override fun lineStyleThickness() = thickness.pxToDp().dp
    override fun lineStyleIsRoundCap() = isRoundCap
    override fun lineStylePattern() = pattern
}
