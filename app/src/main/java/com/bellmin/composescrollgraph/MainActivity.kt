package com.bellmin.composescrollgraph

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.W400
import androidx.compose.ui.text.font.FontWeight.Companion.W700
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.bellmin.composescrollgraph.data.Stt
import com.bellmin.composescrollgraph.data.SttItem
import com.bellmin.composescrollgraph.ui.theme.ComposeScrollGraphTheme
import com.bellmin.scrollablegraph.ScrollableGraph
import com.bellmin.scrollablegraph.data.ChartFrameOption
import com.bellmin.scrollablegraph.data.ChartLine
import com.bellmin.scrollablegraph.data.GridLineStyleDp
import com.bellmin.scrollablegraph.data.LabelStyleDp
import com.bellmin.scrollablegraph.data.LineChartOption
import com.bellmin.scrollablegraph.data.LineOption
import com.bellmin.scrollablegraph.data.LinePattern
import com.bellmin.scrollablegraph.data.LineStyle
import com.bellmin.scrollablegraph.data.LineStyleDp
import com.bellmin.scrollablegraph.data.XAxisMode
import com.bellmin.scrollablegraph.data.XAxisOption
import com.bellmin.scrollablegraph.data.XLabelOption
import com.bellmin.scrollablegraph.data.YAxisOption
import com.bellmin.scrollablegraph.data.YLabelOption
import com.google.gson.Gson

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        enableEdgeToEdge()
        setContent {
            ComposeScrollGraphTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val jsonString = readAssetFile(this, "test.json")
                    val gson = Gson()
                    val stt: Stt = gson.fromJson(jsonString, Stt::class.java)

                    val sttItem = stt.resultData.result["5"]!!

                    val roboto = FontFamily(
                        Font(R.font.spoqa_han_sans_neo_medium, FontWeight.Normal),
                        Font(R.font.spoqa_han_sans_neo_bold, FontWeight.Bold)
                    )

                    // X Label
                    var isShowXLabel by remember { mutableStateOf(true) }
                    var xLabelStyle by remember { mutableStateOf(LabelStyleDp(fontFamily = roboto)) }
                    var xLabelTextSpace by remember { mutableStateOf(10.dp) }
                    var isShowXLabelGridLine by remember { mutableStateOf(true) }
                    var xLabelGridLineStyle by remember { mutableStateOf(GridLineStyleDp()) }

                    // Y Label
                    var isShowYLabel by remember { mutableStateOf(true) }
                    var yLabelStyle by remember { mutableStateOf(LabelStyleDp(fontFamily = roboto)) }
                    var yLabelTextSpace by remember { mutableStateOf(10.dp) }
                    var isShowYLabelGridLine by remember { mutableStateOf(true) }
                    var yLabelGridLineStyle by remember { mutableStateOf(GridLineStyleDp()) }
                    var yLabelCnt by remember { mutableIntStateOf(5) }

                    // outline
                    var isShowTopGridLine by remember { mutableStateOf(true) }
                    var topGridLineStyle by remember { mutableStateOf(GridLineStyleDp()) }
                    var isShowBottomGridLine by remember { mutableStateOf(true) }
                    var bottomGridLineStyle by remember { mutableStateOf(GridLineStyleDp()) }
                    var isShowLeftGridLine by remember { mutableStateOf(true) }
                    var leftGridLineStyle by remember { mutableStateOf(GridLineStyleDp()) }
                    var isShowRightGridLine by remember { mutableStateOf(true) }
                    var rightGridLineStyle by remember { mutableStateOf(GridLineStyleDp()) }

                    var energyLineStyle by remember {
                        mutableStateOf(
                            LineStyleDp(
                                color = Color.Blue,
                                thickness = 6.dp,
                                isRoundCap = true,
                                pattern = LinePattern.Solid
                            )
                        )
                    }

                    var intonationLineStyle by remember {
                        mutableStateOf(
                            LineStyleDp(
                                color = Color.Red,
                                thickness = 6.dp,
                                isRoundCap = true,
                                pattern = LinePattern.Solid
                            )
                        )
                    }

                    val xLabel = sttItem.words!!.map {
                        (((it.start ?: 0.0) + (it.end ?: 0.0)) / 2) to (it.text ?: "")
                    }
                    val chartLines = sttItemToChartLines(
                        sttItem,
                        energyLineStyle,
                        intonationLineStyle
                    )

                    var enableScroll by remember { mutableStateOf(true) }
                    var visibleRange by remember { mutableFloatStateOf(3000f) }


                    val chartOption = LineChartOption(
                        lines = chartLines,
                        xAxisMode = if (enableScroll) {
                            XAxisMode.Scrollable(
                                step = 0.001f,
                                visibleRange = visibleRange
                            )
                        } else {
                            XAxisMode.FixedRange
                        },
                        xAxis = XAxisOption(
                            option = XLabelOption.ShowDp(
                                labelIndices = xLabel,
                                style = if (isShowXLabel) xLabelStyle else null,
                                gridLine = if (isShowXLabelGridLine) xLabelGridLineStyle else null,
                                textSpace = xLabelTextSpace
                            )
                        ),
                        yAxis = YAxisOption(
                            option = YLabelOption.ShowDp().copy(
                                labelCount = yLabelCnt,
                                gridLine = if (isShowYLabelGridLine) yLabelGridLineStyle else null,
                                style = if (isShowYLabel) yLabelStyle else null,
                                textSpace = yLabelTextSpace,
                                formatter = "%.0f"
                            )
                        ),
                        chartFrame = ChartFrameOption(
                            top = if (isShowTopGridLine) LineOption.Show(topGridLineStyle) else LineOption.Hide,
                            bottom = if (isShowBottomGridLine) LineOption.Show(bottomGridLineStyle) else LineOption.Hide,
                            left = if (isShowLeftGridLine) LineOption.Show(leftGridLineStyle) else LineOption.Hide,
                            right = if (isShowRightGridLine) LineOption.Show(rightGridLineStyle) else LineOption.Hide,
                        )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                            .padding(innerPadding)
                    ) {

                        ScrollableGraph(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .padding(20.dp),
                            chartOption = chartOption
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                        ) {
                            ExpandedContent(
                                title = "X Label",
                                content = {
                                    LabelContent(
                                        modifier = Modifier.padding(start = 20.dp),
                                        isShowLabel = isShowXLabel,
                                        isShowGridLine = isShowXLabelGridLine,
                                        labelStyle = xLabelStyle,
                                        gridLineStyle = xLabelGridLineStyle,
                                        textSpace = xLabelTextSpace,
                                        onChangedShowLabel = {
                                            isShowXLabel = it
                                        },
                                        onChangedShowGridLine = {
                                            isShowXLabelGridLine = it
                                        },
                                        onChangedLabelStyle = {
                                            xLabelStyle = it
                                        },
                                        onChangedGridLineStyle = {
                                            xLabelGridLineStyle = it
                                        },
                                        onChangedTextSpace = {
                                            xLabelTextSpace = it
                                        }
                                    )
                                }
                            )

                            ExpandedContent(
                                modifier = Modifier.padding(top = 20.dp),
                                title = "Y Label",
                                content = {
                                    Column(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {

                                        SettingNumberContent(
                                            modifier = Modifier.padding(start = 20.dp),
                                            title = "Label Count",
                                            value = yLabelCnt.toString(),
                                            isShowDown = yLabelCnt > 2,
                                            onDownClick = {
                                                yLabelCnt = yLabelCnt - 1
                                            },
                                            onUpClick = {
                                                yLabelCnt = yLabelCnt + 1
                                            }
                                        )

                                        LabelContent(
                                            modifier = Modifier.padding(start = 20.dp),
                                            isShowLabel = isShowYLabel,
                                            isShowGridLine = isShowYLabelGridLine,
                                            labelStyle = yLabelStyle,
                                            gridLineStyle = yLabelGridLineStyle,
                                            textSpace = yLabelTextSpace,
                                            onChangedShowLabel = {
                                                isShowYLabel = it
                                            },
                                            onChangedShowGridLine = {
                                                isShowYLabelGridLine = it
                                            },
                                            onChangedLabelStyle = {
                                                yLabelStyle = it
                                            },
                                            onChangedGridLineStyle = {
                                                yLabelGridLineStyle = it
                                            },
                                            onChangedTextSpace = {
                                                yLabelTextSpace = it
                                            }
                                        )
                                    }
                                }
                            )

                            ExpandedContent(
                                modifier = Modifier.padding(top = 20.dp),
                                title = "OutLine",
                                content = {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 20.dp)
                                    ) {
                                        val list = listOf(
                                            "Top",
                                            "Bottom",
                                            "Left",
                                            "Right"
                                        )

                                        list.forEach { item ->
                                            Text(
                                                text = item,
                                                style = TextStyle(
                                                    fontSize = 20.dp.textSp,
                                                    fontWeight = W700
                                                ),
                                                color = Color.Black,
                                                modifier = Modifier.padding(top = 20.dp)
                                            )

                                            SettingGridLine(
                                                isShowGridLine = when (item) {
                                                    "Top" -> isShowTopGridLine
                                                    "Bottom" -> isShowBottomGridLine
                                                    "Left" -> isShowLeftGridLine
                                                    else -> isShowRightGridLine
                                                },
                                                gridLineStyle = when (item) {
                                                    "Top" -> topGridLineStyle
                                                    "Bottom" -> bottomGridLineStyle
                                                    "Left" -> leftGridLineStyle
                                                    else -> rightGridLineStyle
                                                },
                                                onChangedShowGridLine = {
                                                    when (item) {
                                                        "Top" -> isShowTopGridLine = it
                                                        "Bottom" -> isShowBottomGridLine = it
                                                        "Left" -> isShowLeftGridLine = it
                                                        "Right" -> isShowRightGridLine = it
                                                    }
                                                },
                                                onChangedGridLineStyle = {
                                                    when (item) {
                                                        "Top" -> topGridLineStyle = it
                                                        "Bottom" -> bottomGridLineStyle = it
                                                        "Left" -> leftGridLineStyle = it
                                                        "Right" -> rightGridLineStyle = it
                                                    }
                                                }
                                            )
                                        }

                                    }
                                }
                            )


                            ExpandedContent(
                                modifier = Modifier.padding(top = 20.dp),
                                title = "Graph Line",
                                content = {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 20.dp)
                                    ) {
                                        val list = listOf(
                                            "energy",
                                            "intonation",
                                        )

                                        list.forEach { item ->
                                            Text(
                                                text = item,
                                                style = TextStyle(
                                                    fontSize = 20.dp.textSp,
                                                    fontWeight = W700
                                                ),
                                                color = Color.Black,
                                                modifier = Modifier.padding(top = 20.dp)
                                            )

                                            SettingLine(
                                                lineStyle = when (item) {
                                                    "energy" -> energyLineStyle
                                                    else -> intonationLineStyle
                                                },
                                                onChangedLineStyle = {
                                                    when (item) {
                                                        "energy" -> energyLineStyle = it
                                                        "intonation" -> intonationLineStyle = it
                                                    }
                                                },
                                            )
                                        }
                                    }
                                }
                            )

                            ExpandedContent(
                                modifier = Modifier.padding(top = 20.dp),
                                title = "Scrollable",
                                content = {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 20.dp)
                                    ) {
                                        SwitchContent(
                                            title = "Scrollable",
                                            isChecked = enableScroll
                                        ) {
                                            enableScroll = it
                                        }

                                        SettingNumberContent(
                                            title = "Visible Range",
                                            value = visibleRange.toString(),
                                            isShowDown = visibleRange >= 500f,
                                            onDownClick = {
                                                visibleRange = visibleRange - 100f
                                            },
                                            onUpClick = {
                                                visibleRange = visibleRange + 100f
                                            }
                                        )

                                        Spacer(Modifier.height(20.dp))
                                    }
                                }
                            )


                            Spacer(Modifier.height(20.dp))
                        }
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
    sttItem: SttItem,
    energyLineStyle: LineStyleDp,
    intonationLineStyle: LineStyleDp
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
                style = energyLineStyle
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
                style = intonationLineStyle
            )
        )
    }
    return lines
}

@Composable
fun ExpandedContent(
    modifier: Modifier = Modifier,
    title: String,
    content: @Composable () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    val arrowRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "arrowRotation"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .background(
                color = Color.LightGray,
                shape = RoundedCornerShape(16.dp)
            )
            .animateContentSize()
    ) {
        Row(
            modifier = Modifier
                .height(40.dp)
                .clickable {
                    isExpanded = !isExpanded
                }
        ) {
            Text(
                text = title,
                modifier = Modifier
                    .padding(start = 20.dp)
                    .align(Alignment.CenterVertically),
                style = TextStyle(
                    fontSize = 20.dp.textSp,
                    fontWeight = W700
                ),
                color = Color.Black
            )

            Icon(
                modifier = Modifier
                    .size(50.dp)
                    .align(Alignment.CenterVertically)
                    .padding(start = 20.dp)
                    .rotate(arrowRotation),
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = "",
                tint = Color.Black
            )
        }


        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            content()
        }
    }
}

@Composable
fun SwitchContent(
    modifier: Modifier = Modifier,
    title: String,
    isChecked: Boolean,
    onChangedChecked: (Boolean) -> Unit
) {
    Row(
        modifier = modifier
    ) {
        Text(
            text = title,
            style = TextStyle(
                fontSize = 16.dp.textSp,
                fontWeight = W400
            ),
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(end = 20.dp),
            color = Color.Black
        )

        Switch(
            checked = isChecked,
            onCheckedChange = {
                onChangedChecked(it)
            }
        )
    }
}

@Composable
fun SettingColorContent(
    modifier: Modifier = Modifier,
    title: String,
    color: Color,
    onChangeColor: (Color) -> Unit,
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = title,
            modifier = Modifier
                .padding(top = 10.dp),
            style = TextStyle(
                fontSize = 16.dp.textSp,
                fontWeight = W400
            ),
            color = Color.Black
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
                .horizontalScroll(rememberScrollState())
        ) {
            val colorList = listOf(
                Color.Black,
                Color.DarkGray,
                Color.Gray,
                Color.LightGray,
                Color.White,
                Color.Red,
                Color.Green,
                Color.Blue,
                Color.Yellow,
                Color.Cyan,
                Color.Magenta
            )
            colorList.forEach {
                val isSelect = it == color

                Box(
                    modifier = Modifier
                        .border(
                            width = 3.dp,
                            color = if (isSelect) {
                                if (it == Color.Red) Color.Black
                                else Color.Red
                            } else Color.Transparent
                        )
                        .clickable {
                            onChangeColor(it)
                        }
                ) {
                    Spacer(
                        modifier = Modifier
                            .size(40.dp)
                            .background(it)
                    )
                }
            }
            Spacer(modifier = Modifier.width(20.dp))
        }
    }
}

@Composable
fun SettingNumberContent(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    isShowDown: Boolean = true,
    isShowUp: Boolean = true,
    onUpClick: () -> Unit,
    onDownClick: () -> Unit
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = title,
            modifier = Modifier
                .padding(top = 20.dp),
            style = TextStyle(
                fontSize = 16.dp.textSp,
                fontWeight = W400
            ),
            color = Color.Black
        )

        Row(
            modifier = Modifier
                .padding(top = 10.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            IconButton(
                modifier = Modifier
                    .size(40.dp)
                    .border(
                        width = 1.dp,
                        color = Color.DarkGray,
                        shape = CircleShape
                    )
                    .align(Alignment.CenterVertically),
                onClick = {
                    if (isShowDown) onDownClick()
                }
            ) {
                if (isShowDown) Icon(
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = "down",
                    tint = Color.Black
                )
            }

            Text(
                text = value,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .align(Alignment.CenterVertically),
                color = Color.Black
            )

            IconButton(
                modifier = Modifier
                    .size(40.dp)
                    .border(
                        width = 1.dp,
                        color = Color.DarkGray,
                        shape = CircleShape
                    )
                    .align(Alignment.CenterVertically),
                onClick = {
                    if (isShowUp) onUpClick()
                }
            ) {
                if (isShowUp) Icon(
                    imageVector = Icons.Rounded.KeyboardArrowUp,
                    contentDescription = "up",
                    tint = Color.Black
                )
            }
        }
    }
}

@Composable
fun SettingLabel(
    modifier: Modifier = Modifier,
    isShowLabel: Boolean,
    labelStyle: LabelStyleDp,
    textSpace: Dp,
    onChangedShowLabel: (Boolean) -> Unit,
    onChangedLabelStyle: (LabelStyleDp) -> Unit,
    onChangedTextSpace: (Dp) -> Unit,
) {
    Column(
        modifier = modifier
    ) {
        SwitchContent(
            title = "Show Label",
            isChecked = isShowLabel,
            onChangedChecked = onChangedShowLabel
        )

        SettingColorContent(
            title = "Label Color",
            color = labelStyle.color,
            onChangeColor = {
                onChangedLabelStyle(
                    labelStyle.copy(
                        color = it
                    )
                )
            }
        )

        SettingNumberContent(
            title = "Label Size",
            value = labelStyle.fontSize.toString(),
            isShowDown = labelStyle.fontSize.value > 0,
            onUpClick = {
                onChangedLabelStyle(
                    labelStyle.copy(
                        fontSize = labelStyle.fontSize + 1.dp
                    )
                )
            },
            onDownClick = {
                onChangedLabelStyle(
                    labelStyle.copy(
                        fontSize = labelStyle.fontSize - 1.dp
                    )
                )
            }
        )

        SettingNumberContent(
            title = "Label Space",
            value = textSpace.toString(),
            isShowDown = (textSpace.value) > 0,
            onUpClick = {
                onChangedTextSpace(textSpace + 1.dp)
            },
            onDownClick = {
                onChangedTextSpace(textSpace - 1.dp)
            }
        )

        SwitchContent(
            title = "Label Bold",
            isChecked = labelStyle.fontWeight >= 700,
            onChangedChecked = {
                onChangedLabelStyle(
                    labelStyle.copy(
                        fontWeight = if (it) 700 else 500
                    )
                )
            }
        )
    }
}

@Composable
fun SettingGridLine(
    modifier: Modifier = Modifier,
    isShowGridLine: Boolean,
    gridLineStyle: GridLineStyleDp,
    onChangedShowGridLine: (Boolean) -> Unit,
    onChangedGridLineStyle: (GridLineStyleDp) -> Unit,
) {
    Column(
        modifier = modifier
    ) {
        SwitchContent(
            title = "Show Line",
            isChecked = isShowGridLine,
            onChangedChecked = onChangedShowGridLine
        )

        SettingColorContent(
            title = "Line Color",
            color = gridLineStyle.color,
            onChangeColor = {
                onChangedGridLineStyle(
                    gridLineStyle.copy(
                        color = it
                    )
                )
            }
        )

        SettingNumberContent(
            title = "Line Width",
            value = gridLineStyle.thickness.toString(),
            isShowDown = (gridLineStyle.thickness) > 0.dp,
            onUpClick = {
                onChangedGridLineStyle(
                    gridLineStyle.copy(
                        thickness = gridLineStyle.thickness + 1.dp
                    )
                )
            },
            onDownClick = {
                onChangedGridLineStyle(
                    gridLineStyle.copy(
                        thickness = gridLineStyle.thickness - 1.dp
                    )
                )
            }
        )

        SwitchContent(
            title = "Line DashLine",
            isChecked = gridLineStyle.pattern is LinePattern.Dashed,
            onChangedChecked = {
                onChangedGridLineStyle(
                    gridLineStyle.copy(
                        pattern = if (it) LinePattern.DashedDp() else LinePattern.Solid
                    )
                )
            }
        )
    }
}


@Composable
fun SettingLine(
    modifier: Modifier = Modifier,
    lineStyle: LineStyleDp,
    onChangedLineStyle: (LineStyleDp) -> Unit,
) {
    Column(
        modifier = modifier
    ) {


        SettingColorContent(
            title = "Line Color",
            color = lineStyle.color,
            onChangeColor = {
                onChangedLineStyle(
                    lineStyle.copy(
                        color = it
                    )
                )
            }
        )

        SettingNumberContent(
            title = "Line Width",
            value = lineStyle.thickness.toString(),
            isShowDown = (lineStyle.thickness) > 0.dp,
            onUpClick = {
                onChangedLineStyle(
                    lineStyle.copy(
                        thickness = lineStyle.thickness + 1.dp
                    )
                )
            },
            onDownClick = {
                onChangedLineStyle(
                    lineStyle.copy(
                        thickness = lineStyle.thickness - 1.dp
                    )
                )
            }
        )

        SwitchContent(
            title = "Line DashLine",
            isChecked = lineStyle.pattern is LinePattern.Dashed,
            onChangedChecked = {
                onChangedLineStyle(
                    lineStyle.copy(
                        pattern = if (it) LinePattern.DashedDp() else LinePattern.Solid
                    )
                )
            }
        )

        SwitchContent(
            title = "RoundCap Line",
            isChecked = lineStyle.isRoundCap,
            onChangedChecked = {
                onChangedLineStyle(
                    lineStyle.copy(
                        isRoundCap = it
                    )
                )
            }
        )
    }
}

@Composable
fun LabelContent(
    modifier: Modifier = Modifier,
    isShowLabel: Boolean,
    isShowGridLine: Boolean,
    labelStyle: LabelStyleDp,
    gridLineStyle: GridLineStyleDp,
    textSpace: Dp,
    onChangedShowLabel: (Boolean) -> Unit,
    onChangedShowGridLine: (Boolean) -> Unit,
    onChangedLabelStyle: (LabelStyleDp) -> Unit,
    onChangedGridLineStyle: (GridLineStyleDp) -> Unit,
    onChangedTextSpace: (Dp) -> Unit,
) {
    Column(
        modifier = modifier
    ) {
        SettingLabel(
            isShowLabel = isShowLabel,
            labelStyle = labelStyle,
            textSpace = textSpace,
            onChangedShowLabel = onChangedShowLabel,
            onChangedLabelStyle = onChangedLabelStyle,
            onChangedTextSpace = onChangedTextSpace
        )

        SettingGridLine(
            isShowGridLine = isShowGridLine,
            gridLineStyle = gridLineStyle,
            onChangedShowGridLine = onChangedShowGridLine,
            onChangedGridLineStyle = onChangedGridLineStyle
        )
    }
}

val Dp.textSp: TextUnit
    @Composable get() = with(LocalDensity.current) {
        this@textSp.toSp()
    }