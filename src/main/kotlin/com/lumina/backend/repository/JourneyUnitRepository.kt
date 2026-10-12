package com.lumina.backend.repository

import com.lumina.backend.model.entity.JourneyUnit
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JourneyUnitRepository : JpaRepository<JourneyUnit, Long> {
    fun findByHskLevelOrderByUnitNumberAsc(hskLevel: String): List<JourneyUnit>
}
