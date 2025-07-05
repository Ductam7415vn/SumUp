package com.example.sumup.presentation.components.logos

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Option 2: Modern Abstract
 * Concept: Three dots connected in a triangular flow (input → process → output)
 * Represents AI processing and summarization flow
 */
@Composable
fun LogoOption2Abstract(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF6B4EFF),
                        Color(0xFF4E9FFF)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.size(size * 0.65f)
        ) {
            drawAbstractFlow(this.size.width)
        }
    }
}

private fun DrawScope.drawAbstractFlow(size: Float) {
    val dotRadius = size * 0.08f
    val strokeWidth = size * 0.04f
    
    // Define positions for three dots in triangular arrangement
    val topDot = Offset(size * 0.5f, size * 0.2f)
    val leftDot = Offset(size * 0.2f, size * 0.7f)
    val rightDot = Offset(size * 0.8f, size * 0.7f)
    
    // Draw connecting lines with gradient effect
    val linePath = Path().apply {
        moveTo(topDot.x, topDot.y)
        quadraticBezierTo(
            size * 0.3f, size * 0.5f,
            leftDot.x, leftDot.y
        )
        moveTo(topDot.x, topDot.y)
        quadraticBezierTo(
            size * 0.7f, size * 0.5f,
            rightDot.x, rightDot.y
        )
        moveTo(leftDot.x, leftDot.y)
        quadraticBezierTo(
            size * 0.5f, size * 0.8f,
            rightDot.x, rightDot.y
        )
    }
    
    drawPath(
        path = linePath,
        color = Color.White.copy(alpha = 0.3f),
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
    )
    
    // Draw dots with different sizes to show progression
    // Input dot (larger)
    drawCircle(
        color = Color.White,
        radius = dotRadius * 1.3f,
        center = topDot
    )
    
    // Process dot (medium)
    drawCircle(
        color = Color.White.copy(alpha = 0.9f),
        radius = dotRadius,
        center = leftDot
    )
    
    // Output dot (smaller, representing compressed result)
    drawCircle(
        color = Color.White.copy(alpha = 0.8f),
        radius = dotRadius * 0.7f,
        center = rightDot
    )
    
    // Add center accent
    drawCircle(
        color = Color.White.copy(alpha = 0.2f),
        radius = size * 0.15f,
        center = Offset(size * 0.5f, size * 0.5f)
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun LogoOption2Preview() {
    Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
        LogoOption2Abstract(size = 120.dp)
    }
}