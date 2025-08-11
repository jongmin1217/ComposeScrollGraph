package com.bellmin.scrollablegraph.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class XAxisOption(
    val option : XLabelOption = XLabelOption.Hide
)

sealed class XLabelOption{
    object Hide : XLabelOption()
    data class Show(
        val labelIndices: List<Pair<Double, String>> = listOf(),
        val style: LabelStyle? = null,
        val gridLine: GridLineStyle? = null,
        val textSpace : Dp = 10.dp
    ) : XLabelOption()
}

