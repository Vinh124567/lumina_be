package com.lumina.backend.service

import com.lumina.backend.dto.response.HskLevelResponse
import com.lumina.backend.dto.response.TopicResponse
import com.lumina.backend.dto.response.VocabularyResponse
import com.lumina.backend.model.entity.Vocabulary
import com.lumina.backend.repository.HskLevelRepository
import com.lumina.backend.repository.TopicRepository
import com.lumina.backend.repository.UserRepository
import com.lumina.backend.repository.VocabularyRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class VocabularyService(
    private val vocabularyRepository: VocabularyRepository,
    private val hskLevelRepository: HskLevelRepository,
    private val topicRepository: TopicRepository,
    private val userRepository: UserRepository
) {

    private fun getCurrentUserId(): Long? {
        val auth = SecurityContextHolder.getContext().authentication
        if (auth != null && auth.isAuthenticated && auth.principal is String) {
            val email = auth.principal as String
            return userRepository.findByEmail(email).map { it.id }.orElse(null)
        }
        return null
    }

    private fun getAccessibleVocabularies(): List<Vocabulary> {
        val currentUserId = getCurrentUserId()
        return if (currentUserId != null) {
            vocabularyRepository.findAllSystemAndUserVocabularies(currentUserId)
        } else {
            vocabularyRepository.findAllByUserIdIsNull()
        }
    }

    private fun getAccessibleVocabularies(hskLevel: String?): List<Vocabulary> {
        val all = getAccessibleVocabularies()
        if (hskLevel.isNullOrBlank() || hskLevel == "Tất cả cấp độ" || hskLevel == "Tất cả") {
            return all
        }
        if (hskLevel == "HSK 5–6") {
            return all.filter { it.hskLevel == "HSK 5" || it.hskLevel == "HSK 6" || it.hskLevel == "HSK 5–6" }
        }
        return all.filter { it.hskLevel == hskLevel }
    }

    @Transactional(readOnly = true)
    fun getHskLevels(): List<HskLevelResponse> {
        val levels = hskLevelRepository.findAllByOrderByOrderIndexAsc()
        val allVocabs = getAccessibleVocabularies()
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

    private data class CategoryDef(
        val id: String,
        val name: String,
        val icon: String,
        val predicate: (Vocabulary) -> Boolean
    )

    private fun getCategoryDefs(): List<CategoryDef> {
        return listOf(
            CategoryDef("all", "Tất cả", "🌐") { true },
            CategoryDef("custom", "Từ tôi tự thêm", "✍️") { it.userId != null },
            CategoryDef("verb", "Động từ & Hành động", "⚡") { it.partOfSpeech.contains("Động từ", ignoreCase = true) },
            CategoryDef("noun", "Danh từ & Khái niệm", "📦") { it.partOfSpeech.contains("Danh từ", ignoreCase = true) },
            CategoryDef("adj", "Tính từ & Miêu tả", "✨") { it.partOfSpeech.contains("Tính từ", ignoreCase = true) },
            CategoryDef("grammar", "Hư từ & Cấu trúc câu", "🔗") {
                val pos = it.partOfSpeech.lowercase()
                pos.contains("phó từ") || pos.contains("liên từ") || pos.contains("giới từ") || pos.contains("trợ từ") || pos.contains("cảm thán")
            },
            CategoryDef("chengyu", "Thành ngữ (成语)", "📜") {
                it.partOfSpeech.contains("Thành ngữ", ignoreCase = true) || it.topic.contains("Thành ngữ", ignoreCase = true)
            },
            CategoryDef("measure", "Lượng từ & Số từ", "🔢") {
                val pos = it.partOfSpeech.lowercase()
                pos.contains("lượng từ") || pos.contains("số từ")
            },
            CategoryDef("biz_tech", "Kinh tế & Công nghệ", "💼") {
                val top = it.topic.lowercase()
                top.contains("kinh tế") || top.contains("tài chính") || top.contains("thương mại") || top.contains("công nghệ") || top.contains("ngân hàng")
            },
            CategoryDef("society", "Đời sống & Xã hội", "👥") {
                val top = it.topic.lowercase()
                top.contains("đời sống") || top.contains("xã hội") || top.contains("gia đình") || top.contains("giao tiếp") || top.contains("chào hỏi") || top.contains("ăn uống") || top.contains("mua sắm") || top.contains("khách sạn") || top.contains("thời trang") || top.contains("nhà cửa")
            },
            CategoryDef("nature", "Tự nhiên & Môi trường", "🌿") {
                val top = it.topic.lowercase()
                top.contains("tự nhiên") || top.contains("động vật") || top.contains("khí hậu") || top.contains("môi trường") || top.contains("thời tiết") || top.contains("phương hướng") || top.contains("màu sắc")
            }
        )
    }

    private fun matchesCategory(vocab: Vocabulary, category: String?): Boolean {
        if (category.isNullOrBlank() ||
            category == "Tất cả" ||
            category == "Tất cả chủ đề" ||
            category.equals("all", ignoreCase = true)) {
            return true
        }

        val pos = vocab.partOfSpeech.lowercase()
        val top = vocab.topic.lowercase()
        val cat = category.lowercase()

        return when {
            cat.contains("tự thêm") || cat.contains("tôi tự thêm") || cat.contains("từ của tôi") || cat == "custom" ->
                vocab.userId != null
            cat.contains("động từ") -> pos.contains("động từ")
            cat.contains("danh từ") -> pos.contains("danh từ")
            cat.contains("tính từ") -> pos.contains("tính từ")
            cat.contains("hư từ") || cat.contains("ngữ pháp") || cat.contains("cấu trúc") ->
                pos.contains("phó từ") || pos.contains("liên từ") || pos.contains("giới từ") || pos.contains("trợ từ") || pos.contains("cảm thán")
            cat.contains("thành ngữ") ->
                pos.contains("thành ngữ") || top.contains("thành ngữ")
            cat.contains("lượng từ") || cat.contains("số từ") ->
                pos.contains("lượng từ") || pos.contains("số từ")
            cat.contains("kinh tế") || cat.contains("công nghệ") ->
                top.contains("kinh tế") || top.contains("tài chính") || top.contains("thương mại") || top.contains("công nghệ") || top.contains("ngân hàng")
            cat.contains("đời sống") || cat.contains("xã hội") ->
                top.contains("đời sống") || top.contains("xã hội") || top.contains("gia đình") || top.contains("giao tiếp") || top.contains("chào hỏi") || top.contains("ăn uống") || top.contains("mua sắm") || top.contains("khách sạn") || top.contains("thời trang") || top.contains("nhà cửa")
            cat.contains("tự nhiên") || cat.contains("môi trường") ->
                top.contains("tự nhiên") || top.contains("động vật") || top.contains("khí hậu") || top.contains("môi trường") || top.contains("thời tiết") || top.contains("phương hướng") || top.contains("màu sắc")
            else -> vocab.topic.contains(category, ignoreCase = true) || vocab.partOfSpeech.contains(category, ignoreCase = true)
        }
    }

    @Transactional(readOnly = true)
    fun getTopics(hskLevel: String? = null): List<TopicResponse> {
        val vocabs = getAccessibleVocabularies(hskLevel)

        val categoryDefs = getCategoryDefs()

        return categoryDefs.mapNotNull { cat ->
            val count = vocabs.count(cat.predicate)
            if (cat.id == "all" || cat.id == "custom" || count > 0) {
                TopicResponse(
                    id = cat.id,
                    name = cat.name,
                    icon = cat.icon,
                    count = count
                )
            } else {
                null
            }
        }
    }

    @Transactional(readOnly = true)
    fun getVocabularies(topic: String?, hskLevel: String?): List<VocabularyResponse> {
        val vocabs = getAccessibleVocabularies(hskLevel)

        val filtered = if (topic.isNullOrBlank() || topic == "Tất cả" || topic == "Tất cả chủ đề" || topic.equals("all", ignoreCase = true)) {
            vocabs
        } else {
            vocabs.filter { matchesCategory(it, topic) }
        }

        return filtered.map { it.toResponse() }
    }

    @Transactional
    fun createVocabulary(request: com.lumina.backend.dto.request.CreateVocabularyRequest): VocabularyResponse {
        val hsk = request.hskLevel?.trim()?.ifBlank { "HSK 1" } ?: "HSK 1"
        val vocab = Vocabulary(
            hanzi = request.hanzi.trim(),
            pinyin = request.pinyin.trim(),
            hanViet = request.hanViet?.trim() ?: "",
            meaning = request.meaning.trim(),
            partOfSpeech = request.partOfSpeech?.trim()?.ifBlank { "Danh từ" } ?: "Danh từ",
            topic = request.topic?.trim()?.ifBlank { "Đời sống & Xã hội" } ?: "Đời sống & Xã hội",
            radical = request.radical?.trim() ?: "",
            strokes = request.strokes?.trim() ?: "",
            exampleHanzi = request.exampleHanzi?.trim() ?: "",
            examplePinyin = request.examplePinyin?.trim() ?: "",
            exampleMeaning = request.exampleMeaning?.trim() ?: "",
            hskLevel = hsk,
            targetScore = "$hsk (Từ vựng cá nhân)",
            isMastered = false,
            userId = getCurrentUserId()
        )
        val saved = vocabularyRepository.save(vocab)
        return saved.toResponse()
    }

    @Transactional(readOnly = true)
    fun aiLookup(query: String): VocabularyResponse {
        val q = query.trim()
        if (q.isBlank()) {
            throw IllegalArgumentException("Nội dung tra cứu không được để trống")
        }

        // 1. Tìm kiếm chính xác theo chữ Hán
        val exactHanzi = vocabularyRepository.findAll().firstOrNull { it.hanzi.equals(q, ignoreCase = true) }
        if (exactHanzi != null) return exactHanzi.toResponse()

        // 2. Tìm kiếm chứa chữ Hán
        val matchHanzi = vocabularyRepository.findAll().firstOrNull { it.hanzi.contains(q, ignoreCase = true) }
        if (matchHanzi != null) return matchHanzi.toResponse()

        // 3. Tìm kiếm theo nghĩa tiếng Việt hoặc Hán Việt
        val matchMeaning = vocabularyRepository.findAll().firstOrNull {
            it.meaning.contains(q, ignoreCase = true) || it.hanViet.contains(q, ignoreCase = true)
        }
        if (matchMeaning != null) return matchMeaning.toResponse()

        // 4. Nếu không có trong kho 4,320 từ HSK, tự động dựng thông tin thông minh
        val isChinese = q.any { Character.UnicodeScript.of(it.code) == Character.UnicodeScript.HAN }
        return VocabularyResponse(
            id = "0",
            hanzi = if (isChinese) q else "",
            pinyin = "",
            hanViet = "",
            meaning = if (!isChinese) q else "",
            partOfSpeech = "Danh từ",
            topic = "Đời sống & Xã hội",
            radical = "",
            strokes = "",
            exampleHanzi = if (isChinese) "我想学习${q}。" else "",
            examplePinyin = "",
            exampleMeaning = if (!isChinese) "Tôi muốn học $q." else "",
            hskLevel = "HSK 1",
            targetScore = "HSK 1 (Mục tiêu 180–200/200 điểm)",
            isMastered = false
        )
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
            isMastered = this.isMastered,
            userId = this.userId
        )
    }
}
