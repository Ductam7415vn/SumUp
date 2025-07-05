package com.example.sumup.presentation.components.animations

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.*
import kotlin.random.Random

/**
 * Confetti celebration effect
 */
@Composable
fun ConfettiCelebration(
    isActive: Boolean,
    modifier: Modifier = Modifier,
    particleCount: Int = 50,
    duration: Long = 3000L
) {
    var confettiParticles by remember { mutableStateOf(listOf<ConfettiParticle>()) }
    val density = LocalDensity.current
    
    LaunchedEffect(isActive) {
        if (isActive) {
            confettiParticles = List(particleCount) {
                ConfettiParticle(
                    x = Random.nextFloat(),
                    y = -0.1f,
                    vx = Random.nextFloat() * 0.002f - 0.001f,
                    vy = Random.nextFloat() * 0.003f + 0.002f,
                    rotation = Random.nextFloat() * 360f,
                    rotationSpeed = Random.nextFloat() * 10f - 5f,
                    color = listOf(
                        Color(0xFFFFC107), // Amber
                        Color(0xFF4CAF50), // Green
                        Color(0xFF2196F3), // Blue
                        Color(0xFFE91E63), // Pink
                        Color(0xFF9C27B0), // Purple
                        Color(0xFFFF5722)  // Orange
                    ).random(),
                    size = with(density) { Random.nextInt(6, 12).dp.toPx() },
                    shape = listOf(
                        ConfettiShape.SQUARE,
                        ConfettiShape.CIRCLE,
                        ConfettiShape.TRIANGLE
                    ).random()
                )
            }
            
            val startTime = System.currentTimeMillis()
            while (System.currentTimeMillis() - startTime < duration) {
                confettiParticles = confettiParticles.map { particle ->
                    particle.copy(
                        x = particle.x + particle.vx,
                        y = particle.y + particle.vy,
                        rotation = particle.rotation + particle.rotationSpeed,
                        vy = particle.vy + 0.0001f // gravity
                    )
                }.filter { it.y < 1.1f }
                
                delay(16) // 60 FPS
            }
            confettiParticles = emptyList()
        }
    }
    
    Canvas(modifier = modifier.fillMaxSize()) {
        confettiParticles.forEach { particle ->
            drawConfettiParticle(particle)
        }
    }
}

private fun DrawScope.drawConfettiParticle(particle: ConfettiParticle) {
    val x = size.width * particle.x
    val y = size.height * particle.y
    
    rotate(
        degrees = particle.rotation,
        pivot = Offset(x, y)
    ) {
        when (particle.shape) {
            ConfettiShape.SQUARE -> {
                drawRect(
                    color = particle.color,
                    topLeft = Offset(x - particle.size / 2, y - particle.size / 2),
                    size = androidx.compose.ui.geometry.Size(particle.size, particle.size)
                )
            }
            ConfettiShape.CIRCLE -> {
                drawCircle(
                    color = particle.color,
                    radius = particle.size / 2,
                    center = Offset(x, y)
                )
            }
            ConfettiShape.TRIANGLE -> {
                val path = Path().apply {
                    moveTo(x, y - particle.size / 2)
                    lineTo(x - particle.size / 2, y + particle.size / 2)
                    lineTo(x + particle.size / 2, y + particle.size / 2)
                    close()
                }
                drawPath(path, particle.color)
            }
        }
    }
}

private data class ConfettiParticle(
    val x: Float,
    val y: Float,
    val vx: Float,
    val vy: Float,
    val rotation: Float,
    val rotationSpeed: Float,
    val color: Color,
    val size: Float,
    val shape: ConfettiShape
)

private enum class ConfettiShape {
    SQUARE, CIRCLE, TRIANGLE
}

/**
 * Success ripple effect
 */
@Composable
fun SuccessRipple(
    isActive: Boolean,
    color: Color = MaterialTheme.colorScheme.primary,
    maxRadius: Dp = 200.dp,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ripple")
    
    val animatedRadius by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rippleRadius"
    )
    
    val animatedAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rippleAlpha"
    )
    
    if (isActive) {
        Canvas(
            modifier = modifier.size(maxRadius * 2)
        ) {
            val radius = maxRadius.toPx() * animatedRadius
            
            drawCircle(
                color = color.copy(alpha = animatedAlpha),
                radius = radius,
                style = Stroke(width = 3.dp.toPx())
            )
            
            // Secondary ripple
            if (animatedRadius > 0.3f) {
                val secondaryRadius = maxRadius.toPx() * (animatedRadius - 0.3f)
                drawCircle(
                    color = color.copy(alpha = animatedAlpha * 0.5f),
                    radius = secondaryRadius,
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }
    }
}

/**
 * Sparkle burst effect
 */
@Composable
fun SparkleBurst(
    trigger: Boolean,
    sparkleColor: Color = MaterialTheme.colorScheme.primary,
    sparkleCount: Int = 8,
    modifier: Modifier = Modifier
) {
    var sparkles by remember { mutableStateOf(listOf<Sparkle>()) }
    
    LaunchedEffect(trigger) {
        if (trigger) {
            sparkles = List(sparkleCount) { index ->
                val angle = (360f / sparkleCount) * index
                Sparkle(
                    angle = angle,
                    distance = 0f,
                    size = Random.nextFloat() * 4f + 2f,
                    alpha = 1f
                )
            }
            
            val animationDuration = 1000L
            val startTime = System.currentTimeMillis()
            
            while (System.currentTimeMillis() - startTime < animationDuration) {
                val progress = (System.currentTimeMillis() - startTime).toFloat() / animationDuration
                
                sparkles = sparkles.map { sparkle ->
                    sparkle.copy(
                        distance = progress * 100f,
                        alpha = 1f - progress
                    )
                }
                
                delay(16)
            }
            
            sparkles = emptyList()
        }
    }
    
    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        
        sparkles.forEach { sparkle ->
            val radians = Math.toRadians(sparkle.angle.toDouble())
            val x = centerX + cos(radians).toFloat() * sparkle.distance
            val y = centerY + sin(radians).toFloat() * sparkle.distance
            
            drawCircle(
                color = sparkleColor.copy(alpha = sparkle.alpha),
                radius = sparkle.size,
                center = Offset(x, y)
            )
            
            // Draw sparkle cross
            drawLine(
                color = sparkleColor.copy(alpha = sparkle.alpha * 0.6f),
                start = Offset(x - sparkle.size * 2, y),
                end = Offset(x + sparkle.size * 2, y),
                strokeWidth = 1.dp.toPx()
            )
            drawLine(
                color = sparkleColor.copy(alpha = sparkle.alpha * 0.6f),
                start = Offset(x, y - sparkle.size * 2),
                end = Offset(x, y + sparkle.size * 2),
                strokeWidth = 1.dp.toPx()
            )
        }
    }
}

private data class Sparkle(
    val angle: Float,
    val distance: Float,
    val size: Float,
    val alpha: Float
)

/**
 * Heart explosion for favorites
 */
@Composable
fun HeartExplosion(
    isActive: Boolean,
    heartColor: Color = MaterialTheme.colorScheme.error,
    modifier: Modifier = Modifier
) {
    var hearts by remember { mutableStateOf(listOf<AnimatedHeart>()) }
    
    LaunchedEffect(isActive) {
        if (isActive) {
            hearts = List(12) { index ->
                val angle = Random.nextFloat() * 360f
                AnimatedHeart(
                    x = 0f,
                    y = 0f,
                    vx = cos(Math.toRadians(angle.toDouble())).toFloat() * Random.nextFloat() * 3f,
                    vy = sin(Math.toRadians(angle.toDouble())).toFloat() * Random.nextFloat() * 3f - 1f,
                    size = Random.nextFloat() * 10f + 5f,
                    rotation = Random.nextFloat() * 360f,
                    alpha = 1f
                )
            }
            
            val duration = 2000L
            val startTime = System.currentTimeMillis()
            
            while (System.currentTimeMillis() - startTime < duration) {
                val deltaTime = 0.016f
                
                hearts = hearts.map { heart ->
                    heart.copy(
                        x = heart.x + heart.vx,
                        y = heart.y + heart.vy,
                        vy = heart.vy + 0.1f, // gravity
                        rotation = heart.rotation + 5f,
                        alpha = heart.alpha - deltaTime * 0.5f
                    )
                }.filter { it.alpha > 0 }
                
                delay(16)
            }
            
            hearts = emptyList()
        }
    }
    
    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        
        hearts.forEach { heart ->
            drawHeart(
                center = Offset(centerX + heart.x * 20, centerY + heart.y * 20),
                size = heart.size,
                color = heartColor.copy(alpha = heart.alpha),
                rotation = heart.rotation
            )
        }
    }
}

private fun DrawScope.drawHeart(
    center: Offset,
    size: Float,
    color: Color,
    rotation: Float
) {
    rotate(degrees = rotation, pivot = center) {
        val path = Path().apply {
            val width = size * 2
            val height = size * 2
            
            moveTo(center.x, center.y + height * 0.25f)
            
            cubicTo(
                center.x - width * 0.5f, center.y - height * 0.25f,
                center.x - width * 0.5f, center.y + height * 0.1f,
                center.x, center.y + height * 0.25f
            )
            
            cubicTo(
                center.x + width * 0.5f, center.y + height * 0.1f,
                center.x + width * 0.5f, center.y - height * 0.25f,
                center.x, center.y + height * 0.25f
            )
        }
        
        drawPath(path, color)
    }
}

private data class AnimatedHeart(
    val x: Float,
    val y: Float,
    val vx: Float,
    val vy: Float,
    val size: Float,
    val rotation: Float,
    val alpha: Float
)