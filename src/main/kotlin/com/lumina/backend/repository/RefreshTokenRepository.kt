package com.lumina.backend.repository

import com.lumina.backend.model.entity.RefreshToken
import com.lumina.backend.model.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface RefreshTokenRepository : JpaRepository<RefreshToken, Long> {
    fun findByToken(token: String): Optional<RefreshToken>
    
    @Modifying
    fun deleteByUser(user: User): Int
}
