package com.koin.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp

@Composable
fun ChangeIndicator(
    isPositive: Boolean,
    modifier: Modifier = Modifier
) {
    val triangleColor = if (isPositive) Color.Green else Color.Red

    Canvas(
        modifier = modifier.size(12.dp) // Adjust size as needed
    ) {
        val trianglePath = Path().apply {
            if (isPositive) {
                // Upward triangle
                moveTo(size.width / 2f, 0f)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
            } else {
                // Downward triangle
                moveTo(0f, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width / 2f, size.height)
            }
            close()
        }
        drawPath(trianglePath, color = triangleColor)
    }
}