package com.example.sumup.presentation.screens.settings.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun AnimatedSettingsList(
    content: LazyListScope.() -> Unit
) {
    val listState = rememberLazyListState()
    var isAnimating by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        isAnimating = true
    }
    
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        content()
    }
}

@Composable
fun AnimatedSettingsItem(
    content: @Composable () -> Unit,
    index: Int,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(index * 50L) // Staggered animation
        isVisible = true
    }
    
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(400)) +
                slideInVertically(
                    initialOffsetY = { 50 },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ),
        modifier = modifier
    ) {
        val scale by animateFloatAsState(
            targetValue = if (isVisible) 1f else 0.8f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy
            ),
            label = "item_scale"
        )
        
        Box(
            modifier = Modifier
                .scale(scale)
                .graphicsLayer {
                    alpha = if (isVisible) 1f else 0f
                }
        ) {
            content()
        }
    }
}

@Composable
fun SettingsListTransition(
    targetState: Boolean,
    content: @Composable (Boolean) -> Unit
) {
    AnimatedContent(
        targetState = targetState,
        transitionSpec = {
            if (targetState) {
                // Entering transition
                fadeIn(animationSpec = tween(300)) +
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) with fadeOut(animationSpec = tween(200)) +
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> -fullWidth },
                    animationSpec = tween(200)
                )
            } else {
                // Exiting transition
                fadeIn(animationSpec = tween(300)) +
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> -fullWidth },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) with fadeOut(animationSpec = tween(200)) +
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(200)
                )
            }
        },
        label = "settings_transition"
    ) { state ->
        content(state)
    }
}

@Composable
fun ExpandableSettingsSection(
    title: String,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Header
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggle() },
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isExpanded) 0.dp else 16.dp,
                bottomEnd = if (isExpanded) 0.dp else 16.dp
            ),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                
                ExpandIcon(isExpanded = isExpanded)
            }
        }
        
        // Content
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )) + fadeIn(),
            exit = shrinkVertically(animationSpec = tween(200)) + fadeOut()
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 0.dp,
                    bottomStart = 16.dp,
                    bottomEnd = 16.dp
                ),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    )
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
private fun ExpandIcon(isExpanded: Boolean) {
    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy
        ),
        label = "expand_rotation"
    )
    
    Icon(
        Icons.Default.ExpandMore,
        contentDescription = if (isExpanded) "Collapse" else "Expand",
        modifier = Modifier
            .size(24.dp)
            .graphicsLayer {
                rotationZ = rotation
            },
        tint = MaterialTheme.colorScheme.onSurfaceVariant
    )
}