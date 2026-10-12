package com.lumina.backend.controller

import com.lumina.backend.dto.response.ApiResponse
import com.lumina.backend.dto.response.LeaderboardResponse
import com.lumina.backend.service.LeaderboardService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/leaderboard")
@CrossOrigin(origins = ["*"])
class LeaderboardController(
    private val leaderboardService: LeaderboardService
) {

    @GetMapping("/weekly")
    fun getWeeklyLeaderboard(
        @RequestParam(defaultValue = "default_user") userId: String
    ): ResponseEntity<ApiResponse<LeaderboardResponse>> {
        val leaderboard = leaderboardService.getWeeklyLeaderboard(userId)
        return ResponseEntity.ok(ApiResponse.success(leaderboard, "Lấy bảng xếp hạng tuần thành công"))
    }
}
