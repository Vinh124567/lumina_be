package com.lumina.backend.repository

import com.lumina.backend.model.entity.ChestClaim
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ChestClaimRepository : JpaRepository<ChestClaim, Long> {
    fun findByUnitNumberAndHskLevel(unitNumber: Int, hskLevel: String): List<ChestClaim>
}
