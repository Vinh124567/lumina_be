package com.lumina.backend.model.entity

import jakarta.persistence.*

@Entity
@Table(name = "daily_wisdoms")
data class DailyWisdom(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, length = 200)
    val chinese: String,

    @Column(nullable = false, length = 250)
    val pinyin: String,

    @Column(nullable = false, length = 500)
    val vietnamese: String,

    @Column(nullable = false, length = 500)
    val meaning: String = "",

    @Column(nullable = false, length = 100)
    val author: String
)
