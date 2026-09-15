package com.lumina.backend.model.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "topics")
data class Topic(
    @Id
    val id: String,

    @Column(nullable = false, length = 100)
    val name: String,

    @Column(nullable = false, length = 20)
    val icon: String,

    @Column(nullable = false)
    val orderIndex: Int = 0
)
