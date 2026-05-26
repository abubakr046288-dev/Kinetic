package com.example.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun KineticLogoSymbol(
    modifier: Modifier = Modifier,
    sizeDp: Dp = 80.dp
) {
    Canvas(modifier = modifier.size(sizeDp)) {
        val w = size.width
        val h = size.height
        val minDim = minOf(w, h)
        
        // Exact proportion formulas
        val strokeWidth = minDim * 0.11f
        val radius = (minDim - strokeWidth) / 2f
        val center = Offset(w / 2f, h / 2f)

        // 1. Top-Left Arc (AccentLime #C7FF3D), sweeping roughly 235 degrees
        // Start angle -45 degrees (around 1:30 position) going counter-clockwise (so negative sweep)
        drawArc(
            color = AccentLime,
            startAngle = -45f,
            sweepAngle = -225f,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2)
        )

        // 2. Bottom-Right Arc (AccentNeonBlue #4FD1FF), sweeping roughly 80 degrees
        // Located symmetrically around 4:30 position
        drawArc(
            color = AccentNeonBlue,
            startAngle = 15f,
            sweepAngle = 90f,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2)
        )

        // 3. Central Chevron ("K" motion arrow / locking lock core) pointing left
        val chevronCapWidth = strokeWidth * 1.05f
        val vertexX = center.x - radius * 0.18f
        val vertexY = center.y
        val legTopX = center.x + radius * 0.23f
        val legTopY = center.y - radius * 0.38f
        val legBottomX = center.x + radius * 0.23f
        val legBottomY = center.y + radius * 0.38f

        // Draw top-left heading leg
        drawLine(
            color = TextPrimary,
            start = Offset(vertexX, vertexY),
            end = Offset(legTopX, legTopY),
            strokeWidth = chevronCapWidth,
            cap = StrokeCap.Round
        )

        // Draw bottom-left heading leg
        drawLine(
            color = TextPrimary,
            start = Offset(vertexX, vertexY),
            end = Offset(legBottomX, legBottomY),
            strokeWidth = chevronCapWidth,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun KineticLogo(
    modifier: Modifier = Modifier,
    iconSize: Dp = 54.dp,
    textSize: TextUnit = 28.sp,
    showTagline: Boolean = true
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        KineticLogoSymbol(sizeDp = iconSize)
        Spacer(modifier = Modifier.width(14.dp))
        Column(verticalArrangement = Arrangement.Center) {
            // Typography with highlighted letters
            Text(
                text = buildAnnotatedString {
                    append("k")
                    withStyle(SpanStyle(color = AccentLime)) {
                        append("i")
                    }
                    append("net")
                    withStyle(SpanStyle(color = AccentLime)) {
                        append("i")
                    }
                    append("c")
                },
                fontSize = textSize,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary,
                letterSpacing = 0.5.sp
            )

            if (showTagline) {
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "MOVE TO ",
                        fontSize = (textSize.value * 0.35f).sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextSecondary,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "UNLOCK",
                        fontSize = (textSize.value * 0.35f).sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AccentLime,
                        letterSpacing = 2.sp
                    )
                }
            }
        }
    }
}
