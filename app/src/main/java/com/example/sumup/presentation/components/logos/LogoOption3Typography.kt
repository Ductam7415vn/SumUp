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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Option 3: Smart Typography
 * Concept: Letter "S" with clever negative space showing compression
 * Modern, bold, and memorable
 */
@Composable
fun LogoOption3Typography(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    primaryColor: Color = Color(0xFF5B3FFF)
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.25f))
            .background(primaryColor),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.size(size * 0.8f)
        ) {
            drawSmartS(
                size = this.size.width,
                backgroundColor = primaryColor
            )
        }
    }
}

private fun DrawScope.drawSmartS(
    size: Float,
    backgroundColor: Color
) {
    val strokeWidth = size * 0.18f
    
    // Create main S path
    val sPath = Path().apply {
        val startY = size * 0.25f
        val endY = size * 0.75f
        val midY = size * 0.5f
        
        moveTo(size * 0.7f, startY)
        cubicTo(
            size * 0.7f, size * 0.1f,
            size * 0.3f, size * 0.1f,
            size * 0.3f, startY
        )
        cubicTo(
            size * 0.3f, size * 0.4f,
            size * 0.3f, size * 0.4f,
            size * 0.5f, midY
        )
        cubicTo(
            size * 0.7f, size * 0.6f,
            size * 0.7f, size * 0.6f,
            size * 0.7f, endY
        )
        cubicTo(
            size * 0.7f, size * 0.9f,
            size * 0.3f, size * 0.9f,
            size * 0.3f, endY
        )
    }
    
    // Draw main S shape
    drawPath(
        path = sPath,
        color = Color.White,
        style = Stroke(
            width = strokeWidth,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )
    
    // Create compression effect with horizontal lines
    val lineSpacing = size * 0.08f
    val lineLength = size * 0.25f
    
    // Top compression lines
    for (i in 0..2) {
        val y = size * 0.3f + i * lineSpacing
        val alpha = 1f - (i * 0.3f)
        drawLine(
            color = Color.White.copy(alpha = alpha * 0.4f),
            start = Offset(size * 0.75f, y),
            end = Offset(size * 0.75f + lineLength * (1f - i * 0.2f), y),
            strokeWidth = strokeWidth * 0.15f,
            cap = StrokeCap.Round
        )
    }
    
    // Bottom compression lines
    for (i in 0..2) {
        val y = size * 0.65f + i * lineSpacing
        val alpha = 1f - (i * 0.3f)
        drawLine(
            color = Color.White.copy(alpha = alpha * 0.4f),
            start = Offset(size * 0.05f, y),
            end = Offset(size * 0.05f + lineLength * (1f - i * 0.2f), y),
            strokeWidth = strokeWidth * 0.15f,
            cap = StrokeCap.Round
        )
    }
    
    // Add subtle gradient overlay for depth
    val gradientPath = Path().apply {
        addRect(Rect(0f, 0f, size, size))
    }
    
    clipPath(gradientPath) {
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.Transparent,
                    Color.Black.copy(alpha = 0.1f)
                ),
                startY = size * 0.6f,
                endY = size
            )
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun LogoOption3Preview() {
    Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
        LogoOption3Typography(size = 120.dp)
    }
}