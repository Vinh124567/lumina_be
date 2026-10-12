package com.lumina.backend.repository

import com.lumina.backend.model.entity.GrammarStructureEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GrammarStructureRepository : JpaRepository<GrammarStructureEntity, String> {
    fun findByHskLevelIgnoreCaseOrderByStructureOrderAsc(hskLevel: String): List<GrammarStructureEntity>
    fun findAllByOrderByHskLevelAscStructureOrderAsc(): List<GrammarStructureEntity>
}
