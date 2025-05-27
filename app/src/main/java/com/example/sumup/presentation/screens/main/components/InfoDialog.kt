package com.example.sumup.presentation.screens.main.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun InfoDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.CenterHorizontally),
                    tint = Color(0xFF2196F3)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Character Limit",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "• Maximum 5,000 characters allowed",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                
                Text(
                    text = "• Minimum 50 characters required",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )
                
                Text(
                    text = "• Counter turns orange at 90% (4,500 chars)",
                    fontSize = 14.sp,
                    color = Color(0xFFFFA500),
                    modifier = Modifier.padding(top = 8.dp)
                )
                
                Text(
                    text = "• Counter turns red at 95% (4,750 chars)",
                    fontSize = 14.sp,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        "Got it",
                        color = Color(0xFF2196F3),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}