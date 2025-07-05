package com.example.sumup.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sumup.R

@Composable
fun SumUpLogo(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    primaryColor: Color = Color(0xFF5B3FFF),
    secondaryColor: Color = Color(0xFF7C5FFF)
) {
    Image(
        painter = painterResource(id = R.drawable.logo_option1_variant2),
        contentDescription = "SumUp Logo",
        modifier = modifier.size(size)
    )
}


@Composable
fun SumUpLogoWithText(
    modifier: Modifier = Modifier,
    logoSize: Dp = 32.dp,
    showText: Boolean = true
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        // Logo
        SumUpLogo(
            size = logoSize
        )
        
        // Text
        if (showText) {
            Spacer(modifier = Modifier.width(logoSize * 0.3f))
            
            Row {
                Text(
                    text = "Sum",
                    fontSize = (logoSize.value * 0.6f).sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF7C3AED)
                )
                Text(
                    text = "Up",
                    fontSize = (logoSize.value * 0.6f).sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4E9FFF)
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun SumUpLogoPreview() {
    SumUpLogo(size = 100.dp)
}

@Preview(showBackground = true)
@Composable
private fun SumUpLogoWithTextPreview() {
    SumUpLogoWithText(
        logoSize = 48.dp,
        showText = true
    )
}