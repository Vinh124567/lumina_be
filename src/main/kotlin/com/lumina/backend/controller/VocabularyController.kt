package com.lumina.backend.controller

import com.lumina.backend.dto.response.ApiResponse
import com.lumina.backend.dto.response.TopicResponse
import com.lumina.backend.dto.response.VocabularyResponse
import com.lumina.backend.service.VocabularyService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/vocabularies")
@CrossOrigin(origins = ["*"])
class VocabularyController(
    private val vocabularyService: VocabularyService
) {

    @GetMapping("/levels")
    fun getLevels(): ResponseEntity<ApiResponse<List<com.lumina.backend.dto.response.HskLevelResponse>>> {
        val levels = vocabularyService.getHskLevels()
        return ResponseEntity.ok(ApiResponse.success(levels, "Lấy danh sách cấp độ từ vựng thành công"))
    }

    @GetMapping("/topics")
    fun getTopics(
        @RequestParam(required = false) hskLevel: String?
    ): ResponseEntity<ApiResponse<List<TopicResponse>>> {
        val topics = vocabularyService.getTopics(hskLevel)
        return ResponseEntity.ok(ApiResponse.success(topics, "Lấy danh sách chủ đề từ vựng thành công"))
    }

    @GetMapping
    fun getVocabularies(
        @RequestParam(required = false) topic: String?,
        @RequestParam(required = false) hskLevel: String?
    ): ResponseEntity<ApiResponse<List<VocabularyResponse>>> {
        val vocabularies = vocabularyService.getVocabularies(topic, hskLevel)
        return ResponseEntity.ok(ApiResponse.success(vocabularies, "Lấy danh sách từ vựng thành công"))
    }

    @GetMapping("/{id}")
    fun getVocabularyById(@PathVariable id: Long): ResponseEntity<ApiResponse<VocabularyResponse>> {
        val vocab = vocabularyService.getVocabularyById(id)
        return ResponseEntity.ok(ApiResponse.success(vocab, "Lấy chi tiết từ vựng thành công"))
    }

    @PostMapping
    fun createVocabulary(
        @jakarta.validation.Valid @RequestBody request: com.lumina.backend.dto.request.CreateVocabularyRequest
    ): ResponseEntity<ApiResponse<VocabularyResponse>> {
        val created = vocabularyService.createVocabulary(request)
        return ResponseEntity.ok(ApiResponse.success(created, "Thêm từ vựng mới thành công"))
    }

    @GetMapping("/ai-lookup")
    fun aiLookup(
        @RequestParam query: String
    ): ResponseEntity<ApiResponse<VocabularyResponse>> {
        val result = vocabularyService.aiLookup(query)
        return ResponseEntity.ok(ApiResponse.success(result, "Tra cứu thông tin từ vựng thành công"))
    }

    @PutMapping("/{id}/toggle-mastered")
    fun toggleMastered(@PathVariable id: Long): ResponseEntity<ApiResponse<VocabularyResponse>> {
        val updated = vocabularyService.toggleMastered(id)
        return ResponseEntity.ok(ApiResponse.success(updated, "Cập nhật trạng thái từ vựng thành công"))
    }
}
