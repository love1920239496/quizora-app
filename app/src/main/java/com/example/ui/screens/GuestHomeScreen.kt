package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.QuizoraViewModel

enum class HomeTab {
    HOME, NOTES, QUIZ, PROFILE
}

@Composable
fun GuestHomeScreen(
    viewModel: QuizoraViewModel,
    onNavigateToDailyChallenge: () -> Unit,
    onNavigateToQuizFlow: () -> Unit,
    onNavigateToChapterProgress: () -> Unit,
    onNavigateToNotesSelector: () -> Unit,
    onSwitchLanguageRequested: () -> Unit
) {
    var activeTab by remember { mutableStateOf(HomeTab.HOME) }
    val progress by viewModel.userProgress.collectAsState()

    GlowBackground {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                BottomNavigationBar(
                    activeTab = activeTab,
                    onTabSelected = { activeTab = it }
                )
            },
            modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.navigationBars)
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Animate content switches between tabs for smooth transitions
                AnimatedContent(
                    targetState = activeTab,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
                    },
                    label = "tab_switches"
                ) { targetTab ->
                    when (targetTab) {
                        HomeTab.HOME -> HomeContent(
                            viewModel = viewModel,
                            onTabSelected = { activeTab = it },
                            onNavigateToDailyChallenge = onNavigateToDailyChallenge,
                            onNavigateToQuizFlow = onNavigateToQuizFlow,
                            onNavigateToChapterProgress = onNavigateToChapterProgress,
                            onNavigateToNotesSelector = onNavigateToNotesSelector,
                            onSwitchLanguage = onSwitchLanguageRequested
                        )
                        HomeTab.NOTES -> NotesScreenWrapper(
                            viewModel = viewModel,
                            onNavigateToStudy = { 
                                onNavigateToQuizFlow() 
                            }
                        )
                        HomeTab.QUIZ -> QuizFlowSelectionScreen(
                            viewModel = viewModel,
                            onNavigateToStudy = {
                                onNavigateToQuizFlow()
                            }
                        )
                        HomeTab.PROFILE -> ProfileScreen(
                            viewModel = viewModel,
                            onSwitchLanguage = onSwitchLanguageRequested
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HomeContent(
    viewModel: QuizoraViewModel,
    onTabSelected: (HomeTab) -> Unit,
    onNavigateToDailyChallenge: () -> Unit,
    onNavigateToQuizFlow: () -> Unit,
    onNavigateToChapterProgress: () -> Unit,
    onNavigateToNotesSelector: () -> Unit,
    onSwitchLanguage: () -> Unit
) {
    val progress by viewModel.userProgress.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(top = 24.dp, bottom = 24.dp)
    ) {
        // --- 1. Top Section ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.ElectricBolt, contentDescription = "Quizora Flash", tint = NeonBlue)
                        Text(
                            text = "QUIZORA",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            color = Color.White
                        )
                    }
                    Text(
                        text = "CBSE SMART LEARNING",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonPurple,
                        letterSpacing = 1.sp
                    )
                }

                // Interactive language and class settings shortcuts
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatusShield(
                        icon = Icons.Default.Translate,
                        label = "MEDIUM",
                        value = progress.selectedLanguage.ifEmpty { "English" },
                        glowColor = NeonPurple,
                        onClick = onSwitchLanguage
                    )
                }
            }
        }

        // --- 2. Gamification Metric Row ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    StatusShield(
                        icon = Icons.Default.Star,
                        label = "STUDENT XP",
                        value = "${progress.xp} XP",
                        glowColor = NeonBlue
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    StatusShield(
                        icon = Icons.Default.LocalFireDepartment,
                        label = "STREAK",
                        value = "${progress.streak} Days",
                        glowColor = GoldStreak
                    )
                }
            }
        }

        // --- 3. Prominent Daily Challenge Alert ---
        item {
            GlassCard(
                borderGlowColor = NeonGreen,
                borderWidth = 1.5.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1.5f)) {
                        Text(
                            text = "DAILY CHALLENGE",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = NeonGreen,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Test your skills with a 10-Question mix for a bonus 50 XP!",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        NeonButton(
                            text = "PLAY NOW",
                            onClick = {
                                viewModel.startDailyChallenge()
                                onNavigateToDailyChallenge()
                            },
                            glowColor = NeonGreen,
                            testTag = "daily_challenge_button"
                        )
                    }
                }
            }
        }

        // --- 4. Main Activity Middle Grid ---
        item {
            Text(
                text = "LEARNING MODULES",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // Row 1
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        ModuleMenuCard(
                            title = "Quiz / Tests",
                            description = "CBSE Chapter MCQs with feedback and animations.",
                            icon = Icons.Default.Gamepad,
                            glowColor = NeonBlue,
                            onClick = {
                                onTabSelected(HomeTab.QUIZ)
                            }
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        ModuleMenuCard(
                            title = "Short Notes",
                            description = "Definitions, formulas, textbook key highlights.",
                            icon = Icons.Default.Book,
                            glowColor = NeonPurple,
                            onClick = {
                                onTabSelected(HomeTab.NOTES)
                            }
                        )
                    }
                }

                // Row 2
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        ModuleMenuCard(
                            title = "Daily Battle",
                            description = "Randomized syllabus challenges and streak protectors.",
                            icon = Icons.Default.Bolt,
                            glowColor = GoldStreak,
                            onClick = {
                                viewModel.startDailyChallenge()
                                onNavigateToDailyChallenge()
                            }
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        ModuleMenuCard(
                            title = "My Progress",
                            description = "Completed courses, revision trackers, metrics.",
                            icon = Icons.Default.ShowChart,
                            glowColor = NeonGreen,
                            onClick = onNavigateToChapterProgress
                        )
                    }
                }
            }
        }

        // --- 5. Custom Gamer Badges Teaser ---
        item {
            GlassCard(borderGlowColor = TextMuted) {
                Text(
                    text = "YOUR ACTIVE BADGES",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                
                val badges = progress.badgesUnlocked.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                if (badges.isEmpty()) {
                    Text(
                        text = "Unlock badges by solving quizzes & scoring perfect points!",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        badges.forEach { badge ->
                            Box(
                                modifier = Modifier
                                    .background(NeonPurple.copy(alpha = 0.15f), shape = RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Default.WorkspacePremium, contentDescription = badge, tint = GoldStreak, modifier = Modifier.size(14.dp))
                                    Text(text = badge, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ModuleMenuCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    glowColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GlassBg),
        border = BorderStroke(1.dp, Brush.horizontalGradient(listOf(glowColor.copy(alpha = 0.5f), Color.Transparent)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = glowColor,
                modifier = Modifier.size(28.dp)
            )
            Column {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 14.sp,
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    activeTab: HomeTab,
    onTabSelected: (HomeTab) -> Unit
) {
    NavigationBar(
        containerColor = Color(0xDC060410),
        tonalElevation = 8.dp,
        modifier = Modifier.navigationBarsPadding()
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home", fontSize = 11.sp) },
            selected = activeTab == HomeTab.HOME,
            onClick = { onTabSelected(HomeTab.HOME) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NeonBlue,
                unselectedIconColor = TextSecondary,
                selectedTextColor = NeonBlue,
                unselectedTextColor = TextSecondary,
                indicatorColor = NeonBlue.copy(alpha = 0.15f)
            ),
            modifier = Modifier.testTag("nav_home")
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Book, contentDescription = "Notes") },
            label = { Text("Notes", fontSize = 11.sp) },
            selected = activeTab == HomeTab.NOTES,
            onClick = { onTabSelected(HomeTab.NOTES) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NeonPurple,
                unselectedIconColor = TextSecondary,
                selectedTextColor = NeonPurple,
                unselectedTextColor = TextSecondary,
                indicatorColor = NeonPurple.copy(alpha = 0.15f)
            ),
            modifier = Modifier.testTag("nav_notes")
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Gamepad, contentDescription = "Quiz") },
            label = { Text("Quiz", fontSize = 11.sp) },
            selected = activeTab == HomeTab.QUIZ,
            onClick = { onTabSelected(HomeTab.QUIZ) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NeonBlue,
                unselectedIconColor = TextSecondary,
                selectedTextColor = NeonBlue,
                unselectedTextColor = TextSecondary,
                indicatorColor = NeonBlue.copy(alpha = 0.15f)
            ),
            modifier = Modifier.testTag("nav_quiz")
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile", fontSize = 11.sp) },
            selected = activeTab == HomeTab.PROFILE,
            onClick = { onTabSelected(HomeTab.PROFILE) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NeonPurple,
                unselectedIconColor = TextSecondary,
                selectedTextColor = NeonPurple,
                unselectedTextColor = TextSecondary,
                indicatorColor = NeonPurple.copy(alpha = 0.15f)
            ),
            modifier = Modifier.testTag("nav_profile")
        )
    }
}
