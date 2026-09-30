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
    var isMastered: Boolean = false,

    @Column(name = "user_id", nullable = true)
    var userId: Long? = null,

    @Column(name = "srs_repetition", columnDefinition = "integer default 0")
    var srsRepetition: Int = 0,

    @Column(name = "srs_interval_days", columnDefinition = "integer default 0")
    var srsIntervalDays: Int = 0,

    @Column(name = "srs_ease_factor", columnDefinition = "real default 2.5")
    var srsEaseFactor: Float = 2.5f,

    @Column(name = "next_review_time_millis", columnDefinition = "bigint default 0")
    var nextReviewTimeMillis: Long = 0L,

    @Column(name = "last_review_time_millis", columnDefinition = "bigint default 0")
    var lastReviewTimeMillis: Long = 0L
)
