package com.lumina.backend.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    @field:NotBlank(message = "Email không được để trống")
    @field:Email(message = "Email không hợp lệ")
    val email: String = "",

    @field:NotBlank(message = "Mật khẩu không được để trống")
    val password: String = ""
)

data class RefreshTokenRequest(
    @field:NotBlank(message = "Refresh token không được để trống")
    val refreshToken: String = ""
)
