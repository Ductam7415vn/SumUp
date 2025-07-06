package com.example.sumup.presentation.screens.main

import androidx.compose.animation.*
import androidx.compose.ui.tooling.preview.Preview
import com.example.sumup.presentation.preview.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.AnnotatedString
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.activity.compose.rememberLauncherForActivityResult
import android.util.Log
import com.example.sumup.presentation.screens.main.components.*
import com.example.sumup.presentation.components.ContextualErrorHandler
import com.example.sumup.presentation.components.SharedElementKeys
import com.example.sumup.presentation.components.AnimatedGradientButton
import com.example.sumup.presentation.components.AnimatedButton
import com.example.sumup.presentation.components.MainScreenSkeleton
import com.example.sumup.presentation.components.AutoSaveIndicator
import com.example.sumup.presentation.components.SaveState
import com.example.sumup.presentation.components.HapticIconButton
import com.example.sumup.presentation.components.HapticButton
import com.example.sumup.presentation.components.HapticCard
import com.example.sumup.presentation.components.HapticFilterChip
import com.example.sumup.presentation.components.hapticClickable
import com.example.sumup.presentation.components.ImprovedPdfPreviewDialog
import com.example.sumup.presentation.components.DocxPreviewDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.sumup.domain.model.DocumentType
import com.example.sumup.presentation.components.ApiStatusBanner
import com.example.sumup.presentation.components.ImprovedCharacterLimitIndicator
import com.example.sumup.presentation.components.CharacterLimitWarningDialog
import com.example.sumup.presentation.components.RateLimitWarningBanner
import com.example.sumup.presentation.components.SumUpLogo
import com.example.sumup.presentation.components.ProcessingMethodDialog
import com.example.sumup.utils.haptic.HapticFeedbackType
import com.example.sumup.utils.haptic.rememberHapticFeedback
import com.example.sumup.ui.theme.extendedColorScheme
import com.example.sumup.ui.theme.Dimensions
import com.example.sumup.ui.theme.Spacing
import com.example.sumup.ui.theme.Accessibility
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToOcr: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToProcessing: () -> Unit = {},
    onNavigateToResult: (String) -> Unit = {},
    onOpenDrawer: (() -> Unit)? = null,
    scannedText: String? = null,
    viewModel: MainViewModel = hiltViewModel()
) {
    val hapticManager = rememberHapticFeedback()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    
    // Check for feature discovery on first composition
    LaunchedEffect(Unit) {
        viewModel.checkAndShowFeatureDiscovery()
    }
    
    // Handle scanned text from OCR
    LaunchedEffect(scannedText) {
        if (!scannedText.isNullOrEmpty()) {
            viewModel.setScannedText(scannedText)
        }
    }

    ContextualErrorHandler(
            error = uiState.error,
            onDismiss = viewModel::dismissError,
            onRetry = when (uiState.error) {
                is com.example.sumup.domain.model.AppError.NetworkError,
                is com.example.sumup.domain.model.AppError.ServerError -> {{ viewModel.summarize() }}
                else -> null
            },
            onNavigateToSettings = onNavigateToSettings,
            isFormContext = true
        )
        
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
                // Modern minimal top bar
                ModernTopBar(
                    onMenuClick = if (onOpenDrawer != null) {
                        {
                            hapticManager.performHapticFeedback(HapticFeedbackType.CLICK)
                            onOpenDrawer()
                        }
                    } else null,
                    onHelpClick = {
                        hapticManager.performHapticFeedback(HapticFeedbackType.CLICK)
                        viewModel.showInfoDialog()
                    },
                    todayCount = uiState.todayCount
                )

                // API Status Banner
                ApiStatusBanner(
                    serviceInfo = uiState.serviceInfo,
                    onConfigureApiKey = onNavigateToSettings
                )
                
                // Rate Limit Warning
                uiState.apiUsageStats?.rateLimitStatus?.let { status ->
                    AnimatedVisibility(
                        visible = status.isNearLimit || status.isOverLimit,
                        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
                    ) {
                        RateLimitWarningBanner(
                            rateLimitStatus = status,
                            onNavigateToSettings = onNavigateToSettings,
                            modifier = Modifier.padding(
                                horizontal = 16.dp,
                                vertical = 8.dp
                            )
                        )
                    }
                }

                // Input Type Selector
                var selectedType by remember { mutableStateOf(MainUiState.InputType.TEXT) }
                InputTypeSelectorAnimated(
                    selectedType = selectedType,
                    onTypeSelected = { 
                        selectedType = it
                        viewModel.selectInputType(it)
                    },
                    modifier = Modifier.padding(horizontal = Spacing.screenPadding)
                )

                // Main Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = Spacing.screenPadding),
                    verticalArrangement = Arrangement.spacedBy(Spacing.contentSpacing)
                ) {
                    // Welcome Card for first-time users
                    if (uiState.showWelcomeCard) {
                        WelcomeCard(
                            onDismiss = { viewModel.dismissWelcomeCard() },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    
                    // Quick Stats
                    QuickStatsRow(
                        todayCount = uiState.todayCount,
                        weekCount = uiState.weekCount,
                        totalCount = uiState.totalCount
                    )

                    // Content based on input type
                    AnimatedContent(
                        targetState = selectedType,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(300)) togetherWith
                                fadeOut(animationSpec = tween(300))
                        }
                    ) { inputType ->
                        when (inputType) {
                            MainUiState.InputType.TEXT -> {
                                EnhancedTextInputSection(
                                    text = uiState.inputText,
                                    onTextChange = viewModel::updateText,
                                    onPaste = {
                                        scope.launch {
                                            clipboardManager.getText()?.let { annotatedString ->
                                                viewModel.updateText(annotatedString.text)
                                            }
                                        }
                                    },
                                    onClear = { viewModel.showClearDialog() },
                                    onHelpClick = { viewModel.showInfoDialog() },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            MainUiState.InputType.DOCUMENT -> {
                                ImprovedDocumentUploadSection(
                                    selectedDocumentUri = uiState.selectedDocumentUri,
                                    selectedDocumentName = uiState.selectedDocumentName,
                                    selectedDocument = uiState.selectedDocument,
                                    serviceInfo = uiState.serviceInfo,
                                    onDocumentSelected = { uri -> 
                                        viewModel.selectDocument(android.net.Uri.parse(uri))
                                    },
                                    onClear = viewModel::clearDocument,
                                    onShowPreview = {
                                        when (uiState.selectedDocument?.type) {
                                            DocumentType.PDF -> viewModel.showPdfPreview()
                                            DocumentType.DOCX -> viewModel.showDocxPreview()
                                            else -> {} // No preview for other types
                                        }
                                    },
                                    onNavigateToSettings = onNavigateToSettings,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            else -> {} // OCR type removed as per HTML design
                        }
                    }

                    // Summary Length Selector removed - all levels generated in one API call
                    // Users can switch between views in the result screen
                    
                    Spacer(modifier = Modifier.height(Dimensions.spacingLg))
                }

                // Action Section at bottom
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.screenPadding, vertical = Spacing.screenPadding),
                    horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingMd),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AnimatedSummarizeButton(
                        onClick = viewModel::summarize,
                        enabled = viewModel.canSummarize,
                        isLoading = uiState.isLoading,
                        modifier = Modifier.weight(1f),
                        textLength = if (uiState.inputType == MainUiState.InputType.TEXT) {
                            uiState.inputText.length
                        } else {
                            0 // For documents, we don't show estimates on button
                        }
                    )
                    
                    HapticCard(
                        onClick = onNavigateToOcr,
                        modifier = Modifier
                            .size(Dimensions.fabSize)
                            .testTag(SharedElementKeys.MAIN_SCAN_FAB),
                        shape = RoundedCornerShape(Dimensions.radiusXl),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Transparent
                        ),
                        border = BorderStroke(
                            width = 2.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                                    MaterialTheme.colorScheme.error
                                )
                            )
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = Dimensions.elevationMd,
                            pressedElevation = Dimensions.elevationSm
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.error,
                                            MaterialTheme.colorScheme.error.copy(alpha = 0.9f)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.CameraAlt,
                                contentDescription = "Scan text with camera",
                                tint = MaterialTheme.colorScheme.onError,
                                modifier = Modifier.size(Dimensions.iconSizeLg)
                            )
                        }
                    }
                }
            }

            // Show dialogs
            if (uiState.showClearDialog) {
                ModernDialog(
                    icon = Icons.Default.Delete,
                    title = "Clear all text?",
                    message = "This will remove all your current text. This action cannot be undone.",
                    onDismiss = viewModel::dismissClearDialog,
                    onConfirm = {
                        viewModel.clearText()
                        viewModel.dismissClearDialog()
                    }
                )
            }

            if (uiState.showInfoDialog) {
                ModernDialog(
                    icon = Icons.Default.Info,
                    title = "How to Use SumUp",
                    message = "Simply paste or type your text (50-5,000 characters) and let our AI create a concise summary. Perfect for articles, documents, and long-form content.",
                    onDismiss = viewModel::dismissInfoDialog,
                    confirmText = "Got it!",
                    showCancel = false
                )
            }

            val selectedPdfUri = uiState.selectedPdfUri
            if (uiState.showPdfPreview && selectedPdfUri != null) {
                ImprovedPdfPreviewDialog(
                    pdfUri = selectedPdfUri,
                    pdfName = uiState.selectedPdfName ?: "Document.pdf",
                    pageCount = uiState.pdfPageCount,
                    onConfirm = { selectedPagesList ->
                        val pageRange = if (selectedPagesList != null && selectedPagesList.isNotEmpty()) {
                            selectedPagesList.minOrNull()!!..selectedPagesList.maxOrNull()!!
                        } else {
                            null
                        }
                        viewModel.processPdfWithPages(pageRange)
                    },
                    onDismiss = viewModel::hidePdfPreview
                )
            }
            
            // DOCX Preview Dialog
            val selectedDocumentUri = uiState.selectedDocumentUri
            if (uiState.showDocxPreview && selectedDocumentUri != null && uiState.isDocxInput) {
                DocxPreviewDialog(
                    documentUri = selectedDocumentUri,
                    documentName = uiState.selectedDocumentName ?: "Document.docx",
                    onDismiss = viewModel::hideDocxPreview,
                    onExtractText = viewModel::extractDocxTextForPreview
                )
            }

            if (uiState.showLargePdfWarning) {
                com.example.sumup.presentation.components.LargePdfWarningDialog(
                    pageCount = uiState.largePdfPageCount,
                    estimatedTime = uiState.largePdfEstimatedTime,
                    onOptionSelected = { option, range ->
                        val largePdfOption = when (option) {
                            com.example.sumup.presentation.components.PdfProcessingOption.PROCESS_ALL -> 
                                com.example.sumup.domain.model.LargePdfOption.FULL_DOCUMENT
                            com.example.sumup.presentation.components.PdfProcessingOption.PROCESS_FIRST_50_PAGES -> 
                                com.example.sumup.domain.model.LargePdfOption.FIRST_50_PAGES
                            com.example.sumup.presentation.components.PdfProcessingOption.PROCESS_CUSTOM_RANGE -> 
                                com.example.sumup.domain.model.LargePdfOption.CUSTOM_RANGE
                            com.example.sumup.presentation.components.PdfProcessingOption.CANCEL -> 
                                com.example.sumup.domain.model.LargePdfOption.CANCEL
                        }
                        viewModel.onLargePdfOptionSelected(largePdfOption)
                    },
                    onDismiss = {
                        viewModel.onLargePdfOptionSelected(
                            com.example.sumup.domain.model.LargePdfOption.CANCEL
                        )
                    }
                )
            }

            if (uiState.showDraftRecoveryDialog) {
                ModernDialog(
                    icon = Icons.Default.Restore,
                    title = "Recover Draft",
                    message = "We found an unsaved draft. Would you like to recover it?",
                    onDismiss = viewModel::dismissDraftRecovery,
                    onConfirm = viewModel::recoverDraft,
                    confirmText = "Recover",
                    cancelText = "Discard"
                )
            }

            // Processing Method Dialog
            ProcessingMethodDialog(
                isVisible = uiState.showProcessingMethodDialog,
                documentSize = uiState.pendingTextForProcessing.length,
                options = uiState.processingOptions,
                onOptionSelected = viewModel::selectProcessingStrategy,
                onDismiss = viewModel::dismissProcessingMethodDialog
            )

            // Feature Discovery Tooltips
            if (uiState.showFeatureDiscovery && uiState.currentFeatureTip != null) {
                val allTips = listOf("summarize_button", "pdf_upload", "summary_length", "ocr_button")
                val currentIndex = allTips.indexOf(uiState.currentFeatureTip).coerceAtLeast(0)
                
                val tip = when (uiState.currentFeatureTip) {
                    "summarize_button" -> com.example.sumup.presentation.components.AppFeatureTips.summarizeButton
                    "pdf_upload" -> com.example.sumup.presentation.components.AppFeatureTips.pdfUpload
                    "summary_length" -> com.example.sumup.presentation.components.AppFeatureTips.summaryLength
                    "ocr_button" -> com.example.sumup.presentation.components.AppFeatureTips.ocrButton
                    else -> null
                }
                
                tip?.let {
                    ImprovedFeatureDiscoveryTooltip(
                        isVisible = true,
                        title = it.title,
                        description = it.description,
                        currentIndex = currentIndex,
                        totalTips = allTips.size,
                        targetOffsetX = it.targetOffsetX,
                        targetOffsetY = it.targetOffsetY,
                        onDismiss = { viewModel.skipAllTooltips() },
                        onNextTip = { viewModel.dismissFeatureTip() },
                        onSkipAll = { viewModel.skipAllTooltips() }
                    )
                }
            }
            
            // Handle navigation after successful summarization
            LaunchedEffect(uiState.navigateToProcessing) {
                if (uiState.navigateToProcessing) {
                    onNavigateToProcessing()
                    viewModel.onNavigationHandled()
                }
            }
            
            // Navigation to result is handled by ProcessingScreen
            // Removed duplicate navigation handling to prevent race condition
        }
    }

// StatusBar removed - using system status bar

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun ModernTopBar(
    onMenuClick: (() -> Unit)? = null,
    onHelpClick: () -> Unit,
    todayCount: Int = 0,
    scrollState: ScrollState? = null
) {
    val showHelp = remember { mutableStateOf(true) }
    
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        shadowElevation = animateDpAsState(
            targetValue = if ((scrollState?.value ?: 0) > 10) 3.dp else 0.dp,
            label = "elevation"
        ).value
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimensions.topBarHeight)
                .padding(horizontal = Dimensions.paddingXs),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Menu Button
            if (onMenuClick != null) {
                HapticIconButton(
                    onClick = onMenuClick,
                    modifier = Modifier.size(Dimensions.minTouchTarget),
                    hapticType = HapticFeedbackType.CLICK
                ) {
                    Icon(
                        Icons.Default.Menu,
                        contentDescription = "Open Menu",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(Dimensions.iconSizeMd)
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(Dimensions.minTouchTarget))
            }
            
            // Context-based Title with Logo
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SumUpLogo(
                        size = 24.dp
                    )
                    Text(
                        text = "SumUp",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = (-0.3).sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            
            // Contextual Action (Help/Tips)
            AnimatedVisibility(
                visible = showHelp.value,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                HapticIconButton(
                    onClick = onHelpClick,
                    modifier = Modifier.size(Dimensions.minTouchTarget),
                    hapticType = HapticFeedbackType.CLICK
                ) {
                    Box {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = "Tips & Help",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        
                        // Pulse animation for attention
                        if (todayCount == 0) {
                            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                            val scale by infiniteTransition.animateFloat(
                                initialValue = 1f,
                                targetValue = 1.3f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(1000),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "pulse_scale"
                            )
                            
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .scale(scale)
                                    .background(
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                        shape = CircleShape
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InputTypeSelectorAnimated(
    selectedType: MainUiState.InputType,
    onTypeSelected: (MainUiState.InputType) -> Unit,
    modifier: Modifier = Modifier
) {
    val types = listOf(
        MainUiState.InputType.TEXT to Triple(Icons.AutoMirrored.Filled.Article, "Text", 0),
        MainUiState.InputType.DOCUMENT to Triple(Icons.Default.Description, "Document", 1)
    )
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            border = BorderStroke(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    )
                )
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(4.dp)
            ) {
                // Animated slider background
                val sliderOffset by animateFloatAsState(
                    targetValue = if (selectedType == MainUiState.InputType.TEXT) 0f else 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "slider"
                )
                
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.5f)
                        .offset(x = (sliderOffset * 150).dp) // Half of typical selector width
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.tertiary
                                )
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(12.dp),
                            spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                        )
                )
                
                // Type buttons
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    types.forEach { (type, triple) ->
                        val (icon, label, _) = triple
                        val isSelected = selectedType == type
                        
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(10.dp))
                                .hapticClickable { onTypeSelected(type) },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = if (isSelected) 
                                        MaterialTheme.colorScheme.onPrimary 
                                    else 
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = label,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) 
                                        MaterialTheme.colorScheme.onPrimary 
                                    else 
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickStatsRow(
    todayCount: Int,
    weekCount: Int,
    totalCount: Int
) {
    val stats = listOf(
        Triple(todayCount.toString(), "Today", MaterialTheme.colorScheme.primary),
        Triple(weekCount.toString(), "This Week", MaterialTheme.colorScheme.primary),
        Triple(formatCount(totalCount), "Total", MaterialTheme.colorScheme.primary)
    )
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        stats.forEach { (value, label, color) ->
            QuickStatCard(
                value = value,
                label = label,
                color = color,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun QuickStatCard(
    value: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    var isHovered by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isHovered) 1.05f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )
    
    HapticCard(
        onClick = { isHovered = !isHovered },
        modifier = modifier
            .scale(scale),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isHovered) 12.dp else 4.dp
        ),
        border = BorderStroke(
            width = 1.dp,
            color = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label.uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
private fun EnhancedTextInputSection(
    text: String,
    onTextChange: (String) -> Unit,
    onPaste: () -> Unit,
    onClear: () -> Unit,
    onHelpClick: () -> Unit,
    isOcrMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }
    val wordCount = remember(text) {
        if (text.trim().isEmpty()) 0 else text.trim().split("\\s+".toRegex()).size
    }
    var showCharLimitWarning by remember { mutableStateOf(false) }
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = if (isOcrMode) "Scanned Text" else "Your Text",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            // Action buttons với better alignment
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Paste button - outlined style
                HapticButton(
                    onClick = onPaste,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    border = BorderStroke(
                        1.dp, 
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.height(36.dp),
                    hapticType = HapticFeedbackType.CLICK
                ) {
                    Icon(
                        Icons.Default.ContentCopy,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Paste", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
                
                // Help button - more prominent với background
                HapticIconButton(
                    onClick = onHelpClick,
                    modifier = Modifier.size(36.dp),
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    hapticType = HapticFeedbackType.CLICK
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.HelpOutline,
                        contentDescription = "Tips & Help",
                        modifier = Modifier.size(Dimensions.iconSizeMd)
                    )
                }
            }
        }
        
        // Text Input Container - Increased height
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(360.dp), // Increased from 280dp to 360dp
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (isFocused) 8.dp else 2.dp
            ),
            border = if (isFocused) BorderStroke(
                2.dp,
                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            ) else null
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                TextField(
                    value = text,
                    onValueChange = { newText ->
                        when {
                            newText.length <= 30000 -> {
                                onTextChange(newText)
                            }
                            text.length < 30000 && newText.length > 30000 -> {
                                // Show warning when user first exceeds limit
                                showCharLimitWarning = true
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .onFocusChanged { focusState ->
                            isFocused = focusState.isFocused
                        },
                    placeholder = {
                        Text(
                            text = if (isOcrMode) 
                                "Text captured from camera will appear here..." 
                            else 
                                "Start typing or paste your text here...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 15.sp,
                        lineHeight = 24.sp
                    )
                )
                
                // Footer
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Improved character limit indicator
                        ImprovedCharacterLimitIndicator(
                            currentLength = text.length,
                            maxLength = 30000,
                            text = text,
                            modifier = Modifier.weight(1f)
                        )
                        
                        HapticButton(
                            onClick = onClear,
                            enabled = text.isNotEmpty(),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            colors = ButtonDefaults.textButtonColors(),
                            elevation = null,
                            hapticType = HapticFeedbackType.CLICK
                        ) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Clear", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
    
    // Character limit warning dialog
    CharacterLimitWarningDialog(
        show = showCharLimitWarning,
        currentLength = text.length,
        maxLength = 30000,
        onDismiss = { showCharLimitWarning = false },
        onTruncate = {
            onTextChange(text.take(30000))
            showCharLimitWarning = false
        }
    )
}

@Composable
private fun ModernPdfUploadSection(
    selectedPdfName: String?,
    onPdfSelected: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    val launcher = rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        Log.d("PDF_DEBUG", "PDF picker result: uri=$uri")
        uri?.let {
            val fileName = it.lastPathSegment ?: "document.pdf"
            Log.d("PDF_DEBUG", "PDF selected: fileName=$fileName, uri=$it")
            onPdfSelected(it.toString()) // Pass full URI, not just filename
        } ?: run {
            Log.d("PDF_DEBUG", "PDF picker cancelled or returned null")
        }
    }
    
    HapticCard(
        onClick = { 
            Log.d("PDF_DEBUG", "PDF card clicked, launching picker...")
            launcher.launch("application/pdf") 
        },
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 280.dp, max = 320.dp), // Responsive height
        shape = RoundedCornerShape(Dimensions.radiusXl),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            Dimensions.spacingXxs,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.surface
                            )
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(24.dp),
                        spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Upload,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = if (selectedPdfName != null) selectedPdfName else "Drop your PDF here",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "or click to browse\nMax file size: 10MB",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            HapticButton(
                onClick = { 
                    Log.d("PDF_DEBUG", "Choose File button clicked")
                    launcher.launch("application/pdf") 
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp,
                    hoveredElevation = 8.dp
                ),
                contentPadding = PaddingValues(horizontal = 32.dp, vertical = 14.dp),
                hapticType = HapticFeedbackType.CLICK
            ) {
                Text(
                    text = "Choose File",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun AnimatedSummarizeButton(
    onClick: () -> Unit,
    enabled: Boolean,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    textLength: Int = 0
) {
    // Calculate estimated requests for text processing
    val estimatedRequests = when {
        textLength < 30_000 -> 1
        textLength < 100_000 -> 2 to 3  // Range
        else -> 4 to 6  // Range for very large texts
    }
    
    val buttonText = if (textLength >= 30_000 && estimatedRequests is Pair<*, *>) {
        "Generate Summary (${estimatedRequests.first}-${estimatedRequests.second} requests)"
    } else if (textLength >= 30_000) {
        "Generate Summary ($estimatedRequests request)"
    } else {
        "Generate Summary"
    }
    
    AnimatedGradientButton(
        onClick = onClick,
        text = buttonText,
        modifier = modifier,
        enabled = enabled,
        icon = Icons.Default.AutoAwesome,
        isLoading = isLoading,
        elevation = 12.dp
    )
}

// ModernBottomNavigation removed - handled by AdaptiveNavigation

@Composable
private fun ModernDialog(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit = {},
    confirmText: String = "Confirm",
    cancelText: String = "Cancel",
    showCancel: Boolean = true
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.clip(RoundedCornerShape(20.dp)),
        containerColor = MaterialTheme.colorScheme.surface,
        icon = {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        title = {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(
                text = message,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        },
        confirmButton = {
            HapticButton(
                onClick = {
                    onConfirm()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                hapticType = HapticFeedbackType.SUCCESS
            ) {
                Text(
                    text = confirmText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        dismissButton = if (showCancel) {
            {
                HapticButton(
                    onClick = onDismiss,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                    elevation = null,
                    hapticType = HapticFeedbackType.CLICK
                ) {
                    Text(
                        text = cancelText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        } else null
    )
}

private fun formatCount(count: Int): String {
    return when {
        count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0)
        count >= 1_000 -> String.format("%.1fk", count / 1_000.0)
        else -> count.toString()
    }
}

@Composable
private fun SummaryLengthSelector(
    selectedLength: MainUiState.SummaryLength,
    onLengthSelected: (MainUiState.SummaryLength) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.TextFields,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Summary Length",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MainUiState.SummaryLength.values().forEach { length ->
                    val isSelected = selectedLength == length
                    
                    HapticFilterChip(
                        selected = isSelected,
                        onClick = { onLengthSelected(length) },
                        label = {
                            Text(
                                text = length.displayName,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            // Info text
            Text(
                text = when (selectedLength) {
                    MainUiState.SummaryLength.BRIEF -> "Quick overview with key points"
                    MainUiState.SummaryLength.STANDARD -> "Balanced summary with good detail"
                    MainUiState.SummaryLength.DETAILED -> "Comprehensive analysis with context"
                },
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// Preview Composables
@ThemePreview
@Composable
fun MainScreenPreview() {
    PreviewWrapper {
        MainScreen(
            onNavigateToHistory = {},
            onNavigateToSettings = {},
            onNavigateToOcr = {},
            onNavigateToProcessing = {}
        )
    }
}

@Preview(name = "Main Screen - With Text", showBackground = true)
@Composable
fun MainScreenWithTextPreview() {
    PreviewWrapper {
        MainScreen(
            onNavigateToHistory = {},
            onNavigateToSettings = {},
            onNavigateToOcr = {},
            onNavigateToProcessing = {}
        )
    }
}

@Preview(name = "Main Screen - PDF Mode", showBackground = true)
@Composable
fun MainScreenPdfModePreview() {
    PreviewWrapper {
        MainScreen(
            onNavigateToHistory = {},
            onNavigateToSettings = {},
            onNavigateToOcr = {},
            onNavigateToProcessing = {}
        )
    }
}

@AllDevicePreview
@Composable
fun MainScreenDevicePreview() {
    PreviewWrapper {
        MainScreen(
            onNavigateToHistory = {},
            onNavigateToSettings = {},
            onNavigateToOcr = {},
            onNavigateToProcessing = {}
        )
    }
}