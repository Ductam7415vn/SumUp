package com.example.sumup.presentation.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun InfoDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Character Limit Information",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Text(
                text = """The character counter shows the current number of characters in your text.
                
• Minimum: 100 characters (for meaningful summaries)
• Maximum: 5,000 characters
                
Color indicators:
• Gray: Normal (100-4,500 characters)
• Orange: Warning (4,501-4,999 characters)
• Red: Error (below 100 or at 5,000 characters)""",
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    "Got it",
                    color = Color(0xFF2196F3),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    )
}