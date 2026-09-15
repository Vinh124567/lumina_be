package com.lumina.backend.repository

import com.lumina.backend.model.entity.DailyWisdom
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DailyWisdomRepository : JpaRepository<DailyWisdom, Long>
