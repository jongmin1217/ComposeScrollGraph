package com.bellmin.scrollablegraph.data

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bellmin.scrollablegraph.utils.colorFromRes

data class GridLineStyle(
    val color: Color = Color.Black,
    val thickness: Dp = 2.dp,
    val pattern: LinePattern = LinePattern.Dashed()
)

data class GraphGridLineStyle(
    val color: Int,
    val thickness: Int = 2,
    val pattern: GraphLinePattern = GraphLinePattern.Dashed(),
    val context : Context
){
    @Composable
    fun toGridLineStyle() = GridLineStyle(
        color = colorFromRes(context, this.color),
        thickness = this.thickness.dp,
        pattern = this.pattern.toLinePattern()
    )
}