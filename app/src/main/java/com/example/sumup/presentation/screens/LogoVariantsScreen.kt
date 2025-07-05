package com.example.sumup.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sumup.R
import com.example.sumup.presentation.components.logos.LogoOption1Geometric

@Composable
fun LogoVariantsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F7FF))
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Logo Variations",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "Geometric Style Variations",
            fontSize = 16.sp,
            color = Color(0xFF666666),
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        // Main Logo
        LogoCard(
            title = "Main Logo",
            description = "Primary design with purple gradient"
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_sumup_logo),
                    contentDescription = "Main logo",
                    modifier = Modifier.size(80.dp)
                )
                LogoOption1Geometric(size = 80.dp)
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Variant 2
        LogoCard(
            title = "Variant 2: Gradient Background",
            description = "Inverted colors with gradient background"
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_option1_variant2),
                    contentDescription = "Variant 2",
                    modifier = Modifier.size(80.dp)
                )
                Image(
                    painter = painterResource(id = R.drawable.logo_option1_variant2),
                    contentDescription = "Variant 2 small",
                    modifier = Modifier.size(48.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Variant 3
        LogoCard(
            title = "Variant 3: Outlined Style",
            description = "Mix of filled and outlined elements"
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_option1_variant3),
                    contentDescription = "Variant 3",
                    modifier = Modifier.size(80.dp)
                )
                Image(
                    painter = painterResource(id = R.drawable.logo_option1_variant3),
                    contentDescription = "Variant 3 small",
                    modifier = Modifier.size(48.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Variant 4
        LogoCard(
            title = "Variant 4: Circle Style",
            description = "Circular background with minimal elements"
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_option1_variant4),
                    contentDescription = "Variant 4",
                    modifier = Modifier.size(80.dp)
                )
                Image(
                    painter = painterResource(id = R.drawable.logo_option1_variant4),
                    contentDescription = "Variant 4 small",
                    modifier = Modifier.size(48.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Usage examples
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
                    text = "Usage Guidelines",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF5B3FFF)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "• Main logo: App icon, splash screen, main branding\n" +
                          "• Variant 2: Dark backgrounds, premium features\n" +
                          "• Variant 3: Light backgrounds, documentation\n" +
                          "• Variant 4: Social media, circular avatars",
                    fontSize = 14.sp,
                    color = Color(0xFF666666),
                    lineHeight = 22.sp
                )
            }
        }
    }
}

@Composable
private fun LogoCard(
    title: String,
    description: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A1A),
                modifier = Modifier.padding(bottom = 4.dp)
            )
            
            Text(
                text = description,
                fontSize = 14.sp,
                color = Color(0xFF666666),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 20.dp)
            )
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color(0xFFF5F5F5),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                content()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LogoVariantsScreenPreview() {
    LogoVariantsScreen()
}