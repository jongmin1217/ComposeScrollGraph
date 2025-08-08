package com.bellmin.scrollablegraph.data


sealed class XAxisMode {
    object FixedRange : XAxisMode()
    data class Scrollable(val visibleRange: Float, val step: Float) : XAxisMode()
}
