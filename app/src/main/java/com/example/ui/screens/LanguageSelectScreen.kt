package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.components.GlowBackground
import com.example.ui.components.NeonButton
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.QuizoraViewModel

@Composable
fun LanguageSelectScreen(
    viewModel: QuizoraViewModel,
    onLanguageSelected: () -> Unit
) {
    GlowBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            // Upper Title Segment
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Translate,
                    contentDescription = "Language Selector icon",
                    tint = NeonBlue,
                    modifier = Modifier.size(52.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "SELECT MEDIUM",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "अपना शिक्षण माध्यम चुनें",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonPurple,
                    textAlign = TextAlign.Center
                )
            }

            // Glass container enclosing choice cards
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                borderGlowColor = NeonPurple
            ) {
                Text(
                    text = "Please select your preferred CBSE instruction medium to customize notes & challenges.",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    color = TextSecondary,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                NeonButton(
                    text = "ENGLISH MEDIUM",
                    onClick = {
                        viewModel.selectLanguage("English")
                        onLanguageSelected()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    glowColor = NeonBlue,
                    testTag = "english_medium_button"
                )

                Spacer(modifier = Modifier.height(16.dp))

                NeonButton(
                    text = "हिंदी माध्यम (HINDI MEDIUM)",
                    onClick = {
                        viewModel.selectLanguage("Hindi")
                        onLanguageSelected()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    glowColor = NeonPurple,
                    testTag = "hindi_medium_button"
                )
            }

            // CBSE footer tag
            Text(
                text = "CBSE Class 6 - 12 (Revised 2026 Board Guidelines)",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }
    }
}
