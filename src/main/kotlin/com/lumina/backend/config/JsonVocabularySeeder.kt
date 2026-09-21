package com.lumina.backend.config

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.lumina.backend.model.entity.Vocabulary
import com.lumina.backend.repository.VocabularyRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * Seeder tự động nạp từ vựng các cấp HSK lưu dưới định dạng JSON từ thư mục resources/data/
 * Hỗ trợ nạp HSK 4 (1.000 từ chuẩn HSK 3.0 Band 4) và các cấp độ mở rộng tiếp theo.
 */
@Component
class JsonVocabularySeeder(
    private val vocabularyRepository: VocabularyRepository,
    private val objectMapper: ObjectMapper
) : CommandLineRunner {

    private val log = LoggerFactory.getLogger(JsonVocabularySeeder::class.java)

    @Transactional
    override fun run(vararg args: String?) {
        seedHskLevelFromJson(
            level = "HSK 4",
            expectedCount = 1000,
            jsonResourcePath = "data/hsk4.json"
        )
        seedHskLevelFromJson(
            level = "HSK 5",
            expectedCount = 1071,
            jsonResourcePath = "data/hsk5.json"
        )
    }

    private fun seedHskLevelFromJson(level: String, expectedCount: Int, jsonResourcePath: String) {
        val resource = ClassPathResource(jsonResourcePath)
        if (!resource.exists()) {
            log.warn(">>> [JsonVocabularySeeder] Không tìm thấy file tài nguyên $jsonResourcePath. Bỏ qua nạp $level.")
            return
        }

        val currentCount = vocabularyRepository.findByHskLevel(level).size
        log.info(">>> [JsonVocabularySeeder] Số lượng từ vựng $level hiện tại trong DB: $currentCount")

        if (currentCount < expectedCount) {
            log.info(">>> [JsonVocabularySeeder] Đang tiến hành đọc và nạp mới $expectedCount từ vựng $level từ $jsonResourcePath...")

            // Xóa dữ liệu cũ nếu chưa đủ để tránh trùng lặp
            val oldWords = vocabularyRepository.findByHskLevel(level)
            if (oldWords.isNotEmpty()) {
                vocabularyRepository.deleteAll(oldWords)
                log.info(">>> [JsonVocabularySeeder] Đã xóa ${oldWords.size} từ vựng $level cũ.")
            }

            resource.inputStream.use { inputStream ->
                val vocabList: List<Vocabulary> = objectMapper.readValue(
                    inputStream,
                    object : TypeReference<List<Vocabulary>>() {}
                )

                log.info(">>> [JsonVocabularySeeder] Đã đọc thành công ${vocabList.size} từ vựng từ $jsonResourcePath. Bắt đầu lưu vào DB...")
                val saved = vocabularyRepository.saveAll(vocabList)
                log.info(">>> [JsonVocabularySeeder] ✅ ĐÃ NẠP THÀNH CÔNG ${saved.size} TỪ VỰNG $level VÀO DATABASE!")
            }
        } else {
            log.info(">>> [JsonVocabularySeeder] ✅ Cơ sở dữ liệu đã có đủ $currentCount từ vựng $level. Bỏ qua seeder.")
        }
    }
}
