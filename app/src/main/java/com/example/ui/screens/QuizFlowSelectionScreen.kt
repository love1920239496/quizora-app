package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.components.NeonButton
import com.example.ui.theme.*
import com.example.viewmodel.QuizoraViewModel
import com.example.data.CBSEContentProvider

enum class SelectionStep {
    CLASS, SUBJECT, CHAPTER
}

@Composable
fun QuizFlowSelectionScreen(
    viewModel: QuizoraViewModel,
    onNavigateToStudy: () -> Unit
) {
    var currentStep by remember { mutableStateOf(SelectionStep.CLASS) }
    
    val selectedClass by viewModel.currentClass.collectAsState()
    val selectedSubject by viewModel.currentSubject.collectAsState()
    val selectedChapter by viewModel.currentChapter.collectAsState()
    val progress by viewModel.userProgress.collectAsState()
    val lang = progress.selectedLanguage.ifEmpty { "English" }

    val classesList = listOf("Class 6", "Class 7", "Class 8", "Class 9", "Class 10", "Class 11", "Class 12")

    // Dynamic subject lists
    val isSeniorClass = selectedClass == "Class 11" || selectedClass == "Class 12"
    val subjectsList = if (isSeniorClass) {
        listOf("Physics", "Chemistry", "Biology", "Mathematics", "English", "Hindi")
    } else {
        listOf("Science", "Mathematics", "English", "Social Science", "Hindi")
    }

    // Dynamic chapter lists (pulling from dynamic CBSEContentProvider)
    val chaptersList = remember(selectedClass, selectedSubject, lang) {
        CBSEContentProvider.getChapters(selectedClass, selectedSubject, lang)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // --- Wizard Banner & Back Navigation ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (currentStep != SelectionStep.CLASS) {
                    IconButton(
                        onClick = {
                            currentStep = when (currentStep) {
                                SelectionStep.SUBJECT -> SelectionStep.CLASS
                                SelectionStep.CHAPTER -> SelectionStep.SUBJECT
                                else -> SelectionStep.CLASS
                            }
                        },
                        modifier = Modifier.testTag("step_back_button")
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Step Back", tint = NeonBlue)
                    }
                }
                Text(
                    text = when (currentStep) {
                        SelectionStep.CLASS -> "STEP 1: SELECT CLASS"
                        SelectionStep.SUBJECT -> "STEP 2: SELECT SUBJECT"
                        SelectionStep.CHAPTER -> "STEP 3: SELECT CHAPTER"
                    },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }

            // Step Progress Chips
            Text(
                text = "${currentStep.ordinal + 1}/3",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = NeonPurple,
                modifier = Modifier
                    .background(NeonPurple.copy(alpha = 0.15f), shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }

        // Selected Status Summary
        if (currentStep != SelectionStep.CLASS) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SelectionProgressPill(label = "Class", value = selectedClass)
                if (currentStep == SelectionStep.CHAPTER) {
                    SelectionProgressPill(label = "Subject", value = selectedSubject)
                }
            }
        }

        // Content Area with smooth transitions
        Box(modifier = Modifier.weight(1f)) {
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    fadeIn(animationSpec = tween(180)) togetherWith fadeOut(animationSpec = tween(180))
                },
                label = "step_wizard_content"
            ) { targetStep ->
                when (targetStep) {
                    SelectionStep.CLASS -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            items(classesList) { className ->
                                ClassSelectionCard(
                                    className = className,
                                    isSelected = selectedClass == className,
                                    onClick = {
                                        viewModel.selectClass(className)
                                        currentStep = SelectionStep.SUBJECT
                                    }
                                )
                            }
                        }
                    }
                    SelectionStep.SUBJECT -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            items(subjectsList) { subjectName ->
                                SubjectSelectionCard(
                                    subjectName = subjectName,
                                    isSelected = selectedSubject == subjectName,
                                    onClick = {
                                        viewModel.selectSubject(subjectName)
                                        currentStep = SelectionStep.CHAPTER
                                    }
                                )
                            }
                        }
                    }
                    SelectionStep.CHAPTER -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            items(chaptersList) { chapterName ->
                                ChapterSelectionCard(
                                    chapterName = chapterName,
                                    onClick = {
                                        viewModel.selectChapter(chapterName)
                                        onNavigateToStudy()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SelectionProgressPill(label: String, value: String) {
    Box(
        modifier = Modifier
            .background(GlassBg, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = "$label: $value",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary
        )
    }
}

@Composable
fun ClassSelectionCard(
    className: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val glowColor = if (isSelected) NeonBlue else TextMuted
    val fillAlpha = if (isSelected) 0.35f else 0.15f
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .clickable(onClick = onClick)
            .testTag("class_card_$className"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) glowColor.copy(alpha = fillAlpha) else GlassBg),
        border = BorderStroke(
            1.dp,
            if (isSelected) glowColor else glowColor.copy(alpha = 0.2f)
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = className,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Text(
                    text = "CBSE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) NeonBlue else TextSecondary
                )
            }
        }
    }
}

@Composable
fun SubjectSelectionCard(
    subjectName: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val glowColor = if (isSelected) NeonPurple else TextMuted
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .clickable(onClick = onClick)
            .testTag("subject_card_$subjectName"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) glowColor.copy(alpha = 0.3f) else GlassBg),
        border = BorderStroke(
            1.dp,
            if (isSelected) glowColor else glowColor.copy(alpha = 0.2f)
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = subjectName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun ChapterSelectionCard(
    chapterName: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("chapter_card_$chapterName"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GlassBg),
        border = BorderStroke(1.dp, NeonBlue.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = chapterName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Syllabus Aligned MCQ Quiz & Notes",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Select Chapter",
                tint = NeonBlue,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun NotesScreenWrapper(
    viewModel: QuizoraViewModel,
    onNavigateToStudy: () -> Unit
) {
    // Quick routing from the main selector direct to Notes tab inside Study screen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "SELECT BOOK NOTES",
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            color = Color.White,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "Notes are paired along with quizzes. Select any chapter code below to access instant notes summary.",
            fontSize = 12.sp,
            color = TextSecondary,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.weight(1f)) {
            QuizFlowSelectionScreen(
                viewModel = viewModel,
                onNavigateToStudy = onNavigateToStudy
            )
        }
    }
}
