package com.example.sumup.presentation.screens.settings.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import com.example.sumup.presentation.components.HapticIconButton
import com.example.sumup.utils.haptic.HapticFeedbackType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.*
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun ModernSettingsTopBar(
    onNavigateBack: () -> Unit,
    scrollOffset: Float = 0f,
    modifier: Modifier = Modifier,
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {},
    userAvatar: String? = null,
    userName: String = "User"
) {
    var isSearchExpanded by remember { mutableStateOf(false) }
    var searchTextFieldValue by remember { mutableStateOf(TextFieldValue(searchQuery)) }
    val scope = rememberCoroutineScope()
    
    // Animation states
    val isScrolled = scrollOffset > 50f
    val scrollProgress = (scrollOffset / 200f).coerceIn(0f, 1f)
    
    // Dynamic height animation
    val topBarHeight by animateDpAsState(
        targetValue = if (isScrolled) 56.dp else 80.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "topbar_height"
    )
    
    // Gradient animation
    val gradientOffset by rememberInfiniteTransition(label = "gradient").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradient_offset"
    )
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(topBarHeight)
            .drawBehind {
                // Animated gradient background
                val gradient = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF6366F1).copy(alpha = 0.9f + 0.1f * gradientOffset),
                        Color(0xFF8B5CF6).copy(alpha = 0.85f + 0.15f * gradientOffset),
                        Color(0xFFEC4899).copy(alpha = 0.8f + 0.2f * gradientOffset)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(size.width * (1f + gradientOffset * 0.3f), size.height)
                )
                drawRect(gradient)
                
                // Glassmorphism overlay
                drawRect(
                    Color.White.copy(alpha = 0.1f * (1f - scrollProgress)),
                    blendMode = BlendMode.Overlay
                )
            }
            .blur(radius = if (isScrolled) 0.dp else 1.dp)
    ) {
        // Particle effects
        ParticleEffect(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = 0.3f * (1f - scrollProgress) }
        )
        
        // Glassmorphism effect
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.White.copy(alpha = 0.05f * scrollProgress)
                )
        )
        
        // Content
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left section - Back button and title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Back button with glow effect
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .drawBehind {
                            if (!isScrolled) {
                                drawCircle(
                                    Color.White.copy(alpha = 0.2f),
                                    radius = 20.dp.toPx(),
                                    blendMode = BlendMode.Plus
                                )
                            }
                        }
                ) {
                    HapticIconButton(
                        onClick = onNavigateBack,
                        hapticType = HapticFeedbackType.NAVIGATION,
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier
                                .size(24.dp)
                                .graphicsLayer {
                                    rotationZ = -10f * (1f - scrollProgress)
                                }
                        )
                    }
                }
                
                // Animated title with search
                AnimatedContent(
                    targetState = isSearchExpanded,
                    transitionSpec = {
                        (fadeIn(animationSpec = tween(300)) + 
                         slideInHorizontally(animationSpec = tween(300)) with
                         fadeOut(animationSpec = tween(200)) + 
                         slideOutHorizontally(animationSpec = tween(200)))
                            .using(SizeTransform(clip = false))
                    }
                ) { expanded ->
                    if (expanded) {
                        // Search field
                        SearchField(
                            value = searchTextFieldValue,
                            onValueChange = { 
                                searchTextFieldValue = it
                                onSearchQueryChange(it.text)
                            },
                            onClose = {
                                isSearchExpanded = false
                                searchTextFieldValue = TextFieldValue("")
                                onSearchQueryChange("")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                        )
                    } else {
                        // Title
                        ModernTitle(
                            isScrolled = isScrolled,
                            scrollProgress = scrollProgress
                        )
                    }
                }
            }
            
            // Right section - Search and profile
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Search button
                if (!isSearchExpanded) {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut() + scaleOut()
                    ) {
                        SearchButton(
                            onClick = { isSearchExpanded = true },
                            isScrolled = isScrolled
                        )
                    }
                }
                
                // Profile avatar
                ProfileAvatar(
                    userAvatar = userAvatar,
                    userName = userName,
                    isScrolled = isScrolled,
                    scrollProgress = scrollProgress
                )
            }
        }
    }
}

@Composable
private fun ModernTitle(
    isScrolled: Boolean,
    scrollProgress: Float
) {
    val scale by animateFloatAsState(
        targetValue = if (isScrolled) 0.85f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy
        ),
        label = "title_scale"
    )
    
    val shimmerTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerOffset by shimmerTransition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_offset"
    )
    
    Column(
        modifier = Modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
            alpha = 1f - scrollProgress * 0.2f
        }
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = if (isScrolled) 22.sp else 28.sp
            ),
            color = Color.White,
            modifier = Modifier.drawWithContent {
                drawContent()
                
                // Shimmer effect
                if (!isScrolled) {
                    val shimmerBrush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.3f),
                            Color.Transparent
                        ),
                        start = Offset(size.width * shimmerOffset - size.width, 0f),
                        end = Offset(size.width * shimmerOffset, size.height)
                    )
                    drawRect(
                        brush = shimmerBrush,
                        blendMode = BlendMode.Plus
                    )
                }
            }
        )
        
        AnimatedVisibility(visible = !isScrolled) {
            Text(
                text = "Personalize your experience",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
private fun SearchField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = Color.White.copy(alpha = 0.15f),
        border = BorderStroke(
            width = 1.dp,
            color = Color.White.copy(alpha = 0.2f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.size(20.dp)
            )
            
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.White
                ),
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                decorationBox = { innerTextField ->
                    Box {
                        if (value.text.isEmpty()) {
                            Text(
                                "Search settings...",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White.copy(alpha = 0.5f)
                            )
                        }
                        innerTextField()
                    }
                }
            )
            
            IconButton(
                onClick = onClose,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Close search",
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun SearchButton(
    onClick: () -> Unit,
    isScrolled: Boolean
) {
    val pulseAnimation = rememberInfiniteTransition(label = "pulse")
    val pulseScale by pulseAnimation.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )
    
    Box(
        modifier = Modifier
            .size(40.dp)
            .drawBehind {
                if (!isScrolled) {
                    drawCircle(
                        Color.White.copy(alpha = 0.1f),
                        radius = 20.dp.toPx() * pulseScale,
                        blendMode = BlendMode.Plus
                    )
                }
            }
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Icon(
                Icons.Default.Search,
                contentDescription = "Search",
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .graphicsLayer {
                        scaleX = if (!isScrolled) pulseScale else 1f
                        scaleY = if (!isScrolled) pulseScale else 1f
                    }
            )
        }
    }
}

@Composable
private fun ProfileAvatar(
    userAvatar: String?,
    userName: String,
    isScrolled: Boolean,
    scrollProgress: Float
) {
    val rotation by rememberInfiniteTransition(label = "avatar_rotation").animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "avatar_rotation"
    )
    
    Box(
        modifier = Modifier
            .padding(end = 8.dp)
            .size(if (isScrolled) 36.dp else 44.dp)
            .drawBehind {
                // Gradient border
                val gradientBrush = Brush.sweepGradient(
                    colors = listOf(
                        Color(0xFF6366F1),
                        Color(0xFF8B5CF6),
                        Color(0xFFEC4899),
                        Color(0xFF6366F1)
                    ),
                    center = Offset(size.width / 2, size.height / 2)
                )
                
                rotate(rotation) {
                    drawCircle(
                        brush = gradientBrush,
                        radius = size.minDimension / 2,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
            }
            .clickable { /* TODO: Open profile */ }
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(3.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary
        ) {
            if (userAvatar != null) {
                // TODO: Load actual avatar image
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userName.firstOrNull()?.uppercase() ?: "U",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = if (isScrolled) 16.sp else 20.sp
                    )
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userName.firstOrNull()?.uppercase() ?: "U",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = if (isScrolled) 16.sp else 20.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ParticleEffect(
    modifier: Modifier = Modifier
) {
    val particles = remember { List(15) { TopBarParticle() } }
    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    
    // Animate all particles outside the Canvas
    val particleProgresses = particles.map { particle ->
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = particle.duration,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart
            ),
            label = "particle_${particle.id}"
        ).value
    }
    
    Canvas(modifier = modifier) {
        particles.forEachIndexed { index, particle ->
            val progress = particleProgresses[index]
            
            val y = size.height * (1f - progress)
            val x = particle.startX * size.width + 
                    sin(progress * PI * 2 * particle.frequency).toFloat() * particle.amplitude
            
            drawCircle(
                color = Color.White.copy(alpha = particle.alpha * (1f - progress)),
                radius = particle.size,
                center = Offset(x, y),
                blendMode = BlendMode.Plus
            )
        }
    }
}

private data class TopBarParticle(
    val id: Int = Random.nextInt(),
    val startX: Float = Random.nextFloat(),
    val size: Float = Random.nextFloat() * 3f + 1f,
    val duration: Int = Random.nextInt(3000, 6000),
    val frequency: Float = Random.nextFloat() * 2f + 0.5f,
    val amplitude: Float = Random.nextFloat() * 50f + 20f,
    val alpha: Float = Random.nextFloat() * 0.3f + 0.1f
)