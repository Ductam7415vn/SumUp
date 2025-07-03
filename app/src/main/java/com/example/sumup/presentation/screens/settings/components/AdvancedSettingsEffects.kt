package com.example.sumup.presentation.screens.settings.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun AnimatedBackgroundEffect(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "background")
    
    val particles = remember {
        List(20) {
            Particle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 3f + 1f,
                speed = Random.nextFloat() * 0.5f + 0.5f,
                angle = Random.nextFloat() * 360f
            )
        }
    }
    
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )
    
    Canvas(
        modifier = modifier.fillMaxSize()
    ) {
        particles.forEach { particle ->
            drawParticle(particle, time)
        }
    }
}

private fun DrawScope.drawParticle(particle: Particle, time: Float) {
    val x = (particle.x + time * particle.speed) % 1f * size.width
    val y = (particle.y + sin(time * 2 * PI * particle.speed).toFloat() * 50f) % size.height
    
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.3f),
                Color.White.copy(alpha = 0f)
            ),
            radius = particle.size.dp.toPx()
        ),
        radius = particle.size.dp.toPx(),
        center = Offset(x, y)
    )
}

private data class Particle(
    val x: Float,
    val y: Float,
    val size: Float,
    val speed: Float,
    val angle: Float
)

@Composable
fun PulsingGlow(
    color: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_scale"
    )
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(radius = 20.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            color.copy(alpha = alpha),
                            color.copy(alpha = 0f)
                        )
                    )
                )
        )
    }
}

@Composable
fun AnimatedWaveEffect(
    color: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val density = LocalDensity.current
    
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_phase"
    )
    
    Canvas(
        modifier = modifier.fillMaxSize()
    ) {
        val wavePath = Path()
        val amplitude = 20.dp.toPx()
        val wavelength = size.width / 3
        
        wavePath.moveTo(0f, size.height / 2)
        
        for (x in 0..size.width.toInt()) {
            val y = size.height / 2 + amplitude * sin(
                (x / wavelength * 2 * PI + wavePhase).toDouble()
            ).toFloat()
            wavePath.lineTo(x.toFloat(), y)
        }
        
        wavePath.lineTo(size.width, size.height)
        wavePath.lineTo(0f, size.height)
        wavePath.close()
        
        drawPath(
            path = wavePath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    color.copy(alpha = 0.3f),
                    color.copy(alpha = 0.1f)
                )
            )
        )
    }
}

@Composable
fun ShimmerEffect(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    
    val shimmerTranslateAnim by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )
    
    Box(
        modifier = modifier
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.LightGray.copy(alpha = 0.6f),
                        Color.LightGray.copy(alpha = 0.2f),
                        Color.LightGray.copy(alpha = 0.6f)
                    ),
                    start = Offset(shimmerTranslateAnim * 600f, 0f),
                    end = Offset(shimmerTranslateAnim * 600f + 300f, 0f)
                )
            )
    )
}

@Composable
fun FloatingBubbles(
    bubbleCount: Int = 10,
    modifier: Modifier = Modifier
) {
    val bubbles = remember {
        List(bubbleCount) {
            Bubble(
                startX = Random.nextFloat(),
                startY = Random.nextFloat() + 1f,
                size = Random.nextFloat() * 20f + 10f,
                duration = Random.nextInt(3000, 6000)
            )
        }
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        bubbles.forEach { bubble ->
            AnimatedBubble(bubble = bubble)
        }
    }
}

@Composable
private fun AnimatedBubble(bubble: Bubble) {
    val infiniteTransition = rememberInfiniteTransition(label = "bubble_${bubble.hashCode()}")
    
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = -0.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(bubble.duration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "bubble_y"
    )
    
    val offsetX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(bubble.duration / 2, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bubble_x"
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                translationX = (bubble.startX + offsetX) * size.width
                translationY = offsetY * size.height
            }
    ) {
        Canvas(
            modifier = Modifier.size(bubble.size.dp)
        ) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.3f),
                        Color.White.copy(alpha = 0.1f)
                    )
                ),
                radius = bubble.size.dp.toPx() / 2
            )
        }
    }
}

private data class Bubble(
    val startX: Float,
    val startY: Float,
    val size: Float,
    val duration: Int
)

@Composable
fun GeometricPattern(
    color: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pattern")
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pattern_rotation"
    )
    
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer {
                alpha = 0.1f
            }
    ) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = minOf(size.width, size.height) / 3
        
        // Draw rotating hexagon pattern
        for (i in 0..5) {
            rotate(rotation + i * 60f, pivot = Offset(centerX, centerY)) {
                drawHexagon(
                    center = Offset(centerX, centerY),
                    radius = radius,
                    color = color.copy(alpha = 0.2f)
                )
            }
        }
    }
}

private fun DrawScope.drawHexagon(
    center: Offset,
    radius: Float,
    color: Color
) {
    val path = Path()
    val angleStep = 60f
    
    for (i in 0..5) {
        val angle = i * angleStep * PI / 180
        val x = center.x + radius * cos(angle).toFloat()
        val y = center.y + radius * sin(angle).toFloat()
        
        if (i == 0) {
            path.moveTo(x, y)
        } else {
            path.lineTo(x, y)
        }
    }
    
    path.close()
    
    drawPath(
        path = path,
        color = color,
        style = Stroke(width = 2.dp.toPx())
    )
}