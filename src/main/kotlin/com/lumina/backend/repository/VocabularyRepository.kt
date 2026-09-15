package com.lumina.backend.repository

import com.lumina.backend.model.entity.Vocabulary
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface VocabularyRepository : JpaRepository<Vocabulary, Long> {
    fun findByTopic(topic: String): List<Vocabulary>
    fun findByHskLevel(hskLevel: String): List<Vocabulary>
    fun findByTopicAndHskLevel(topic: String, hskLevel: String): List<Vocabulary>
}
