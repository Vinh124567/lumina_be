package com.lumina.backend.config

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.core.annotation.Order
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

@Component
@Order(1)
class DatabaseMigrationRunner(
    private val jdbcTemplate: JdbcTemplate
) : CommandLineRunner {

    private val logger = LoggerFactory.getLogger(DatabaseMigrationRunner::class.java)

    override fun run(vararg args: String?) {
        try {
            logger.info("Executing database migration check...")
            jdbcTemplate.execute("ALTER TABLE lessons ADD COLUMN IF NOT EXISTS stars integer NOT NULL DEFAULT 0;")
            jdbcTemplate.execute("ALTER TABLE lessons ADD COLUMN IF NOT EXISTS needs_review boolean NOT NULL DEFAULT false;")
            logger.info("Database migration check completed: columns 'stars' and 'needs_review' ensured on 'lessons' table.")
        } catch (e: Exception) {
            logger.warn("Database migration check note: ${e.message}")
        }
    }
}
