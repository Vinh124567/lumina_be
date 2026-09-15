package com.lumina.backend.repository

import com.lumina.backend.model.entity.Topic
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TopicRepository : JpaRepository<Topic, String> {
    fun findAllByOrderByOrderIndexAsc(): List<Topic>
}
