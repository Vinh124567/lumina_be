package com.lumina.backend.repository

import com.lumina.backend.model.entity.HskLevel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface HskLevelRepository : JpaRepository<HskLevel, String> {
    fun findAllByOrderByOrderIndexAsc(): List<HskLevel>
}
