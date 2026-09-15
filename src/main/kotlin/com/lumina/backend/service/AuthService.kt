package com.lumina.backend.service

import com.lumina.backend.dto.request.LoginRequest
import com.lumina.backend.dto.request.RefreshTokenRequest
import com.lumina.backend.dto.response.AuthResponse
import com.lumina.backend.dto.response.UserResponse
import com.lumina.backend.exception.AppException
import com.lumina.backend.exception.ErrorCode
import com.lumina.backend.model.entity.RefreshToken
import com.lumina.backend.model.entity.User
import com.lumina.backend.repository.RefreshTokenRepository
import com.lumina.backend.repository.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val jwtService: JwtService,
    private val passwordEncoder: PasswordEncoder,
    @Value("\${jwt.refresh-expiration-ms}") private val refreshExpirationMs: Long
) {

    @Transactional
    fun login(request: LoginRequest): AuthResponse {
        val user = userRepository.findByEmail(request.email)
            .orElseThrow { AppException(ErrorCode.INVALID_CREDENTIALS) }

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw AppException(ErrorCode.INVALID_CREDENTIALS)
        }

        // Tạo JWT Access Token
        val accessToken = jwtService.generateToken(user.email)

        // Tạo & lưu Refresh Token
        val refreshToken = createRefreshToken(user)

        return AuthResponse(
            accessToken = accessToken,
            refreshToken = refreshToken.token,
            tokenType = "Bearer",
            expiresIn = jwtService.jwtExpirationMs / 1000,
            user = UserResponse(
                id = user.id ?: 0,
                email = user.email,
                fullName = user.fullName
            )
        )
    }

    @Transactional
    fun refreshToken(request: RefreshTokenRequest): AuthResponse {
        val tokenEntity = refreshTokenRepository.findByToken(request.refreshToken)
            .orElseThrow { AppException(ErrorCode.REFRESH_TOKEN_INVALID) }

        if (tokenEntity.revoked) {
            throw AppException(ErrorCode.REFRESH_TOKEN_REVOKED)
        }

        if (tokenEntity.expiryDate.isBefore(Instant.now())) {
            refreshTokenRepository.delete(tokenEntity)
            throw AppException(ErrorCode.REFRESH_TOKEN_EXPIRED)
        }

        val user = tokenEntity.user
            ?: throw AppException(ErrorCode.USER_NOT_FOUND)

        // Sinh access token mới
        val newAccessToken = jwtService.generateToken(user.email)

        return AuthResponse(
            accessToken = newAccessToken,
            refreshToken = tokenEntity.token,
            tokenType = "Bearer",
            expiresIn = jwtService.jwtExpirationMs / 1000,
            user = UserResponse(
                id = user.id ?: 0,
                email = user.email,
                fullName = user.fullName
            )
        )
    }

    private fun createRefreshToken(user: User): RefreshToken {
        // Xóa các refresh token cũ của user nếu muốn dọn dẹp hoặc cho phép phiên đơn
        refreshTokenRepository.deleteByUser(user)

        val token = RefreshToken(
            token = UUID.randomUUID().toString(),
            user = user,
            expiryDate = Instant.now().plusMillis(refreshExpirationMs),
            revoked = false
        )
        return refreshTokenRepository.save(token)
    }
}
