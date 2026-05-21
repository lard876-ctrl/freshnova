package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.KitchenGreen
import com.example.ui.theme.KitchenLightBackground

@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    // Elegant pulsing and scaling animations for a premium entrance feel
    val infiniteTransition = rememberInfiniteTransition(label = "Pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        KitchenLightBackground,
                        Color(0xFFE8FFE9),
                        Color(0xFFFFFFFF)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Decorative background subtle drawings (floating fruits/veggie sketches)
        BackgroundDecorations()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // High fidelity drawing of shopping bag with green checkmark (matches mockup #8)
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .graphicsLayer {
                        scaleX = pulseScale
                        scaleY = pulseScale
                    },
                contentAlignment = Alignment.Center
            ) {
                ShoppingBagLogo()
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "FreshNova",
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = KitchenGreen,
                textAlign = TextAlign.Center,
                letterSpacing = (-1.5).sp,
                fontFamily = FontFamily.SansSerif
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Track expiry • Save food • Save money",
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                fontFamily = FontFamily.SansSerif
            )

            Spacer(modifier = Modifier.height(56.dp))

            CircularProgressIndicator(
                color = KitchenGreen,
                strokeWidth = 3.dp,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Smart Kitchen Management",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun ShoppingBagLogo() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // 1. Draw elegant bag handle
        val handlePath = Path().apply {
            moveTo(w * 0.35f, h * 0.4f)
            cubicTo(
                w * 0.35f, h * 0.18f,
                w * 0.65f, h * 0.18f,
                w * 0.65f, h * 0.4f
            )
        }
        drawPath(
            path = handlePath,
            color = Color(0xFF1B5E20),
            style = Stroke(width = 12f, cap = StrokeCap.Round)
        )

        // 2. Draw glossy rounded green bag body
        val bodyPath = Path().apply {
            moveTo(w * 0.25f, h * 0.42f)
            lineTo(w * 0.75f, h * 0.42f)
            quadraticTo(w * 0.82f, h * 0.42f, w * 0.80f, h * 0.5f)
            lineTo(w * 0.75f, h * 0.80f)
            quadraticTo(w * 0.72f, h * 0.88f, w * 0.65f, h * 0.88f)
            lineTo(w * 0.35f, h * 0.88f)
            quadraticTo(w * 0.28f, h * 0.88f, w * 0.25f, h * 0.80f)
            lineTo(w * 0.20f, h * 0.5f)
            quadraticTo(w * 0.18f, h * 0.42f, w * 0.25f, h * 0.42f)
            close()
        }
        drawPath(
            path = bodyPath,
            color = KitchenGreen
        )

        // 3. Draw white circle badge in the center of the bag (mockup image #8)
        drawCircle(
            color = Color.White,
            radius = w * 0.16f,
            center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.65f)
        )

        // 4. Draw green checkmark inside the circle
        val checkPath = Path().apply {
            moveTo(w * 0.43f, h * 0.65f)
            lineTo(w * 0.48f, h * 0.70f)
            lineTo(w * 0.58f, h * 0.58f)
        }
        drawPath(
            path = checkPath,
            color = KitchenGreen,
            style = Stroke(width = 10f, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun BackgroundDecorations() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Draw soft decorative floating elements
        drawCircle(
            color = KitchenGreen.copy(alpha = 0.04f),
            radius = 120f,
            center = androidx.compose.ui.geometry.Offset(w * 0.15f, h * 0.22f)
        )
        drawCircle(
            color = Color(0xFFF97316).copy(alpha = 0.03f),
            radius = 90f,
            center = androidx.compose.ui.geometry.Offset(w * 0.85f, h * 0.35f)
        )
        drawCircle(
            color = KitchenGreen.copy(alpha = 0.04f),
            radius = 160f,
            center = androidx.compose.ui.geometry.Offset(w * 0.25f, h * 0.85f)
        )
    }
}
