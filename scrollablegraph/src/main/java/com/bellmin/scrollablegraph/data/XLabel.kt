package com.bellmin.scrollablegraph.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class XAxisLabelOption(
    val labelIndices: List<Pair<Double, String>>,   // 라벨이 표시될 x값 인덱스 (혹은 x값 리스트)
    val style: LabelStyle,         // 라벨 텍스트 스타일
    val gridLine: GridLineStyle?,   // 해당 라벨 위치에 선 표시 옵션(null이면 안 그림)
    val textSpace : Dp = 10.dp
)

// 텍스트 스타일
data class LabelStyle(
    val color: Color = Color.Black,
    val fontSize: Dp = 20.dp,
    val fontFamily: String? = null,
    val fontWeight: Int = 700      // 400=normal, 700=bold 등
)

// 라벨용 선(그리드라인)
data class GridLineStyle(
    val color: Color = Color.Black,
    val thickness: Dp = 2.dp,
    val pattern: LinePattern = LinePattern.Solid
)
