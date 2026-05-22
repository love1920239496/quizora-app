package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CircularGlowProgress
import com.example.ui.components.GlassCard
import com.example.ui.components.GlowBackground
import com.example.ui.components.NeonButton
import com.example.ui.theme.*
import com.example.viewmodel.QuizoraViewModel

@Composable
fun ResultScreen(
    viewModel: QuizoraViewModel,
    onBackToHome: () -> Unit
) {
    val correctCount by viewModel.correctAnswersCount.collectAsState()
    val wrongCount by viewModel.wrongAnswersCount.collectAsState()
    val scoreVal by viewModel.quizScore.collectAsState()
    val isDailyActive by viewModel.isDailyChallengeActive.collectAsState()

    val chapterName by viewModel.currentChapter.collectAsState()
    val subjectName by viewModel.currentSubject.collectAsState()

    val totalQuestions = correctCount + wrongCount
    val accuracyPercentage = if (totalQuestions > 0) {
        (correctCount.toFloat() / totalQuestions.toFloat())
    } else {
        0f
    }

    // Determine motivational messages and reward metrics
    val performanceTier = when {
        accuracyPercentage >= 0.9f -> ResultTier.PERFECT
        accuracyPercentage >= 0.7f -> ResultTier.GREAT
        accuracyPercentage >= 0.4f -> ResultTier.GOOD
        else -> ResultTier.REDO
    }

    // Compute earned XP
    val baseXp = maxOf(0, scoreVal * 15)
    val perfectBonus = if (wrongCount == 0 && correctCount > 0) 25 else 0
    val challengeBonus = if (isDailyActive) 50 else 0
    val totalXpEarned = baseXp + perfectBonus + challengeBonus

    GlowBackground {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 40.dp, bottom = 40.dp)
        ) {
            // Header Statement
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = performanceTier.icon,
                        contentDescription = "Performance Emblem",
                        tint = performanceTier.glowColor,
                        modifier = Modifier.size(60.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (isDailyActive) "CHALLENGE COMPLETED" else "SESSION COMPLETE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonBlue,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = performanceTier.title,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // circular gauge displaying accuracy index
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularGlowProgress(
                        percentage = accuracyPercentage,
                        title = "Accuracy Rating",
                        scoreText = "$correctCount / $totalQuestions",
                        glowColor = performanceTier.glowColor
                    )
                }
            }

            // Stats grid block (XP gained, scores indices, wrong counts)
            item {
                GlassCard(borderGlowColor = NeonPurple) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ResultMetricItem(
                            icon = Icons.Default.AddCircle,
                            label = "XP Earned",
                            value = "+$totalXpEarned XP",
                            color = NeonGreen
                        )
                        ResultMetricItem(
                            icon = Icons.Default.Check,
                            label = "Correct",
                            value = "$correctCount",
                            color = NeonGreen
                        )
                        ResultMetricItem(
                            icon = Icons.Default.Close,
                            label = "Wrong",
                            value = "$wrongCount",
                            color = NeonRed
                        )
                    }
                }
            }

            // Weak chapter recommendations
            item {
                GlassCard(borderGlowColor = if (performanceTier == ResultTier.REDO) NeonRed else TextMuted) {
                    Text(
                        text = "CHAPTER ANALYSIS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = TextSecondary,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    if (isDailyActive) {
                        Text(
                            text = "Daily Challenge complete! You solved mixed syllabus trivia flawlessly. Your streak has been safely protected.",
                            fontSize = 13.sp,
                            color = Color.White,
                            lineHeight = 18.sp
                        )
                    } else if (performanceTier == ResultTier.REDO) {
                        Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Feedback, contentDescription = null, tint = NeonRed, modifier = Modifier.size(20.dp))
                            Column {
                                Text(
                                    text = "Weak Foundation detected in $chapterName.",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonRed
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "We have added this chapter to your 'Revision List'. Spend 10 minutes reading the 'Short Notes' tab and try again to master this concepts.",
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    } else {
                        Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(20.dp))
                            Column {
                                Text(
                                    text = "Strong Foundation in $chapterName!",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonGreen
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Great job! Your grasp of CBSE definitions and key equations is excellent. You are ready for CBSE Board level mock exams.",
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }

            // Motivation tagline text
            item {
                Text(
                    text = performanceTier.motivation,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

            // Action triggers to dismiss and return home and reset states
            item {
                NeonButton(
                    text = "CONTINUE JOURNEY",
                    onClick = {
                        viewModel.resetQuizState()
                        viewModel.finishDailyChallengeState()
                        onBackToHome()
                    },
                    modifier = Modifier.fillMaxWidth().testTag("result_continue_button"),
                    glowColor = NeonBlue
                )
            }
        }
    }
}

@Composable
fun ResultMetricItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, fontSize = 11.sp, color = TextSecondary)
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

enum class ResultTier(
    val title: String,
    val motivation: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val glowColor: Color
) {
    PERFECT(
        title = "CBSE MASTERMIND!",
        motivation = "Perfect 100%! You solved every board question flawlessly. Quizora badges have been updated.",
        icon = Icons.Default.EmojiEvents,
        glowColor = NeonGreen
    ),
    GREAT(
        title = "GENIUS MARKS!",
        motivation = "Stellar score! You possess a solid grip on these equations. Minor errors are easily fixed with a quick revision notes sweep.",
        icon = Icons.Default.WorkspacePremium,
        glowColor = NeonBlue
    ),
    GOOD(
        title = "WELL SOLVED!",
        motivation = "Good attempt! You cleared the passing brackets. Check your incorrect answer explanations to reinforce memory.",
        icon = Icons.Default.Stars,
        glowColor = NeonPurple
    ),
    REDO(
        title = "KEEP IMPROVING",
        motivation = "Do not worry! Learning happens through mistakes. Take another look at the short summaries and tricks, then retry.",
        icon = Icons.Default.ChangeHistory,
        glowColor = NeonRed
    )
}
