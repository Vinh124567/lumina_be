package com.lumina.backend.controller

import com.lumina.backend.dto.response.ApiResponse
import com.lumina.backend.dto.response.DailyWisdomResponse
import com.lumina.backend.service.DailyWisdomService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/wisdom")
@CrossOrigin(origins = ["*"])
class DailyWisdomController(
    private val dailyWisdomService: DailyWisdomService
) {

    @GetMapping("/today")
    fun getTodayWisdom(): ResponseEntity<ApiResponse<DailyWisdomResponse>> {
        val wisdom = dailyWisdomService.getTodayWisdom()
        return ResponseEntity.ok(ApiResponse.success(wisdom, "Lấy thành ngữ hôm nay thành công"))
    }

    @GetMapping("/random")
    fun getRandomWisdom(): ResponseEntity<ApiResponse<DailyWisdomResponse>> {
        val wisdom = dailyWisdomService.getRandomWisdom()
        return ResponseEntity.ok(ApiResponse.success(wisdom, "Lấy thành ngữ ngẫu nhiên thành công"))
    }
}
