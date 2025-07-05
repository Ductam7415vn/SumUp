package com.example.sumup.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sumup.presentation.components.logos.LogoOption1Geometric
import com.example.sumup.presentation.components.logos.LogoOption2Abstract
import com.example.sumup.presentation.components.logos.LogoOption3Typography

@Composable
fun LogoSelectionScreen() {
    var selectedOption by remember { mutableStateOf(1) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F7FF))
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Choose Your Logo",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "Select the design that best represents SumUp",
            fontSize = 16.sp,
            color = Color(0xFF666666),
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        // Option 1: Geometric
        LogoOptionCard(
            title = "Option 1: Minimalist Geometric",
            description = "Clean, modern design with overlapping shapes forming an abstract 'S'. Represents document compression through simple geometry.",
            isSelected = selectedOption == 1,
            onClick = { selectedOption = 1 }
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LogoOption1Geometric(size = 80.dp)
                LogoOption1Geometric(size = 48.dp)
                LogoOption1Geometric(size = 32.dp)
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Option 2: Abstract Flow
        LogoOptionCard(
            title = "Option 2: Modern Abstract",
            description = "Dynamic flow design with three connected dots representing input → AI processing → output. Shows the summarization journey.",
            isSelected = selectedOption == 2,
            onClick = { selectedOption = 2 }
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LogoOption2Abstract(size = 80.dp)
                LogoOption2Abstract(size = 48.dp)
                LogoOption2Abstract(size = 32.dp)
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Option 3: Typography
        LogoOptionCard(
            title = "Option 3: Smart Typography",
            description = "Bold 'S' with compression lines showing text being condensed. Strong brand identity with clever use of negative space.",
            isSelected = selectedOption == 3,
            onClick = { selectedOption = 3 }
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LogoOption3Typography(size = 80.dp)
                LogoOption3Typography(size = 48.dp)
                LogoOption3Typography(size = 32.dp)
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Selection info
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF5B3FFF).copy(alpha = 0.1f)
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Selected: Option $selectedOption",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF5B3FFF)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = when (selectedOption) {
                        1 -> "Geometric design offers great scalability and modern appeal."
                        2 -> "Abstract flow clearly communicates the AI processing concept."
                        3 -> "Typography option provides strong brand recognition."
                        else -> ""
                    },
                    fontSize = 14.sp,
                    color = Color(0xFF666666)
                )
            }
        }
    }
}

@Composable
private fun LogoOptionCard(
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) Color(0xFF5B3FFF) else Color(0xFFE0E0E0),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = description,
                fontSize = 14.sp,
                color = Color(0xFF666666),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color(0xFFF5F5F5),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                content()
            }
            
            if (isSelected) {
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .background(
                            Color(0xFF5B3FFF),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "SELECTED",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LogoSelectionScreenPreview() {
    LogoSelectionScreen()
}