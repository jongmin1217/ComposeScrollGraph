package com.bellmin.scrollablegraph.data

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bellmin.scrollablegraph.utils.pxToDp

data class YAxisOption(
    val option: YLabelOption = YLabelOption.Hide,   // Y축 라벨 표시 여부
)

interface ShowY{
    fun showLabelCount(): Int
    fun showMin(): Float?
    fun showMax(): Float?
    fun showStyle(): LabelStyle?
    fun showTextSpace(): Dp
    fun showFormatter(): String
    fun showGridLine(): GridLineStyle?
}

fun ShowY.labelCount() = showLabelCount()
fun ShowY.min() = showMin()
fun ShowY.max() = showMax()
fun ShowY.style() = showStyle()
fun ShowY.textSpace() = showTextSpace()
fun ShowY.formatter() = showFormatter()
fun ShowY.gridLine() = showGridLine()

sealed class YLabelOption{
    object Hide : YLabelOption()
    data class ShowDp(
        val labelCount: Int = 10,
        val min: Float? = null,
        val max: Float? = null,
        val style: LabelStyle? = null,
        val textSpace : Dp = 10.dp,
        val formatter: String = "%.1f",
        val gridLine: GridLineStyle? = null,
    ) : YLabelOption(), ShowY{
        override fun showLabelCount() = labelCount
        override fun showMin() = min
        override fun showMax() = max
        override fun showStyle() = style
        override fun showTextSpace() = textSpace
        override fun showFormatter() = formatter
        override fun showGridLine() = gridLine
    }


    data class ShowPx(
        val labelCount: Int = 10,
        val min: Float? = null,
        val max: Float? = null,
        val style: LabelStyle? = null,
        val textSpace : Float = 10f,
        val formatter: String = "%.1f",
        val gridLine: GridLineStyle? = null,
    ) : YLabelOption(), ShowY{
        override fun showLabelCount() = labelCount
        override fun showMin() = min
        override fun showMax() = max
        override fun showStyle() = style
        override fun showTextSpace() = textSpace.pxToDp().dp
        override fun showFormatter() = formatter
        override fun showGridLine() = gridLine
    }
}
