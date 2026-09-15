package com.lumina.backend.controller

import com.lumina.backend.dto.response.ApiResponse
import com.lumina.backend.exception.AppException
import com.lumina.backend.exception.ErrorCode
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(AppException::class)
    fun handleAppException(ex: AppException): ResponseEntity<ApiResponse<Any>> {
        return ResponseEntity.status(ex.errorCode.httpStatus)
            .body(
                ApiResponse(
                    success = false,
                    message = ex.message,
                    errorCode = ex.errorCode.code,
                    data = ex.details
                )
            )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationErrors(ex: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Map<String, String>>> {
        val errors = ex.bindingResult.fieldErrors.associate { it.field to (it.defaultMessage ?: "Giá trị không hợp lệ") }
        val firstMessage = errors.values.firstOrNull() ?: ErrorCode.VALIDATION_ERROR.defaultMessage
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(
                ApiResponse(
                    success = false,
                    message = firstMessage,
                    errorCode = ErrorCode.VALIDATION_ERROR.code,
                    data = errors
                )
            )
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(
                ApiResponse(
                    success = false,
                    message = ex.message ?: ErrorCode.INVALID_REQUEST.defaultMessage,
                    errorCode = ErrorCode.INVALID_REQUEST.code,
                    data = null
                )
            )
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(ex: NoSuchElementException): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(
                ApiResponse(
                    success = false,
                    message = ex.message ?: ErrorCode.RESOURCE_NOT_FOUND.defaultMessage,
                    errorCode = ErrorCode.RESOURCE_NOT_FOUND.code,
                    data = null
                )
            )
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneralException(ex: Exception): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                ApiResponse(
                    success = false,
                    message = "Lỗi hệ thống: ${ex.message}",
                    errorCode = ErrorCode.INTERNAL_ERROR.code,
                    data = null
                )
            )
    }
}
