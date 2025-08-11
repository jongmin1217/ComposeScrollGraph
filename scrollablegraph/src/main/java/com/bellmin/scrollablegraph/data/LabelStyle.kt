package com.bellmin.scrollablegraph.data

import android.content.Context
import android.os.Build
import androidx.annotation.FontRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.Typeface
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bellmin.scrollablegraph.utils.colorFromRes

interface LabelStyle{
    fun labelColor(): Color
    fun labelFontSize(): Dp
    fun labelFontFamily(): FontFamily?
    fun labelFontWeight(): Int
}

fun LabelStyle.color() = labelColor()
fun LabelStyle.fontSize() = labelFontSize()
fun LabelStyle.fontFamily() = labelFontFamily()
fun LabelStyle.fontWeight() = labelFontWeight()

data class LabelStyleDP(
    val color: Color = Color.Black,
    val fontSize: Dp = 20.dp,
    val fontFamily: FontFamily? = null,
    val fontWeight: Int = 700      // 400=normal, 700=bold 등
) : LabelStyle{
    override fun labelColor() = color
    override fun labelFontSize() = fontSize
    override fun labelFontFamily() = fontFamily
    override fun labelFontWeight() = fontWeight
}

data class LabelStyleInt(
    val color: Int,
    val fontSize: Int = 20,
    val fontFamily: List<GraphFontList>? = null,
    val fontWeight: Int = 700,
    val context : Context
): LabelStyle{
    override fun labelColor() = colorFromRes(context, color)
    override fun labelFontSize() = fontSize.dp
    override fun labelFontFamily() = fontFamily?.let {
        FontFamily(
            fontFamily.map{
                Font(it.resId, if(it.isBold) FontWeight.Bold else FontWeight.Normal)
            }
        )
    }
    override fun labelFontWeight() = fontWeight
}

data class GraphFontList(
    @FontRes val resId : Int,
    val isBold : Boolean
)

