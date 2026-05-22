package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizoraDao {

    // --- User Progress ---
    @Query("SELECT * FROM user_progress WHERE id = 1")
    fun getProgressFlow(): Flow<UserProgress?>

    @Query("SELECT * FROM user_progress WHERE id = 1")
    suspend fun getProgress(): UserProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: UserProgress)

    // --- CBSE Quiz Questions ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<CBSEQuestion>)

    @Query("SELECT * FROM cbse_questions WHERE clazz = :clazz AND language = :language AND subject = :subject AND chapter = :chapter")
    suspend fun getQuestionsForChapter(clazz: String, language: String, subject: String, chapter: String): List<CBSEQuestion>

    @Query("SELECT COUNT(*) FROM cbse_questions")
    suspend fun getQuestionCount(): Int

    // --- CBSE Short Notes ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotes(notes: List<CBSENotes>)

    @Query("SELECT * FROM cbse_notes WHERE clazz = :clazz AND language = :language AND subject = :subject AND chapter = :chapter LIMIT 1")
    suspend fun getNotesForChapter(clazz: String, language: String, subject: String, chapter: String): CBSENotes?

    @Query("SELECT COUNT(*) FROM cbse_notes")
    suspend fun getNotesCount(): Int
}
