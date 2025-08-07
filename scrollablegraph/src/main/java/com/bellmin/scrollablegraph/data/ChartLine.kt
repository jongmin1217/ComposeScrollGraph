package com.bellmin.scrollablegraph.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

// 1. 실제 차트 데이터(선 여러 개 지원)
data class ChartLine(
    val label: String,                  // 선 이름(범례용 등)
    val points: List<Pair<Float, Float>>, // (x, y) 데이터
    val style: LineStyle                // 선의 스타일
)

// 2. 선 스타일
data class LineStyle(
    val color: Color,                    // 선 색상 (ex: ColorInt)
    val thickness: Dp,              // 선 굵기
    val isRoundCap: Boolean,           // 라운드 끝 처리
    val pattern: LinePattern = LinePattern.Solid // 직선/점선
)

// 2-1. 선 패턴 (직선, 점선 등)
sealed class LinePattern {
    object Solid : LinePattern()
    data class Dashed(val dashLength: Dp, val gapLength: Dp) : LinePattern()
}
