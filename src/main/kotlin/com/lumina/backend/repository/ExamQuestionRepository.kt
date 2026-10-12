package com.lumina.backend.repository

import com.lumina.backend.model.entity.ExamQuestion
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ExamQuestionRepository : JpaRepository<ExamQuestion, Long> {
    fun findByHskLevelOrderByOrderIndexAsc(hskLevel: String): List<ExamQuestion>
}
