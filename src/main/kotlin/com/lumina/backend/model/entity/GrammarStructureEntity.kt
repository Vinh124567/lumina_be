package com.lumina.backend.model.entity

import jakarta.persistence.*

@Entity
@Table(name = "grammar_structures")
class GrammarStructureEntity(
    @Id
    @Column(length = 64)
    var id: String = "",

    @Column(nullable = false, length = 30)
    var hskLevel: String = "HSK 1",

    @Column(nullable = false)
    var structureOrder: Int = 1,

    @Column(nullable = false, length = 255)
    var title: String = "",

    @Column(nullable = false, columnDefinition = "TEXT")
    var formula: String = "",

    @Column(nullable = false, columnDefinition = "TEXT")
    var explanation: String = "",

    @Column(columnDefinition = "TEXT")
    var examTip: String? = null,

    @Column(nullable = false, columnDefinition = "TEXT")
    var examplesJson: String = "[]"
)
