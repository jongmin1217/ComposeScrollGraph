package com.bellmin.composescrollgraph.data

import com.google.gson.annotations.SerializedName

data class Stt(
    @SerializedName("resultCode")
    val resultCode: String,
    @SerializedName("resultTime")
    val resultTime: Long,
    @SerializedName("resultData")
    val resultData: SttResult,
    @SerializedName("resultMsg")
    val resultMsg: String,
)

data class SttResult(
    @SerializedName("result")
    val result: Map<String, SttItem>
)

data class SttItem(
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("partial")
    val partial: String? = null,
    @SerializedName("result")
    val resultText: String? = null,
    @SerializedName("start")
    val start: String? = null,
    @SerializedName("end")
    val end: String? = null,
    @SerializedName("similarity")
    val similarity: Double? = null,
    @SerializedName("noisePossibility")
    val noisePossibility: String? = null,
    @SerializedName("REF")
    val ref: List<String>? = null,
    @SerializedName("HYP")
    val hyp: List<String>? = null,
    @SerializedName("TAG")
    val tag: List<String>? = null,
    @SerializedName("ERR")
    val err: Int? = null,
    @SerializedName("intonation_time")
    val intonationTime: List<Double>? = null,
    @SerializedName("intonation_data")
    val intonationData: List<Double>? = null,
    @SerializedName("energy_time")
    val energyTime: List<Double>? = null,
    @SerializedName("energy_data")
    val energyData: List<Double>? = null,
    @SerializedName("phones")
    val phones: List<PhoneInfo>? = null,
    @SerializedName("words")
    val words: List<WordInfo>? = null,
    @SerializedName("dictionary")
    val dictionary: List<List<String>>? = null,
    @SerializedName("GOP")
    val gop: GopInfo? = null,
    @SerializedName("intonation_score")
    val intonationScore: Double? = null,
    @SerializedName("speechrate_score")
    val speechrateScore: Double? = null
)

data class PhoneInfo(
    @SerializedName("s")
    val start: Double? = null,
    @SerializedName("e")
    val end: Double? = null,
    @SerializedName("t")
    val type: String? = null,
    @SerializedName("f1")
    val f1: Double? = null,
    @SerializedName("f2")
    val f2: Double? = null
)

data class WordInfo(
    @SerializedName("s")
    val start: Double? = null,
    @SerializedName("e")
    val end: Double? = null,
    @SerializedName("t")
    val text: String? = null,
    @SerializedName("f1")
    val f1: Double? = null,
    @SerializedName("f2")
    val f2: Double? = null
)

data class GopInfo(
    @SerializedName("phones")
    val phones: List<GopPhone>? = null,
    @SerializedName("dictionary")
    val dictionary: List<Any>? = null
)

data class GopPhone(
    @SerializedName("phone")
    val phone: String? = null,
    @SerializedName("gop")
    val gop: Int? = null,
    @SerializedName("overlap")
    val overlap: Int? = null,
    @SerializedName("frame")
    val frame: Int? = null
)

