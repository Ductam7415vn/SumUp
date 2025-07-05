package com.example.sumup.presentation.screens.onboarding

import androidx.compose.animation.*
import androidx.compose.ui.tooling.preview.Preview
import com.example.sumup.presentation.preview.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sumup.domain.model.OnboardingPage
import com.example.sumup.domain.model.OnboardingAction
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onOnboardingComplete: () -> Unit,
    onNavigateToSettings: (() -> Unit)? = null,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { uiState.pages.size })
    val hapticFeedback = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    // Sync pager state with viewmodel
    LaunchedEffect(uiState.currentPage) {
        if (pagerState.currentPage != uiState.currentPage) {
            pagerState.animateScrollToPage(uiState.currentPage)
        }
    }
    
    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage != uiState.currentPage) {
            viewModel.jumpToPage(pagerState.currentPage)
        }
    }
    
    // Removed auto-completion logic that was causing onboarding to close automatically
    // Navigation should only happen when user explicitly completes onboarding
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Main content
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pageIndex ->
            OnboardingPageContent(
                page = uiState.pages[pageIndex],
                isActive = pageIndex == uiState.currentPage,
                onActionClick = { action ->
                    when (action) {
                        OnboardingAction.REQUEST_API_KEY -> {
                            // Open Gemini API key website
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://makersuite.google.com/app/apikey"))
                            context.startActivity(intent)
                            // Also navigate to settings after a delay
                            scope.launch {
                                delay(500)
                                onNavigateToSettings?.invoke()
                            }
                        }
                        OnboardingAction.GRANT_PERMISSIONS -> {
                            // Handle permissions if needed
                        }
                        OnboardingAction.SHOW_FEATURES -> {
                            // Show features or continue
                        }
                    }
                }
            )
        }
        
        // Top bar with skip button
        if (uiState.showSkipButton) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .statusBarsPadding(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = { 
                        hapticFeedback.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                        viewModel.skipOnboarding() 
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.White.copy(alpha = 0.8f)
                    )
                ) {
                    Text(
                        text = "Skip",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
        
        // Bottom navigation
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(24.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Page indicators
            PageIndicators(
                totalPages = uiState.pages.size,
                currentPage = uiState.currentPage,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            
            // Navigation buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Previous button
                AnimatedVisibility(
                    visible = uiState.currentPage > 0,
                    enter = fadeIn() + slideInHorizontally(),
                    exit = fadeOut() + slideOutHorizontally()
                ) {
                    OutlinedButton(
                        onClick = {
                            hapticFeedback.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                            viewModel.previousPage()
                        },
                        modifier = Modifier.height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White,
                            containerColor = Color.White.copy(alpha = 0.1f)
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.3f),
                                    Color.White.copy(alpha = 0.1f)
                                )
                            )
                        )
                    ) {
                        Text(
                            text = "Previous",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Next/Complete button
                EnhancedActionButton(
                    isLastPage = uiState.isLastPage,
                    isLoading = uiState.isLoading,
                    onClick = {
                        hapticFeedback.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                        if (uiState.isLastPage) {
                            viewModel.completeOnboarding()
                        } else {
                            viewModel.nextPage()
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun OnboardingPageContent(
    page: OnboardingPage,
    isActive: Boolean,
    modifier: Modifier = Modifier,
    onActionClick: (OnboardingAction) -> Unit = {}
) {
    val backgroundColor = Color(android.graphics.Color.parseColor(page.backgroundColor))
    val textColor = Color(android.graphics.Color.parseColor(page.textColor))
    
    // Animated scale for active page
    val scale by animateFloatAsState(
        targetValue = if (isActive) 1f else 0.8f,
        animationSpec = spring(dampingRatio = 0.8f),
        label = "pageScale"
    )
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        backgroundColor,
                        Color(ColorUtils.blendARGB(backgroundColor.toArgb(), Color.Black.toArgb(), 0.2f))
                    )
                )
            )
            .scale(scale),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Animated icon
            AnimatedIconDisplay(
                icon = page.icon,
                isActive = isActive,
                tint = textColor
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Title with animation
            AnimatedVisibility(
                visible = isActive,
                enter = fadeIn(animationSpec = tween(600, delayMillis = 200)) + 
                       slideInVertically(animationSpec = tween(600, delayMillis = 200)),
                exit = fadeOut(animationSpec = tween(300))
            ) {
                Text(
                    text = page.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Description with animation
            AnimatedVisibility(
                visible = isActive,
                enter = fadeIn(animationSpec = tween(600, delayMillis = 400)) + 
                       slideInVertically(animationSpec = tween(600, delayMillis = 400)),
                exit = fadeOut(animationSpec = tween(300))
            ) {
                Text(
                    text = page.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = textColor.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center,
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.4,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            
            // Action button if page has an action
            if (page.action != null && page.actionButtonText != null && isActive) {
                Spacer(modifier = Modifier.height(32.dp))
                AnimatedVisibility(
                    visible = isActive,
                    enter = fadeIn(animationSpec = tween(600, delayMillis = 600)) + 
                           slideInVertically(animationSpec = tween(600, delayMillis = 600)),
                    exit = fadeOut(animationSpec = tween(300))
                ) {
                    OutlinedButton(
                        onClick = { onActionClick(page.action) },
                        modifier = Modifier.padding(horizontal = 48.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = textColor
                        ),
                        border = BorderStroke(
                            width = 2.dp,
                            color = textColor.copy(alpha = 0.8f)
                        )
                    ) {
                        Text(
                            text = page.actionButtonText,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimatedIconDisplay(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isActive: Boolean,
    tint: Color,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isActive) 1f else 0.7f,
        animationSpec = spring(dampingRatio = 0.6f),
        label = "iconScale"
    )
    
    val rotation by animateFloatAsState(
        targetValue = if (isActive) 0f else -10f,
        animationSpec = spring(dampingRatio = 0.8f),
        label = "iconRotation"
    )
    
    Box(
        modifier = modifier
            .size(120.dp)
            .scale(scale)
            .background(
                color = tint.copy(alpha = 0.2f),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = tint
        )
    }
}

@Composable
private fun PageIndicators(
    totalPages: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalPages) { index ->
            PageIndicator(
                isSelected = index == currentPage,
                isCompleted = index < currentPage
            )
        }
    }
}

@Composable
private fun PageIndicator(
    isSelected: Boolean,
    isCompleted: Boolean,
    modifier: Modifier = Modifier
) {
    val targetWidth by animateFloatAsState(
        targetValue = if (isSelected) 32f else 8f,
        animationSpec = spring(dampingRatio = 0.8f),
        label = "indicatorWidth"
    )
    
    val targetColor by animateColorAsState(
        targetValue = when {
            isCompleted -> Color.White
            isSelected -> Color.White
            else -> Color.White.copy(alpha = 0.3f)
        },
        animationSpec = tween(300),
        label = "indicatorColor"
    )
    
    Box(
        modifier = modifier
            .width(targetWidth.dp)
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(targetColor)
    )
}

@Composable
private fun EnhancedActionButton(
    isLastPage: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonText = if (isLastPage) "Get Started" else "Next"
    val buttonIcon = if (isLastPage) Icons.Default.Check else Icons.AutoMirrored.Filled.ArrowForward
    
    Button(
        onClick = onClick,
        modifier = modifier
            .height(56.dp)
            .widthIn(min = 120.dp),
        enabled = !isLoading,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 8.dp,
            pressedElevation = 4.dp
        )
    ) {
        AnimatedContent(
            targetState = isLoading,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
            },
            label = "buttonContent"
        ) { loading ->
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.Black,
                    strokeWidth = 2.dp
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = buttonText,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(
                        imageVector = buttonIcon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// Preview Composables
@ThemePreview
@Composable
fun OnboardingScreenPreview() {
    PreviewWrapper {
        OnboardingScreen(
            onOnboardingComplete = {}
        )
    }
}

@Preview(name = "Onboarding - Welcome Page", showBackground = true)
@Composable
fun OnboardingWelcomePreview() {
    PreviewWrapper {
        OnboardingScreen(
            onOnboardingComplete = {}
        )
    }
}

@AllDevicePreview
@Composable
fun OnboardingScreenDevicePreview() {
    PreviewWrapper {
        OnboardingScreen(
            onOnboardingComplete = {}
        )
    }
}