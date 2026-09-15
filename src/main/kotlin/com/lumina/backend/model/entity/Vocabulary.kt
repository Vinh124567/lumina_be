package com.lumina.backend.model.entity

import jakarta.persistence.*

@Entity
@Table(name = "vocabularies")
data class Vocabulary(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, length = 50)
    val hanzi: String,

    @Column(nullable = false, length = 100)
    val pinyin: String,

    @Column(nullable = false, length = 100)
    val hanViet: String,

    @Column(nullable = false, length = 500)
    val meaning: String,

    @Column(nullable = false, length = 50)
    val partOfSpeech: String,

    @Column(nullable = false, length = 100)
    val topic: String,

    @Column(nullable = false, length = 100)
    val radical: String,

    @Column(nullable = false, length = 50)
    val strokes: String,

    @Column(nullable = false, length = 500)
    val exampleHanzi: String,

    @Column(nullable = false, length = 500)
    val examplePinyin: String,

    @Column(nullable = false, length = 500)
    val exampleMeaning: String,

    @Column(nullable = false, length = 50)
    val hskLevel: String = "HSK 1",

    @Column(nullable = false, length = 200)
    val targetScore: String = "HSK 1 (Mục tiêu 180–200/200 điểm)",

    @Column(nullable = false)
    var isMastered: Boolean = false
)
