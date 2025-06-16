package com.example.sumup.presentation.components.animations

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.*
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.*
import kotlin.random.Random

/**
 * Animated floating action button with scale and rotation effects
 */
@Composable
fun AnimatedFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.85f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "fabScale"
    )
    
    val rotation by animateFloatAsState(
        targetValue = if (isPressed) 15f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "fabRotation"
    )
    
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier
            .scale(scale)
            .graphicsLayer {
                rotationZ = rotation
            },
        containerColor = containerColor,
        contentColor = contentColor,
        interactionSource = interactionSource
    ) {
        content()
    }
}

/**
 * Pulsing dot indicator for active states
 */
@Composable
fun PulsingDot(
    color: Color,
    size: Dp = 8.dp,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulsing")
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )
    
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .size(size)
                .scale(scale)
        ) {
            drawCircle(
                color = color.copy(alpha = alpha),
                radius = size.toPx() / 2
            )
        }
    }
}

/**
 * Bouncing button with spring animation
 */
@Composable
fun BouncyButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = when {
            !enabled -> 0.95f
            isPressed -> 0.9f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "buttonScale"
    )
    
    Button(
        onClick = onClick,
        modifier = modifier.scale(scale),
        enabled = enabled,
        colors = colors,
        contentPadding = contentPadding,
        interactionSource = interactionSource
    ) {
        content()
    }
}

/**
 * Animated success checkmark
 */
@Composable
fun AnimatedCheckmark(
    isVisible: Boolean,
    color: Color = MaterialTheme.colorScheme.primary,
    size: Dp = 24.dp,
    strokeWidth: Dp = 2.dp,
    modifier: Modifier = Modifier
) {
    val transition = updateTransition(targetState = isVisible, label = "checkmark")
    
    val progress by transition.animateFloat(
        transitionSpec = {
            if (targetState) {
                spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            } else {
                tween(150)
            }
        },
        label = "checkmarkProgress"
    ) { visible ->
        if (visible) 1f else 0f
    }
    
    val scale by transition.animateFloat(
        transitionSpec = {
            if (targetState) {
                spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            } else {
                tween(150)
            }
        },
        label = "checkmarkScale"
    ) { visible ->
        if (visible) 1f else 0.8f
    }
    
    if (progress > 0f) {
        Canvas(
            modifier = modifier
                .size(size)
                .scale(scale)
        ) {
            val path = Path().apply {
                val width = size.toPx()
                val height = size.toPx()
                
                moveTo(width * 0.2f, height * 0.5f)
                
                if (progress > 0.5f) {
                    lineTo(width * 0.4f, height * 0.7f)
                }
                
                if (progress > 0.5f) {
                    val endProgress = (progress - 0.5f) * 2f
                    lineTo(
                        width * 0.4f + (width * 0.4f * endProgress),
                        height * 0.7f - (height * 0.4f * endProgress)
                    )
                }
            }
            
            drawPath(
                path = path,
                color = color,
                style = Stroke(
                    width = strokeWidth.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
    }
}

/**
 * Particle explosion effect for celebrations
 */
@Composable
fun ParticleExplosion(
    isActive: Boolean,
    particleColor: Color = MaterialTheme.colorScheme.primary,
    particleCount: Int = 20,
    modifier: Modifier = Modifier
) {
    var particles by remember { mutableStateOf(listOf<Particle>()) }
    val density = LocalDensity.current
    
    LaunchedEffect(isActive) {
        if (isActive) {
            particles = List(particleCount) {
                Particle(
                    x = 0f,
                    y = 0f,
                    vx = Random.nextFloat() * 10f - 5f,
                    vy = Random.nextFloat() * -15f - 5f,
                    color = particleColor.copy(
                        alpha = Random.nextFloat() * 0.5f + 0.5f
                    ),
                    size = with(density) { Random.nextInt(3, 8).dp.toPx() },
                    lifespan = Random.nextFloat() * 0.5f + 0.5f
                )
            }
            
            val startTime = System.currentTimeMillis()
            while (System.currentTimeMillis() - startTime < 2000) {
                val deltaTime = 0.016f // 60 FPS
                particles = particles.map { particle ->
                    particle.copy(
                        x = particle.x + particle.vx,
                        y = particle.y + particle.vy,
                        vy = particle.vy + 0.5f, // gravity
                        lifespan = particle.lifespan - deltaTime
                    )
                }.filter { it.lifespan > 0 }
                
                kotlinx.coroutines.delay(16)
            }
            particles = emptyList()
        }
    }
    
    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        
        particles.forEach { particle ->
            drawCircle(
                color = particle.color.copy(alpha = particle.color.alpha * particle.lifespan),
                radius = particle.size * particle.lifespan,
                center = Offset(
                    centerX + particle.x * 10,
                    centerY + particle.y * 10
                )
            )
        }
    }
}

private data class Particle(
    val x: Float,
    val y: Float,
    val vx: Float,
    val vy: Float,
    val color: Color,
    val size: Float,
    val lifespan: Float
)

/**
 * Morphing shape animation
 */
@Composable
fun MorphingShape(
    progress: Float,
    fromShape: Shape,
    toShape: Shape,
    color: Color,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "morphProgress"
    )
    
    // This is a simplified morph - in practice you'd interpolate between paths
    Box(
        modifier = modifier
            .graphicsLayer {
                alpha = if (animatedProgress < 0.5f) 1f - animatedProgress * 2 else 0f
            }
    ) {
        Surface(
            color = color,
            shape = fromShape,
            modifier = Modifier.fillMaxSize()
        ) {}
    }
    
    Box(
        modifier = modifier
            .graphicsLayer {
                alpha = if (animatedProgress > 0.5f) (animatedProgress - 0.5f) * 2 else 0f
            }
    ) {
        Surface(
            color = color,
            shape = toShape,
            modifier = Modifier.fillMaxSize()
        ) {}
    }
}

/**
 * Animated number counter
 */
@Composable
fun AnimatedCounter(
    count: Int,
    modifier: Modifier = Modifier,
    style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.headlineMedium,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    var oldCount by remember { mutableIntStateOf(count) }
    
    SideEffect {
        oldCount = count
    }
    
    Row(modifier = modifier) {
        val countString = count.toString()
        val oldCountString = oldCount.toString()
        
        for (i in countString.indices) {
            val oldChar = oldCountString.getOrNull(i)
            val newChar = countString[i]
            val char = if (oldChar == newChar) {
                oldCountString[i]
            } else {
                countString[i]
            }
            
            AnimatedContent(
                targetState = char,
                transitionSpec = {
                    slideInVertically { height -> height } togetherWith slideOutVertically { height -> -height }
                },
                label = "counterDigit$i"
            ) { digit ->
                Text(
                    text = digit.toString(),
                    style = style,
                    color = color,
                    softWrap = false
                )
            }
        }
    }
}

/**
 * Loading dots animation
 */
@Composable
fun LoadingDots(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    dotSize: Dp = 8.dp,
    spaceBetween: Dp = 4.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loadingDots")
    
    @Composable
    fun Dot(delay: Int) {
        val alpha by infiniteTransition.animateFloat(
            initialValue = 0.25f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 1200
                    0.25f at delay with LinearEasing
                    1f at delay + 400 with LinearEasing
                    0.25f at delay + 800 with LinearEasing
                },
                repeatMode = RepeatMode.Restart
            ),
            label = "dotAlpha$delay"
        )
        
        Box(
            modifier = Modifier
                .size(dotSize)
                .graphicsLayer { this.alpha = alpha }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(color = color)
            }
        }
    }
    
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spaceBetween)
    ) {
        Dot(delay = 0)
        Dot(delay = 200)
        Dot(delay = 400)
    }
}