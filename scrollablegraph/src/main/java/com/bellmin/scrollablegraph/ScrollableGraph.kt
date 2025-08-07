package com.bellmin.scrollablegraph

import android.annotation.SuppressLint
import android.graphics.Paint
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bellmin.scrollablegraph.data.ChartFrameOption
import com.bellmin.scrollablegraph.data.ChartLine
import com.bellmin.scrollablegraph.data.GridLineStyle
import com.bellmin.scrollablegraph.data.LineChartOption
import com.bellmin.scrollablegraph.data.LineOption
import com.bellmin.scrollablegraph.data.LinePattern
import com.bellmin.scrollablegraph.data.LineStyle
import com.bellmin.scrollablegraph.data.XAxisLabelOption
import com.bellmin.scrollablegraph.data.XAxisMode
import com.bellmin.scrollablegraph.data.YAxisOption


@Composable
fun ScrollableGraph(
    modifier: Modifier = Modifier,
    chartOption: LineChartOption
) {
    val density = LocalDensity.current

    var canvasWidth by remember { mutableIntStateOf(0) }

    val chartFrame = chartOption.chartFrame
    val lines = chartOption.lines
    val xAxisMode = chartOption.xAxisMode
    val yAxisOpt = chartOption.yAxis
    val xAxisLabelOpt = chartOption.xAxisLabelOption
    val screenWidth = LocalConfiguration.current.screenWidthDp


    // 1. 모든 포인트 통합해 X, Y의 전체 범위 산출
    val allX = lines.flatMap { it.points.map { p -> p.first } }
    val allY = lines.flatMap { it.points.map { p -> p.second } }
    val minX = allX.minOrNull() ?: 0f
    val maxX = allX.maxOrNull() ?: 1f
    val minY = yAxisOpt.min ?: allY.minOrNull() ?: 0f
    val maxY = yAxisOpt.max ?: allY.maxOrNull() ?: 1f

    val paint = Paint().apply {
        textSize = dpToPx(xAxisLabelOpt.style.fontSize, density)
        color = xAxisLabelOpt.style.color.toArgb()
        isFakeBoldText = xAxisLabelOpt.style.fontWeight > 500
    }
    val fontMetrics = paint.fontMetrics
    val descent = fontMetrics.descent

    val bottomPadding = xAxisLabelOpt.style.fontSize + pxToDp(descent, density) + xAxisLabelOpt.textSpace

    // 3. 실제 그래프 그리기
    Box(modifier = modifier) {
        if (xAxisMode is XAxisMode.Scrollable) {
            // 스크롤이 가능하면 HorizontalScroll
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .horizontalScroll(rememberScrollState())
                    .padding(
                        top = if (chartFrame.top is LineOption.Show) chartFrame.top.style.thickness else 0.dp,
                        start = if (chartFrame.left is LineOption.Show) chartFrame.left.style.thickness else 0.dp,
                        end = if (chartFrame.right is LineOption.Show) chartFrame.right.style.thickness else 0.dp
                    )
                    .onGloballyPositioned{
                        canvasWidth = it.size.width
                    }
            ) {

                val cnt = (maxX - minX) / xAxisMode.step
                val ratio = cnt / xAxisMode.visibleRange
                val width = screenWidth * ratio

                ChartCanvas(
                    modifier = Modifier.width(width.dp).fillMaxHeight(),
                    lines = lines,
                    minX = minX,
                    maxX = maxX,
                    minY = minY,
                    maxY = maxY,
                    yAxisOpt = yAxisOpt,
                    xAxisLabelOpt = xAxisLabelOpt,
                    xLabelPaint = paint,
                    density = density,
                    bottomPadding = bottomPadding
                )
            }
        } else {
            ChartCanvas(
                modifier = Modifier.fillMaxSize(),
                lines = lines,
                minX = minX,
                maxX = maxX,
                minY = minY,
                maxY = maxY,
                yAxisOpt = yAxisOpt,
                xAxisLabelOpt = xAxisLabelOpt,
                xLabelPaint = paint,
                density = density,
                bottomPadding = bottomPadding
            )
        }

        with(chartFrame) {
            if (left is LineOption.Show) {
                VerticalDivider(
                    thickness = left.style.thickness,
                    color = left.style.color,
                    modifier = Modifier.align(Alignment.CenterStart).padding(bottom = bottomPadding)
                )
            }

            if (right is LineOption.Show) {
                VerticalDivider(
                    thickness = right.style.thickness,
                    color = right.style.color,
                    modifier = Modifier.align(Alignment.CenterEnd).padding(bottom = bottomPadding)
                )
            }

            if (top is LineOption.Show) {
                HorizontalDivider(
                    thickness = top.style.thickness,
                    color = top.style.color,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }

            if (bottom is LineOption.Show) {
                HorizontalDivider(
                    thickness = bottom.style.thickness,
                    color = bottom.style.color,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = bottomPadding)
                )
            }
        }
    }
}

// 내부 캔버스(축/프레임/데이터/라벨 모두 포함)
@SuppressLint("UnusedBoxWithConstraintsScope", "DefaultLocale")
@Composable
fun ChartCanvas(
    modifier: Modifier,
    lines: List<ChartLine>,
    minX: Float,
    maxX: Float,
    minY: Float,
    maxY: Float,
    yAxisOpt: YAxisOption,
    xAxisLabelOpt: XAxisLabelOption,
    xLabelPaint: Paint,
    density : Density,
    bottomPadding: Dp
) {
    BoxWithConstraints(
        modifier = modifier
    ) {

        val w = constraints.maxWidth.toFloat()
        val h = constraints.maxHeight.toFloat()
        val chartW = w
        val chartH = h

        Canvas(modifier = Modifier.fillMaxSize()) {


            // Y축 라벨 & 보조선
            if (yAxisOpt.enabled) {
                val yStep = (maxY - minY) / (yAxisOpt.labelCount - 1)
                repeat(yAxisOpt.labelCount) { i ->
                    val y = chartH - (chartH * i / (yAxisOpt.labelCount - 1))
                    // Y축 보조선
                    drawLine(
                        color = Color.LightGray,
                        start = Offset(0f, y),
                        end = Offset(w, y),
                        strokeWidth = 1.dp.toPx()
                    )
                    // Y축 라벨 (왼쪽)
                    drawContext.canvas.nativeCanvas.drawText(
                        String.format("%.1f", minY + i * yStep),
                        0f, y + 8, Paint().apply {
                            textSize = 24f
                            color = android.graphics.Color.DKGRAY
                        }
                    )
                }
            }

            xAxisLabelOpt.labelIndices.forEach { (xVal, text) ->
                val x = ((xVal.toFloat() - minX) / (maxX - minX)) * chartW

                // 라벨용 그리드라인
                xAxisLabelOpt.gridLine?.let { gridStyle ->
                    drawGridLineY(
                        x = x,
                        chartH = chartH - dpToPx(bottomPadding, density),
                        gridStyle = gridStyle,
                        lineWidth = dpToPx(xAxisLabelOpt.gridLine.thickness, density),
                        density = density
                    )
                }


                val textWidth = xLabelPaint.measureText(text)

                drawContext.canvas.nativeCanvas.drawText(
                    text,
                    x - textWidth / 2, // 중앙 정렬!
                    h - dpToPx(xAxisLabelOpt.style.fontSize, density) / 2,
                    xLabelPaint
                )
            }

            // 선(여러 개)
            lines.forEach { line ->
                drawLinePath(
                    points = line.points,
                    minX = minX,
                    maxX = maxX,
                    minY = minY,
                    maxY = maxY,
                    chartW = chartW,
                    chartH = chartH - dpToPx(bottomPadding, density),
                    style = line.style,
                    density = density
                )
            }
        }
    }
}


// 라인 그리기
fun DrawScope.drawLinePath(
    points: List<Pair<Float, Float>>,
    minX: Float,
    maxX: Float,
    minY: Float,
    maxY: Float,
    chartW: Float,
    chartH: Float,
    style: LineStyle,
    density: Density
) {
    if (points.size < 2) return
    val path = Path().apply {
        points.forEachIndexed { i, (xVal, yVal) ->
            val x = ((xVal - minX) / (maxX - minX)) * chartW
            val y = chartH - ((yVal - minY) / (maxY - minY)) * chartH
            if (i == 0) moveTo(x, y) else lineTo(x, y)
        }
    }
    drawPath(
        path = path,
        color = style.color,
        style = Stroke(
            width = dpToPx(style.thickness, density),
            cap = if (style.isRoundCap) StrokeCap.Round else StrokeCap.Butt,
            pathEffect = style.pattern.toPathEffect(density)
        )
    )
}

// Y축 그리드라인
fun DrawScope.drawGridLineY(
    x: Float,
    chartH: Float,
    gridStyle: GridLineStyle,
    lineWidth: Float,
    density: Density
) {
    drawLine(
        color = gridStyle.color,
        start = Offset(x, 0f),
        end = Offset(x, chartH),
        strokeWidth = lineWidth,
        pathEffect = gridStyle.pattern.toPathEffect(density)
    )
}

// 패턴 → PathEffect 변환
fun LinePattern.toPathEffect(density: Density): PathEffect? = when (this) {
    is LinePattern.Solid -> null
    is LinePattern.Dashed -> PathEffect.dashPathEffect(floatArrayOf(dpToPx(dashLength, density), dpToPx(gapLength, density)), 0f)
}

// Float? → 기본값 보정
fun Float?.orZero(def: Float) = this ?: def

fun dpToPx(dp: Dp, density: Density): Float {
    return with(density) { dp.toPx() }
}

fun pxToDp(px: Float, density: Density): Dp {
    return with(density) { px.toDp() }
}