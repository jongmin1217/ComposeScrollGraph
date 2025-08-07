package com.bellmin.composescrollgraph

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bellmin.composescrollgraph.data.Stt
import com.bellmin.composescrollgraph.data.SttItem
import com.bellmin.composescrollgraph.ui.theme.ComposeScrollGraphTheme
import com.bellmin.scrollablegraph.ScrollableGraph
import com.bellmin.scrollablegraph.data.ChartFrameOption
import com.bellmin.scrollablegraph.data.ChartLine
import com.bellmin.scrollablegraph.data.GridLineStyle
import com.bellmin.scrollablegraph.data.LabelStyle
import com.bellmin.scrollablegraph.data.LineChartOption
import com.bellmin.scrollablegraph.data.LinePattern
import com.bellmin.scrollablegraph.data.LineStyle
import com.bellmin.scrollablegraph.data.XAxisLabelOption
import com.bellmin.scrollablegraph.data.XAxisMode
import com.bellmin.scrollablegraph.data.YAxisOption
import com.google.gson.Gson
import kotlin.jvm.java

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val jsonString = readAssetFile(this, "Intonationexample.json")
        val gson = Gson()
        val stt: Stt = gson.fromJson(jsonString, Stt::class.java)

        enableEdgeToEdge()
        setContent {
            ComposeScrollGraphTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val sttItem = stt.resultData.result["5"]!!

                    val xLabel = sttItem.words!!.map { (((it.start?:0.0) + (it.end?:0.0))/2) to (it.text?:"") }
                    // 1. STT를 ChartLine 리스트로 변환
                    val chartLines = sttItemToChartLines(sttItem)

                    // 2. 그래프 옵션(예시)
                    val chartOption = LineChartOption(
                        lines = chartLines,
                        xAxisMode = XAxisMode.Scrollable(
                            step = 0.001f, // 데이터 해상도에 맞춰서
                            visibleRange = 1000f // 한 화면에 50step
                        ),
                        xAxisLabelOption = XAxisLabelOption(
                            labelIndices = xLabel, // 원하는 구간 인덱스
                            style = LabelStyle(
                                color = Color.Black,
                                fontWeight = 700
                            ),
                            gridLine = GridLineStyle(
                                color = Color.LightGray,
                                pattern = LinePattern.Dashed(6.dp, 3.dp)
                            )
                        ),
                        chartFrame = ChartFrameOption()
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                            .padding(innerPadding)
                    ) {
                        // 3. 실제 그래프 그리기
                        ScrollableGraph(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp)
                                .padding(20.dp),
                            chartOption = chartOption
                        )
                    }

                }
            }
        }
    }
}

fun readAssetFile(context: Context, fileName: String): String {
    return context.assets.open(fileName).bufferedReader().use { it.readText() }
}

fun sttItemToChartLines(
    sttItem: SttItem
): List<ChartLine> {
    val lines = mutableListOf<ChartLine>()

    // 예시: energy 그래프
    if (!sttItem.energyTime.isNullOrEmpty() && !sttItem.energyData.isNullOrEmpty()) {
        val pointCount = minOf(sttItem.energyTime.size, sttItem.energyData.size)
        val points = (0 until pointCount).map { i ->
            sttItem.energyTime[i].toFloat() to sttItem.energyData[i].toFloat()
        }
        lines.add(
            ChartLine(
                label = "energy",
                points = points,
                style = LineStyle(
                    color = Color(0xFF2196F3), // 예시 파랑
                    thickness = 6.dp,
                    isRoundCap = true,
                    pattern = LinePattern.Solid
                )
            )
        )
    }

    // 예시: intonation 그래프
    if (!sttItem.intonationTime.isNullOrEmpty() && !sttItem.intonationData.isNullOrEmpty()) {
        val pointCount = minOf(sttItem.intonationTime.size, sttItem.intonationData.size)
        val points = (0 until pointCount).map { i ->
            sttItem.intonationTime[i].toFloat() to sttItem.intonationData[i].toFloat()
        }
        lines.add(
            ChartLine(
                label = "intonation",
                points = points,
                style = LineStyle(
                    color = Color(0xFFE91E63), // 예시 분홍
                    thickness = 6.dp,
                    isRoundCap = true,
                    pattern = LinePattern.Solid
                )
            )
        )
    }
    return lines
}