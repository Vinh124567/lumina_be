package com.lumina.backend.config

import com.lumina.backend.repository.VocabularyRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * Seeder nạp toàn bộ 973 từ vựng chuẩn HSK 3 theo khung HSK 3.0 Band 3 vào cơ sở dữ liệu PostgreSQL.
 * Đảm bảo tính chuẩn xác tuyệt đối về chữ Hán, Pinyin có dấu thanh điệu, Hán Việt, nghĩa tiếng Việt,
 * từ loại, chủ đề, bộ thủ, số nét và câu ví dụ song ngữ thực tế.
 */
@Component
class Hsk3VocabularySeeder(
    private val vocabularyRepository: VocabularyRepository
) : CommandLineRunner {

    private val log = LoggerFactory.getLogger(Hsk3VocabularySeeder::class.java)

    @Transactional
    override fun run(vararg args: String?) {
        val currentCount = vocabularyRepository.findByHskLevel("HSK 3").size
        log.info(">>> [Hsk3VocabularySeeder] Số lượng từ vựng HSK 3 hiện tại trong DB: $currentCount")

        if (currentCount < 973) {
            log.info(">>> [Hsk3VocabularySeeder] Đang tiến hành làm sạch và nạp mới 973 từ vựng HSK 3...")

            // Xóa các bản ghi HSK 3 cũ nếu có để tránh trùng lặp
            val oldWords = vocabularyRepository.findByHskLevel("HSK 3")
            if (oldWords.isNotEmpty()) {
                vocabularyRepository.deleteAll(oldWords)
                log.info(">>> [Hsk3VocabularySeeder] Đã xóa ${oldWords.size} từ vựng HSK 3 cũ.")
            }

            val part1 = Hsk3DataPart1.words
            val part2 = Hsk3DataPart2.words
            val part3 = Hsk3DataPart3.words
            val part4 = Hsk3DataPart4.words
            val allHsk3Words = part1 + part2 + part3 + part4

            log.info(">>> [Hsk3VocabularySeeder] Tổng số từ vựng HSK 3 chuẩn bị lưu: ${allHsk3Words.size} (Part1: ${part1.size}, Part2: ${part2.size}, Part3: ${part3.size}, Part4: ${part4.size})")

            val saved = vocabularyRepository.saveAll(allHsk3Words)
            log.info(">>> [Hsk3VocabularySeeder] ✅ ĐÃ NẠP THÀNH CÔNG ${saved.size} TỪ VỰNG HSK 3 CHUẨN ĐẦU RA VÀO DATABASE!")
        } else {
            log.info(">>> [Hsk3VocabularySeeder] ✅ Cơ sở dữ liệu đã có đủ $currentCount từ vựng HSK 3. Bỏ qua seeder.")
        }
    }
}
