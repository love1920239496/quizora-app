package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.components.GlowBackground
import com.example.ui.components.NeonButton
import com.example.ui.components.StatusShield
import com.example.ui.theme.*
import com.example.viewmodel.QuizoraViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun StudyMainScreen(
    viewModel: QuizoraViewModel?,
    onNavigateBack: () -> Unit,
    onNavigateToResult: () -> Unit
) {
    if (viewModel == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "Error Loading Content",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
        return
    }

    var selectedTab by remember { mutableStateOf(0) } // 0 = Notes, 1 = Quiz
    val coroutineScope = rememberCoroutineScope()

    val elapsedScore by viewModel.quizScore.collectAsState()
    val isAnswered by viewModel.isAnswered.collectAsState()
    val isCorrectState by viewModel.isCorrect.collectAsState()
    val activeQuestionsList by viewModel.activeQuestions.collectAsState()
    val dailyQuestionsList by viewModel.dailyChallengeQuestions.collectAsState()
    val isDailyChallengeActive by viewModel.isDailyChallengeActive.collectAsState()
    val currentIdx by viewModel.currentQuestionIndex.collectAsState()

    val isCompleted by viewModel.isQuizCompleted.collectAsState()

    // Load actual content
    LaunchedEffect(Unit) {
        if (!isDailyChallengeActive) {
            viewModel.loadChapterContent()
        }
    }

    // Auto navigate to result when quiz is complete
    LaunchedEffect(isCompleted) {
        if (isCompleted) {
            onNavigateToResult()
        }
    }

    // Auto next question transition on CORRECT answer
    LaunchedEffect(isAnswered, isCorrectState) {
        if (isAnswered && isCorrectState) {
            delay(1500)
            viewModel.nextQuestion()
        }
    }

    GlowBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                StudyHeader(
                    viewModel = viewModel,
                    onNavigateBack = {
                        viewModel.resetQuizState()
                        onNavigateBack()
                    }
                )
            },
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Interactive Tab Picker
                StudyTabRow(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )

                Spacer(modifier = Modifier.height(10.dp))

                Box(modifier = Modifier.weight(1f)) {
                    if (selectedTab == 0) {
                        NotesViewTab(viewModel = viewModel)
                    } else {
                        QuizViewTab(viewModel = viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun StudyHeader(
    viewModel: QuizoraViewModel?,
    onNavigateBack: () -> Unit
) {
    if (viewModel == null) return

    val progress by viewModel.userProgress.collectAsState()
    val chapter by viewModel.currentChapter.collectAsState()
    val subject by viewModel.currentSubject.collectAsState()

    val chapterText = chapter?.takeIf { it.isNotBlank() } ?: "Study Chapter"
    val subjectText = (subject?.takeIf { it.isNotBlank() } ?: "Syllabus").uppercase()
    val xpValue = progress?.xp ?: 0

    Row(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.testTag("study_header_back")
            ) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = NeonBlue)
            }
            Column {
                Text(
                    text = subjectText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonBlue,
                    letterSpacing = 1.sp
                )
                Text(
                    text = chapterText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    maxLines = 1
                )
            }
        }

        // Active Score Meter
        StatusShield(
            icon = Icons.Default.Star,
            label = "XP STAT",
            value = "$xpValue XP",
            glowColor = NeonPurple
        )
    }
}

@Composable
fun StudyTabRow(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .height(48.dp)
            .background(Color(0x20000000), shape = RoundedCornerShape(12.dp))
            .border(0.5.dp, TextMuted.copy(alpha = 0.3f), shape = RoundedCornerShape(12.dp))
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(8.dp))
                .background(if (selectedTab == 0) NeonPurple.copy(alpha = 0.25f) else Color.Transparent)
                .clickable { onTabSelected(0) }
                .testTag("tab_notes_button"),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(
                    imageVector = Icons.Default.Book,
                    contentDescription = null,
                    tint = if (selectedTab == 0) NeonPurple else TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Short Notes",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedTab == 0) Color.White else TextSecondary
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(8.dp))
                .background(if (selectedTab == 1) NeonBlue.copy(alpha = 0.25f) else Color.Transparent)
                .clickable { onTabSelected(1) }
                .testTag("tab_quiz_button"),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(
                    imageVector = Icons.Default.Gamepad,
                    contentDescription = null,
                    tint = if (selectedTab == 1) NeonBlue else TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Quiz Game",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedTab == 1) Color.White else TextSecondary
                )
            }
        }
    }
}

@Composable
fun NotesViewTab(viewModel: QuizoraViewModel?) {
    if (viewModel == null) return
    val notes by viewModel.activeNotes.collectAsState()

    val safeNotes = notes
    if (safeNotes == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = NeonBlue)
        }
        return
    }

    val summaryText = safeNotes.summary?.takeIf { it.isNotBlank() } ?: "No Summary Available"
    val keyPointsText = safeNotes.keyPoints?.takeIf { it.isNotBlank() } ?: "No Key Points Available"
    val definitionsText = safeNotes.definitions?.takeIf { it.isNotBlank() } ?: "No Definitions Available"
    val formulasText = safeNotes.formulas?.takeIf { it.isNotBlank() } ?: "No Formulas Available"
    val importantLinesText = safeNotes.importantLines?.takeIf { it.isNotBlank() } ?: "No Important Highlights Available"
    val revisionTricksText = safeNotes.revisionTricks?.takeIf { it.isNotBlank() } ?: "No Revision Tricks Available"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // 1. Chapter Summary
        item {
            GlassCard(borderGlowColor = NeonPurple) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = NeonPurple)
                    Text(text = "SUMMARY", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NeonPurple)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = summaryText,
                    fontSize = 13.sp,
                    color = Color.White,
                    lineHeight = 18.sp
                )
            }
        }

        // 2. Key Textbook Points
        item {
            GlassCard(borderGlowColor = NeonBlue) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.FormatListBulleted, contentDescription = null, tint = NeonBlue)
                    Text(text = "KEY POINTS", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NeonBlue)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = keyPointsText,
                    fontSize = 13.sp,
                    color = Color.White,
                    lineHeight = 18.sp
                )
            }
        }

        // 3. Definitions Block
        item {
            GlassCard(borderGlowColor = NeonGreen) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Spellcheck, contentDescription = null, tint = NeonGreen)
                    Text(text = "DEFINITIONS", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NeonGreen)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = definitionsText,
                    fontSize = 13.sp,
                    color = Color.White,
                    lineHeight = 18.sp
                )
            }
        }

        // 4. Critical Formulas
        item {
            GlassCard(borderGlowColor = GoldStreak) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Functions, contentDescription = null, tint = GoldStreak)
                    Text(text = "FORMULAS", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GoldStreak)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = formulasText,
                    fontSize = 13.sp,
                    color = Color.White,
                    lineHeight = 18.sp
                )
            }
        }

        // 5. NCERT Focus Highlighter
        item {
            GlassCard(borderGlowColor = NeonPurple) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.MenuBook, contentDescription = null, tint = NeonPurple)
                    Text(text = "CBSE IMPORTANT HIGHLIGHTS", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NeonPurple)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = importantLinesText,
                    fontSize = 13.sp,
                    color = Color.White,
                    lineHeight = 18.sp
                )
            }
        }

        // 6. Revision Tricks / Memory Mnemonics
        item {
            GlassCard(borderGlowColor = NeonGreen) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Psychology, contentDescription = null, tint = NeonGreen)
                    Text(text = "REVISION TRICKS (MNEMONICS)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NeonGreen)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = revisionTricksText,
                    fontSize = 13.sp,
                    color = Color.White,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
fun QuizViewTab(viewModel: QuizoraViewModel?) {
    if (viewModel == null) return

    val activeQuestionsList by viewModel.activeQuestions.collectAsState()
    val dailyQuestionsList by viewModel.dailyChallengeQuestions.collectAsState()
    val isDailyChallengeActive by viewModel.isDailyChallengeActive.collectAsState()
    val questions = (if (isDailyChallengeActive) dailyQuestionsList else activeQuestionsList) ?: emptyList()
    val currentIdx by viewModel.currentQuestionIndex.collectAsState()
    val selectedAns by viewModel.selectedAnswerIndex.collectAsState()
    val isAnswered by viewModel.isAnswered.collectAsState()
    val isCorrectVal by viewModel.isCorrect.collectAsState()
    val scoreVal by viewModel.quizScore.collectAsState()
    val activeNotes by viewModel.activeNotes.collectAsState()

    // Create a local debounced loading tracker so that if loading from DB is in transient state
    // we do not flicker a "No Questions Available" screen.
    var isLocalLoading by remember(questions) { mutableStateOf(questions.isEmpty()) }
    LaunchedEffect(questions) {
        if (questions.isNotEmpty()) {
            isLocalLoading = false
        } else {
            delay(1000)
            if (questions.isEmpty()) {
                isLocalLoading = false
            }
        }
    }

    val isStillFetchingContent = isLocalLoading || (!isDailyChallengeActive && activeNotes == null)

    if (isStillFetchingContent) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = NeonBlue)
        }
        return
    }

    if (questions.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "No Questions Available",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
        return
    }

    // Boundary protection for currentIdx across asynchronous flows
    if (currentIdx < 0 || currentIdx >= questions.size) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "No Questions Available",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
        return
    }

    val activeQuestion = questions.getOrNull(currentIdx)
    if (activeQuestion == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "No Questions Available",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
        return
    }

    val questionText = activeQuestion.question?.takeIf { it.isNotBlank() } ?: "Question"
    
    // Defensive extraction of options with solid fallback option strings
    val optionsList = activeQuestion.getOptions()?.map { it?.takeIf { it.isNotBlank() } ?: "Option" }?.takeIf { it.isNotEmpty() }
        ?: listOf("Option A", "Option B", "Option C", "Option D")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Progress Row and live points meter
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "QUESTION ${currentIdx + 1} OF ${questions.size}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
                Text(
                    text = "SCORE: $scoreVal PTS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (scoreVal >= 0) NeonGreen else NeonRed
                )
            }
        }

        // Main Question Statement Progress
        item {
            val progressVal = if (questions.isNotEmpty()) {
                (currentIdx.toFloat() / questions.size.toFloat()).coerceIn(0f, 1f)
            } else {
                0f
            }
            LinearProgressIndicator(
                progress = { progressVal },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = NeonBlue,
                trackColor = Color(0x306C688D)
            )
        }

        item {
            GlassCard(borderGlowColor = NeonBlue) {
                Text(
                    text = questionText,
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 24.sp
                )
            }
        }

        // Defensive options list items with boundary protection
        if (optionsList.isNotEmpty()) {
            items(optionsList.size) { index ->
                if (index >= 0 && index < optionsList.size) {
                    val optionText = optionsList[index] ?: "Option"
                    val correctIndex = activeQuestion.correctIndex
                    val optionState = when {
                        !isAnswered -> OptionVisualState.NORMAL
                        index == correctIndex -> OptionVisualState.CORRECT
                        index == selectedAns -> OptionVisualState.WRONG
                        else -> OptionVisualState.NORMAL
                    }

                    QuizOptionRow(
                        index = index,
                        text = optionText,
                        state = optionState,
                        onClick = {
                            if (!isAnswered) {
                                if (index >= 0 && index < optionsList.size) {
                                    viewModel.submitAnswer(index)
                                }
                            }
                        }
                    )
                }
            }
        } else {
            item {
                Text(
                    text = "No Options Available",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                )
            }
        }

        // Flash overlays or Custom explanation popups
        item {
            AnimatedVisibility(
                visible = isAnswered,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    if (isCorrectVal) {
                        // Flash Correct state
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(NeonGreen.copy(alpha = 0.15f), shape = RoundedCornerShape(16.dp))
                                .border(1.dp, NeonGreen, shape = RoundedCornerShape(16.dp))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NeonGreen)
                                Text(
                                    text = "EXCELLENT! CORRECT (+1 XP)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonGreen
                                )
                            }
                        }
                    } else {
                        // Explanation and Manual Proceed trigger for incorrect answers
                        val corrIdx = activeQuestion.correctIndex
                        val corrText = if (corrIdx >= 0 && corrIdx < optionsList.size) {
                            optionsList[corrIdx] ?: ""
                        } else {
                            ""
                        }
                        val explanationText = activeQuestion.explanation?.takeIf { it.isNotBlank() } ?: "No explanation available"

                        GlassCard(borderGlowColor = NeonRed) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = NeonRed)
                                Text(
                                    text = "INCORRECT (-1 XP)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonRed
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Correct answer is: Option ${corrIdx + 1}: $corrText",
                                fontSize = 13.sp,
                                color = NeonGreen,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            Text(
                                text = explanationText,
                                fontSize = 12.sp,
                                color = TextSecondary,
                                lineHeight = 16.sp
                            )
                        }

                        NeonButton(
                            text = "PROCEED TO NEXT",
                            onClick = { viewModel.nextQuestion() },
                            modifier = Modifier.fillMaxWidth(),
                            glowColor = NeonPurple,
                            testTag = "quiz_proceed_button"
                        )
                    }
                }
            }
        }
    }
}

enum class OptionVisualState {
    NORMAL, CORRECT, WRONG
}

@Composable
fun QuizOptionRow(
    index: Int,
    text: String,
    state: OptionVisualState,
    onClick: () -> Unit
) {
    val borderColor = when (state) {
        OptionVisualState.NORMAL -> TextMuted.copy(alpha = 0.4f)
        OptionVisualState.CORRECT -> NeonGreen
        OptionVisualState.WRONG -> NeonRed
    }

    val glowColor = when (state) {
        OptionVisualState.NORMAL -> Color.Transparent
        OptionVisualState.CORRECT -> NeonGreen.copy(alpha = 0.15f)
        OptionVisualState.WRONG -> NeonRed.copy(alpha = 0.15f)
    }

    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (state == OptionVisualState.NORMAL) GlassBg else glowColor)
            .border(BorderStroke(1.2.dp, borderColor), shape)
            .clickable(onClick = onClick)
            .padding(16.dp)
            .testTag("quiz_option_row_$index"),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(
                        color = when (state) {
                            OptionVisualState.NORMAL -> Color(0x306C688D)
                            OptionVisualState.CORRECT -> NeonGreen
                            OptionVisualState.WRONG -> NeonRed
                        },
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Indicator letter, or symbol check
                if (state == OptionVisualState.CORRECT) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                } else if (state == OptionVisualState.WRONG) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                } else {
                    val letter = if (index in 0..25) ('A' + index).toString() else "?"
                    Text(
                        text = letter,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}
