package com.example.sumup.presentation.screens.ocr.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.sumup.presentation.components.EmptyStateComponent
import com.example.sumup.presentation.components.EmptyStateType

@Composable
fun PermissionContent(
    onEnableCamera: () -> Unit,
    onNotNow: () -> Unit,
    modifier: Modifier = Modifier
) {
    EmptyStateComponent(
        type = EmptyStateType.CAMERA_PERMISSION_DENIED,
        modifier = modifier,
        title = "📸 Scan Text Instantly", 
        description = "Point your camera at any document to import text in seconds. You can always type or paste text manually.",
        actionText = "Enable Camera",
        onActionClick = onEnableCamera,
        secondaryActionText = "Not Now",
        onSecondaryActionClick = onNotNow
    )
}