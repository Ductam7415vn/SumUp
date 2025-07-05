package com.example.sumup.presentation.screens.main.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Help
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.semantics.*
import com.example.sumup.presentation.components.AutoSaveTextField
import com.example.sumup.presentation.components.InlineErrorDisplay
import com.example.sumup.presentation.components.FieldErrorIndicator
import com.example.sumup.presentation.components.AccessibilityUtils
import com.example.sumup.domain.model.AppError
import com.example.sumup.utils.drafts.DraftInputType
import com.example.sumup.utils.drafts.DraftManager
import androidx.compose.ui.platform.LocalContext

@Composable
fun TextInputSection(
    text: String,
    onTextChange: (String) -> Unit,
    isError: Boolean = false,
    inlineError: AppError? = null,
    modifier: Modifier = Modifier,
    onHelpClick: () -> Unit = {},
    draftManager: DraftManager? = null
) {
    var showToast by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val isScreenReaderOn = remember { AccessibilityUtils.isScreenReaderEnabled(context) }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        AutoSaveTextField(
            value = text,
            onValueChange = { newText ->
                if (newText.length <= 30000) {
                    onTextChange(newText)
                } else {
                    onTextChange(newText.take(30000))
                    showToast = true
                    if (isScreenReaderOn) {
                        AccessibilityUtils.announceForAccessibility(
                            context, 
                            "Character limit reached. Maximum 30,000 characters allowed."
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp)
                .weight(1f, fill = false)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("input_field")
                .semantics {
                    contentDescription = "Text input field. ${text.length} of 30,000 characters"
                    if (text.length > 27000) {
                        stateDescription = "${30000 - text.length} characters remaining"
                    }
                    if (isError || inlineError != null) {
                        error("Input error: ${inlineError?.message ?: "Invalid input"}")
                    }
                },
            placeholder = {
                Text(
                    text = "Paste or type your text here...\n\nTip: Supports up to 30,000 characters (~6,000 words)",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            },
            supportingText = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CharacterCounter(
                        current = text.length,
                        max = 30000,
                        modifier = Modifier.testTag("char_counter")
                    )
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FieldErrorIndicator(
                            hasError = inlineError != null
                        )
                        
                        IconButton(
                            onClick = onHelpClick,
                            modifier = Modifier
                                .size(20.dp)
                                .semantics {
                                    contentDescription = "Get help with text input"
                                    role = Role.Button
                                }
                        ) {
                            Icon(
                                Icons.Outlined.Help,
                                contentDescription = null, // contentDescription is set on IconButton
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            },
            trailingIcon = null, // Auto-save status will be shown automatically
            isError = isError,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                autoCorrect = true,
                keyboardType = KeyboardType.Text
            ),
            maxLines = Int.MAX_VALUE,
            autoSaveEnabled = true,
            draftInputType = DraftInputType.TEXT,
            showAutoSaveStatus = true,
            draftManager = draftManager
        )
        
        // Show inline error for field-specific errors
        InlineErrorDisplay(
            error = inlineError,
            modifier = Modifier.fillMaxWidth()
        )
        
        // Spacer to push content up when keyboard appears
        Spacer(modifier = Modifier.weight(1f))
    }
    
    if (showToast) {
        LaunchedEffect(showToast) {
            // In a real app, show a Toast or Snackbar
            // For now, just reset the flag
            kotlinx.coroutines.delay(2000)
            showToast = false
        }
    }
}