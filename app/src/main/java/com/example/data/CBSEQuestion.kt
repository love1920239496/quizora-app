package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cbse_questions")
data class CBSEQuestion(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val clazz: String,         // e.g. "Class 10", "Class 12"
    val language: String,      // "English" or "Hindi"
    val subject: String,       // "Science", "Mathematics", "Physics", etc.
    val chapter: String,       // "Light - Reflection & Refraction" etc.
    val question: String,
    val option1: String,
    val option2: String,
    val option3: String,
    val option4: String,
    val correctIndex: Int,     // 0 for option1, 1 for option2, etc.
    val explanation: String
) {
    fun getOptions(): List<String> = listOf(option1, option2, option3, option4)
}
