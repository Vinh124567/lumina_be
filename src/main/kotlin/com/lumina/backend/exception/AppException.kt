package com.lumina.backend.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val code: String,
    val defaultMessage: String,
    val httpStatus: HttpStatus
) {
    // Chung (1000)
    SUCCESS("SUCCESS", "Thao tác thành công", HttpStatus.OK),
    INTERNAL_ERROR("INTERNAL_ERROR", "Đã xảy ra lỗi hệ thống nội bộ", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_REQUEST("INVALID_REQUEST", "Tham số yêu cầu không hợp lệ", HttpStatus.BAD_REQUEST),
    VALIDATION_ERROR("VALIDATION_ERROR", "Dữ liệu gửi lên không đúng định dạng", HttpStatus.BAD_REQUEST),
    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND", "Không tìm thấy tài nguyên yêu cầu", HttpStatus.NOT_FOUND),

    // Xác thực & Tài khoản (2000)
    UNAUTHORIZED("UNAUTHORIZED", "Chưa đăng nhập hoặc phiên làm việc không hợp lệ", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("FORBIDDEN", "Bạn không có quyền thực hiện thao tác này", HttpStatus.FORBIDDEN),
    INVALID_CREDENTIALS("INVALID_CREDENTIALS", "Email hoặc mật khẩu không chính xác", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED("TOKEN_EXPIRED", "Token đã hết hạn", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_INVALID("REFRESH_TOKEN_INVALID", "Refresh token không hợp lệ", HttpStatus.BAD_REQUEST),
    REFRESH_TOKEN_EXPIRED("REFRESH_TOKEN_EXPIRED", "Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_REVOKED("REFRESH_TOKEN_REVOKED", "Phiên đăng nhập đã bị thu hồi", HttpStatus.UNAUTHORIZED),
    USER_NOT_FOUND("USER_NOT_FOUND", "Không tìm thấy thông tin người dùng", HttpStatus.NOT_FOUND),
    EMAIL_ALREADY_EXISTS("EMAIL_ALREADY_EXISTS", "Email này đã được sử dụng", HttpStatus.CONFLICT),

    // Bài học (3000)
    LESSON_NOT_FOUND("LESSON_NOT_FOUND", "Không tìm thấy bài học", HttpStatus.NOT_FOUND),
    SLIDE_NOT_FOUND("SLIDE_NOT_FOUND", "Không tìm thấy slide bài học", HttpStatus.NOT_FOUND)
}

class AppException(
    val errorCode: ErrorCode,
    override val message: String = errorCode.defaultMessage,
    val details: Any? = null
) : RuntimeException(message)
