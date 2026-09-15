package com.lumina.backend.repository

import com.lumina.backend.model.entity.Lesson
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface LessonRepository : JpaRepository<Lesson, String> {
    @Query("SELECT DISTINCT l FROM Lesson l LEFT JOIN FETCH l.slides WHERE l.isRecommended = true")
    fun findAllRecommended(): List<Lesson>

    @Query("SELECT DISTINCT l FROM Lesson l LEFT JOIN FETCH l.slides ORDER BY l.orderIndex ASC")
    fun findAllWithSlides(): List<Lesson>
}
