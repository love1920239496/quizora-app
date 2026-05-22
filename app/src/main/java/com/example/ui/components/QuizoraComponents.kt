package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun GlowBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    // Elegant dual gradient simulating deep purple-indigo cybernetic atmosphere
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(SpaceBgStart, SpaceBgEnd)
    )
    
    // Pulse animation for ambient neon spheres
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_background")
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ambient_glow"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .drawBehind {
                // Outer space cyan ambient glow
                drawCircle(
                    color = NeonBlue.copy(alpha = 0.08f * alphaAnim),
                    radius = size.width / 1.5f,
                    center = androidx.compose.ui.geometry.Offset(0f, 0f)
                )
                // Bottom right purple ambient glow
                drawCircle(
                    color = NeonPurple.copy(alpha = 0.12f * alphaAnim),
                    radius = size.width / 1.2f,
                    center = androidx.compose.ui.geometry.Offset(size.width, size.height)
                )
            }
    ) {
        content()
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    borderGlowColor: Color = NeonBlue,
    borderWidth: Dp = 1.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val roundedShape = RoundedCornerShape(24.dp)
    
    Card(
        modifier = modifier
            .clip(roundedShape)
            .background(Color.Transparent),
        shape = roundedShape,
        colors = CardDefaults.cardColors(
            containerColor = GlassBg
        ),
        border = BorderStroke(
            borderWidth,
            Brush.linearGradient(
                colors = listOf(borderGlowColor.copy(alpha = 0.8f), borderGlowColor.copy(alpha = 0.1f))
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            content = content
        )
    }
}

@Composable
fun NeonButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = true,
    enabled: Boolean = true,
    glowColor: Color = NeonBlue,
    testTag: String? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "btn_glow")
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val shape = RoundedCornerShape(16.dp)
    
    val buttonModifier = if (testTag != null) {
        modifier.testTag(testTag)
    } else {
        modifier
    }

    Surface(
        modifier = buttonModifier
            .height(52.dp)
            .clip(shape)
            .clickable(
                enabled = enabled,
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple()
            ),
        shape = shape,
        color = if (isPrimary) glowColor.copy(alpha = 0.2f) else Color.Transparent,
        border = BorderStroke(
            width = if (isPrimary) 1.5.dp else 1.dp,
            brush = if (isPrimary) {
                Brush.horizontalGradient(
                    colors = listOf(glowColor, glowColor.copy(alpha = 0.4f))
                )
            } else {
                Brush.horizontalGradient(
                    colors = listOf(TextMuted, TextMuted.copy(alpha = 0.2f))
                )
            }
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = if (isPrimary) Color.White else TextSecondary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun StatusShield(
    icon: ImageVector,
    label: String,
    value: String,
    glowColor: Color = NeonBlue,
    onClick: (() -> Unit)? = null
) {
    val roundedShape = RoundedCornerShape(14.dp)
    val clickableModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
    }

    Box(
        modifier = Modifier
            .then(clickableModifier)
            .clip(roundedShape)
            .background(GlassBg)
            .border(
                border = BorderStroke(
                    0.5.dp,
                    Brush.verticalGradient(listOf(glowColor.copy(alpha = 0.4f), Color.Transparent))
                ),
                shape = roundedShape
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = glowColor,
                modifier = Modifier.size(18.dp)
            )
            Column {
                Text(
                    text = label,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary
                )
                Text(
                    text = value,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun CircularGlowProgress(
    percentage: Float,
    title: String,
    scoreText: String,
    glowColor: Color = NeonBlue,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 10.dp
) {
    val animatedProgress = remember { Animatable(0f) }
    LaunchedEffect(percentage) {
        animatedProgress.animateTo(
            targetValue = percentage.coerceIn(0f, 1f),
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .drawBehind {
                    // Gray background arc
                    drawArc(
                        color = Color(0x306C688D),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                            width = strokeWidth.toPx(),
                            cap = androidx.compose.ui.graphics.StrokeCap.Round
                        )
                    )

                    // Draw the glow backdrop for the neon arc
                    drawArc(
                        color = glowColor.copy(alpha = 0.25f),
                        startAngle = -90f,
                        sweepAngle = 360f * animatedProgress.value,
                        useCenter = false,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                            width = (strokeWidth + 4.dp).toPx(),
                            cap = androidx.compose.ui.graphics.StrokeCap.Round
                        )
                    )

                    // Draw the primary colored sweep arc
                    drawArc(
                        color = glowColor,
                        startAngle = -90f,
                        sweepAngle = 360f * animatedProgress.value,
                        useCenter = false,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                            width = strokeWidth.toPx(),
                            cap = androidx.compose.ui.graphics.StrokeCap.Round
                        )
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = scoreText,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Text(
                    text = "${(animatedProgress.value * 100).toInt()}%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}
