package com.lumina.backend.service

import com.lumina.backend.dto.response.HskLevelResponse
import com.lumina.backend.dto.response.TopicResponse
import com.lumina.backend.dto.response.VocabularyResponse
import com.lumina.backend.model.entity.Vocabulary
import com.lumina.backend.repository.HskLevelRepository
import com.lumina.backend.repository.TopicRepository
import com.lumina.backend.repository.VocabularyRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class VocabularyService(
    private val vocabularyRepository: VocabularyRepository,
    private val hskLevelRepository: HskLevelRepository,
    private val topicRepository: TopicRepository
) {

    @Transactional(readOnly = true)
    fun getHskLevels(): List<HskLevelResponse> {
        val levels = hskLevelRepository.findAllByOrderByOrderIndexAsc()
        val allVocabs = vocabularyRepository.findAll()
        val countByLevel = allVocabs.groupBy { it.hskLevel }
            .mapValues { it.value.size }

        return levels.map { level ->
            val count = if (level.id == "all" || level.title == "Tất cả cấp độ") {
                allVocabs.size
            } else if (level.title == "HSK 5–6") {
                (countByLevel["HSK 5"] ?: 0) + (countByLevel["HSK 6"] ?: 0) + (countByLevel["HSK 5–6"] ?: 0)
            } else {
                countByLevel[level.title] ?: 0
            }
            HskLevelResponse(
                id = level.id,
                title = level.title,
                scoreRange = level.scoreRange,
                count = count
            )
        }
    }

    @Transactional(readOnly = true)
    fun getTopics(hskLevel: String? = null): List<TopicResponse> {
        val topics = topicRepository.findAllByOrderByOrderIndexAsc()
        val vocabs = if (!hskLevel.isNullOrBlank() && hskLevel != "Tất cả cấp độ") {
            vocabularyRepository.findByHskLevel(hskLevel)
        } else {
            vocabularyRepository.findAll()
        }
        val totalCount = vocabs.size

        // Đếm số từ theo từng chủ đề
        val countByTopic = vocabs.groupBy { it.topic }
            .mapValues { it.value.size }

        return topics.map { topic ->
            val count = if (topic.id == "all" || topic.name == "Tất cả chủ đề") {
                totalCount
            } else {
                countByTopic[topic.name] ?: 0
            }
            TopicResponse(
                id = topic.id,
                name = topic.name,
                icon = topic.icon,
                count = count
            )
        }
    }

    @Transactional(readOnly = true)
    fun getVocabularies(topic: String?, hskLevel: String?): List<VocabularyResponse> {
        val list = when {
            !topic.isNullOrBlank() && topic != "Tất cả chủ đề" && !hskLevel.isNullOrBlank() -> {
                vocabularyRepository.findByTopicAndHskLevel(topic, hskLevel)
            }
            !topic.isNullOrBlank() && topic != "Tất cả chủ đề" -> {
                vocabularyRepository.findByTopic(topic)
            }
            !hskLevel.isNullOrBlank() -> {
                vocabularyRepository.findByHskLevel(hskLevel)
            }
            else -> {
                vocabularyRepository.findAll()
            }
        }

        return list.map { it.toResponse() }
    }

    @Transactional(readOnly = true)
    fun getVocabularyById(id: Long): VocabularyResponse {
        val vocab = vocabularyRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Không tìm thấy từ vựng với id $id") }
        return vocab.toResponse()
    }

    @Transactional
    fun toggleMastered(id: Long): VocabularyResponse {
        val vocab = vocabularyRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Không tìm thấy từ vựng với id $id") }
        vocab.isMastered = !vocab.isMastered
        val saved = vocabularyRepository.save(vocab)
        return saved.toResponse()
    }

    private fun Vocabulary.toResponse(): VocabularyResponse {
        return VocabularyResponse(
            id = this.id.toString(),
            hanzi = this.hanzi,
            pinyin = this.pinyin,
            hanViet = this.hanViet,
            meaning = this.meaning,
            partOfSpeech = this.partOfSpeech,
            topic = this.topic,
            radical = this.radical,
            strokes = this.strokes,
            exampleHanzi = this.exampleHanzi,
            examplePinyin = this.examplePinyin,
            exampleMeaning = this.exampleMeaning,
            hskLevel = this.hskLevel,
            targetScore = this.targetScore,
            isMastered = this.isMastered
        )
    }
}
