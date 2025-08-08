package com.bellmin.scrollablegraph

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bellmin.scrollablegraph.data.DividerOrientation

@Composable
fun DashedDivider(
    modifier: Modifier = Modifier,
    orientation: DividerOrientation = DividerOrientation.Horizontal,
    color: Color = MaterialTheme.colorScheme.outlineVariant,
    thickness: Dp = 1.dp,
    dashLength: Dp = 6.dp,
    gapLength: Dp = 4.dp,
    startIndent: Dp = 0.dp,   // Horizontal: left,  Vertical: top
    endIndent: Dp = 0.dp,     // Horizontal: right, Vertical: bottom
    cap: StrokeCap = StrokeCap.Round
) {
    val density = LocalDensity.current
    val strokePx = with(density) { thickness.toPx() }
    val dashPx = with(density) { dashLength.toPx() }
    val gapPx = with(density) { gapLength.toPx() }
    val startPx = with(density) { startIndent.toPx() }
    val endPx = with(density) { endIndent.toPx() }

    val sizedModifier = when (orientation) {
        DividerOrientation.Horizontal -> modifier.fillMaxWidth().height(thickness)
        DividerOrientation.Vertical   -> modifier.fillMaxHeight().width(thickness)
    }

    Box(
        modifier = sizedModifier.drawBehind {
            clipRect(0f, 0f, size.width, size.height) {
                when (orientation) {
                    DividerOrientation.Horizontal -> {
                        val y = size.height / 2f
                        val x0 = startPx.coerceAtLeast(0f)
                        val x1 = (size.width - endPx).coerceAtLeast(x0)
                        drawLine(
                            color = color,
                            start = Offset(x0, y),
                            end = Offset(x1, y),
                            strokeWidth = strokePx,
                            cap = cap,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashPx, gapPx), 0f)
                        )
                    }
                    DividerOrientation.Vertical -> {
                        val x = size.width / 2f
                        val y0 = startPx.coerceAtLeast(0f)
                        val y1 = (size.height - endPx).coerceAtLeast(y0)
                        drawLine(
                            color = color,
                            start = Offset(x, y0),
                            end = Offset(x, y1),
                            strokeWidth = strokePx,
                            cap = cap,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashPx, gapPx), 0f)
                        )
                    }
                }
            }
        }
    )
}

/** 가로 점선 Divider (Material3 HorizontalDivider 대체용) */
@Composable
fun DashedHorizontalDivider(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.outlineVariant,
    thickness: Dp = 1.dp,
    dashLength: Dp = 6.dp,
    gapLength: Dp = 4.dp,
    startIndent: Dp = 0.dp,
    endIndent: Dp = 0.dp,
    cap: StrokeCap = StrokeCap.Butt
) = DashedDivider(
    modifier = modifier,
    orientation = DividerOrientation.Horizontal,
    color = color,
    thickness = thickness,
    dashLength = dashLength,
    gapLength = gapLength,
    startIndent = startIndent,
    endIndent = endIndent,
    cap = cap
)

/** 세로 점선 Divider */
@Composable
fun DashedVerticalDivider(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.outlineVariant,
    thickness: Dp = 1.dp,
    dashLength: Dp = 6.dp,
    gapLength: Dp = 4.dp,
    topIndent: Dp = 0.dp,
    bottomIndent: Dp = 0.dp,
    cap: StrokeCap = StrokeCap.Butt
) = DashedDivider(
    modifier = modifier,
    orientation = DividerOrientation.Vertical,
    color = color,
    thickness = thickness,
    dashLength = dashLength,
    gapLength = gapLength,
    startIndent = topIndent,
    endIndent = bottomIndent,
    cap = cap
)