package com.lumina.backend.controller

import com.lumina.backend.dto.response.AnalyticsSummaryResponse
import com.lumina.backend.dto.response.ApiResponse
import com.lumina.backend.service.AnalyticsService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user/analytics")
@CrossOrigin(origins = ["*"])
class AnalyticsController(
    private val analyticsService: AnalyticsService
) {

    @GetMapping
    fun getUserAnalytics(
        @RequestParam(defaultValue = "default_user") userId: String
    ): ResponseEntity<ApiResponse<AnalyticsSummaryResponse>> {
        val analytics = analyticsService.getUserAnalytics(userId)
        return ResponseEntity.ok(ApiResponse.success(analytics, "Lấy thống kê và phân tích kỹ năng thành công"))
    }
}
