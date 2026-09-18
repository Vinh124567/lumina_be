package com.lumina.backend.model.entity

import jakarta.persistence.*

@Entity
@Table(name = "lessons")
class Lesson(
    @Id
    var id: String = "",

    @Column(nullable = false)
    var category: String = "",

    @Column(nullable = false)
    var categoryBgColor: String = "#F3E8FF",

    @Column(nullable = false)
    var categoryTextColor: String = "#7E22CE",

    @Column(nullable = false)
    var level: String = "Cơ bản",

    @Column(nullable = false)
    var pinyinHanziTitle: String = "",

    @Column(nullable = false)
    var title: String = "",

    @Column(columnDefinition = "TEXT")
    var description: String = "",

    @Column(nullable = false)
    var durationMins: Int = 0,

    @Column(nullable = false)
    var sparks: Int = 0,

    @Column(nullable = false)
    var isCompleted: Boolean = false,

    @Column(nullable = false)
    var isRecommended: Boolean = true,

    @Column(nullable = false)
    var totalSlides: Int = 0,

    @Column(nullable = false)
    var stage: String = "FOUNDATION",

    @Column(nullable = false)
    var orderIndex: Int = 1,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "lesson_objectives", joinColumns = [JoinColumn(name = "lesson_id")])
    @Column(name = "objective", columnDefinition = "TEXT")
    @OrderColumn(name = "objective_order")
    var objectives: MutableList<String> = mutableListOf(),

    @Column(columnDefinition = "TEXT")
    var grammarStructuresJson: String = "[]",

    @Column(columnDefinition = "TEXT")
    var dialogueContext: String = "",

    @Column(columnDefinition = "TEXT")
    var dialoguesJson: String = "[]",

    @Column(columnDefinition = "TEXT")
    var coreVocabulariesJson: String = "[]",

    @OneToMany(mappedBy = "lesson", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("slideIndex ASC")
    var slides: MutableList<LessonSlide> = mutableListOf()
)
