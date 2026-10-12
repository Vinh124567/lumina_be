package com.lumina.backend.model.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "user_hearts")
class UserHeart(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "user_id", nullable = false, unique = true)
    var userId: String = "default_user",

    @Column(name = "current_hearts", nullable = false)
    var currentHearts: Int = 5,

    @Column(name = "max_hearts", nullable = false)
    var maxHearts: Int = 5,

    @Column(name = "last_consumed_at")
    var lastConsumedAt: LocalDateTime? = null
)
