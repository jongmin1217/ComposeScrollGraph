package com.bellmin.scrollablegraph.data

// x축 표현방식(스크롤 or 비스크롤)
sealed class XAxisMode {
    // 한 화면에 모든 데이터
    object FixedRange : XAxisMode()
    // 스크롤(화면 크기, 한 화면에 보여질 step)
    data class Scrollable(val visibleRange: Float, val step: Float) : XAxisMode()
}
