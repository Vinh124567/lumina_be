package com.lumina.backend.config

import com.lumina.backend.repository.VocabularyRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * Seeder nạp toàn bộ 504 từ vựng chuẩn HSK 1 vào cơ sở dữ liệu PostgreSQL.
 * Đảm bảo tính chuẩn xác tuyệt đối về chữ Hán, Pinyin, Hán Việt, nghĩa tiếng Việt và câu ví dụ.
 */
@Component
class Hsk1VocabularySeeder(
    private val vocabularyRepository: VocabularyRepository
) : CommandLineRunner {

    private val log = LoggerFactory.getLogger(Hsk1VocabularySeeder::class.java)

    @Transactional
    override fun run(vararg args: String?) {
        val currentCount = vocabularyRepository.findByHskLevel("HSK 1").size
        log.info(">>> [Hsk1VocabularySeeder] Số lượng từ vựng HSK 1 hiện tại trong DB: $currentCount")

        if (currentCount < 504) {
            log.info(">>> [Hsk1VocabularySeeder] Đang tiến hành làm sạch và nạp mới 504 từ vựng HSK 1...")
            
            // Xóa các bản ghi HSK 1 cũ để tránh trùng lặp
            val oldWords = vocabularyRepository.findByHskLevel("HSK 1")
            if (oldWords.isNotEmpty()) {
                vocabularyRepository.deleteAll(oldWords)
                log.info(">>> [Hsk1VocabularySeeder] Đã xóa ${oldWords.size} từ vựng HSK 1 cũ.")
            }

            val part1 = Hsk1DataPart1.words
            val part2 = Hsk1DataPart2.words
            val allHsk1Words = part1 + part2

            log.info(">>> [Hsk1VocabularySeeder] Tổng số từ vựng chuẩn bị lưu: ${allHsk1Words.size} (Part1: ${part1.size}, Part2: ${part2.size})")

            val saved = vocabularyRepository.saveAll(allHsk1Words)
            log.info(">>> [Hsk1VocabularySeeder] ✅ ĐÃ NẠP THÀNH CÔNG ${saved.size} TỪ VỰNG HSK 1 CHUẨN ĐẦU RA VÀO DATABASE!")
        } else {
            log.info(">>> [Hsk1VocabularySeeder] ✅ Cơ sở dữ liệu đã có đủ $currentCount từ vựng HSK 1. Bỏ qua seeder.")
        }
    }
}
