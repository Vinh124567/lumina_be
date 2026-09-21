package com.lumina.backend.repository

import com.lumina.backend.model.entity.Vocabulary
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

@Repository
interface VocabularyRepository : JpaRepository<Vocabulary, Long> {
    fun findByTopic(topic: String): List<Vocabulary>
    fun findByHskLevel(hskLevel: String): List<Vocabulary>
    fun findByTopicAndHskLevel(topic: String, hskLevel: String): List<Vocabulary>

    @Query("SELECT v FROM Vocabulary v WHERE v.userId IS NULL OR v.userId = :userId")
    fun findAllSystemAndUserVocabularies(@Param("userId") userId: Long): List<Vocabulary>

    fun findAllByUserIdIsNull(): List<Vocabulary>
}
