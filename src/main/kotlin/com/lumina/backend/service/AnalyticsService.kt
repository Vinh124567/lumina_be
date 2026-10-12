package com.lumina.backend.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.lumina.backend.dto.response.AnalyticsSummaryResponse
import com.lumina.backend.dto.response.LessonVocabResponse
import com.lumina.backend.dto.response.SkillRadarScoreResponse
import com.lumina.backend.repository.LessonRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AnalyticsService(
    private val lessonRepository: LessonRepository
) {
    private val objectMapper = jacksonObjectMapper()

    @Transactional(readOnly = true)
    fun getUserAnalytics(userId: String = "default_user"): AnalyticsSummaryResponse {
        val allLessons = lessonRepository.findAllWithSlides()
        val completedLessons = allLessons.filter { it.isCompleted }

        val totalCompleted = completedLessons.size
        val totalStars = completedLessons.sumOf { it.stars }

        // Đếm tổng số từ vựng nắm vững từ các bài học đã hoàn thành trong Database
        val masteredVocabs = completedLessons.flatMap { lesson ->
            try {
                if (lesson.coreVocabulariesJson.isNotBlank() && lesson.coreVocabulariesJson != "[]") {
                    objectMapper.readValue<List<LessonVocabResponse>>(lesson.coreVocabulariesJson)
                } else emptyList()
            } catch (_: Exception) {
                emptyList()
            }
        }.distinctBy { it.hanzi }.size

        // Tính tỉ lệ phần trăm chính xác (Accuracy)
        val maxPossibleStars = maxOf(1, totalCompleted * 3)
        val accuracyPercentage = if (totalCompleted > 0) {
            ((totalStars.toDouble() / maxPossibleStars) * 100).toInt().coerceIn(60, 100)
        } else {
            85
        }

        // Tính toán chỉ số Radar 5 cánh động dựa trên số bài hoàn thành theo từng Stage
        val foundationCount = completedLessons.count { it.stage == "FOUNDATION" }
        val grammarCount = completedLessons.count { it.stage == "GRAMMAR" }
        val conversationCount = completedLessons.count { it.stage == "CONVERSATION" }

        val phoneticsScore = (70 + foundationCount * 10).coerceIn(65, 98)
        val vocabularyScore = (68 + (masteredVocabs * 2)).coerceIn(60, 95)
        val grammarScore = (65 + grammarCount * 12).coerceIn(60, 96)
        val dialogueScore = (72 + conversationCount * 10).coerceIn(65, 94)
        val accuracyScore = accuracyPercentage.coerceIn(70, 99)

        val radarScores = SkillRadarScoreResponse(
            phonetics = phoneticsScore,
            vocabulary = vocabularyScore,
            grammar = grammarScore,
            dialogue = dialogueScore,
            accuracy = accuracyScore
        )

        return AnalyticsSummaryResponse(
            totalLessonsCompleted = totalCompleted,
            totalStars = totalStars,
            masteredVocabsCount = masteredVocabs,
            accuracyPercentage = accuracyPercentage,
            radarScores = radarScores
        )
    }
}
