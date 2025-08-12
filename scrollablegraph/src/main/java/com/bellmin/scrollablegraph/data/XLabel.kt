package com.bellmin.scrollablegraph.data

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bellmin.scrollablegraph.utils.pxToDp

data class XAxisOption(
    val option : XLabelOption = XLabelOption.Hide
)

interface ShowX{
    fun showLabelIndices(): List<Pair<Double, String>>
    fun showStyle(): LabelStyle?
    fun showGridLine(): GridLineStyle?
    fun showTextSpace(): Dp
}

fun ShowX.labelIndices() = showLabelIndices()
fun ShowX.style() = showStyle()
fun ShowX.gridLine() = showGridLine()
fun ShowX.textSpace() = showTextSpace()

sealed class XLabelOption{
    object Hide : XLabelOption()
    data class ShowDp(
        val labelIndices: List<Pair<Double, String>> = listOf(),
        val style: LabelStyle? = null,
        val gridLine: GridLineStyle? = null,
        val textSpace : Dp = 10.dp
    ) : XLabelOption(), ShowX{
        override fun showLabelIndices() = labelIndices
        override fun showStyle() = style
        override fun showGridLine() = gridLine
        override fun showTextSpace() = textSpace
    }

    data class ShowPx(
        val labelIndices: List<Pair<Double, String>> = listOf(),
        val style: LabelStyle? = null,
        val gridLine: GridLineStyle? = null,
        val textSpace : Float = 10f
    ) : XLabelOption(), ShowX{
        override fun showLabelIndices() = labelIndices
        override fun showStyle() = style
        override fun showGridLine() = gridLine
        override fun showTextSpace() = textSpace.pxToDp().dp
    }
}

