package com.lumina.backend.controller

import com.lumina.backend.dto.response.ApiResponse
import com.lumina.backend.dto.response.HeartStatusResponse
import com.lumina.backend.service.HeartService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user/hearts")
@CrossOrigin(origins = ["*"])
class HeartController(
    private val heartService: HeartService
) {

    @GetMapping
    fun getHeartStatus(
        @RequestParam(defaultValue = "default_user") userId: String
    ): ResponseEntity<ApiResponse<HeartStatusResponse>> {
        val status = heartService.getHeartStatus(userId)
        return ResponseEntity.ok(ApiResponse.success(status, "Lấy thông tin trái tim thành công"))
    }

    @PostMapping("/consume")
    fun consumeHeart(
        @RequestParam(defaultValue = "default_user") userId: String
    ): ResponseEntity<ApiResponse<HeartStatusResponse>> {
        val status = heartService.consumeHeart(userId)
        return ResponseEntity.ok(ApiResponse.success(status, "Cập nhật tiêu hao trái tim"))
    }

    @PostMapping("/refill")
    fun refillHearts(
        @RequestParam(defaultValue = "default_user") userId: String
    ): ResponseEntity<ApiResponse<HeartStatusResponse>> {
        val status = heartService.refillHearts(userId)
        return ResponseEntity.ok(ApiResponse.success(status, "Nạp đầy trái tim thành công"))
    }
}
