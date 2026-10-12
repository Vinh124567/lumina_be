package com.lumina.backend.service

import com.lumina.backend.dto.response.HeartStatusResponse
import com.lumina.backend.model.entity.UserHeart
import com.lumina.backend.repository.UserHeartRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.LocalDateTime

@Service
class HeartService(
    private val userHeartRepository: UserHeartRepository
) {
    companion object {
        const val MAX_HEARTS = 5
        const val REFILL_INTERVAL_MINUTES = 30L
        const val SPARKS_REFILL_COST = 50
    }

    @Transactional
    fun getHeartStatus(userId: String = "default_user"): HeartStatusResponse {
        val userHeart = getOrCreateUserHeart(userId)
        updateAutoRefill(userHeart)
        val saved = userHeartRepository.save(userHeart)

        val nextRefillMinutes = calculateNextRefillMinutes(saved)

        return HeartStatusResponse(
            currentHearts = saved.currentHearts,
            maxHearts = saved.maxHearts,
            nextRefillMinutes = nextRefillMinutes,
            sparksRefillCost = SPARKS_REFILL_COST,
            message = if (saved.currentHearts >= saved.maxHearts) "Trái tim đã đầy!" else "Đang hồi tim tự động"
        )
    }

    @Transactional
    fun consumeHeart(userId: String = "default_user"): HeartStatusResponse {
        val userHeart = getOrCreateUserHeart(userId)
        updateAutoRefill(userHeart)

        if (userHeart.currentHearts > 0) {
            userHeart.currentHearts -= 1
            if (userHeart.lastConsumedAt == null) {
                userHeart.lastConsumedAt = LocalDateTime.now()
            }
        }

        val saved = userHeartRepository.save(userHeart)
        val nextRefillMinutes = calculateNextRefillMinutes(saved)

        return HeartStatusResponse(
            currentHearts = saved.currentHearts,
            maxHearts = saved.maxHearts,
            nextRefillMinutes = nextRefillMinutes,
            sparksRefillCost = SPARKS_REFILL_COST,
            message = if (saved.currentHearts > 0) "Đã trừ 1 trái tim" else "Bạn đã hết tim! Hãy ôn tập bài cũ hoặc nạp thêm"
        )
    }

    @Transactional
    fun refillHearts(userId: String = "default_user"): HeartStatusResponse {
        val userHeart = getOrCreateUserHeart(userId)
        userHeart.currentHearts = MAX_HEARTS
        userHeart.lastConsumedAt = null

        val saved = userHeartRepository.save(userHeart)

        return HeartStatusResponse(
            currentHearts = saved.currentHearts,
            maxHearts = saved.maxHearts,
            nextRefillMinutes = 0,
            sparksRefillCost = SPARKS_REFILL_COST,
            message = "Đã nạp đầy 5 trái tim thành công! Sẵn sàng học tiếp 🎉"
        )
    }

    private fun getOrCreateUserHeart(userId: String): UserHeart {
        return userHeartRepository.findByUserId(userId) ?: run {
            val newHeart = UserHeart(
                userId = userId,
                currentHearts = MAX_HEARTS,
                maxHearts = MAX_HEARTS,
                lastConsumedAt = null
            )
            userHeartRepository.save(newHeart)
        }
    }

    private fun updateAutoRefill(userHeart: UserHeart) {
        if (userHeart.currentHearts >= userHeart.maxHearts) {
            userHeart.lastConsumedAt = null
            return
        }

        val lastConsumed = userHeart.lastConsumedAt ?: return
        val minutesElapsed = Duration.between(lastConsumed, LocalDateTime.now()).toMinutes()
        val recoveredHearts = (minutesElapsed / REFILL_INTERVAL_MINUTES).toInt()

        if (recoveredHearts > 0) {
            userHeart.currentHearts = minOf(userHeart.maxHearts, userHeart.currentHearts + recoveredHearts)
            if (userHeart.currentHearts >= userHeart.maxHearts) {
                userHeart.lastConsumedAt = null
            } else {
                userHeart.lastConsumedAt = lastConsumed.plusMinutes(recoveredHearts * REFILL_INTERVAL_MINUTES)
            }
        }
    }

    private fun calculateNextRefillMinutes(userHeart: UserHeart): Int {
        if (userHeart.currentHearts >= userHeart.maxHearts || userHeart.lastConsumedAt == null) {
            return 0
        }
        val minutesElapsed = Duration.between(userHeart.lastConsumedAt, LocalDateTime.now()).toMinutes()
        val remaining = (REFILL_INTERVAL_MINUTES - (minutesElapsed % REFILL_INTERVAL_MINUTES)).toInt()
        return maxOf(1, remaining)
    }
}
