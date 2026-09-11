package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DgAmber
import com.example.ui.theme.DgNavyDark
import com.example.ui.theme.DgNavyPrimary
import com.example.ui.theme.DgSaffron

/**
 * Official DG with Anup Brand Logo Composable
 * High-fidelity vector shield badge with vibrant saffron and navy styling.
 */
@Composable
fun DgBrandLogo(
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    showText: Boolean = true,
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Shield Badge Icon
        Box(
            modifier = Modifier
                .size(size)
                .clip(RoundedCornerShape(size * 0.28f))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(DgNavyDark, DgNavyPrimary)
                    )
                )
                .border(
                    width = (size.value * 0.04f).dp.coerceAtLeast(1.dp),
                    brush = Brush.linearGradient(listOf(DgAmber, DgSaffron)),
                    shape = RoundedCornerShape(size * 0.28f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "D",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = (size.value * 0.44f).sp,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "G",
                    color = DgAmber,
                    fontWeight = FontWeight.Black,
                    fontSize = (size.value * 0.44f).sp,
                    letterSpacing = (-0.5).sp
                )
            }
        }

        if (showText) {
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "DG",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = DgNavyPrimary,
                        fontSize = 17.sp,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "with Anup",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DgSaffron,
                        fontSize = 17.sp
                    )
                }
                Text(
                    text = "GOVT UTILITY & CAREERS",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF64748B),
                    fontSize = 9.sp,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}
