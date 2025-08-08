package com.bellmin.scrollablegraph

import android.annotation.SuppressLint
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.bellmin.scrollablegraph.data.ChartLine
import com.bellmin.scrollablegraph.data.DividerOrientation
import com.bellmin.scrollablegraph.data.GridLineStyle
import com.bellmin.scrollablegraph.data.LineChartOption
import com.bellmin.scrollablegraph.data.LineOption
import com.bellmin.scrollablegraph.data.LinePattern
import com.bellmin.scrollablegraph.data.LineStyle
import com.bellmin.scrollablegraph.data.XAxisMode
import com.bellmin.scrollablegraph.data.XAxisOption
import com.bellmin.scrollablegraph.data.XLabelOption
import com.bellmin.scrollablegraph.data.YLabelOption
import kotlin.math.max

@SuppressLint("DefaultLocale", "UnusedBoxWithConstraintsScope")
@Composable
fun ScrollableGraph(
    modifier: Modifier = Modifier,
    chartOption: LineChartOption
) {
    val density = LocalDensity.current
    val chartFrame = chartOption.chartFrame
    val lines = chartOption.lines
    val xAxisMode = chartOption.xAxisMode
    val yAxisOpt = chartOption.yAxis
    val xAxisOpt = chartOption.xAxis
    val screenWidth = LocalConfiguration.current.screenWidthDp

    val allX by remember(lines) { mutableStateOf(lines.flatMap { it.points.map { p -> p.first } }) }
    val allY by remember(lines) { mutableStateOf(lines.flatMap { it.points.map { p -> p.second } }) }
    val minX by remember(allX) { mutableFloatStateOf(allX.minOrNull() ?: 0f) }
    val maxX by remember(allX) { mutableFloatStateOf(allX.maxOrNull() ?: 1f) }
    val yMinBound = (yAxisOpt.option as? YLabelOption.Show)?.min
    val yMaxBound = (yAxisOpt.option as? YLabelOption.Show)?.max
    val minY by remember(allY, yMinBound) { mutableFloatStateOf(yMinBound ?: allY.minOrNull() ?: 0f) }
    val maxY by remember(allY, yMaxBound) { mutableFloatStateOf(yMaxBound ?: allY.maxOrNull() ?: 1f) }

    val resolver = LocalFontFamilyResolver.current

    val xLabelPaint = remember(xAxisOpt) {
        (xAxisOpt.option as? XLabelOption.Show)?.let {
            Paint().apply {
                typeface =resolver.resolve(xAxisOpt.option.style.fontFamily, FontWeight.Normal, FontStyle.Normal, FontSynthesis.All).value as android.graphics.Typeface
                textSize = dpToPx(it.style.fontSize, density)
                color = it.style.color.toArgb()
                isFakeBoldText = it.style.fontWeight > 500
            }
        } ?: Paint()
    }
    val yLabelPaint = remember(yAxisOpt) {
        (yAxisOpt.option as? YLabelOption.Show)?.let {
            Paint().apply {
                typeface =resolver.resolve(yAxisOpt.option.style.fontFamily, FontWeight.Normal, FontStyle.Normal, FontSynthesis.All).value as android.graphics.Typeface
                textSize = dpToPx(it.style.fontSize, density)
                color = it.style.color.toArgb()
                isFakeBoldText = it.style.fontWeight > 500
            }
        } ?: Paint()
    }

    val yList by remember(minY, maxY, yAxisOpt) {
        mutableStateOf(
            (yAxisOpt.option as? YLabelOption.Show)?.let { opt ->
                if (opt.labelCount >= 2) generateSteps(minY, maxY, opt.labelCount) else emptyList()
            } ?: emptyList()
        )
    }

    val bottomPadding by remember(xAxisOpt, xLabelPaint) {
        mutableStateOf(
            (xAxisOpt.option as? XLabelOption.Show)?.let {
                val d = xLabelPaint.fontMetrics.descent
                it.style.fontSize + pxToDp(d, density) + it.textSpace
            } ?: ((yAxisOpt.option as? YLabelOption.Show)?.style?.fontSize?.div(2) ?: 0.dp)
        )
    }

    val startPadding by remember(yAxisOpt, yList, yLabelPaint) {
        mutableStateOf(
            (yAxisOpt.option as? YLabelOption.Show)?.let { opt ->
                val w = yList.maxOfOrNull { v -> yLabelPaint.measureText(String.format(opt.formatter, v)) } ?: 0f
                pxToDp(w, density) + opt.textSpace
            } ?: 0.dp
        )
    }
    val topPadding by remember(yAxisOpt) {
        mutableStateOf((yAxisOpt.option as? YLabelOption.Show)?.style?.fontSize?.div(2) ?: 0.dp)
    }

    BoxWithConstraints(modifier = modifier) {
        val heightDp = with(LocalDensity.current) { constraints.maxHeight.toDp() }
        if (yAxisOpt.option is YLabelOption.Show && yList.size >= 2) {
            val sortedDesc = remember(yList) { yList.sortedDescending() }
            val space = (heightDp - topPadding - bottomPadding) / max(1, yAxisOpt.option.labelCount - 1)
            if (space > 0.dp) {
                repeat(yAxisOpt.option.labelCount) { i ->
                    Box(
                        modifier = Modifier
                            .width(startPadding - yAxisOpt.option.textSpace)
                            .fillMaxHeight()
                            .align(Alignment.TopStart)
                    ) {
                        Text(
                            text = String.format(yAxisOpt.option.formatter, sortedDesc[i]),
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = space * i),
                            style = TextStyle(
                                color = yAxisOpt.option.style.color,
                                fontFamily = yAxisOpt.option.style.fontFamily,
                                fontWeight = FontWeight(yAxisOpt.option.style.fontWeight)
                            ),
                            fontSize = yAxisOpt.option.style.fontSize.toSp()
                        )
                    }

                    if (i in 1 until (yAxisOpt.option.labelCount - 1)) {
                        yAxisOpt.option.gridLine?.let { grid ->
                            val lineMod = Modifier
                                .align(Alignment.TopStart)
                                .padding(
                                    start = startPadding,
                                    top = space * i + yAxisOpt.option.style.fontSize / 2
                                )
                            if (grid.pattern is LinePattern.Dashed) {
                                DashedDivider(
                                    modifier = lineMod,
                                    orientation = DividerOrientation.Horizontal,
                                    thickness = grid.thickness,
                                    color = grid.color,
                                    dashLength = grid.pattern.dashLength,
                                    gapLength = grid.pattern.gapLength
                                )
                            } else {
                                HorizontalDivider(
                                    thickness = grid.thickness,
                                    color = grid.color,
                                    modifier = lineMod.then(Modifier.padding(top = -(yAxisOpt.option.style.fontSize / 2)))
                                )
                            }
                        }
                    }
                }
            }
        }

        val contentPad = Modifier.padding(
            top = (if (chartFrame.top is LineOption.Show) chartFrame.top.style.thickness else 0.dp) + topPadding,
            start = (if (chartFrame.left is LineOption.Show) chartFrame.left.style.thickness else 0.dp) + startPadding,
            end = if (chartFrame.right is LineOption.Show) chartFrame.right.style.thickness else 0.dp
        )

        if (xAxisMode is XAxisMode.Scrollable) {
            val cnt = (maxX - minX) / xAxisMode.step
            val ratio = cnt / xAxisMode.visibleRange
            val width = (screenWidth * ratio).dp
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .then(contentPad)
                    .clipToBounds()
                    .horizontalScroll(rememberScrollState())
            ) {
                ChartCanvas(
                    modifier = Modifier.width(width).fillMaxHeight(),
                    lines = lines,
                    minX = minX,
                    maxX = maxX,
                    minY = minY,
                    maxY = maxY,
                    xAxisOpt = xAxisOpt,
                    xLabelPaint = xLabelPaint,
                    density = density,
                    bottomPadding = bottomPadding
                )
            }
        } else {
            ChartCanvas(
                modifier = Modifier
                    .fillMaxSize()
                    .then(contentPad)
                    .clipToBounds(),
                lines = lines,
                minX = minX,
                maxX = maxX,
                minY = minY,
                maxY = maxY,
                xAxisOpt = xAxisOpt,
                xLabelPaint = xLabelPaint,
                density = density,
                bottomPadding = bottomPadding
            )
        }

        with(chartFrame) {
            fun showDashed(opt: LineOption.Show) = opt.style.pattern is LinePattern.Dashed
            if (left is LineOption.Show) {
                if (showDashed(left)) {
                    DashedDivider(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = startPadding, bottom = bottomPadding, top = topPadding),
                        orientation = DividerOrientation.Vertical,
                        thickness = left.style.thickness,
                        color = left.style.color,
                        dashLength = (left.style.pattern as LinePattern.Dashed).dashLength,
                        gapLength = left.style.pattern.gapLength
                    )
                } else {
                    VerticalDivider(
                        thickness = left.style.thickness,
                        color = left.style.color,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = startPadding, bottom = bottomPadding, top = topPadding)
                    )
                }
            }
            if (right is LineOption.Show) {
                if (showDashed(right)) {
                    DashedDivider(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(bottom = bottomPadding, top = topPadding),
                        orientation = DividerOrientation.Vertical,
                        thickness = right.style.thickness,
                        color = right.style.color,
                        dashLength = (right.style.pattern as LinePattern.Dashed).dashLength,
                        gapLength = right.style.pattern.gapLength
                    )
                } else {
                    VerticalDivider(
                        thickness = right.style.thickness,
                        color = right.style.color,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(bottom = bottomPadding, top = topPadding)
                    )
                }
            }
            if (top is LineOption.Show) {
                if (showDashed(top)) {
                    DashedDivider(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(start = startPadding, top = topPadding),
                        orientation = DividerOrientation.Horizontal,
                        thickness = top.style.thickness,
                        color = top.style.color,
                        dashLength = (top.style.pattern as LinePattern.Dashed).dashLength,
                        gapLength = top.style.pattern.gapLength
                    )
                } else {
                    HorizontalDivider(
                        thickness = top.style.thickness,
                        color = top.style.color,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(start = startPadding, top = topPadding)
                    )
                }
            }
            if (bottom is LineOption.Show) {
                if (showDashed(bottom)) {
                    DashedDivider(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(start = startPadding, bottom = bottomPadding),
                        orientation = DividerOrientation.Horizontal,
                        thickness = bottom.style.thickness,
                        color = bottom.style.color,
                        dashLength = (bottom.style.pattern as LinePattern.Dashed).dashLength,
                        gapLength = bottom.style.pattern.gapLength
                    )
                } else {
                    HorizontalDivider(
                        thickness = bottom.style.thickness,
                        color = bottom.style.color,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(start = startPadding, bottom = bottomPadding)
                    )
                }
            }
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope", "DefaultLocale")
@Composable
fun ChartCanvas(
    modifier: Modifier,
    lines: List<ChartLine>,
    minX: Float,
    maxX: Float,
    minY: Float,
    maxY: Float,
    xAxisOpt: XAxisOption,
    xLabelPaint: Paint,
    density: Density,
    bottomPadding: Dp
) {
    BoxWithConstraints(modifier = modifier) {
        val w = constraints.maxWidth.toFloat()
        val h = constraints.maxHeight.toFloat()
        val chartW = w
        val chartH = h

        Canvas(Modifier.fillMaxSize()) {
            (xAxisOpt.option as? XLabelOption.Show)?.let { opt ->
                opt.labelIndices.forEach { (xVal, text) ->
                    val x = ((xVal.toFloat() - minX) / (maxX - minX)) * chartW
                    opt.gridLine?.let { grid ->
                        drawGridLineY(
                            x = x,
                            chartH = chartH - dpToPx(bottomPadding, density),
                            gridStyle = grid,
                            lineWidth = dpToPx(grid.thickness, density),
                            density = density
                        )
                    }
                    val textWidth = xLabelPaint.measureText(text)
                    drawContext.canvas.nativeCanvas.drawText(
                        text,
                        x - textWidth / 2,
                        h - dpToPx(opt.style.fontSize, density) / 2,
                        xLabelPaint
                    )
                }
            }
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
    if (points.size < 2 || maxX == minX || maxY == minY) return
    val sx = chartW / (maxX - minX)
    val sy = chartH / (maxY - minY)
    val strokePx = dpToPx(style.thickness, density)
    val path = Path().apply {
        points.forEachIndexed { i, (xVal, yVal) ->
            val x = (xVal - minX) * sx
            val y = chartH - (yVal - minY) * sy
            if (i == 0) moveTo(x, y) else lineTo(x, y)
        }
    }
    clipRect(0f, 0f, chartW, chartH) {
        drawPath(
            path = path,
            color = style.color,
            style = Stroke(
                width = strokePx,
                cap = if (style.isRoundCap) StrokeCap.Round else StrokeCap.Butt,
                pathEffect = style.pattern.toPathEffect(density)
            )
        )
    }
}

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

fun LinePattern.toPathEffect(density: Density): PathEffect? = when (this) {
    is LinePattern.Solid -> null
    is LinePattern.Dashed -> PathEffect.dashPathEffect(
        floatArrayOf(dpToPx(dashLength, density), dpToPx(gapLength, density)),
        0f
    )
}

@Composable
fun Dp.toSp(): TextUnit {
    val density = LocalDensity.current
    return with(density) { toSp() }
}

fun dpToPx(dp: Dp, density: Density): Float = with(density) { dp.toPx() }
fun pxToDp(px: Float, density: Density): Dp = with(density) { px.toDp() }

fun generateSteps(min: Float, max: Float, step: Int): List<Float> {
    require(step >= 2)
    if (step == 2) return listOf(min, max)
    val interval = (max - min) / (step - 1)
    return List(step) { i -> min + interval * i }
}
