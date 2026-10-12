package com.lumina.backend.repository

import com.lumina.backend.model.entity.UserHeart
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserHeartRepository : JpaRepository<UserHeart, Long> {
    fun findByUserId(userId: String): UserHeart?
}
