package com.lumina.backend

import com.lumina.backend.config.Hsk1DataPart1
import com.lumina.backend.config.Hsk1DataPart2
import com.lumina.backend.config.Hsk1VocabularySeeder
import com.lumina.backend.config.Hsk2DataPart1
import com.lumina.backend.config.Hsk2DataPart2
import com.lumina.backend.config.Hsk2DataPart3
import com.lumina.backend.config.Hsk2VocabularySeeder
import com.lumina.backend.config.Hsk3DataPart1
import com.lumina.backend.config.Hsk3DataPart2
import com.lumina.backend.config.Hsk3DataPart3
import com.lumina.backend.config.Hsk3DataPart4
import com.lumina.backend.config.Hsk3VocabularySeeder
import com.lumina.backend.repository.VocabularyRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class LuminaApplicationTests {

    @Autowired(required = false)
    private var vocabularyRepository: VocabularyRepository? = null

    @Autowired(required = false)
    private var seeder: Hsk1VocabularySeeder? = null

    @Autowired(required = false)
    private var hsk2Seeder: Hsk2VocabularySeeder? = null

    @Autowired(required = false)
    private var hsk3Seeder: Hsk3VocabularySeeder? = null

    @Autowired(required = false)
    private var jsonVocabularySeeder: com.lumina.backend.config.JsonVocabularySeeder? = null

    @Autowired
    private lateinit var objectMapper: com.fasterxml.jackson.databind.ObjectMapper

    @Test
    fun testVerifyHsk1DataCountAndQuality() {
        val part1 = Hsk1DataPart1.words
        val part2 = Hsk1DataPart2.words
        val allHsk1 = part1 + part2

        println("Part 1 count: ${part1.size}")
        println("Part 2 count: ${part2.size}")
        println("Total HSK 1 count: ${allHsk1.size}")
        assertEquals(504, allHsk1.size, "Tổng số từ vựng HSK 1 phải đủ chính xác 504 từ!")

        val hsk1HanziSet = allHsk1.map { it.hanzi }.toSet()
        val duplicates = allHsk1.groupBy { it.hanzi }.filter { it.value.size > 1 }
        assertTrue(duplicates.isEmpty(), "Không được phép có từ Hanzi trùng lặp trong HSK 1! Tìm thấy: ${duplicates.keys}")

        // Kiểm tra HSK 2
        val hsk2Part1 = Hsk2DataPart1.words
        val hsk2Part2 = Hsk2DataPart2.words
        val hsk2Part3 = Hsk2DataPart3.words
        val allHsk2 = hsk2Part1 + hsk2Part2 + hsk2Part3

        println(">>> Current Hsk2DataPart1 count: ${hsk2Part1.size}")
        println(">>> Current Hsk2DataPart2 count: ${hsk2Part2.size}")
        println(">>> Current Hsk2DataPart3 count: ${hsk2Part3.size}")
        println(">>> Total HSK 2 count: ${allHsk2.size}")

        assertEquals(258, hsk2Part1.size, "Hsk2DataPart1 must have exactly 258 words!")
        assertEquals(258, hsk2Part2.size, "Hsk2DataPart2 must have exactly 258 words!")
        assertEquals(256, hsk2Part3.size, "Hsk2DataPart3 must have exactly 256 words!")
        assertEquals(772, allHsk2.size, "Total HSK 2 vocabulary count must be exactly 772 words according to HSK 3.0 Band 2!")

        val hsk2Duplicates = allHsk2.groupBy { it.hanzi }.filter { it.value.size > 1 }
        assertTrue(hsk2Duplicates.isEmpty(), "No duplicate Hanzi allowed in HSK 2! Found: ${hsk2Duplicates.keys}")

        val hsk2OverlapWithHsk1 = allHsk2.map { it.hanzi }.filter { hsk1HanziSet.contains(it) }
        assertTrue(hsk2OverlapWithHsk1.isEmpty(), "HSK 2 should have 0 overlap with HSK 1! Found: $hsk2OverlapWithHsk1")

        // Kiểm tra HSK 3
        val hsk3Part1 = Hsk3DataPart1.words
        val hsk3Part2 = Hsk3DataPart2.words
        val hsk3Part3 = Hsk3DataPart3.words
        val hsk3Part4 = Hsk3DataPart4.words
        val allHsk3 = hsk3Part1 + hsk3Part2 + hsk3Part3 + hsk3Part4

        println(">>> Hsk3DataPart1 count: ${hsk3Part1.size}")
        println(">>> Hsk3DataPart2 count: ${hsk3Part2.size}")
        println(">>> Hsk3DataPart3 count: ${hsk3Part3.size}")
        println(">>> Hsk3DataPart4 count: ${hsk3Part4.size}")
        println(">>> Total HSK 3 count: ${allHsk3.size}")

        assertEquals(245, hsk3Part1.size, "Hsk3DataPart1 must have exactly 245 words!")
        assertEquals(250, hsk3Part2.size, "Hsk3DataPart2 must have exactly 250 words!")
        assertEquals(240, hsk3Part3.size, "Hsk3DataPart3 must have exactly 240 words!")
        assertEquals(238, hsk3Part4.size, "Hsk3DataPart4 must have exactly 238 words!")
        assertEquals(973, allHsk3.size, "Total HSK 3 vocabulary count must be exactly 973 words according to HSK 3.0 Band 3!")

        val hsk3Duplicates = allHsk3.groupBy { it.hanzi }.filter { it.value.size > 1 }
        assertTrue(hsk3Duplicates.isEmpty(), "No duplicate Hanzi allowed in HSK 3! Found: ${hsk3Duplicates.keys}")

        val hsk2HanziSet = allHsk2.map { it.hanzi }.toSet()
        val hsk3OverlapWithHsk1 = allHsk3.map { it.hanzi }.filter { hsk1HanziSet.contains(it) }
        val hsk3OverlapWithHsk2 = allHsk3.map { it.hanzi }.filter { hsk2HanziSet.contains(it) }

        assertTrue(hsk3OverlapWithHsk1.isEmpty(), "HSK 3 should have 0 overlap with HSK 1! Found: $hsk3OverlapWithHsk1")
        assertTrue(hsk3OverlapWithHsk2.isEmpty(), "HSK 3 should have 0 overlap with HSK 2! Found: $hsk3OverlapWithHsk2")

        // Kiểm tra chất lượng dữ liệu từng từ HSK 3
        for ((index, item) in allHsk3.withIndex()) {
            assertTrue(item.hanzi.isNotBlank(), "HSK 3 Từ thứ ${index + 1} có Hanzi bị rỗng!")
            assertTrue(item.pinyin.isNotBlank(), "HSK 3 Từ '${item.hanzi}' có Pinyin bị rỗng!")
            assertTrue(item.hanViet.isNotBlank(), "HSK 3 Từ '${item.hanzi}' có Hán Việt bị rỗng!")
            assertTrue(item.meaning.isNotBlank(), "HSK 3 Từ '${item.hanzi}' có nghĩa bị rỗng!")
            assertTrue(item.partOfSpeech.isNotBlank(), "HSK 3 Từ '${item.hanzi}' có từ loại bị rỗng!")
            assertTrue(item.topic.isNotBlank(), "HSK 3 Từ '${item.hanzi}' có chủ đề bị rỗng!")
            assertTrue(item.radical.isNotBlank(), "HSK 3 Từ '${item.hanzi}' có bộ thủ bị rỗng!")
            assertTrue(item.strokes.isNotBlank(), "HSK 3 Từ '${item.hanzi}' có số nét bị rỗng!")
            assertTrue(item.exampleHanzi.isNotBlank(), "HSK 3 Từ '${item.hanzi}' có câu ví dụ Hanzi bị rỗng!")
            assertTrue(item.examplePinyin.isNotBlank(), "HSK 3 Từ '${item.hanzi}' có câu ví dụ Pinyin bị rỗng!")
            assertTrue(item.exampleMeaning.isNotBlank(), "HSK 3 Từ '${item.hanzi}' có câu ví dụ nghĩa bị rỗng!")
        }

        // Kiểm tra HSK 4 từ data/hsk4.json
        val hsk4Resource = org.springframework.core.io.ClassPathResource("data/hsk4.json")
        assertTrue(hsk4Resource.exists(), "File data/hsk4.json phải tồn tại trong resources!")

        val allHsk4: List<com.lumina.backend.model.entity.Vocabulary> = hsk4Resource.inputStream.use { stream ->
            objectMapper.readValue(stream, object : com.fasterxml.jackson.core.type.TypeReference<List<com.lumina.backend.model.entity.Vocabulary>>() {})
        }

        println(">>> Total HSK 4 count from JSON: ${allHsk4.size}")
        assertEquals(1000, allHsk4.size, "Tổng số từ vựng HSK 4 phải đủ chính xác 1.000 từ!")

        val hsk4Duplicates = allHsk4.groupBy { it.hanzi }.filter { it.value.size > 1 }
        assertTrue(hsk4Duplicates.isEmpty(), "Không được phép có từ Hanzi trùng lặp trong HSK 4! Tìm thấy: ${hsk4Duplicates.keys}")

        val hsk3HanziSet = allHsk3.map { it.hanzi }.toSet()
        val hsk4OverlapWithHsk1 = allHsk4.map { it.hanzi }.filter { hsk1HanziSet.contains(it) }
        val hsk4OverlapWithHsk2 = allHsk4.map { it.hanzi }.filter { hsk2HanziSet.contains(it) }
        val hsk4OverlapWithHsk3 = allHsk4.map { it.hanzi }.filter { hsk3HanziSet.contains(it) }

        assertTrue(hsk4OverlapWithHsk1.isEmpty(), "HSK 4 không được trùng với HSK 1! Trùng: $hsk4OverlapWithHsk1")
        assertTrue(hsk4OverlapWithHsk2.isEmpty(), "HSK 4 không được trùng với HSK 2! Trùng: $hsk4OverlapWithHsk2")
        assertTrue(hsk4OverlapWithHsk3.isEmpty(), "HSK 4 không được trùng với HSK 3! Trùng: $hsk4OverlapWithHsk3")

        // Kiểm tra chất lượng dữ liệu từng từ HSK 4
        for ((index, item) in allHsk4.withIndex()) {
            assertTrue(item.hanzi.isNotBlank(), "HSK 4 Từ thứ ${index + 1} có Hanzi bị rỗng!")
            assertTrue(item.pinyin.isNotBlank(), "HSK 4 Từ '${item.hanzi}' có Pinyin bị rỗng!")
            assertTrue(item.hanViet.isNotBlank(), "HSK 4 Từ '${item.hanzi}' có Hán Việt bị rỗng!")
            assertTrue(item.meaning.isNotBlank(), "HSK 4 Từ '${item.hanzi}' có nghĩa bị rỗng!")
            assertTrue(item.partOfSpeech.isNotBlank(), "HSK 4 Từ '${item.hanzi}' có từ loại bị rỗng!")
            assertTrue(item.topic.isNotBlank(), "HSK 4 Từ '${item.hanzi}' có chủ đề bị rỗng!")
            assertTrue(item.radical.isNotBlank(), "HSK 4 Từ '${item.hanzi}' có bộ thủ bị rỗng!")
            assertTrue(item.strokes.isNotBlank(), "HSK 4 Từ '${item.hanzi}' có số nét bị rỗng!")
            assertTrue(item.exampleHanzi.isNotBlank(), "HSK 4 Từ '${item.hanzi}' có câu ví dụ Hanzi bị rỗng!")
            assertTrue(item.examplePinyin.isNotBlank(), "HSK 4 Từ '${item.hanzi}' có câu ví dụ Pinyin bị rỗng!")
            assertTrue(item.exampleMeaning.isNotBlank(), "HSK 4 Từ '${item.hanzi}' có câu ví dụ nghĩa bị rỗng!")
            assertEquals("HSK 4", item.hskLevel, "HSK 4 Từ '${item.hanzi}' có hskLevel không khớp!")
        }

        // Kiểm tra HSK 5 từ data/hsk5.json
        val hsk5Resource = org.springframework.core.io.ClassPathResource("data/hsk5.json")
        assertTrue(hsk5Resource.exists(), "File data/hsk5.json phải tồn tại trong resources!")

        val allHsk5: List<com.lumina.backend.model.entity.Vocabulary> = hsk5Resource.inputStream.use { stream ->
            objectMapper.readValue(stream, object : com.fasterxml.jackson.core.type.TypeReference<List<com.lumina.backend.model.entity.Vocabulary>>() {})
        }

        println(">>> Total HSK 5 count from JSON: ${allHsk5.size}")
        assertEquals(1071, allHsk5.size, "Tổng số từ vựng HSK 5 phải đủ chính xác 1.071 từ!")

        val hsk5Duplicates = allHsk5.groupBy { it.hanzi }.filter { it.value.size > 1 }
        assertTrue(hsk5Duplicates.isEmpty(), "Không được phép có từ Hanzi trùng lặp trong HSK 5! Tìm thấy: ${hsk5Duplicates.keys}")

        val hsk4HanziSet = allHsk4.map { it.hanzi }.toSet()
        val hsk5OverlapWithHsk1 = allHsk5.map { it.hanzi }.filter { hsk1HanziSet.contains(it) }
        val hsk5OverlapWithHsk2 = allHsk5.map { it.hanzi }.filter { hsk2HanziSet.contains(it) }
        val hsk5OverlapWithHsk3 = allHsk5.map { it.hanzi }.filter { hsk3HanziSet.contains(it) }
        val hsk5OverlapWithHsk4 = allHsk5.map { it.hanzi }.filter { hsk4HanziSet.contains(it) }

        assertTrue(hsk5OverlapWithHsk1.isEmpty(), "HSK 5 không được trùng với HSK 1! Trùng: $hsk5OverlapWithHsk1")
        assertTrue(hsk5OverlapWithHsk2.isEmpty(), "HSK 5 không được trùng với HSK 2! Trùng: $hsk5OverlapWithHsk2")
        assertTrue(hsk5OverlapWithHsk3.isEmpty(), "HSK 5 không được trùng với HSK 3! Trùng: $hsk5OverlapWithHsk3")
        assertTrue(hsk5OverlapWithHsk4.isEmpty(), "HSK 5 không được trùng với HSK 4! Trùng: $hsk5OverlapWithHsk4")

        // Kiểm tra chất lượng dữ liệu từng từ HSK 5
        for ((index, item) in allHsk5.withIndex()) {
            assertTrue(item.hanzi.isNotBlank(), "HSK 5 Từ thứ ${index + 1} có Hanzi bị rỗng!")
            assertTrue(item.pinyin.isNotBlank(), "HSK 5 Từ '${item.hanzi}' có Pinyin bị rỗng!")
            assertTrue(item.hanViet.isNotBlank(), "HSK 5 Từ '${item.hanzi}' có Hán Việt bị rỗng!")
            assertTrue(item.meaning.isNotBlank(), "HSK 5 Từ '${item.hanzi}' có nghĩa bị rỗng!")
            assertTrue(item.partOfSpeech.isNotBlank(), "HSK 5 Từ '${item.hanzi}' có từ loại bị rỗng!")
            assertTrue(item.topic.isNotBlank(), "HSK 5 Từ '${item.hanzi}' có chủ đề bị rỗng!")
            assertTrue(item.radical.isNotBlank(), "HSK 5 Từ '${item.hanzi}' có bộ thủ bị rỗng!")
            assertTrue(item.strokes.isNotBlank(), "HSK 5 Từ '${item.hanzi}' có số nét bị rỗng!")
            assertTrue(item.exampleHanzi.isNotBlank(), "HSK 5 Từ '${item.hanzi}' có câu ví dụ Hanzi bị rỗng!")
            assertTrue(item.examplePinyin.isNotBlank(), "HSK 5 Từ '${item.hanzi}' có câu ví dụ Pinyin bị rỗng!")
            assertTrue(item.exampleMeaning.isNotBlank(), "HSK 5 Từ '${item.hanzi}' có câu ví dụ nghĩa bị rỗng!")
            assertEquals("HSK 5", item.hskLevel, "HSK 5 Từ '${item.hanzi}' có hskLevel không khớp!")
        }

        // Kiểm tra DB & Seeder
        vocabularyRepository?.let { repo ->
            seeder?.run()
            val dbHsk1Words = repo.findByHskLevel("HSK 1")
            println(">>> Số lượng từ vựng HSK 1 thực tế trong Neon PostgreSQL: ${dbHsk1Words.size}")
            assertEquals(504, dbHsk1Words.size, "Số lượng từ vựng HSK 1 trong DB phải đúng 504!")

            hsk2Seeder?.run()
            val dbHsk2Words = repo.findByHskLevel("HSK 2")
            println(">>> Số lượng từ vựng HSK 2 thực tế trong Neon PostgreSQL: ${dbHsk2Words.size}")
            assertEquals(772, dbHsk2Words.size, "Số lượng từ vựng HSK 2 trong DB phải đúng 772!")

            hsk3Seeder?.run()
            val dbHsk3Words = repo.findByHskLevel("HSK 3")
            println(">>> Số lượng từ vựng HSK 3 thực tế trong Neon PostgreSQL: ${dbHsk3Words.size}")
            assertEquals(973, dbHsk3Words.size, "Số lượng từ vựng HSK 3 trong DB phải đúng 973!")

            jsonVocabularySeeder?.run()
            val dbHsk4Words = repo.findByHskLevel("HSK 4")
            println(">>> Số lượng từ vựng HSK 4 thực tế trong Neon PostgreSQL: ${dbHsk4Words.size}")
            assertEquals(1000, dbHsk4Words.size, "Số lượng từ vựng HSK 4 trong DB phải đúng 1000!")

            val dbHsk5Words = repo.findByHskLevel("HSK 5")
            println(">>> Số lượng từ vựng HSK 5 thực tế trong Neon PostgreSQL: ${dbHsk5Words.size}")
            assertEquals(1071, dbHsk5Words.size, "Số lượng từ vựng HSK 5 trong DB phải đúng 1071!")
        }
    }
}
