package com.example.sumup.presentation.components.logos

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Option 1: Minimalist Geometric
 * Concept: Two overlapping rectangles forming an abstract "S" shape
 * Represents document compression/summarization
 */
@Composable
fun LogoOption1Geometric(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    primaryColor: Color = Color(0xFF5B3FFF),
    secondaryColor: Color = Color(0xFF7C5FFF)
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.22f))
            .background(Color(0xFFF5F3FF)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.size(size * 0.7f)
        ) {
            drawGeometricS(
                size = this.size,
                primaryColor = primaryColor,
                secondaryColor = secondaryColor
            )
        }
    }
}

private fun DrawScope.drawGeometricS(
    size: Size,
    primaryColor: Color,
    secondaryColor: Color
) {
    val rectWidth = size.width * 0.35f
    val rectHeight = size.height * 0.25f
    val cornerRadius = size.width * 0.08f
    
    // Top rectangle (primary color)
    drawRoundRect(
        color = primaryColor,
        topLeft = Offset(size.width * 0.15f, size.height * 0.2f),
        size = Size(rectWidth * 1.5f, rectHeight),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius)
    )
    
    // Middle connecting piece
    drawRoundRect(
        color = primaryColor.copy(alpha = 0.7f),
        topLeft = Offset(size.width * 0.35f, size.height * 0.4f),
        size = Size(rectWidth * 0.8f, rectHeight * 0.8f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius)
    )
    
    // Bottom rectangle (secondary color)
    drawRoundRect(
        color = secondaryColor,
        topLeft = Offset(size.width * 0.35f, size.height * 0.55f),
        size = Size(rectWidth * 1.5f, rectHeight),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius)
    )
    
    // Small accent dot
    drawCircle(
        color = primaryColor,
        radius = size.width * 0.04f,
        center = Offset(size.width * 0.8f, size.height * 0.8f)
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun LogoOption1Preview() {
    Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
        LogoOption1Geometric(size = 120.dp)
    }
}