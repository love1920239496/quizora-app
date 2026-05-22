package com.example.ui.screens

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

@Composable
fun ProfileScreen(
    viewModel: QuizoraViewModel,
    onSwitchLanguage: () -> Unit
) {
    val progress by viewModel.userProgress.collectAsState()
    val activeClass by viewModel.currentClass.collectAsState()

    val badgesList = remember(progress.badgesUnlocked) {
        val unlocked = progress.badgesUnlocked.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet()
        listOf(
            BadgeDetail("Quiz Rookie", "Awarded upon entering Quizora in Guest Mode.", "WorkspacePremium", unlocked.contains("Quiz Rookie")),
            BadgeDetail("Quiz King", "Reach 200 XP to master speed challenges.", "EmojiEvents", unlocked.contains("Quiz King")),
            BadgeDetail("Science Master", "Unlock 350 XP to claims the science scepter.", "Science", unlocked.contains("Science Master")),
            BadgeDetail("Math Genius", "Unlock 3 or more total badges successfully.", "Functions", unlocked.contains("Math Genius")),
            BadgeDetail("CBSE Champion", "Earn 500 XP to achieve the board ranking.", "LocalActivity", unlocked.contains("CBSE Champion"))
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 24.dp, bottom = 24.dp)
    ) {
        // Upper Gamer Avatar Badge Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GlassBg, RoundedCornerShape(24.dp))
                    .border(1.dp, NeonBlue.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(76.dp)
                            .background(NeonPurple.copy(alpha = 0.2f), shape = CircleShape)
                            .border(1.5.dp, NeonPurple, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Guest Student Avatar",
                            tint = NeonPurple,
                            modifier = Modifier.size(52.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "QUIZORA GUEST",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = "CBSE ${activeClass.uppercase()} STUDENT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonBlue,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        // Stats Display Block
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GamerStatBox(
                    modifier = Modifier.weight(1f),
                    title = "TOTAL XP",
                    value = "${progress.xp} XP",
                    color = NeonBlue,
                    icon = Icons.Default.Star
                )
                GamerStatBox(
                    modifier = Modifier.weight(1f),
                    title = "DAILY STREAK",
                    value = "${progress.streak} Days",
                    color = GoldStreak,
                    icon = Icons.Default.LocalFireDepartment
                )
            }
        }

        // Language / Class Quick Selectors
        item {
            GlassCard(borderGlowColor = NeonPurple) {
                Text(
                    text = "ACADEMICS SETTINGS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonPurple,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(14.dp))

                SettingsInteractiveRow(
                    title = "Instruction Medium",
                    subtitle = progress.selectedLanguage.ifEmpty { "English" },
                    icon = Icons.Default.Translate,
                    onClick = onSwitchLanguage
                )

                Spacer(modifier = Modifier.height(10.dp))
                Divider(color = TextMuted.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(10.dp))

                SettingsInteractiveRow(
                    title = "Selected Standard",
                    subtitle = activeClass,
                    icon = Icons.Default.School,
                    onClick = { /* Standard changing triggered via home screens */ }
                )
            }
        }

        // Gamer Badges Checklist
        item {
            Text(
                text = "BADGES PROGRESSION",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(top = 6.dp)
            )
        }

        items(badgesList) { badge ->
            BadgeRowCard(badge = badge)
        }

        // Reset statistics trigger for easy testing
        item {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Developer Diagnostics Mode (Guest Sandbox)",
                    fontSize = 11.sp,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(8.dp))
                NeonButton(
                    text = "RESET ALL STATISTICS",
                    onClick = { viewModel.resetAllProgress() },
                    modifier = Modifier.fillMaxWidth().testTag("reset_progress_button"),
                    isPrimary = false,
                    glowColor = NeonRed
                )
            }
        }
    }
}

data class BadgeDetail(
    val title: String,
    val description: String,
    val iconName: String,
    val isUnlocked: Boolean
)

@Composable
fun GamerStatBox(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Box(
        modifier = modifier
            .background(GlassBg, RoundedCornerShape(16.dp))
            .border(0.5.dp, color.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, fontSize = 10.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
            Text(text = value, fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SettingsInteractiveRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(imageVector = icon, contentDescription = title, tint = TextSecondary, modifier = Modifier.size(20.dp))
            Column {
                Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(text = subtitle, fontSize = 12.sp, color = TextMuted)
            }
        }
        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted, modifier = Modifier.size(20.dp))
    }
}

@Composable
fun BadgeRowCard(badge: BadgeDetail) {
    val borderColor = if (badge.isUnlocked) NeonPurple.copy(alpha = 0.6f) else TextMuted.copy(alpha = 0.2f)
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(GlassBg, RoundedCornerShape(16.dp))
            .border(0.5.dp, borderColor, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(42.dp)
                        .background(if (badge.isUnlocked) NeonPurple.copy(alpha = 0.2f) else Color(0x10FFFFFF), shape = CircleShape)
                        .border(1.dp, if (badge.isUnlocked) NeonPurple else TextMuted, CircleShape)
                ) {
                    val icon = when (badge.iconName) {
                        "EmojiEvents" -> Icons.Default.EmojiEvents
                        "Science" -> Icons.Default.Science
                        "Functions" -> Icons.Default.Functions
                        "LocalActivity" -> Icons.Default.LocalActivity
                        else -> Icons.Default.WorkspacePremium
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = badge.title,
                        tint = if (badge.isUnlocked) GoldStreak else TextMuted,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column {
                    Text(
                        text = badge.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (badge.isUnlocked) Color.White else TextSecondary
                    )
                    Text(
                        text = badge.description,
                        fontSize = 11.sp,
                        color = TextMuted,
                        lineHeight = 14.sp
                    )
                }
            }

            if (badge.isUnlocked) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Unlocked badge icon indicator",
                    tint = NeonGreen,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked badge",
                    tint = TextMuted,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
