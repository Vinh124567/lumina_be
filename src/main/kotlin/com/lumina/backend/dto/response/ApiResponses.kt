package com.lumina.backend.dto.response

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val errorCode: String? = null,
    val data: T? = null
) {
    companion object {
        fun <T> success(data: T, message: String = "Thành công"): ApiResponse<T> =
            ApiResponse(success = true, message = message, errorCode = null, data = data)

        fun <T> error(
            message: String,
            errorCode: String = "ERROR",
            data: T? = null
        ): ApiResponse<T> =
            ApiResponse(success = false, message = message, errorCode = errorCode, data = data)

        fun <T> error(
            errorCode: com.lumina.backend.exception.ErrorCode,
            message: String = errorCode.defaultMessage,
            data: T? = null
        ): ApiResponse<T> =
            ApiResponse(success = false, message = message, errorCode = errorCode.code, data = data)
    }
}

data class UserResponse(
    val id: Long,
    val email: String,
    val fullName: String
)

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long,
    val user: UserResponse
)

data class ToneRuleResponse(
    val number: Int,
    val text: String
)

data class SlideResponse(
    val slideIndex: Int,
    val category: String,
    val cardType: String,
    val slideType: com.lumina.backend.model.entity.SlideType = com.lumina.backend.model.entity.SlideType.CONCEPT,
    val subTitle: String,
    val title: String,
    val pinyinVariants: List<String> = emptyList(),
    val hanziVariants: List<String> = emptyList(),
    val hanViet: String = "",
    val meaning: String = "",
    val explanationText: String = "",
    val explanationSubtitle: String = "",
    val explanationContent: String = "",
    val boxColor: String = "",
    val toneRules: List<ToneRuleResponse> = emptyList(),
    val audioUrl: String = "",
    // Quiz fields
    val question: String = "",
    val options: List<String> = emptyList(),
    val correctAnswerIndex: Int = -1,
    val quizExplanation: String = "",
    val currentIndex: Int,
    val totalCount: Int
)

data class GrammarExampleResponse(
    val hanzi: String,
    val pinyinOriginal: String = "",
    val pinyinActual: String = "",
    val meaning: String = "",
    val tip: String? = null,
    val warning: String? = null,
    val audioText: String? = null
)

data class GrammarStructureResponse(
    val structureOrder: Int,
    val title: String,
    val formula: String,
    val explanation: String,
    val examples: List<GrammarExampleResponse> = emptyList(),
    val examTip: String? = null
)

data class DialogueLineResponse(
    val speakerRole: String,
    val speakerName: String,
    val chinese: String,
    val pinyin: String,
    val vietnamese: String,
    val badgeColor: String = "#5538EE"
)

data class LessonVocabResponse(
    val hanzi: String,
    val pinyin: String,
    val hanViet: String,
    val meaning: String,
    val partOfSpeech: String,
    val exampleHanzi: String,
    val examplePinyin: String,
    val exampleMeaning: String
)

data class LessonResponse(
    val id: String,
    val category: String,
    val categoryBgColor: String,
    val categoryTextColor: String,
    val level: String,
    val pinyinHanziTitle: String,
    val title: String,
    val description: String,
    val durationMins: Int,
    val sparks: Int,
    val isCompleted: Boolean,
    val totalSlides: Int,
    val stage: String = "FOUNDATION",
    val orderIndex: Int = 1,
    val objectives: List<String> = emptyList(),
    val grammarStructures: List<GrammarStructureResponse> = emptyList(),
    val dialogueContext: String = "",
    val dialogues: List<DialogueLineResponse> = emptyList(),
    val coreVocabularies: List<LessonVocabResponse> = emptyList(),
    val slides: List<SlideResponse>
)

data class DailyWisdomResponse(
    val id: Long,
    val chinese: String,
    val pinyin: String,
    val vietnamese: String,
    val meaning: String = "",
    val author: String
)

data class TopicResponse(
    val id: String,
    val name: String,
    val icon: String,
    val count: Int
)

data class VocabularyResponse(
    val id: String,
    val hanzi: String,
    val pinyin: String,
    val hanViet: String,
    val meaning: String,
    val partOfSpeech: String,
    val topic: String,
    val radical: String,
    val strokes: String,
    val exampleHanzi: String,
    val examplePinyin: String,
    val exampleMeaning: String,
    val hskLevel: String,
    val targetScore: String,
    val isMastered: Boolean,
    val userId: Long? = null,
    val srsRepetition: Int = 0,
    val srsIntervalDays: Int = 0,
    val srsEaseFactor: Float = 2.5f,
    val nextReviewTimeMillis: Long = 0L,
    val lastReviewTimeMillis: Long = 0L
)

data class HskLevelResponse(
    val id: String,
    val title: String,
    val scoreRange: String? = null,
    val count: Int = 0
)
