package com.bellmin.scrollablegraph.data

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class YAxisOption(
    val option: YLabelOption = YLabelOption.Show(),   // Y축 라벨 표시 여부
)

sealed class YLabelOption{
    object Hide : YLabelOption()
    data class Show(
        val labelCount: Int = 10,
        val min: Float? = null,
        val max: Float? = null,
        val style: LabelStyle? = null,
        val textSpace : Dp = 10.dp,
        val formatter: String = "%.1f",
        val gridLine: GridLineStyle? = null,
    ) : YLabelOption()
}
