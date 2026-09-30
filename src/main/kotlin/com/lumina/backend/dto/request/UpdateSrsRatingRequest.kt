package com.lumina.backend.dto.request

data class UpdateSrsRatingRequest(
    val repetition: Int = 0,
    val intervalDays: Int = 0,
    val easeFactor: Float = 2.5f,
    val nextReviewTimeMillis: Long = 0L,
    val lastReviewTimeMillis: Long = 0L
)
