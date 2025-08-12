package com.bellmin.scrollablegraph.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.bellmin.scrollablegraph.R
import com.bellmin.scrollablegraph.ScrollableGraph
import com.bellmin.scrollablegraph.data.ChartFrameOption
import com.bellmin.scrollablegraph.data.ChartLine
import com.bellmin.scrollablegraph.data.LineChartOption
import com.bellmin.scrollablegraph.data.XAxisMode
import com.bellmin.scrollablegraph.data.XAxisOption
import com.bellmin.scrollablegraph.data.YAxisOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class ScrollableGraphView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val composeView: ComposeView
    private val optionState = MutableStateFlow(LineChartOption(lines = emptyList()))

    init {
        LayoutInflater.from(context).inflate(R.layout.view_scrollable_graph, this, true)
        composeView = findViewById(R.id.composeView)

        composeView.setViewCompositionStrategy(
            ViewCompositionStrategy.DisposeOnDetachedFromWindow
        )

        composeView.setContent {
            val option by optionState.collectAsState()

            ScrollableGraph(chartOption = option)
        }
    }

    fun setOption(lineChartOption: LineChartOption) {
        optionState.value = lineChartOption
    }

    fun setChart(chartLines: List<ChartLine>) {
        optionState.update { it.copy(lines = chartLines) }
    }

    fun setXAxisMode(xAxisMode: XAxisMode) {
        optionState.update { it.copy(xAxisMode = xAxisMode) }
    }

    fun setYAxis(yAxisOption: YAxisOption) {
        optionState.update { it.copy(yAxis = yAxisOption) }
    }

    fun setXAxis(xAxisOption: XAxisOption) {
        optionState.update { it.copy(xAxis = xAxisOption) }
    }

    fun setChartFrame(chartFrameOption: ChartFrameOption) {
        optionState.update { it.copy(chartFrame = chartFrameOption) }
    }

}