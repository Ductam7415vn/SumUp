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

@Composable
fun TextInputSection(
    text: String,
    onTextChange: (String) -> Unit,
    isError: Boolean = false,
    modifier: Modifier = Modifier,
    onHelpClick: () -> Unit = {}
) {
    var showToast by remember { mutableStateOf(false) }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { newText ->
                if (newText.length <= 5000) {
                    onTextChange(newText)
                } else {
                    onTextChange(newText.take(5000))
                    showToast = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp)
                .weight(1f, fill = false)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("input_field"),
            placeholder = {
                Text(
                    text = "Paste or type your text here...\n\nTip: Works best with 100-2000 words",
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
                        max = 5000,
                        modifier = Modifier.testTag("char_counter")
                    )
                    
                    IconButton(
                        onClick = onHelpClick,
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Help,
                            contentDescription = "Help",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            isError = isError,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                autoCorrect = true,
                keyboardType = KeyboardType.Text
            ),
            maxLines = Int.MAX_VALUE
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