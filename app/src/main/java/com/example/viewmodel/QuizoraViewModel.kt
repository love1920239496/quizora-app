package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

class QuizoraViewModel(application: Application) : AndroidViewModel(application) {

    private val database = QuizoraDatabase.getInstance(application)
    private val repository = QuizoraRepository(database.quizoraDao(), application)

    // UI state for progress
    val userProgress: StateFlow<UserProgress> = repository.userProgressFlow
        .map { it ?: UserProgress() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserProgress()
        )

    // Current Navigation/Selection Selections
    var currentClass = MutableStateFlow("Class 10")
    var currentSubject = MutableStateFlow("Science")
    var currentChapter = MutableStateFlow("Light - Reflection & Refraction")

    // Active study notes & active quiz questions states
    val activeNotes = MutableStateFlow<CBSENotes?>(null)
    val activeQuestions = MutableStateFlow<List<CBSEQuestion>>(emptyList())

    // Quiz Execution State
    var currentQuestionIndex = MutableStateFlow(0)
    var selectedAnswerIndex = MutableStateFlow(-1) // -1 is none
    var isAnswered = MutableStateFlow(false)
    var isCorrect = MutableStateFlow(false)
    var quizScore = MutableStateFlow(0)
    var correctAnswersCount = MutableStateFlow(0)
    var wrongAnswersCount = MutableStateFlow(0)
    var isQuizCompleted = MutableStateFlow(false)

    // Daily Challenge Mode State (10 questions list)
    var isDailyChallengeActive = MutableStateFlow(false)
    var dailyChallengeQuestions = MutableStateFlow<List<CBSEQuestion>>(emptyList())

    init {
        // Automatically sync selected class/language to viewmodel flow on startup
        viewModelScope.launch {
            userProgress.collect { progress ->
                if (progress.selectedClass.isNotEmpty()) {
                    currentClass.value = progress.selectedClass
                }
            }
        }
    }

    fun selectLanguage(lang: String) {
        viewModelScope.launch {
            val progress = repository.getProgress()
            repository.saveProgress(progress.copy(selectedLanguage = lang))
        }
    }

    fun selectClass(className: String) {
        viewModelScope.launch {
            currentClass.value = className
            val progress = repository.getProgress()
            repository.saveProgress(progress.copy(selectedClass = className))
        }
    }

    fun selectSubject(subjectName: String) {
        currentSubject.value = subjectName
    }

    fun selectChapter(chapterName: String) {
        currentChapter.value = chapterName
        loadChapterContent()
    }

    fun loadChapterContent() {
        viewModelScope.launch {
            // Reset active state and quiz indexes first to avoid out-of-bounds recompositions
            activeNotes.value = null
            activeQuestions.value = emptyList()
            resetQuizState()

            val progress = userProgress.value
            val lang = if (progress.selectedLanguage.isEmpty()) "English" else progress.selectedLanguage
            val notes = repository.getNotes(currentClass.value, lang, currentSubject.value, currentChapter.value)
            val rawQuestions = repository.getQuestions(currentClass.value, lang, currentSubject.value, currentChapter.value)
            
            // Randomly select up to 10 unique questions and shuffle option order
            val processedQuestions = rawQuestions.shuffled().take(10).map { question ->
                val originalOptions = listOf(question.option1, question.option2, question.option3, question.option4)
                val correctText = originalOptions.getOrNull(question.correctIndex) ?: question.option1
                val shuffledOptions = originalOptions.shuffled()
                val updatedCorrectIndex = shuffledOptions.indexOf(correctText).coerceIn(0, 3)
                
                question.copy(
                    option1 = shuffledOptions[0],
                    option2 = shuffledOptions[1],
                    option3 = shuffledOptions[2],
                    option4 = shuffledOptions[3],
                    correctIndex = updatedCorrectIndex
                )
            }
            
            activeNotes.value = notes
            activeQuestions.value = processedQuestions
        }
    }

    fun resetQuizState() {
        currentQuestionIndex.value = 0
        selectedAnswerIndex.value = -1
        isAnswered.value = false
        isCorrect.value = false
        quizScore.value = 0
        correctAnswersCount.value = 0
        wrongAnswersCount.value = 0
        isQuizCompleted.value = false
    }

    fun submitAnswer(index: Int) {
        if (isAnswered.value) return // Only 1 attempt allowed
        
        selectedAnswerIndex.value = index
        isAnswered.value = true
        
        val questionsList = if (isDailyChallengeActive.value) dailyChallengeQuestions.value else activeQuestions.value
        if (questionsList.isEmpty()) return
        
        val currentIndex = currentQuestionIndex.value
        if (currentIndex < 0 || currentIndex >= questionsList.size) return
        
        val currentQuestion = questionsList[currentIndex]
        val isAnswerCorrect = currentQuestion.correctIndex == index
        isCorrect.value = isAnswerCorrect

        if (isAnswerCorrect) {
            quizScore.value += 1
            correctAnswersCount.value += 1
        } else {
            quizScore.value -= 1
            wrongAnswersCount.value += 1
        }
    }

    fun nextQuestion() {
        val questionsList = if (isDailyChallengeActive.value) dailyChallengeQuestions.value else activeQuestions.value
        val hasNext = currentQuestionIndex.value + 1 < questionsList.size
        
        if (hasNext) {
            currentQuestionIndex.value += 1
            selectedAnswerIndex.value = -1
            isAnswered.value = false
            isCorrect.value = false
        } else {
            completeQuizAndRewards()
        }
    }

    private fun completeQuizAndRewards() {
        isQuizCompleted.value = true
        viewModelScope.launch {
            // Calculate final earned XP: Max(0, score * 15 XP) + bonus of 10 XP if 100% correct
            val score = quizScore.value
            var xpEarned = maxOf(0, score * 15)
            if (wrongAnswersCount.value == 0 && correctAnswersCount.value > 0) {
                xpEarned += 25 // Perfect score bonus
            }
            if (isDailyChallengeActive.value) {
                xpEarned += 50 // Special daily challenge completion bonus
                val current = repository.getProgress()
                repository.saveProgress(current.copy(
                    dailyChallengeCompletedToday = true,
                    lastChallengeDate = "2026-05-22" // Stubbed dynamic date
                ))
            }
            
            val chapterId = "${currentClass.value} - ${currentSubject.value} - ${currentChapter.value}"
            repository.incrementXpAndStreak(xpEarned, completeQuiz = true, chapterName = chapterId)
            
            // If performance was poor (accuracy < 50%), suggest revision
            val total = correctAnswersCount.value + wrongAnswersCount.value
            if (total > 0) {
                val accuracy = (correctAnswersCount.value.toFloat() / total.toFloat()) * 100
                if (accuracy < 50) {
                    repository.markChapterForRevision(chapterId)
                } else {
                    // Remove from revision if previously marked and now cleared with high scores
                    repository.removeChapterFromRevision(chapterId)
                }
            }
        }
    }

    fun markCurrentChapterForRevisionDirectly() {
        viewModelScope.launch {
            val chapterId = "${currentClass.value} - ${currentSubject.value} - ${currentChapter.value}"
            repository.markChapterForRevision(chapterId)
        }
    }

    fun startDailyChallenge() {
        viewModelScope.launch {
            isDailyChallengeActive.value = true
            resetQuizState()
            
            val progress = userProgress.value
            val lang = if (progress.selectedLanguage.isEmpty()) "English" else progress.selectedLanguage
            
            // Collect questions from across ANY subject to build a fun 10-question mixed daily challenge
            // We'll seed or extract science/maths questions
            val mockList = mutableListOf<CBSEQuestion>()
            
            // Fetch default questions for the current class
            val scienceQuestions = repository.getQuestions(currentClass.value, lang, "Science", "Light - Reflection & Refraction")
            val mathQuestions = repository.getQuestions(currentClass.value, lang, "Mathematics", "Quadratic Equations")
            val physicsQuestions = repository.getQuestions(currentClass.value, lang, "Physics", "Electric Charges & Fields")
            
            mockList.addAll(scienceQuestions)
            mockList.addAll(mathQuestions)
            mockList.addAll(physicsQuestions)
            
            // Ensure we have a set of questions (if empty, pad with default items)
            if (mockList.isEmpty()) {
                val introQ = repository.getQuestions(currentClass.value, lang, "Science", if (lang == "English") "Science Introduction Chapter" else "Science परिचय अध्याय")
                mockList.addAll(introQ)
            }
            if (mockList.isEmpty()) {
                // Ultimate fallback to Class 10 Science which is guaranteed to be seeded for both languages
                val fallbackQ = repository.getQuestions("Class 10", lang, "Science", if (lang == "English") "Light - Reflection & Refraction" else "प्रकाश - परावर्तन तथा अपवर्तन")
                mockList.addAll(fallbackQ)
            }
            
            // Take exactly 10 random mixed questions and shuffle their options!
            val processedChallenge = mockList.shuffled().take(10).map { question ->
                val originalOptions = listOf(question.option1, question.option2, question.option3, question.option4)
                val correctText = originalOptions.getOrNull(question.correctIndex) ?: question.option1
                val shuffledOptions = originalOptions.shuffled()
                val updatedCorrectIndex = shuffledOptions.indexOf(correctText).coerceIn(0, 3)
                
                question.copy(
                    option1 = shuffledOptions[0],
                    option2 = shuffledOptions[1],
                    option3 = shuffledOptions[2],
                    option4 = shuffledOptions[3],
                    correctIndex = updatedCorrectIndex
                )
            }
            
            dailyChallengeQuestions.value = processedChallenge
        }
    }

    fun finishDailyChallengeState() {
        isDailyChallengeActive.value = false
    }

    fun resetAllProgress() {
        viewModelScope.launch {
            repository.saveProgress(UserProgress())
        }
    }
}
