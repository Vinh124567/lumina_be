package com.lumina.backend.controller

import com.lumina.backend.dto.response.ApiResponse
import com.lumina.backend.dto.response.LessonResponse
import com.lumina.backend.service.LessonService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/lessons")
@CrossOrigin(origins = ["*"])
class LessonController(
    private val lessonService: LessonService
) {

    @GetMapping
    fun getAllLessons(
        @RequestParam(required = false) level: String?,
        @RequestParam(required = false) category: String?
    ): ResponseEntity<ApiResponse<List<LessonResponse>>> {
        val lessons = lessonService.getAllLessons(level, category)
        return ResponseEntity.ok(ApiResponse.success(lessons, "Lấy danh sách tất cả bài học thành công"))
    }

    @GetMapping("/recommended")
    fun getRecommendedLessons(): ResponseEntity<ApiResponse<List<LessonResponse>>> {
        val lessons = lessonService.getRecommendedLessons()
        return ResponseEntity.ok(ApiResponse.success(lessons, "Lấy danh sách bài học đề xuất thành công"))
    }

    @GetMapping("/{id}")
    fun getLessonById(@PathVariable id: String): ResponseEntity<ApiResponse<LessonResponse>> {
        val lesson = lessonService.getLessonById(id)
        return ResponseEntity.ok(ApiResponse.success(lesson, "Lấy chi tiết bài học thành công"))
    }

    @PostMapping("/{id}/complete")
    fun completeLesson(
        @PathVariable id: String,
        @RequestParam(defaultValue = "3") stars: Int
    ): ResponseEntity<ApiResponse<LessonResponse>> {
        val lesson = lessonService.completeLesson(id, stars)
        return ResponseEntity.ok(ApiResponse.success(lesson, "Hoàn thành bài học thành công"))
    }

    @GetMapping("/{id}/review-questions")
    fun getReviewQuestions(
        @PathVariable id: String
    ): ResponseEntity<ApiResponse<List<com.lumina.backend.dto.response.QuickReviewQuestionResponse>>> {
        val questions = lessonService.getReviewQuestions(id)
        return ResponseEntity.ok(ApiResponse.success(questions, "Lấy danh sách câu hỏi ôn tập thành công"))
    }

    @PostMapping("/{id}/review")
    fun submitReview(
        @PathVariable id: String
    ): ResponseEntity<ApiResponse<com.lumina.backend.dto.response.ReviewResultResponse>> {
        val result = lessonService.submitReview(id)
        return ResponseEntity.ok(ApiResponse.success(result, "Hoàn thành ôn tập bài học thành công"))
    }

    @GetMapping("/exam")
    fun getExamQuestions(
        @RequestParam(defaultValue = "HSK 1") level: String
    ): ResponseEntity<ApiResponse<List<com.lumina.backend.dto.response.ExamQuestionResponse>>> {
        val questions = lessonService.getExamQuestions(level)
        return ResponseEntity.ok(ApiResponse.success(questions, "Lấy danh sách câu hỏi thi vượt cấp thành công"))
    }

    @PostMapping("/units/{unitNumber}/claim-chest")
    fun claimChestReward(
        @PathVariable unitNumber: Int
    ): ResponseEntity<ApiResponse<com.lumina.backend.dto.response.ChestRewardResponse>> {
        val reward = lessonService.claimChestReward(unitNumber)
        return ResponseEntity.ok(ApiResponse.success(reward, "Mở rương phần thưởng thành công"))
    }

    @GetMapping("/units")
    fun getJourneyUnits(
        @RequestParam(defaultValue = "HSK 1") level: String
    ): ResponseEntity<ApiResponse<List<com.lumina.backend.dto.response.JourneyUnitResponse>>> {
        val units = lessonService.getJourneyUnits(level)
        return ResponseEntity.ok(ApiResponse.success(units, "Lấy danh sách các Unit thành công"))
    }

    @GetMapping("/grammar")
    fun getGrammarStructures(
        @RequestParam(required = false) level: String?
    ): ResponseEntity<ApiResponse<List<com.lumina.backend.dto.response.GrammarStructureResponse>>> {
        val grammars = lessonService.getGrammarStructuresByLevel(level)
        return ResponseEntity.ok(ApiResponse.success(grammars, "Lấy danh sách cấu trúc ngữ pháp thành công"))
    }

    @PostMapping("/grammar/sync")
    fun syncGrammarStructures(
        @RequestParam(defaultValue = "false") force: Boolean
    ): ResponseEntity<ApiResponse<Map<String, Any>>> {
        val count = lessonService.syncGrammarFromResource(force)
        return ResponseEntity.ok(ApiResponse.success(mapOf("count" to count), "Đồng bộ $count cấu trúc ngữ pháp vào Database thành công"))
    }

    @PostMapping("/sync")
    fun syncLessons(
        @RequestParam(defaultValue = "false") force: Boolean
    ): ResponseEntity<ApiResponse<Map<String, Any>>> {
        val count = lessonService.syncLessonsFromResource(force)
        return ResponseEntity.ok(ApiResponse.success(mapOf("count" to count), "Đồng bộ $count bài học vào Database thành công"))
    }
}

