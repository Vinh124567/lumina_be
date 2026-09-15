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
}
