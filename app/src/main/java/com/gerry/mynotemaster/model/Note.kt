package com.gerry.mynotemaster.model

/**
 * Note adalah "cetakan" (blueprint) satu catatan.
 */
data class Note(
    val id: Long = 0,
    val content: String,
    val color: Long = 0xFFFFF9C4, // Default Kuning Muda
    val isPinned: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
