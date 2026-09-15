package com.lumina.backend.controller

import com.lumina.backend.dto.request.LoginRequest
import com.lumina.backend.dto.request.RefreshTokenRequest
import com.lumina.backend.dto.response.ApiResponse
import com.lumina.backend.dto.response.AuthResponse
import com.lumina.backend.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = ["*"])
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<ApiResponse<AuthResponse>> {
        val authResponse = authService.login(request)
        return ResponseEntity.ok(ApiResponse.success(authResponse, "Đăng nhập thành công"))
    }

    @PostMapping("/refresh")
    fun refresh(@Valid @RequestBody request: RefreshTokenRequest): ResponseEntity<ApiResponse<AuthResponse>> {
        val authResponse = authService.refreshToken(request)
        return ResponseEntity.ok(ApiResponse.success(authResponse, "Làm mới token thành công"))
    }
}
