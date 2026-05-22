package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.components.GlowBackground
import com.example.ui.components.StatusShield
import com.example.ui.theme.*
import com.example.viewmodel.QuizoraViewModel

@Composable
fun ChapterProgressScreen(
    viewModel: QuizoraViewModel,
    onNavigateBack: () -> Unit
) {
    val progress by viewModel.userProgress.collectAsState()
    val activeClass by viewModel.currentClass.collectAsState()
    val lang = progress.selectedLanguage.ifEmpty { "English" }

    // Aggregate standard syllabus for the current class to map accomplishments
    val standardSyllabus = remember(activeClass, lang) {
        when (activeClass) {
            "Class 10" -> listOf(
                SyllabusChapter("Science", if (lang == "English") "Light - Reflection & Refraction" else "प्रकाश - परावर्तन तथा अपवर्तन"),
                SyllabusChapter("Science", "Chemical Reactions"),
                SyllabusChapter("Science", "Acid, Bases & Salts"),
                SyllabusChapter("Mathematics", if (lang == "English") "Quadratic Equations" else "द्विघात समीकरण"),
                SyllabusChapter("Mathematics", "Real Numbers"),
                SyllabusChapter("Social Science", "The Rise of Nationalism in Europe")
            )
            "Class 12" -> listOf(
                SyllabusChapter("Physics", "Electric Charges & Fields"),
                SyllabusChapter("Physics", "Electrostatic Potential & Capacitance"),
                SyllabusChapter("Biology", "Sexual Reproduction in Flowering Plants")
            )
            "Class 8" -> listOf(
                SyllabusChapter("English", "The Best Christmas Present in the World")
            )
            else -> listOf(
                SyllabusChapter("Science", if (lang == "English") "Science Introduction Chapter" else "Science परिचय अध्याय")
            )
        }
    }

    val completedSet = remember(progress.completedChapters) {
        progress.completedChapters.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet()
    }

    val revisionSet = remember(progress.revisionChapters) {
        progress.revisionChapters.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet()
    }

    // Categorize standard syllabus
    val completedChaptersList = standardSyllabus.filter {
        completedSet.contains("${activeClass} - ${it.subject} - ${it.name}") || completedSet.contains(it.name)
    }

    val revisionChaptersList = standardSyllabus.filter {
        revisionSet.contains("${activeClass} - ${it.subject} - ${it.name}")
    }

    val pendingChaptersList = standardSyllabus.filter {
        val compositeId = "${activeClass} - ${it.subject} - ${it.name}"
        !completedSet.contains(compositeId) && !completedSet.contains(it.name) && !revisionSet.contains(compositeId)
    }

    // Compute progress stats
    val totalCount = standardSyllabus.size
    val completedCount = completedChaptersList.size
    val progressRatio = if (totalCount > 0) completedCount.toFloat() / totalCount.toFloat() else 0f

    GlowBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
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
                            modifier = Modifier.testTag("progress_screen_back")
                        ) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = NeonBlue)
                        }
                        Text(
                            text = "SYLLABUS REPORT",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }

                    StatusShield(
                        icon = Icons.Default.PlayForWork,
                        label = "GRADE",
                        value = activeClass,
                        glowColor = NeonPurple
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                // Progress Gauge header
                item {
                    GlassCard(borderGlowColor = NeonBlue) {
                        Text(
                            text = "TOTAL SYLLABUS PROGRESS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonBlue,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                text = "Active Syllabus completed:",
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = "$completedCount / $totalCount Chapters",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(10.dp))
                        
                        LinearProgressIndicator(
                            progress = { progressRatio },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape),
                            color = NeonBlue,
                            trackColor = Color(0x20FFFFFF)
                        )
                    }
                }

                // 1. Revision Required Chapters List
                if (revisionChaptersList.isNotEmpty()) {
                    item {
                        ProgressHeadingRow(title = "REVISION REQUIRED (${revisionChaptersList.size})", color = NeonRed, icon = Icons.Default.Warning)
                    }
                    items(revisionChaptersList) { item ->
                        ProgressStatusCard(chapter = item, status = ChapterState.REVISION, color = NeonRed)
                    }
                }

                // 2. Completed Chapters List
                item {
                    ProgressHeadingRow(title = "COMPLETED UNITS (${completedChaptersList.size})", color = NeonGreen, icon = Icons.Default.CheckCircle)
                }
                if (completedChaptersList.isEmpty()) {
                    item {
                        EmptyProgressLabel(message = "Solve quizzes with high accuracy to list completed chapters here!")
                    }
                } else {
                    items(completedChaptersList) { item ->
                        ProgressStatusCard(chapter = item, status = ChapterState.COMPLETED, color = NeonGreen)
                    }
                }

                // 3. Pending Chapters List
                item {
                    ProgressHeadingRow(title = "PENDING ON DECK (${pendingChaptersList.size})", color = TextSecondary, icon = Icons.Default.HourglassEmpty)
                }
                if (pendingChaptersList.isEmpty()) {
                    item {
                        EmptyProgressLabel(message = "Congratulations! Active syllabus is 100% complete!")
                    }
                } else {
                    items(pendingChaptersList) { item ->
                        ProgressStatusCard(chapter = item, status = ChapterState.PENDING, color = TextMuted)
                    }
                }
            }
        }
    }
}

data class SyllabusChapter(
    val subject: String,
    val name: String
)

enum class ChapterState {
    COMPLETED, REVISION, PENDING
}

@Composable
fun ProgressHeadingRow(
    title: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            color = color,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun ProgressStatusCard(
    chapter: SyllabusChapter,
    status: ChapterState,
    color: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(GlassBg, RoundedCornerShape(16.dp))
            .border(0.5.dp, color.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = chapter.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Subject: ${chapter.subject} • Board: CBSE",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }

            Box(
                modifier = Modifier
                    .background(color.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = status.name,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = color
                )
            }
        }
    }
}

@Composable
fun EmptyProgressLabel(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(GlassBg, RoundedCornerShape(16.dp))
            .border(0.5.dp, TextMuted.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            fontSize = 11.sp,
            color = TextMuted,
            fontWeight = FontWeight.Medium
        )
    }
}
