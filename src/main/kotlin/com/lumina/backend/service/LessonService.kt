package com.lumina.backend.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.lumina.backend.dto.response.ChestRewardResponse
import com.lumina.backend.dto.response.DialogueLineResponse
import com.lumina.backend.dto.response.ExamQuestionResponse
import com.lumina.backend.dto.response.GrammarStructureResponse
import com.lumina.backend.dto.response.JourneyUnitResponse
import com.lumina.backend.dto.response.LessonResponse
import com.lumina.backend.dto.response.LessonVocabResponse
import com.lumina.backend.dto.response.QuickReviewQuestionResponse
import com.lumina.backend.dto.response.ReviewResultResponse
import com.lumina.backend.dto.response.SlideResponse
import com.lumina.backend.exception.AppException
import com.lumina.backend.exception.ErrorCode
import com.lumina.backend.model.entity.ChestClaim
import com.lumina.backend.model.entity.ExamQuestion
import com.lumina.backend.model.entity.JourneyUnit
import com.lumina.backend.model.entity.GrammarStructureEntity
import com.lumina.backend.model.entity.Lesson
import com.lumina.backend.model.entity.LessonSlide
import com.lumina.backend.model.entity.SlideType
import com.lumina.backend.model.entity.ToneRuleItemEntity
import com.lumina.backend.repository.ChestClaimRepository
import com.lumina.backend.repository.ExamQuestionRepository
import com.lumina.backend.repository.GrammarStructureRepository
import com.lumina.backend.repository.JourneyUnitRepository
import com.lumina.backend.repository.LessonRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LessonService(
    private val lessonRepository: LessonRepository,
    private val examQuestionRepository: ExamQuestionRepository,
    private val journeyUnitRepository: JourneyUnitRepository,
    private val chestClaimRepository: ChestClaimRepository,
    private val grammarStructureRepository: GrammarStructureRepository
) {
    private val objectMapper = jacksonObjectMapper()

    @Transactional
    fun getRecommendedLessons(): List<LessonResponse> {
        if (lessonRepository.count() == 0L) {
            syncLessonsFromResource(force = false)
        }
        val allLessons = lessonRepository.findAllRecommended()
        if (allLessons.isEmpty()) return emptyList()

        // Phân loại các bài học theo trụ cột lộ trình chuẩn HSK 1:
        // 1. Nhóm Nền tảng (Pinyin & Bộ thủ)
        val foundationLessons = allLessons.filter { it.stage == "FOUNDATION" }.sortedBy { it.orderIndex }
        // 2. Nhóm Giao tiếp tình huống thực tế
        val conversationLessons = allLessons.filter { it.stage == "CONVERSATION" }.sortedBy { it.orderIndex }
        // 3. Nhóm Cấu trúc câu & Ngữ pháp
        val grammarLessons = allLessons.filter { it.stage == "GRAMMAR" }.sortedBy { it.orderIndex }

        // Sử dụng ngày hiện tại (Epoch Day) làm seed xoay tua có quy tắc theo ngày
        val epochDay = java.time.LocalDate.now().toEpochDay().toInt()

        val dailyRecommended = mutableListOf<Lesson>()

        // Quy tắc 1: Mỗi ngày lấy 1 bài Nền tảng luân phiên theo lộ trình
        if (foundationLessons.isNotEmpty()) {
            val idx = Math.floorMod(epochDay, foundationLessons.size)
            dailyRecommended.add(foundationLessons[idx])
        }

        // Quy tắc 2: Mỗi ngày lấy 1 bài Giao tiếp luân phiên theo lộ trình
        if (conversationLessons.isNotEmpty()) {
            val idx = Math.floorMod(epochDay, conversationLessons.size)
            dailyRecommended.add(conversationLessons[idx])
        }

        // Quy tắc 3: Mỗi ngày lấy 1 bài Ngữ pháp / Mẫu câu luân phiên theo lộ trình
        if (grammarLessons.isNotEmpty()) {
            val idx = Math.floorMod(epochDay, grammarLessons.size)
            dailyRecommended.add(grammarLessons[idx])
        }

        // Đảm bảo luôn trả về ít nhất 2 bài học
        if (dailyRecommended.size < 2) {
            allLessons.filterNot { it in dailyRecommended }.take(2 - dailyRecommended.size).forEach {
                dailyRecommended.add(it)
            }
        }

        return dailyRecommended.map { mapToResponse(it, includeSlides = false) }
    }

    @Transactional
    fun getAllLessons(level: String? = null, category: String? = null): List<LessonResponse> {
        if (lessonRepository.count() == 0L) {
            syncLessonsFromResource(force = false)
        }
        val allLessons = lessonRepository.findAllWithSlides()
        val filtered = allLessons.filter { lesson ->
            val matchLevel = level.isNullOrBlank() || level.equals("all", ignoreCase = true) || lesson.level.equals(level, ignoreCase = true)
            val matchCategory = category.isNullOrBlank() || category.equals("all", ignoreCase = true) || lesson.category.contains(category, ignoreCase = true)
            matchLevel && matchCategory
        }
        return filtered.map { mapToResponse(it, includeSlides = true) }
    }

    @Transactional(readOnly = true)
    fun getLessonById(id: String): LessonResponse {
        val lesson = lessonRepository.findById(id)
            .orElseThrow { AppException(ErrorCode.LESSON_NOT_FOUND, "Không tìm thấy bài học với ID: $id") }
        return mapToResponse(lesson, includeSlides = true)
    }

    @Transactional
    fun completeLesson(id: String, stars: Int = 3): LessonResponse {
        val lesson = lessonRepository.findById(id)
            .orElseThrow { AppException(ErrorCode.LESSON_NOT_FOUND, "Không tìm thấy bài học với ID: $id") }
        lesson.isCompleted = true
        lesson.stars = maxOf(lesson.stars, stars.coerceIn(1, 3))
        val saved = lessonRepository.save(lesson)
        return mapToResponse(saved, includeSlides = true)
    }

    private fun mapToResponse(lesson: Lesson, includeSlides: Boolean = true): LessonResponse {
        val slides = if (includeSlides) {
            lesson.slides.map { slide ->
                SlideResponse(
                    slideIndex = slide.slideIndex,
                    category = slide.category,
                    cardType = slide.cardType,
                    slideType = slide.slideType,
                    subTitle = slide.subTitle,
                    title = slide.title,
                    pinyinVariants = slide.pinyinVariants.toList(),
                    hanziVariants = slide.hanziVariants.toList(),
                    hanViet = slide.hanViet,
                    meaning = slide.meaning,
                    explanationText = slide.explanationText,
                    explanationSubtitle = slide.explanationSubtitle,
                    explanationContent = slide.explanationContent,
                    boxColor = slide.boxColor,
                    toneRules = slide.toneRules.map { com.lumina.backend.dto.response.ToneRuleResponse(it.number, it.text) },
                    audioUrl = slide.audioUrl,
                    question = slide.question,
                    options = slide.options.toList(),
                    correctAnswerIndex = slide.correctAnswerIndex,
                    quizExplanation = slide.quizExplanation,
                    currentIndex = slide.slideIndex,
                    totalCount = lesson.totalSlides
                )
            }
        } else {
            emptyList()
        }

        val grammarStructures = try {
            if (lesson.grammarStructuresJson.isNotBlank() && lesson.grammarStructuresJson != "[]") {
                objectMapper.readValue<List<GrammarStructureResponse>>(lesson.grammarStructuresJson)
            } else {
                emptyList()
            }
        } catch (_: Exception) {
            emptyList()
        }

        val dialogues = try {
            if (lesson.dialoguesJson.isNotBlank() && lesson.dialoguesJson != "[]") {
                objectMapper.readValue<List<DialogueLineResponse>>(lesson.dialoguesJson)
            } else {
                emptyList()
            }
        } catch (_: Exception) {
            emptyList()
        }

        val coreVocabularies = try {
            if (lesson.coreVocabulariesJson.isNotBlank() && lesson.coreVocabulariesJson != "[]") {
                objectMapper.readValue<List<LessonVocabResponse>>(lesson.coreVocabulariesJson)
            } else {
                emptyList()
            }
        } catch (_: Exception) {
            emptyList()
        }

        return LessonResponse(
            id = lesson.id,
            category = lesson.category,
            categoryBgColor = lesson.categoryBgColor,
            categoryTextColor = lesson.categoryTextColor,
            level = lesson.level,
            pinyinHanziTitle = lesson.pinyinHanziTitle,
            title = lesson.title,
            description = lesson.description,
            durationMins = lesson.durationMins,
            sparks = lesson.sparks,
            stars = lesson.stars,
            isCompleted = lesson.isCompleted,
            totalSlides = lesson.totalSlides,
            stage = lesson.stage,
            orderIndex = lesson.orderIndex,
            objectives = lesson.objectives.toList(),
            grammarStructures = grammarStructures,
            dialogueContext = lesson.dialogueContext,
            dialogues = dialogues,
            coreVocabularies = coreVocabularies,
            needsReview = lesson.needsReview || (lesson.isCompleted && lesson.stars in 1..2),
            slides = slides
        )
    }

    @Transactional(readOnly = true)
    fun getReviewQuestions(id: String): List<QuickReviewQuestionResponse> {
        val lesson = lessonRepository.findById(id)
            .orElseThrow { AppException(ErrorCode.LESSON_NOT_FOUND, "Không tìm thấy bài học với ID: $id") }

        // 1. Thu thập kho từ vựng động từ toàn bộ Database (không bao giờ dùng danh sách tĩnh)
        val globalVocabPool = getAllVocabulariesFromDb()

        // 2. Thu thập từ vựng và ngữ cảnh của chính bài học hiện tại
        val currentVocabs = parseLessonVocabs(lesson.coreVocabulariesJson)

        val candidateQuestions = mutableListOf<QuickReviewQuestionResponse>()

        // 3. GENERATOR 1: Ngữ cảnh thực tế - Điền từ vào chỗ trống (Contextual Cloze Deletion)
        // Lấy câu ví dụ của từ vựng trong bài, ẩn từ khóa đi để học sinh điền từ chuẩn ngữ cảnh
        currentVocabs
            .filter { it.exampleHanzi.isNotBlank() && it.exampleHanzi.contains(it.hanzi) }
            .shuffled()
            .forEach { vocab ->
                val clozeSentence = vocab.exampleHanzi.replace(vocab.hanzi, " [ ___ ] ")
                val correctWord = vocab.hanzi

                // Chọn từ gây nhiễu từ DB có cùng độ dài chữ Hán để câu hỏi công bằng và thử thách
                val distractors = globalVocabPool
                    .filter { it.hanzi != correctWord && it.hanzi.length == correctWord.length }
                    .map { it.hanzi }
                    .distinct()
                    .shuffled()
                    .take(3)

                if (distractors.size == 3) {
                    val allOptions = (distractors + correctWord).shuffled()
                    candidateQuestions.add(
                        QuickReviewQuestionResponse(
                            id = 0,
                            hanzi = vocab.hanzi,
                            pinyin = vocab.pinyin,
                            question = "Điền từ thích hợp vào chỗ trống: \"$clozeSentence\" (${vocab.exampleMeaning.ifBlank { vocab.meaning }})",
                            options = allOptions,
                            correctIndex = allOptions.indexOf(correctWord)
                        )
                    )
                }
            }

        // 4. GENERATOR 2: Phản xạ ngược (Reverse Retrieval: Nghĩa Tiếng Việt -> Chọn Chữ Hán & Pinyin)
        currentVocabs.shuffled().forEach { vocab ->
            val correctOption = "${vocab.hanzi} (${vocab.pinyin})"

            // Ưu tiên các từ gây nhiễu cùng từ loại (partOfSpeech) từ Database
            val samePosDistractors = globalVocabPool
                .filter { it.hanzi != vocab.hanzi && it.partOfSpeech.isNotBlank() && it.partOfSpeech.equals(vocab.partOfSpeech, ignoreCase = true) }
                .map { "${it.hanzi} (${it.pinyin})" }
                .distinct()
                .shuffled()

            val fallbackDistractors = globalVocabPool
                .filter { it.hanzi != vocab.hanzi }
                .map { "${it.hanzi} (${it.pinyin})" }
                .distinct()
                .shuffled()

            val distractors = (samePosDistractors + fallbackDistractors)
                .distinct()
                .filter { it != correctOption }
                .take(3)

            if (distractors.size == 3) {
                val allOptions = (distractors + correctOption).shuffled()
                candidateQuestions.add(
                    QuickReviewQuestionResponse(
                        id = 0,
                        hanzi = vocab.hanzi,
                        pinyin = vocab.pinyin,
                        question = "Từ tiếng Trung nào có nghĩa là: \"${vocab.meaning}\"?",
                        options = allOptions,
                        correctIndex = allOptions.indexOf(correctOption)
                    )
                )
            }
        }

        // 5. GENERATOR 3: Nhận diện Hán tự -> Nghĩa Tiếng Việt (Forward Meaning Recognition)
        currentVocabs.shuffled().forEach { vocab ->
            val correctMeaning = vocab.meaning

            val distractors = globalVocabPool
                .filter { it.hanzi != vocab.hanzi && it.meaning.isNotBlank() && it.meaning != correctMeaning }
                .map { it.meaning }
                .distinct()
                .shuffled()
                .take(3)

            if (distractors.size == 3) {
                val allOptions = (distractors + correctMeaning).shuffled()
                candidateQuestions.add(
                    QuickReviewQuestionResponse(
                        id = 0,
                        hanzi = vocab.hanzi,
                        pinyin = vocab.pinyin,
                        question = "Nghĩa chính xác của chữ Hán '${vocab.hanzi}' (${vocab.pinyin}) là gì?",
                        options = allOptions,
                        correctIndex = allOptions.indexOf(correctMeaning)
                    )
                )
            }
        }

        // 6. GENERATOR 4: Nhận diện Thanh điệu & Phiên âm Pinyin (Phonetic / Pinyin Identification)
        currentVocabs.shuffled().forEach { vocab ->
            val correctPinyin = vocab.pinyin

            val distractors = globalVocabPool
                .filter { it.hanzi != vocab.hanzi && it.pinyin.isNotBlank() && it.pinyin != correctPinyin }
                .map { it.pinyin }
                .distinct()
                .shuffled()
                .take(3)

            if (distractors.size == 3) {
                val allOptions = (distractors + correctPinyin).shuffled()
                candidateQuestions.add(
                    QuickReviewQuestionResponse(
                        id = 0,
                        hanzi = vocab.hanzi,
                        pinyin = vocab.pinyin,
                        question = "Phiên âm Pinyin chuẩn xác của chữ '${vocab.hanzi}' (${vocab.meaning}) là gì?",
                        options = allOptions,
                        correctIndex = allOptions.indexOf(correctPinyin)
                    )
                )
            }
        }

        // 7. GENERATOR 5: Quiz Slides nguyên bản của bài học trong Database
        val quizSlides = lesson.slides.filter { it.options.isNotEmpty() && it.question.isNotBlank() }
        quizSlides.shuffled().forEach { slide ->
            candidateQuestions.add(
                QuickReviewQuestionResponse(
                    id = 0,
                    hanzi = slide.hanziVariants.firstOrNull() ?: "",
                    pinyin = slide.pinyinVariants.firstOrNull() ?: "",
                    question = slide.question,
                    options = slide.options.toList(),
                    correctIndex = slide.correctAnswerIndex
                )
            )
        }

        // 8. Thuật toán chọn lọc thích ứng & Đánh số ngẫu nhiên (Adaptive Sampling)
        val selectedQuestions = candidateQuestions
            .distinctBy { it.question }
            .shuffled()
            .take(4)
            .mapIndexed { index, q ->
                q.copy(id = index + 1)
            }

        // Dự phòng khi bài học chưa có đủ từ vựng trong DB
        if (selectedQuestions.isEmpty()) {
            return listOf(
                QuickReviewQuestionResponse(
                    id = 1,
                    hanzi = lesson.pinyinHanziTitle,
                    pinyin = lesson.title,
                    question = "Bài học '${lesson.title}' thuộc chủ đề nào?",
                    options = listOf(lesson.category, "Hỏi đường", "Mua sắm", "Thời tiết").shuffled(),
                    correctIndex = 0
                )
            )
        }

        return selectedQuestions
    }

    private fun getAllVocabulariesFromDb(): List<LessonVocabResponse> {
        return lessonRepository.findAll().flatMap { l ->
            parseLessonVocabs(l.coreVocabulariesJson)
        }.distinctBy { it.hanzi }
    }

    private fun parseLessonVocabs(json: String): List<LessonVocabResponse> {
        return try {
            if (json.isNotBlank() && json != "[]") {
                objectMapper.readValue<List<LessonVocabResponse>>(json)
            } else emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    @Transactional
    fun submitReview(id: String): ReviewResultResponse {
        val lesson = lessonRepository.findById(id)
            .orElseThrow { AppException(ErrorCode.LESSON_NOT_FOUND, "Không tìm thấy bài học với ID: $id") }

        lesson.needsReview = false
        lesson.stars = 3
        lesson.sparks += 15
        lessonRepository.save(lesson)

        return ReviewResultResponse(
            lessonId = lesson.id,
            sparksBonus = 15,
            newStars = 3,
            message = "Chúc mừng bạn đã ôn tập thành công! Đã khôi phục 3 sao và nhận 15 Tia sáng."
        )
    }

    @Transactional(readOnly = true)
    fun getExamQuestions(level: String): List<ExamQuestionResponse> {
        val normalizedLevel = if (level.isBlank()) "HSK 1" else level.trim()

        // 1. Nếu Database đã có cấu hình câu hỏi thi riêng thì ưu tiên dùng
        val existingQuestions = examQuestionRepository.findByHskLevelOrderByOrderIndexAsc(normalizedLevel)
        if (existingQuestions.isNotEmpty()) {
            return existingQuestions.map { q ->
                ExamQuestionResponse(
                    id = q.id.toInt(),
                    typeTitle = q.typeTitle,
                    question = q.question,
                    pinyinHint = q.pinyinHint,
                    options = q.options.toList(),
                    correctIndex = q.correctIndex,
                    explanation = q.explanation
                )
            }
        }

        // 2. Thuật toán sinh đề thi vượt cấp tổng hợp ĐỘNG 100% từ toàn bộ bài học trong Database
        val allLessons = lessonRepository.findAllWithSlides()
        val lessonsInLevel = allLessons.filter { it.level.equals(normalizedLevel, ignoreCase = true) }
            .ifEmpty { allLessons }

        val levelVocabs = lessonsInLevel.flatMap { parseLessonVocabs(it.coreVocabulariesJson) }.distinctBy { it.hanzi }
        val levelGrammars = lessonsInLevel.flatMap { parseLessonGrammar(it.grammarStructuresJson) }
        val levelQuizSlides = lessonsInLevel.flatMap { it.slides }.filter { it.options.isNotEmpty() && it.question.isNotBlank() }
        val globalVocabPool = allLessons.flatMap { parseLessonVocabs(it.coreVocabulariesJson) }.distinctBy { it.hanzi }

        val candidateExamQuestions = mutableListOf<ExamQuestionResponse>()

        // Type 1: Kiểm tra Ngữ âm & Thanh điệu Pinyin từ kho từ vựng thật của cấp độ
        levelVocabs.shuffled().take(4).forEach { vocab ->
            val correctPinyin = vocab.pinyin
            val distractors = globalVocabPool
                .filter { it.hanzi != vocab.hanzi && it.pinyin.isNotBlank() && it.pinyin != correctPinyin }
                .map { it.pinyin }
                .distinct()
                .shuffled()
                .take(3)

            if (distractors.size == 3) {
                val opts = (distractors + correctPinyin).shuffled()
                candidateExamQuestions.add(
                    ExamQuestionResponse(
                        id = 0,
                        typeTitle = "Nhận diện Pinyin & Thanh điệu",
                        question = "Phiên âm Pinyin chuẩn xác của chữ \"${vocab.hanzi}\" (${vocab.meaning}) là gì?",
                        pinyinHint = "Kiểm tra ngữ âm cấp độ $normalizedLevel",
                        options = opts,
                        correctIndex = opts.indexOf(correctPinyin),
                        explanation = "Chữ \"${vocab.hanzi}\" có phiên âm là ${vocab.pinyin}, nghĩa là \"${vocab.meaning}\"."
                    )
                )
            }
        }

        // Type 2: Từ vựng cốt lõi & Phản xạ Hán tự (Tiếng Việt -> Hán tự kèm Pinyin)
        levelVocabs.shuffled().take(4).forEach { vocab ->
            val correctOpt = "${vocab.hanzi} (${vocab.pinyin})"
            val distractors = globalVocabPool
                .filter { it.hanzi != vocab.hanzi }
                .map { "${it.hanzi} (${it.pinyin})" }
                .distinct()
                .shuffled()
                .take(3)

            if (distractors.size == 3) {
                val opts = (distractors + correctOpt).shuffled()
                candidateExamQuestions.add(
                    ExamQuestionResponse(
                        id = 0,
                        typeTitle = "Từ vựng cốt lõi",
                        question = "\"${vocab.meaning}\" trong tiếng Trung là từ nào sau đây?",
                        pinyinHint = "Từ vựng trọng tâm $normalizedLevel",
                        options = opts,
                        correctIndex = opts.indexOf(correctOpt),
                        explanation = "\"${vocab.meaning}\" là \"${vocab.hanzi}\" (${vocab.pinyin})."
                    )
                )
            }
        }

        // Type 3: Điền từ vào ngữ cảnh câu hoàn chỉnh (Contextual Cloze Deletion)
        levelVocabs
            .filter { it.exampleHanzi.isNotBlank() && it.exampleHanzi.contains(it.hanzi) }
            .shuffled()
            .take(4)
            .forEach { vocab ->
                val clozeSentence = vocab.exampleHanzi.replace(vocab.hanzi, " [ ___ ] ")
                val correctWord = vocab.hanzi
                val distractors = globalVocabPool
                    .filter { it.hanzi != correctWord && it.hanzi.length == correctWord.length }
                    .map { it.hanzi }
                    .distinct()
                    .shuffled()
                    .take(3)

                if (distractors.size == 3) {
                    val opts = (distractors + correctWord).shuffled()
                    candidateExamQuestions.add(
                        ExamQuestionResponse(
                            id = 0,
                            typeTitle = "Hoàn thành câu & Ngữ cảnh",
                            question = "Điền từ thích hợp vào chỗ trống: \"$clozeSentence\"",
                            pinyinHint = vocab.exampleMeaning.ifBlank { "Nghĩa: ${vocab.meaning}" },
                            options = opts,
                            correctIndex = opts.indexOf(correctWord),
                            explanation = "Câu hoàn chỉnh: \"${vocab.exampleHanzi}\" (${vocab.exampleMeaning.ifBlank { vocab.meaning }})."
                        )
                    )
                }
            }

        // Type 4: Cấu trúc câu & Điểm ngữ pháp từ Database
        levelGrammars.shuffled().take(3).forEach { grammar ->
            val sampleEx = grammar.examples.firstOrNull { it.hanzi.isNotBlank() && it.meaning.isNotBlank() }
            if (sampleEx != null) {
                val correctOpt = "${sampleEx.hanzi} (${sampleEx.meaning})"
                val fallbackDistractors = listOf(
                    "明天见 (Hẹn gặp lại ngày mai)",
                    "不客气 (Không có chi)",
                    "对不起 (Xin lỗi)"
                )
                val opts = (fallbackDistractors + correctOpt).shuffled()
                candidateExamQuestions.add(
                    ExamQuestionResponse(
                        id = 0,
                        typeTitle = "Cấu trúc câu & Ngữ pháp",
                        question = "Cấu trúc \"${grammar.title}\" (${grammar.formula}) được áp dụng trong câu nào?",
                        pinyinHint = grammar.explanation.take(60),
                        options = opts,
                        correctIndex = opts.indexOf(correctOpt),
                        explanation = "${grammar.explanation}. Ví dụ chuẩn: ${sampleEx.hanzi} (${sampleEx.meaning})."
                    )
                )
            }
        }

        // Type 5: Trích xuất các câu Quiz Slides nguyên bản của bài học
        levelQuizSlides.shuffled().take(4).forEach { slide ->
            candidateExamQuestions.add(
                ExamQuestionResponse(
                    id = 0,
                    typeTitle = "Tổng hợp kiến thức bài học",
                    question = slide.question,
                    pinyinHint = slide.pinyinVariants.firstOrNull(),
                    options = slide.options.toList(),
                    correctIndex = slide.correctAnswerIndex,
                    explanation = slide.quizExplanation.ifBlank { "Đáp án chuẩn: ${slide.options.getOrNull(slide.correctAnswerIndex) ?: ""}" }
                )
            )
        }

        // Chọn lọc 8 câu hỏi phong phú nhất, đánh số thứ tự câu
        val exam = candidateExamQuestions
            .distinctBy { it.question }
            .shuffled()
            .take(8)
            .mapIndexed { index, q ->
                q.copy(
                    id = index + 1,
                    typeTitle = "${index + 1}. ${q.typeTitle}"
                )
            }

        return exam
    }

    private fun parseLessonGrammar(json: String): List<GrammarStructureResponse> {
        return try {
            if (json.isNotBlank() && json != "[]") {
                objectMapper.readValue<List<GrammarStructureResponse>>(json)
            } else emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    @Transactional
    fun claimChestReward(unitNumber: Int): ChestRewardResponse {
        val claim = ChestClaim(
            unitNumber = unitNumber,
            hskLevel = "HSK 1",
            sparksEarned = 50,
            expEarned = 100,
            claimedAt = java.time.LocalDateTime.now()
        )
        chestClaimRepository.save(claim)

        return ChestRewardResponse(
            unitNumber = unitNumber,
            sparksEarned = claim.sparksEarned,
            expEarned = claim.expEarned,
            message = "Chúc mừng bạn đã mở rương Phần $unitNumber thành công!"
        )
    }

    @Transactional(readOnly = true)
    fun getJourneyUnits(level: String): List<JourneyUnitResponse> {
        val normalizedLevel = if (level.isBlank()) "HSK 1" else level.trim()
        val units = journeyUnitRepository.findByHskLevelOrderByUnitNumberAsc(normalizedLevel)
        if (units.isNotEmpty()) {
            return units.map { u ->
                JourneyUnitResponse(
                    unitNumber = u.unitNumber,
                    title = u.title,
                    description = u.description,
                    icon = u.icon,
                    gradientStart = u.gradientStart,
                    gradientEnd = u.gradientEnd
                )
            }
        }

        // Nếu bảng journey_units chưa có dữ liệu riêng, tự động tổng hợp động từ các bài học trong Database
        val lessonsInLevel = lessonRepository.findAll()
            .filter { it.level.equals(normalizedLevel, ignoreCase = true) }
            .sortedBy { it.orderIndex }

        val gradients = listOf(
            Pair("#4F46E5", "#6366F1"),
            Pair("#0D9488", "#10B981"),
            Pair("#D97706", "#F59E0B"),
            Pair("#0284C7", "#06B6D4")
        )

        val stages = lessonsInLevel.map { it.stage }.distinct()
        val dynamicUnits = mutableListOf<JourneyUnitResponse>()
        var unitCount = 1

        stages.forEachIndexed { idx, stageKey ->
            val stageLessons = lessonsInLevel.filter { it.stage == stageKey }
            if (stageLessons.isNotEmpty()) {
                val grad = gradients[idx % gradients.size]
                val firstLesson = stageLessons.first()
                val icon = when (stageKey) {
                    "FOUNDATION" -> "🗣️"
                    "CONVERSATION" -> "💬"
                    "GRAMMAR" -> "📐"
                    "BUSINESS" -> "💼"
                    "ACADEMIC" -> "🎓"
                    else -> "⭐"
                }
                dynamicUnits.add(
                    JourneyUnitResponse(
                        unitNumber = unitCount++,
                        title = firstLesson.category.uppercase(),
                        description = "Gồm ${stageLessons.size} bài học: " + stageLessons.take(3).joinToString(", ") { it.title },
                        icon = icon,
                        gradientStart = grad.first,
                        gradientEnd = grad.second
                    )
                )
            }
        }

        if (dynamicUnits.isEmpty()) {
            dynamicUnits.add(
                JourneyUnitResponse(
                    unitNumber = 1,
                    title = "LỘ TRÌNH $normalizedLevel",
                    description = "Chinh phục toàn bộ các bài học trong cấp độ $normalizedLevel",
                    icon = "⭐",
                    gradientStart = "#4F46E5",
                    gradientEnd = "#6366F1"
                )
            )
        }

        return dynamicUnits
    }

    @Transactional
    fun syncLessonsFromResource(force: Boolean = false): Int {
        if (!force && lessonRepository.count() > 0L) {
            return lessonRepository.count().toInt()
        }

        try {
            val inputStream = javaClass.classLoader.getResourceAsStream("data/lessons_full.json")
                ?: return 0

            val rootNode = objectMapper.readTree(inputStream)
            val lessons = mutableListOf<Lesson>()

            rootNode.forEach { node ->
                val id = node.get("id")?.asText() ?: java.util.UUID.randomUUID().toString()
                val category = node.get("category")?.asText() ?: ""
                val categoryBgColor = node.get("categoryBgColor")?.asText() ?: "#F3E8FF"
                val categoryTextColor = node.get("categoryTextColor")?.asText() ?: "#7E22CE"
                val level = node.get("level")?.asText() ?: "HSK 1"
                val pinyinHanziTitle = node.get("pinyinHanziTitle")?.asText() ?: ""
                val title = node.get("title")?.asText() ?: ""
                val description = node.get("description")?.asText() ?: ""
                val durationMins = node.get("durationMins")?.asInt() ?: 3
                val sparks = node.get("sparks")?.asInt() ?: 20
                val stars = node.get("stars")?.asInt() ?: 0
                val isCompleted = node.get("isCompleted")?.asBoolean() ?: false
                val needsReview = node.get("needsReview")?.asBoolean() ?: false
                val isRecommended = node.get("isRecommended")?.asBoolean() ?: true
                val totalSlides = node.get("totalSlides")?.asInt() ?: 0
                val stage = node.get("stage")?.asText() ?: "FOUNDATION"
                val orderIndex = node.get("orderIndex")?.asInt() ?: 1

                val objectivesList = mutableListOf<String>()
                node.get("objectives")?.forEach { obj -> objectivesList.add(obj.asText()) }

                val grammarStructuresJson = node.get("grammarStructuresJson")?.let {
                    if (it.isTextual) it.asText() else objectMapper.writeValueAsString(it)
                } ?: "[]"

                val dialogueContext = node.get("dialogueContext")?.asText() ?: ""

                val dialoguesJson = node.get("dialoguesJson")?.let {
                    if (it.isTextual) it.asText() else objectMapper.writeValueAsString(it)
                } ?: "[]"

                val coreVocabulariesJson = node.get("coreVocabulariesJson")?.let {
                    if (it.isTextual) it.asText() else objectMapper.writeValueAsString(it)
                } ?: "[]"

                val lesson = Lesson(
                    id = id,
                    category = category,
                    categoryBgColor = categoryBgColor,
                    categoryTextColor = categoryTextColor,
                    level = level,
                    pinyinHanziTitle = pinyinHanziTitle,
                    title = title,
                    description = description,
                    durationMins = durationMins,
                    sparks = sparks,
                    stars = stars,
                    isCompleted = isCompleted,
                    needsReview = needsReview,
                    isRecommended = isRecommended,
                    totalSlides = totalSlides,
                    stage = stage,
                    orderIndex = orderIndex,
                    objectives = objectivesList,
                    grammarStructuresJson = grammarStructuresJson,
                    dialogueContext = dialogueContext,
                    dialoguesJson = dialoguesJson,
                    coreVocabulariesJson = coreVocabulariesJson,
                    slides = mutableListOf()
                )

                node.get("slides")?.forEach { sNode ->
                    val slideIndex = sNode.get("slideIndex")?.asInt() ?: 1
                    val slideCategory = sNode.get("category")?.asText() ?: category
                    val cardType = sNode.get("cardType")?.asText() ?: "Khái niệm"
                    val slideTypeStr = sNode.get("slideType")?.asText() ?: "CONCEPT"
                    val slideType = try {
                        SlideType.valueOf(slideTypeStr)
                    } catch (_: Exception) {
                        SlideType.CONCEPT
                    }
                    val subTitle = sNode.get("subTitle")?.asText() ?: ""
                    val slideTitle = sNode.get("title")?.asText() ?: ""

                    val pinyinVariants = mutableListOf<String>()
                    sNode.get("pinyinVariants")?.forEach { pinyinVariants.add(it.asText()) }

                    val hanziVariants = mutableListOf<String>()
                    sNode.get("hanziVariants")?.forEach { hanziVariants.add(it.asText()) }

                    val hanViet = sNode.get("hanViet")?.asText() ?: ""
                    val meaning = sNode.get("meaning")?.asText() ?: ""
                    val explanationText = sNode.get("explanationText")?.asText() ?: ""
                    val explanationSubtitle = sNode.get("explanationSubtitle")?.asText() ?: ""
                    val explanationContent = sNode.get("explanationContent")?.asText() ?: ""
                    val boxColor = sNode.get("boxColor")?.asText() ?: ""
                    val audioUrl = sNode.get("audioUrl")?.asText() ?: ""

                    val toneRules = mutableListOf<ToneRuleItemEntity>()
                    sNode.get("toneRules")?.forEach { trNode ->
                        val num = trNode.get("number")?.asInt() ?: 1
                        val text = trNode.get("text")?.asText() ?: ""
                        toneRules.add(ToneRuleItemEntity(number = num, text = text))
                    }

                    val question = sNode.get("question")?.asText() ?: ""
                    val options = mutableListOf<String>()
                    sNode.get("options")?.forEach { options.add(it.asText()) }
                    val correctAnswerIndex = sNode.get("correctAnswerIndex")?.asInt() ?: -1
                    val quizExplanation = sNode.get("quizExplanation")?.asText() ?: ""

                    val slide = LessonSlide(
                        lesson = lesson,
                        slideIndex = slideIndex,
                        category = slideCategory,
                        cardType = cardType,
                        slideType = slideType,
                        subTitle = subTitle,
                        title = slideTitle,
                        pinyinVariants = pinyinVariants,
                        hanziVariants = hanziVariants,
                        hanViet = hanViet,
                        meaning = meaning,
                        explanationText = explanationText,
                        explanationSubtitle = explanationSubtitle,
                        explanationContent = explanationContent,
                        boxColor = boxColor,
                        toneRules = toneRules,
                        audioUrl = audioUrl,
                        question = question,
                        options = options,
                        correctAnswerIndex = correctAnswerIndex,
                        quizExplanation = quizExplanation
                    )
                    lesson.slides.add(slide)
                }

                lessons.add(lesson)
            }

            if (force) {
                lessonRepository.deleteAll()
            }

            if (lessons.isNotEmpty()) {
                lessonRepository.saveAll(lessons)
            }

            return lessons.size
        } catch (e: Exception) {
            println(">>> Lỗi khi nạp dữ liệu lessons_full.json: ${e.message}")
            e.printStackTrace()
            return 0
        }
    }

    @Transactional
    fun syncGrammarFromResource(force: Boolean = false): Int {
        if (!force && grammarStructureRepository.count() > 0L) {
            return grammarStructureRepository.count().toInt()
        }

        try {
            val inputStream = javaClass.classLoader.getResourceAsStream("data/hsk_grammar_full.json")
                ?: return 0

            val rootNode = objectMapper.readTree(inputStream)
            val entities = mutableListOf<GrammarStructureEntity>()

            rootNode.forEach { node ->
                val id = node.get("id")?.asText() ?: java.util.UUID.randomUUID().toString()
                val hskLevel = node.get("hskLevel")?.asText() ?: "HSK 1"
                val structureOrder = node.get("structureOrder")?.asInt() ?: 1
                val title = node.get("title")?.asText() ?: ""
                val formula = node.get("formula")?.asText() ?: ""
                val explanation = node.get("explanation")?.asText() ?: ""
                val examTip = node.get("examTip")?.asText()
                val examplesNode = node.get("examples")
                val examplesJson = if (examplesNode != null) objectMapper.writeValueAsString(examplesNode) else "[]"

                entities.add(
                    GrammarStructureEntity(
                        id = id,
                        hskLevel = hskLevel,
                        structureOrder = structureOrder,
                        title = title,
                        formula = formula,
                        explanation = explanation,
                        examTip = examTip,
                        examplesJson = examplesJson
                    )
                )
            }

            if (force) {
                grammarStructureRepository.deleteAll()
            }

            if (entities.isNotEmpty()) {
                grammarStructureRepository.saveAll(entities)
            }

            return entities.size
        } catch (e: Exception) {
            println(">>> Lỗi khi nạp dữ liệu hsk_grammar_full.json: ${e.message}")
            return 0
        }
    }

    @Transactional
    fun getGrammarStructuresByLevel(level: String?): List<GrammarStructureResponse> {
        // Tự động nạp 72 cấu trúc từ JSON vào bảng PostgreSQL nếu database chưa có bản ghi
        if (grammarStructureRepository.count() == 0L) {
            syncGrammarFromResource(force = false)
        }

        val entities = if (level.isNullOrBlank() || level.equals("all", ignoreCase = true)) {
            grammarStructureRepository.findAllByOrderByHskLevelAscStructureOrderAsc()
        } else {
            val normalizedLevel = if (level.startsWith("HSK", ignoreCase = true)) level.uppercase() else "HSK $level"
            grammarStructureRepository.findByHskLevelIgnoreCaseOrderByStructureOrderAsc(normalizedLevel)
        }

        if (entities.isNotEmpty()) {
            return entities.map { entity ->
                GrammarStructureResponse(
                    structureOrder = entity.structureOrder,
                    title = entity.title,
                    formula = entity.formula,
                    explanation = entity.explanation,
                    examples = parseLessonGrammarExamples(entity.examplesJson),
                    examTip = entity.examTip
                )
            }
        }

        // Fallback đọc từ lessons nếu chưa nạp được vào bảng riêng
        val allLessons = lessonRepository.findAllWithSlides()
        val lessons = if (level.isNullOrBlank() || level.equals("all", ignoreCase = true)) {
            allLessons
        } else {
            val normalizedLevel = if (level.startsWith("HSK", ignoreCase = true)) level.uppercase() else "HSK $level"
            allLessons.filter { it.level.equals(normalizedLevel, ignoreCase = true) }
        }

        return lessons
            .sortedBy { it.orderIndex }
            .flatMap { parseLessonGrammar(it.grammarStructuresJson) }
    }

    private fun parseLessonGrammarExamples(json: String): List<com.lumina.backend.dto.response.GrammarExampleResponse> {
        return try {
            if (json.isNotBlank() && json != "[]") {
                objectMapper.readValue<List<com.lumina.backend.dto.response.GrammarExampleResponse>>(json)
            } else emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }
}

