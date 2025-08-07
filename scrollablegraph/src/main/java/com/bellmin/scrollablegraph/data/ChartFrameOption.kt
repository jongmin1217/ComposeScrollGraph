package com.bellmin.scrollablegraph.data

data class ChartFrameOption(
    val top: LineOption = LineOption.Show(),
    val left: LineOption = LineOption.Show(),
    val right: LineOption = LineOption.Show(),
    val bottom: LineOption = LineOption.Show(),
)

sealed class LineOption{
    object Hide : LineOption()
    data class Show(
        val style : GridLineStyle = GridLineStyle()
    ) : LineOption()
}