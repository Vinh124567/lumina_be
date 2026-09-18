package com.lumina.backend.config

import com.lumina.backend.repository.VocabularyRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * Seeder nạp toàn bộ 772 từ vựng chuẩn HSK 2 theo khung HSK 3.0 Band 2 vào cơ sở dữ liệu PostgreSQL.
 * Đảm bảo tính chuẩn xác tuyệt đối về chữ Hán, Pinyin có dấu, Hán Việt, nghĩa tiếng Việt, từ loại, chủ đề, bộ thủ, số nét và câu ví dụ song ngữ.
 */
@Component
class Hsk2VocabularySeeder(
    private val vocabularyRepository: VocabularyRepository
) : CommandLineRunner {

    private val log = LoggerFactory.getLogger(Hsk2VocabularySeeder::class.java)

    @Transactional
    override fun run(vararg args: String?) {
        val currentCount = vocabularyRepository.findByHskLevel("HSK 2").size
        log.info(">>> [Hsk2VocabularySeeder] Số lượng từ vựng HSK 2 hiện tại trong DB: $currentCount")

        if (currentCount < 772) {
            log.info(">>> [Hsk2VocabularySeeder] Đang tiến hành làm sạch và nạp mới 772 từ vựng HSK 2...")

            // Xóa các bản ghi HSK 2 cũ nếu có để tránh trùng lặp
            val oldWords = vocabularyRepository.findByHskLevel("HSK 2")
            if (oldWords.isNotEmpty()) {
                vocabularyRepository.deleteAll(oldWords)
                log.info(">>> [Hsk2VocabularySeeder] Đã xóa ${oldWords.size} từ vựng HSK 2 cũ.")
            }

            val part1 = Hsk2DataPart1.words
            val part2 = Hsk2DataPart2.words
            val part3 = Hsk2DataPart3.words
            val allHsk2Words = part1 + part2 + part3

            log.info(">>> [Hsk2VocabularySeeder] Tổng số từ vựng HSK 2 chuẩn bị lưu: ${allHsk2Words.size} (Part1: ${part1.size}, Part2: ${part2.size}, Part3: ${part3.size})")

            val saved = vocabularyRepository.saveAll(allHsk2Words)
            log.info(">>> [Hsk2VocabularySeeder] ✅ ĐÃ NẠP THÀNH CÔNG ${saved.size} TỪ VỰNG HSK 2 CHUẨN ĐẦU RA VÀO DATABASE!")
        } else {
            log.info(">>> [Hsk2VocabularySeeder] ✅ Cơ sở dữ liệu đã có đủ $currentCount từ vựng HSK 2. Bỏ qua seeder.")
        }
    }
}
