package com.lumina.backend.model.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "hsk_levels")
data class HskLevel(
    @Id
    val id: String,

    @Column(nullable = false, length = 100)
    val title: String,

    @Column(nullable = true, length = 100)
    val scoreRange: String? = null,

    @Column(nullable = false)
    val orderIndex: Int = 0
)
