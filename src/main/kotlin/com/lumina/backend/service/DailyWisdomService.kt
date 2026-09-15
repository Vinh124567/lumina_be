package com.lumina.backend.service

import com.lumina.backend.dto.response.DailyWisdomResponse
import com.lumina.backend.repository.DailyWisdomRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class DailyWisdomService(
    private val dailyWisdomRepository: DailyWisdomRepository
) {
    fun getTodayWisdom(): DailyWisdomResponse {
        val all = dailyWisdomRepository.findAll()
        if (all.isEmpty()) {
            return DailyWisdomResponse(
                id = 1,
                chinese = "千里之行，始于足下",
                pinyin = "Qiān lǐ zhī xíng, shǐ yú zú xià",
                vietnamese = "Hành trình vạn dặm bắt đầu từ một bước chân.",
                meaning = "Mọi thành công vĩ đại đều khởi đầu từ những hành động nhỏ bé nhưng kiên trì nhất.",
                author = "Lão Tử • 老子"
            )
        }
        val dayOfYear = LocalDate.now().dayOfYear
        val index = (dayOfYear - 1) % all.size
        val wisdom = all[index]
        return DailyWisdomResponse(
            id = wisdom.id,
            chinese = wisdom.chinese,
            pinyin = wisdom.pinyin,
            vietnamese = wisdom.vietnamese,
            meaning = wisdom.meaning,
            author = wisdom.author
        )
    }

    fun getRandomWisdom(): DailyWisdomResponse {
        val all = dailyWisdomRepository.findAll()
        val wisdom = all.randomOrNull() ?: return getTodayWisdom()
        return DailyWisdomResponse(
            id = wisdom.id,
            chinese = wisdom.chinese,
            pinyin = wisdom.pinyin,
            vietnamese = wisdom.vietnamese,
            meaning = wisdom.meaning,
            author = wisdom.author
        )
    }
}
