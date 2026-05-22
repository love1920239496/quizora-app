package com.example.data

import android.content.Context
import com.example.SafeAssetHelper
import kotlinx.coroutines.flow.Flow
import org.json.JSONObject

class QuizoraRepository(private val dao: QuizoraDao, private val context: Context) {

    val userProgressFlow: Flow<UserProgress?> = dao.getProgressFlow()

    suspend fun getProgress(): UserProgress {
        var progress = dao.getProgress()
        if (progress == null) {
            progress = UserProgress()
            dao.saveProgress(progress)
        }
        return progress
    }

    suspend fun saveProgress(progress: UserProgress) {
        dao.saveProgress(progress)
    }

    suspend fun incrementXpAndStreak(xpPoints: Int, completeQuiz: Boolean, chapterName: String? = null) {
        val current = getProgress()
        val newXp = current.xp + xpPoints
        
        val newStreak = current.streak + 1

        var completed = current.completedChapters
        if (completeQuiz && chapterName != null) {
            val list = current.completedChapters.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toMutableSet()
            list.add(chapterName)
            completed = list.joinToString(",")
        }

        val badgeSet = current.badgesUnlocked.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toMutableSet()
        if (newXp >= 200 && !badgeSet.contains("Quiz King")) {
            badgeSet.add("Quiz King")
        }
        if (newXp >= 350 && !badgeSet.contains("Science Master")) {
            badgeSet.add("Science Master")
        }
        if (newXp >= 500 && !badgeSet.contains("CBSE Champion")) {
            badgeSet.add("CBSE Champion")
        }
        if (badgeSet.size >= 3 && !badgeSet.contains("Math Genius")) {
            badgeSet.add("Math Genius")
        }

        val updated = current.copy(
            xp = newXp,
            streak = if (completeQuiz) newStreak else current.streak,
            completedChapters = completed,
            badgesUnlocked = badgeSet.joinToString(",")
        )
        dao.saveProgress(updated)
    }

    suspend fun markChapterForRevision(chapterName: String) {
        val current = getProgress()
        val list = current.revisionChapters.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toMutableSet()
        list.add(chapterName)
        dao.saveProgress(current.copy(revisionChapters = list.joinToString(",")))
    }

    suspend fun removeChapterFromRevision(chapterName: String) {
        val current = getProgress()
        val list = current.revisionChapters.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toMutableSet()
        list.remove(chapterName)
        dao.saveProgress(current.copy(revisionChapters = list.joinToString(",")))
    }

    suspend fun getQuestions(clazz: String, language: String, subject: String, chapter: String): List<CBSEQuestion> {
        // Query from SQLite first
        val cached = dao.getQuestionsForChapter(clazz, language, subject, chapter)
        if (cached.isNotEmpty()) return cached

        // If not found in SQLite, perform smart lazy-seeding
        val loadedQuestions = mutableListOf<CBSEQuestion>()

        // 1. Try to load from assets JSON files
        val assetPath = getAssetPath(clazz, subject, chapter)
        if (assetPath != null) {
            val parsed = loadFromAsset(assetPath, clazz, language, subject, chapter)
            if (parsed != null) {
                loadedQuestions.addAll(parsed.first)
                // Cache notes and questions to SQLite database
                dao.insertQuestions(parsed.first)
                dao.insertNotes(listOf(parsed.second))
            }
        }

        // 2. Fallback to smart CBSE Question Generator
        if (loadedQuestions.isEmpty()) {
            val generated = CBSEQuestionGenerator.generateQuestions(clazz, subject, chapter, language)
            loadedQuestions.addAll(generated)
            // Cache generated content so it behaves like a persistent offline database
            dao.insertQuestions(generated)
            val generatedNotes = CBSEQuestionGenerator.generateNotes(clazz, subject, chapter, language)
            dao.insertNotes(listOf(generatedNotes))
        }

        return loadedQuestions
    }

    suspend fun getNotes(clazz: String, language: String, subject: String, chapter: String): CBSENotes? {
        // Query notes from SQLite first
        val cached = dao.getNotesForChapter(clazz, language, subject, chapter)
        if (cached != null) return cached

        // If not in database, invoke `getQuestions` to trigger lazy-database loading in a single flow
        getQuestions(clazz, language, subject, chapter)

        return dao.getNotesForChapter(clazz, language, subject, chapter)
    }

    private fun getAssetPath(clazz: String, subject: String, chapter: String): String? {
        return when {
            clazz == "Class 10" && subject == "Science" && (chapter.contains("Light", ignoreCase = true) || chapter.contains("प्रकाश", ignoreCase = true)) -> {
                "data/class_10/science_light.json"
            }
            clazz == "Class 10" && subject == "Mathematics" && (chapter.contains("Quadratic", ignoreCase = true) || chapter.contains("द्विघात", ignoreCase = true)) -> {
                "data/class_10/mathematics_quadratic.json"
            }
            clazz == "Class 12" && subject == "Physics" && chapter.contains("Charges", ignoreCase = true) -> {
                "data/class_12/physics_field.json"
            }
            else -> null
        }
    }

    private suspend fun loadFromAsset(
        path: String,
        clazz: String,
        language: String,
        subject: String,
        chapter: String
    ): Pair<List<CBSEQuestion>, CBSENotes>? {
        return try {
            val jsonString = SafeAssetHelper.loadJsonAsset(context, path) ?: return null
            val root = JSONObject(jsonString)

            val notesJson = root.getJSONObject("notes")
            val notesObj = CBSENotes(
                clazz = clazz,
                language = language,
                subject = subject,
                chapter = chapter,
                summary = notesJson.optString("summary", "N/A"),
                keyPoints = notesJson.optString("keyPoints", "N/A"),
                definitions = notesJson.optString("definitions", "N/A"),
                formulas = notesJson.optString("formulas", "N/A"),
                importantLines = notesJson.optString("importantLines", "N/A"),
                revisionTricks = notesJson.optString("revisionTricks", "N/A")
            )

            val questionsArray = root.getJSONArray("questions")
            val questionsList = mutableListOf<CBSEQuestion>()
            for (i in 0 until questionsArray.length()) {
                val qItem = questionsArray.getJSONObject(i)
                val optionsArray = qItem.getJSONArray("options")
                
                questionsList.add(
                    CBSEQuestion(
                        clazz = clazz,
                        language = language,
                        subject = subject,
                        chapter = chapter,
                        question = qItem.optString("question", "N/A"),
                        option1 = optionsArray.optString(0, "N/A"),
                        option2 = optionsArray.optString(1, "N/A"),
                        option3 = optionsArray.optString(2, "N/A"),
                        option4 = optionsArray.optString(3, "N/A"),
                        correctIndex = qItem.optInt("correctIndex", 0),
                        explanation = qItem.optString("explanation", "N/A")
                    )
                )
            }
            Pair(questionsList, notesObj)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
