package com.lumina.backend.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.lumina.backend.dto.response.DialogueLineResponse
import com.lumina.backend.dto.response.GrammarStructureResponse
import com.lumina.backend.dto.response.LessonResponse
import com.lumina.backend.dto.response.LessonVocabResponse
import com.lumina.backend.dto.response.SlideResponse
import com.lumina.backend.exception.AppException
import com.lumina.backend.exception.ErrorCode
import com.lumina.backend.model.entity.Lesson
import com.lumina.backend.repository.LessonRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LessonService(
    private val lessonRepository: LessonRepository
) {
    private val objectMapper = jacksonObjectMapper()

    @Transactional(readOnly = true)
    fun getRecommendedLessons(): List<LessonResponse> {
        val allLessons = lessonRepository.findAllRecommended()
        if (allLessons.isEmpty()) return emptyList()

        // Phân loại các bài học theo trụ cột lộ trình chuẩn HSK 1:
        // 1. Nhóm Nền tảng (Pinyin & Bộ thủ)
        val foundationLessons = allLessons.filter { it.stage == "FOUNDATION" }.sortedBy { it.orderIndex }
        // 2. Nhóm Giao tiếp tình huống thực tế
        val conversationLessons = allLessons.filter { it.stage == "CONVERSATION" }.sortedBy { it.orderIndex }
        // 3. Nhóm Cấu trúc câu & Ngữ pháp
        val grammarLessons = allLessons.filter { it.stage == "GRAMMAR" }.sortedBy { it.orderIndex }

        // Sử dụng ngày hiện tại (Epoch Day) làm seed xoay tua có quy tắc theo ngày
        val epochDay = java.time.LocalDate.now().toEpochDay().toInt()

        val dailyRecommended = mutableListOf<Lesson>()

        // Quy tắc 1: Mỗi ngày lấy 1 bài Nền tảng luân phiên theo lộ trình
        if (foundationLessons.isNotEmpty()) {
            val idx = Math.floorMod(epochDay, foundationLessons.size)
            dailyRecommended.add(foundationLessons[idx])
        }

        // Quy tắc 2: Mỗi ngày lấy 1 bài Giao tiếp luân phiên theo lộ trình
        if (conversationLessons.isNotEmpty()) {
            val idx = Math.floorMod(epochDay, conversationLessons.size)
            dailyRecommended.add(conversationLessons[idx])
        }

        // Quy tắc 3: Mỗi ngày lấy 1 bài Ngữ pháp / Mẫu câu luân phiên theo lộ trình
        if (grammarLessons.isNotEmpty()) {
            val idx = Math.floorMod(epochDay, grammarLessons.size)
            dailyRecommended.add(grammarLessons[idx])
        }

        // Đảm bảo luôn trả về ít nhất 2 bài học
        if (dailyRecommended.size < 2) {
            allLessons.filterNot { it in dailyRecommended }.take(2 - dailyRecommended.size).forEach {
                dailyRecommended.add(it)
            }
        }

        return dailyRecommended.map { mapToResponse(it, includeSlides = false) }
    }

    @Transactional(readOnly = true)
    fun getAllLessons(level: String? = null, category: String? = null): List<LessonResponse> {
        val allLessons = lessonRepository.findAllWithSlides()
        val filtered = allLessons.filter { lesson ->
            val matchLevel = level.isNullOrBlank() || level.equals("all", ignoreCase = true) || lesson.level.equals(level, ignoreCase = true)
            val matchCategory = category.isNullOrBlank() || category.equals("all", ignoreCase = true) || lesson.category.contains(category, ignoreCase = true)
            matchLevel && matchCategory
        }
        return filtered.map { mapToResponse(it, includeSlides = true) }
    }

    @Transactional(readOnly = true)
    fun getLessonById(id: String): LessonResponse {
        val lesson = lessonRepository.findById(id)
            .orElseThrow { AppException(ErrorCode.LESSON_NOT_FOUND, "Không tìm thấy bài học với ID: $id") }
        return mapToResponse(lesson, includeSlides = true)
    }

    private fun mapToResponse(lesson: Lesson, includeSlides: Boolean = true): LessonResponse {
        val slides = if (includeSlides) {
            lesson.slides.map { slide ->
                SlideResponse(
                    slideIndex = slide.slideIndex,
                    category = slide.category,
                    cardType = slide.cardType,
                    slideType = slide.slideType,
                    subTitle = slide.subTitle,
                    title = slide.title,
                    pinyinVariants = slide.pinyinVariants.toList(),
                    hanziVariants = slide.hanziVariants.toList(),
                    hanViet = slide.hanViet,
                    meaning = slide.meaning,
                    explanationText = slide.explanationText,
                    explanationSubtitle = slide.explanationSubtitle,
                    explanationContent = slide.explanationContent,
                    boxColor = slide.boxColor,
                    toneRules = slide.toneRules.map { com.lumina.backend.dto.response.ToneRuleResponse(it.number, it.text) },
                    audioUrl = slide.audioUrl,
                    question = slide.question,
                    options = slide.options.toList(),
                    correctAnswerIndex = slide.correctAnswerIndex,
                    quizExplanation = slide.quizExplanation,
                    currentIndex = slide.slideIndex,
                    totalCount = lesson.totalSlides
                )
            }
        } else {
            emptyList()
        }

        val grammarStructures = try {
            if (lesson.grammarStructuresJson.isNotBlank() && lesson.grammarStructuresJson != "[]") {
                objectMapper.readValue<List<GrammarStructureResponse>>(lesson.grammarStructuresJson)
            } else {
                emptyList()
            }
        } catch (_: Exception) {
            emptyList()
        }

        val dialogues = try {
            if (lesson.dialoguesJson.isNotBlank() && lesson.dialoguesJson != "[]") {
                objectMapper.readValue<List<DialogueLineResponse>>(lesson.dialoguesJson)
            } else {
                emptyList()
            }
        } catch (_: Exception) {
            emptyList()
        }

        val coreVocabularies = try {
            if (lesson.coreVocabulariesJson.isNotBlank() && lesson.coreVocabulariesJson != "[]") {
                objectMapper.readValue<List<LessonVocabResponse>>(lesson.coreVocabulariesJson)
            } else {
                emptyList()
            }
        } catch (_: Exception) {
            emptyList()
        }

        return LessonResponse(
            id = lesson.id,
            category = lesson.category,
            categoryBgColor = lesson.categoryBgColor,
            categoryTextColor = lesson.categoryTextColor,
            level = lesson.level,
            pinyinHanziTitle = lesson.pinyinHanziTitle,
            title = lesson.title,
            description = lesson.description,
            durationMins = lesson.durationMins,
            sparks = lesson.sparks,
            isCompleted = lesson.isCompleted,
            totalSlides = lesson.totalSlides,
            stage = lesson.stage,
            orderIndex = lesson.orderIndex,
            objectives = lesson.objectives.toList(),
            grammarStructures = grammarStructures,
            dialogueContext = lesson.dialogueContext,
            dialogues = dialogues,
            coreVocabularies = coreVocabularies,
            slides = slides
        )
    }
}
