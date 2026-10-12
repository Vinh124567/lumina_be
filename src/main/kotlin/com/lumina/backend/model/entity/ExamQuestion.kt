package com.lumina.backend.model.entity

import jakarta.persistence.*

@Entity
@Table(name = "exam_questions")
class ExamQuestion(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false, length = 30)
    var hskLevel: String = "HSK 1",

    @Column(nullable = false, length = 100)
    var typeTitle: String = "",

    @Column(nullable = false, columnDefinition = "TEXT")
    var question: String = "",

    @Column(length = 255)
    var pinyinHint: String? = null,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "exam_question_options", joinColumns = [JoinColumn(name = "question_id")])
    @Column(name = "option_text")
    @OrderColumn(name = "option_order")
    var options: MutableList<String> = mutableListOf(),

    @Column(nullable = false)
    var correctIndex: Int = 0,

    @Column(columnDefinition = "TEXT")
    var explanation: String = "",

    @Column(nullable = false)
    var orderIndex: Int = 0
)
