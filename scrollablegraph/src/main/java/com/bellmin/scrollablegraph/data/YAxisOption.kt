package com.bellmin.scrollablegraph.data

data class YAxisOption(
    val enabled: Boolean = false,   // Y축 라벨 표시 여부
    val labelCount: Int = 5,       // 분할 개수
    val min: Float? = null,        // 최소값(없으면 데이터 기준)
    val max: Float? = null         // 최대값(없으면 데이터 기준)
)
