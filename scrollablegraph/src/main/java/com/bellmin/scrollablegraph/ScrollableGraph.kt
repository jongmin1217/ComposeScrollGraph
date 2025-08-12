package com.bellmin.scrollablegraph

import android.annotation.SuppressLint
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
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
import com.bellmin.scrollablegraph.data.LabelStyle
import com.bellmin.scrollablegraph.data.LineChartOption
import com.bellmin.scrollablegraph.data.LineOption
import com.bellmin.scrollablegraph.data.LinePattern
import com.bellmin.scrollablegraph.data.LineStyle
import com.bellmin.scrollablegraph.data.ShowX
import com.bellmin.scrollablegraph.data.ShowY
import com.bellmin.scrollablegraph.data.XAxisMode
import com.bellmin.scrollablegraph.data.XAxisOption
import com.bellmin.scrollablegraph.data.YAxisOption
import com.bellmin.scrollablegraph.data.color
import com.bellmin.scrollablegraph.data.fontFamily
import com.bellmin.scrollablegraph.data.fontSize
import com.bellmin.scrollablegraph.data.fontWeight
import com.bellmin.scrollablegraph.data.formatter
import com.bellmin.scrollablegraph.data.gridLine
import com.bellmin.scrollablegraph.data.isRoundCap
import com.bellmin.scrollablegraph.data.labelCount
import com.bellmin.scrollablegraph.data.labelIndices
import com.bellmin.scrollablegraph.data.max
import com.bellmin.scrollablegraph.data.min
import com.bellmin.scrollablegraph.data.pattern
import com.bellmin.scrollablegraph.data.style
import com.bellmin.scrollablegraph.data.textSpace
import com.bellmin.scrollablegraph.data.thickness
import kotlin.math.max

private enum class FrameSide { Left, Right, Top, Bottom }

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

    val scrollState = rememberScrollState()

    val allX by remember(lines) { mutableStateOf(lines.flatMap { it.points.map { p -> p.first } }) }
    val allY by remember(lines) { mutableStateOf(lines.flatMap { it.points.map { p -> p.second } }) }
    val minX by remember(allX) { mutableFloatStateOf(allX.minOrNull() ?: 0f) }
    val maxX by remember(allX) { mutableFloatStateOf(allX.maxOrNull() ?: 1f) }

    val yShow = yAxisOpt.option as? ShowY
    val xShow = xAxisOpt.option as? ShowX

    val yMinBound = yShow?.min()
    val yMaxBound = yShow?.max()

    val minY by remember(allY, yMinBound) { mutableFloatStateOf(yMinBound ?: allY.minOrNull() ?: 0f) }
    val maxY by remember(allY, yMaxBound) { mutableFloatStateOf(yMaxBound ?: allY.maxOrNull() ?: 1f) }

    val resolver = LocalFontFamilyResolver.current

    val xLabelPaint = remember(xShow) {
        val st = xShow?.style() ?: return@remember Paint()
        buildLabelPaint(st, resolver, density)
    }
    val yLabelPaint = remember(yShow) {
        val st = yShow?.style() ?: return@remember Paint()
        buildLabelPaint(st, resolver, density)
    }

    val yList by remember(minY, maxY, yShow) {
        mutableStateOf(
            if ((yShow?.labelCount() ?: 0) >= 2) generateSteps(minY, maxY, yShow!!.labelCount()) else emptyList()
        )
    }

    val bottomPadding by remember(xShow, xLabelPaint, yShow) {
        mutableStateOf(
            if (xShow?.style() != null) {
                val d = xLabelPaint.fontMetrics.descent
                xShow.style()!!.fontSize() + pxToDp(d, density) + xShow.textSpace()
            } else {
                (yShow?.style()?.fontSize()?.div(2) ?: 0.dp)
            }
        )
    }

    val startPadding by remember(yShow, yList, yLabelPaint) {
        mutableStateOf(
            if (yShow?.style() != null) {
                val w = yList.maxOfOrNull { v ->
                    yLabelPaint.measureText(String.format(yShow.formatter(), v))
                } ?: 0f
                pxToDp(w, density) + yShow.textSpace()
            } else 0.dp
        )
    }

    val topPadding by remember(yShow) {
        mutableStateOf(yShow?.style()?.fontSize()?.div(2) ?: 0.dp)
    }

    BoxWithConstraints(modifier = modifier) {
        val heightDp = with(LocalDensity.current) { constraints.maxHeight.toDp() }
        val containerWidthDp = with(LocalDensity.current) { constraints.maxWidth.toDp() }

        if (yShow != null && yList.size >= 2) {
            val sortedDesc = remember(yList) { yList.sortedDescending() }
            val stepCount = max(1, yShow.labelCount() - 1)
            val space = (heightDp - topPadding - bottomPadding) / stepCount

            if (space > 0.dp && yShow.style() != null) {
                repeat(yShow.labelCount()) { i ->
                    Box(
                        modifier = Modifier
                            .width(startPadding - yShow.textSpace())
                            .fillMaxHeight()
                            .align(Alignment.TopStart)
                    ) {
                        Text(
                            text = String.format(yShow.formatter(), sortedDesc[i]),
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = space * i),
                            style = TextStyle(
                                color = yShow.style()!!.color(),
                                fontFamily = yShow.style()!!.fontFamily(),
                                fontWeight = FontWeight(yShow.style()!!.fontWeight())
                            ),
                            fontSize = yShow.style()!!.fontSize().toSp()
                        )
                    }
                }
            }
        }

        val contentPad = Modifier.padding(
            top = (if (chartFrame.top is LineOption.Show) chartFrame.top.style.thickness() else 0.dp) + topPadding,
            start = (if (chartFrame.left is LineOption.Show) chartFrame.left.style.thickness() else 0.dp) + startPadding,
            end = if (chartFrame.right is LineOption.Show) chartFrame.right.style.thickness() else 0.dp
        )

        if (xAxisMode is XAxisMode.Scrollable) {
            val cnt = (maxX - minX) / xAxisMode.step
            val ratio = if (xAxisMode.visibleRange > 0f) cnt / xAxisMode.visibleRange else 1f
            val width = (containerWidthDp * ratio).coerceAtLeast(0.dp)

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .then(contentPad)
                    .clipToBounds()
                    .horizontalScroll(scrollState)
            ) {
                ChartCanvas(
                    modifier = Modifier
                        .width(width)
                        .fillMaxHeight(),
                    lines = lines,
                    minX = minX,
                    maxX = maxX,
                    minY = minY,
                    maxY = maxY,
                    xAxisOpt = xAxisOpt,
                    yAxisOpt = yAxisOpt,
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
                yAxisOpt = yAxisOpt,
                xLabelPaint = xLabelPaint,
                density = density,
                bottomPadding = bottomPadding
            )
        }

        with(chartFrame) {
            listOf(
                FrameSide.Left   to left,
                FrameSide.Right  to right,
                FrameSide.Top    to top,
                FrameSide.Bottom to bottom
            ).forEach { (side, opt) ->
                FrameSideDivider(
                    side = side,
                    opt = opt,
                    startPadding = startPadding,
                    topPadding = topPadding,
                    bottomPadding = bottomPadding
                )
            }
        }
    }
}

@Composable
private fun BoxScope.FrameSideDivider(
    side: FrameSide,
    opt: LineOption,
    startPadding: Dp,
    topPadding: Dp,
    bottomPadding: Dp
) {
    val show = opt as? LineOption.Show ?: return
    val style = show.style
    val isVertical = side == FrameSide.Left || side == FrameSide.Right

    val align = when (side) {
        FrameSide.Left   -> Alignment.CenterStart
        FrameSide.Right  -> Alignment.CenterEnd
        FrameSide.Top    -> Alignment.TopCenter
        FrameSide.Bottom -> Alignment.BottomCenter
    }
    val mod = when (side) {
        FrameSide.Left   -> Modifier.align(align).padding(start = startPadding, top = topPadding, bottom = bottomPadding)
        FrameSide.Right  -> Modifier.align(align).padding(top = topPadding, bottom = bottomPadding)
        FrameSide.Top    -> Modifier.align(align).padding(start = startPadding, top = topPadding)
        FrameSide.Bottom -> Modifier.align(align).padding(start = startPadding, bottom = bottomPadding)
    }

    when (val pattern = style.pattern()) {
        is LinePattern.Dashed -> {
            val dash = pattern.lineDashLength()
            val gap  = pattern.lineGapLength()
            if (isVertical) {
                DashedDivider(
                    modifier = mod,
                    orientation = DividerOrientation.Vertical,
                    thickness = style.thickness(),
                    color = style.color(),
                    dashLength = dash,
                    gapLength = gap
                )
            } else {
                DashedDivider(
                    modifier = mod,
                    orientation = DividerOrientation.Horizontal,
                    thickness = style.thickness(),
                    color = style.color(),
                    dashLength = dash,
                    gapLength = gap
                )
            }
        }
        else -> {
            if (isVertical) {
                VerticalDivider(thickness = style.thickness(), color = style.color(), modifier = mod)
            } else {
                HorizontalDivider(thickness = style.thickness(), color = style.color(), modifier = mod)
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
    yAxisOpt: YAxisOption,
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
            (xAxisOpt.option as? ShowX)?.let { opt ->
                opt.labelIndices().forEach { (xVal, text) ->
                    val x = ((xVal.toFloat() - minX) / (maxX - minX)).coerceIn(0f, 1f) * chartW
                    opt.gridLine()?.let { grid ->
                        drawGridLineY(
                            x = x,
                            chartH = chartH - dpToPx(bottomPadding, density),
                            gridStyle = grid,
                            lineWidth = dpToPx(grid.thickness(), density),
                            density = density
                        )
                    }
                    opt.style()?.let { labelStyle ->
                        val textWidth = xLabelPaint.measureText(text)
                        drawContext.canvas.nativeCanvas.drawText(
                            text,
                            x - textWidth / 2,
                            h - dpToPx(labelStyle.fontSize(), density) / 2,
                            xLabelPaint
                        )
                    }
                }
            }

            (yAxisOpt.option as? ShowY)?.let { opt ->
                val gridLine = yAxisOpt.option.gridLine()
                if (gridLine != null && opt.labelCount() > 2) {
                    val space = (chartH - dpToPx(bottomPadding, density)) / (opt.labelCount() - 1)
                    repeat(opt.labelCount() - 2) { idx ->
                        drawGridLineX(
                            y = space * (idx + 1),
                            chartW = chartW,
                            gridStyle = gridLine,
                            lineWidth = dpToPx(gridLine.thickness(), density),
                            density = density
                        )
                    }
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
    val strokePx = dpToPx(style.thickness(), density)

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
            color = style.color(),
            style = Stroke(
                width = strokePx,
                cap = if (style.isRoundCap()) StrokeCap.Round else StrokeCap.Butt,
                pathEffect = style.pattern().toPathEffect(density)
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
        color = gridStyle.color(),
        start = Offset(x, 0f),
        end = Offset(x, chartH),
        strokeWidth = lineWidth,
        pathEffect = gridStyle.pattern().toPathEffect(density)
    )
}

fun DrawScope.drawGridLineX(
    y: Float,
    chartW: Float,
    gridStyle: GridLineStyle,
    lineWidth: Float,
    density: Density
) {
    drawLine(
        color = gridStyle.color(),
        start = Offset(0f, y),
        end = Offset(chartW, y),
        strokeWidth = lineWidth,
        pathEffect = gridStyle.pattern().toPathEffect(density)
    )
}

fun LinePattern.toPathEffect(density: Density): PathEffect? = when (this) {
    is LinePattern.Dashed -> PathEffect.dashPathEffect(
        floatArrayOf(dpToPx(lineDashLength(), density), dpToPx(lineGapLength(), density)), 0f
    )
    else -> null
}

@Composable
fun Dp.toSp(): TextUnit = with(LocalDensity.current) { toSp() }

fun dpToPx(dp: Dp, density: Density): Float = with(density) { dp.toPx() }
fun pxToDp(px: Float, density: Density): Dp = with(density) { px.toDp() }

fun generateSteps(min: Float, max: Float, step: Int): List<Float> {
    require(step >= 2)
    if (step == 2) return listOf(min, max)
    val interval = (max - min) / (step - 1)
    return List(step) { i -> min + interval * i }
}

private fun buildLabelPaint(
    style: LabelStyle,
    resolver: FontFamily.Resolver,
    density: Density
): Paint = Paint().apply {
    typeface = resolver.resolve(style.fontFamily(), FontWeight.Normal, FontStyle.Normal, FontSynthesis.All).value as android.graphics.Typeface
    textSize = dpToPx(style.fontSize(), density)
    color = style.color().toArgb()
    isFakeBoldText = style.fontWeight() > 500
}

