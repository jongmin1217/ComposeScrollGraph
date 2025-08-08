package com.bellmin.scrollablegraph.data

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class YAxisOption(
    val option: YLabelOption = YLabelOption.Show(),   // Y축 라벨 표시 여부
)

sealed class YLabelOption{
    object Hide : YLabelOption()
    data class Show(
        val labelCount: Int = 10,       // 분할 개수
        val min: Float? = null,        // 최소값(없으면 데이터 기준)
        val max: Float? = null,         // 최대값(없으면 데이터 기준)
        val style: LabelStyle = LabelStyle(),
        val textSpace : Dp = 10.dp,
        val formatter: String = "%.1f",
        val gridLine: GridLineStyle? = null,
    ) : YLabelOption()
}
