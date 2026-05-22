package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgress(
    @PrimaryKey val id: Int = 1,
    val xp: Int = 150, // Starter XP
    val streak: Int = 3, // Current active daily streak
    val selectedLanguage: String = "", // Selected Medium: "English" or "Hindi" (empty means selection needed)
    val selectedClass: String = "Class 10", // Default class
    val badgesUnlocked: String = "Quiz Rookie", // Comma-separated list of badges
    val completedChapters: String = "Chemical Reactions", // Chapters completed (comma-separated with Subject)
    val revisionChapters: String = "", // Chapters marked for revision
    val dailyChallengeCompletedToday: Boolean = false,
    val lastChallengeDate: String = "" // ISO date string or formatting like "2026-05-22"
)
