package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cbse_notes")
data class CBSENotes(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val clazz: String,          // e.g. "Class 10"
    val language: String,       // "English" or "Hindi"
    val subject: String,        // "Science"
    val chapter: String,        // "Light"
    val summary: String,
    val keyPoints: String,      // Raw newline-separated or formatted text
    val definitions: String,    // Raw formatted text
    val formulas: String,       // Raw formatted text
    val importantLines: String, // Critical CBSE textbook lines
    val revisionTricks: String  // Mnemonics / tricks for fast revision
)
