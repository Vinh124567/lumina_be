package com.lumina.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LuminaApplication

fun main(args: Array<String>) {
    runApplication<LuminaApplication>(*args)
}
