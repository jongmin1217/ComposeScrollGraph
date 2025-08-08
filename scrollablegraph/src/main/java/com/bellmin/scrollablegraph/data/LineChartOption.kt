package com.bellmin.scrollablegraph.data

data class LineChartOption(
    val lines: List<ChartLine>,
    val xAxisMode: XAxisMode = XAxisMode.FixedRange,
    val yAxis: YAxisOption = YAxisOption(),
    val xAxis: XAxisOption = XAxisOption(),
    val chartFrame: ChartFrameOption = ChartFrameOption()
)
