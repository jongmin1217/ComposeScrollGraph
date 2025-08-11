package com.bellmin.scrollablegraph.data

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bellmin.scrollablegraph.utils.colorFromRes

data class ChartLine(
    val label: String,
    val points: List<Pair<Float, Float>>,
    val style: LineStyle
)

data class LineStyle(
    val color: Color,
    val thickness: Dp,
    val isRoundCap: Boolean,
    val pattern: LinePattern = LinePattern.Solid
)

sealed class LinePattern {
    object Solid : LinePattern()
    data class Dashed(val dashLength: Dp = 6.dp, val gapLength: Dp = 4.dp) : LinePattern()
}

data class GraphChartLine(
    val label: String,
    val points: List<Pair<Float, Float>>,
    val style: GraphLineStyle
){
    @Composable
    fun toChartLine() = ChartLine(
        label = this.label,
        points = this.points,
        style = this.style.toLineStyle()
    )
}

data class GraphLineStyle(
    val color: Int,
    val thickness: Int,
    val isRoundCap: Boolean,
    val pattern: GraphLinePattern = GraphLinePattern.Solid,
    val context : Context
){
    @Composable
    fun toLineStyle() = LineStyle(
        color = colorFromRes(context, this.color),
        thickness = this.thickness.dp,
        isRoundCap = this.isRoundCap,
        pattern = this.pattern.toLinePattern()
    )
}

sealed class GraphLinePattern {
    object Solid : GraphLinePattern()
    data class Dashed(val dashLength: Int = 6, val gapLength: Int = 4) : GraphLinePattern()

    fun toLinePattern() : LinePattern{
        return if(this is Dashed) LinePattern.Dashed(this.dashLength.dp, this.gapLength.dp)
        else LinePattern.Solid
    }
}
