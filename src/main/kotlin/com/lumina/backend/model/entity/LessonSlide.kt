package com.lumina.backend.model.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*

@Embeddable
class ToneRuleItemEntity(
    @Column(nullable = false)
    var number: Int = 1,

    @Column(nullable = false, length = 1024)
    var text: String = ""
)

enum class SlideType {
    CONCEPT,
    INTERACTIVE,
    QUIZ,
    TAKEAWAY
}

@Entity
@Table(name = "lesson_slides")
class LessonSlide(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    @JsonIgnore
    var lesson: Lesson? = null,

    @Column(nullable = false)
    var slideIndex: Int = 1,

    @Column(nullable = false)
    var category: String = "",

    @Column(nullable = false)
    var cardType: String = "Khái niệm",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var slideType: SlideType = SlideType.CONCEPT,

    @Column(nullable = false)
    var subTitle: String = "",

    @Column(nullable = false)
    var title: String = "",

    @ElementCollection
    @CollectionTable(name = "slide_pinyin_variants", joinColumns = [JoinColumn(name = "slide_id")])
    @Column(name = "variant")
    var pinyinVariants: MutableList<String> = mutableListOf(),

    @ElementCollection
    @CollectionTable(name = "slide_hanzi_variants", joinColumns = [JoinColumn(name = "slide_id")])
    @Column(name = "hanzi")
    var hanziVariants: MutableList<String> = mutableListOf(),

    @Column(columnDefinition = "TEXT")
    var hanViet: String = "",

    @Column(columnDefinition = "TEXT")
    var meaning: String = "",

    @Column(columnDefinition = "TEXT")
    var explanationText: String = "",

    @Column(columnDefinition = "TEXT")
    var explanationSubtitle: String = "",

    @Column(columnDefinition = "TEXT")
    var explanationContent: String = "",

    @Column(length = 32)
    var boxColor: String = "",

    @ElementCollection
    @CollectionTable(name = "slide_tone_rules", joinColumns = [JoinColumn(name = "slide_id")])
    var toneRules: MutableList<ToneRuleItemEntity> = mutableListOf(),

    @Column(length = 1024)
    var audioUrl: String = "",

    // Quiz fields
    @Column(columnDefinition = "TEXT")
    var question: String = "",

    @ElementCollection
    @CollectionTable(name = "slide_quiz_options", joinColumns = [JoinColumn(name = "slide_id")])
    @Column(name = "option_text")
    @OrderColumn(name = "option_order")
    var options: MutableList<String> = mutableListOf(),

    var correctAnswerIndex: Int = -1,

    @Column(columnDefinition = "TEXT")
    var quizExplanation: String = ""
)
