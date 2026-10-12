package com.lumina.backend.model.entity

import jakarta.persistence.*

@Entity
@Table(name = "journey_units")
class JourneyUnit(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false, length = 30)
    var hskLevel: String = "HSK 1",

    @Column(nullable = false)
    var unitNumber: Int = 1,

    @Column(nullable = false, length = 100)
    var title: String = "",

    @Column(nullable = false, columnDefinition = "TEXT")
    var description: String = "",

    @Column(nullable = false, length = 20)
    var icon: String = "🗣️",

    @Column(nullable = false, length = 20)
    var gradientStart: String = "#4F46E5",

    @Column(nullable = false, length = 20)
    var gradientEnd: String = "#6366F1"
)
