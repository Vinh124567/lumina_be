package com.lumina.backend.dto.request

import jakarta.validation.constraints.NotBlank

data class CreateVocabularyRequest(
    @field:NotBlank(message = "Chữ Hán không được để trống")
    val hanzi: String,

    @field:NotBlank(message = "Phiên âm Pinyin không được để trống")
    val pinyin: String,

    val hanViet: String? = "",

    @field:NotBlank(message = "Nghĩa tiếng Việt không được để trống")
    val meaning: String,

    val hskLevel: String? = "HSK 1",

    val topic: String? = "Đời sống & Xã hội",

    val partOfSpeech: String? = "Danh từ",

    val radical: String? = "",

    val strokes: String? = "",

    val exampleHanzi: String? = "",

    val examplePinyin: String? = "",

    val exampleMeaning: String? = "",

    val note: String? = ""
)
