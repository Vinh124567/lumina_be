package com.lumina.backend.model.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "user_chest_claims")
class ChestClaim(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false)
    var unitNumber: Int = 1,

    @Column(nullable = false, length = 30)
    var hskLevel: String = "HSK 1",

    @Column(nullable = false)
    var sparksEarned: Int = 50,

    @Column(nullable = false)
    var expEarned: Int = 100,

    @Column(nullable = false)
    var claimedAt: LocalDateTime = LocalDateTime.now()
)
