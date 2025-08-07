package com.bellmin.scrollablegraph.data

data class LineChartOption(
    val lines: List<ChartLine>,               // 여러개 선
    val xAxisMode: XAxisMode,                 // x축 표현 방식(스크롤 등)
    val yAxis: YAxisOption = YAxisOption(),                   // y축
    val xAxisLabelOption: XAxisLabelOption,   // x축 라벨 및 라벨선
    val chartFrame: ChartFrameOption          // 외곽선
)
