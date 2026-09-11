package com.example.ui.components.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.editor.model.ValidationItem
import com.example.editor.model.ValidationReport
import com.example.ui.theme.DgBorderLight
import com.example.ui.theme.DgEmerald
import com.example.ui.theme.DgNavyDark

@Composable
fun ValidationPanel(
    report: ValidationReport?,
    modifier: Modifier = Modifier
) {
    if (report == null) return

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("validation_panel"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DgBorderLight)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = if (report.isAllValid) DgEmerald else Color(0xFFE11D48),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Live Validation / लाइव सत्यापन",
                        fontWeight = FontWeight.Bold,
                        color = DgNavyDark,
                        fontSize = 15.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (report.isAllValid) Color(0xFFDCFCE7) else Color(0xFFFFE4E6))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (report.isAllValid) "READY FOR PORTAL" else "ATTENTION NEEDED",
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = if (report.isAllValid) Color(0xFF166534) else Color(0xFF9F1239)
                    )
                }
            }

            Text(
                text = "Automated pre-submission check against portal standards",
                color = Color(0xFF64748B),
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            ValidationRowItem(report.dimensions)
            ValidationRowItem(report.aspectRatio)
            ValidationRowItem(report.format)
            ValidationRowItem(report.fileSize)
            ValidationRowItem(report.imageQuality)
            ValidationRowItem(report.background)
        }
    }
}

@Composable
fun ValidationRowItem(item: ValidationItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(if (item.isValid) Color(0xFFDCFCE7) else Color(0xFFFFE4E6)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (item.isValid) Icons.Default.Check else Icons.Default.Close,
                contentDescription = null,
                tint = if (item.isValid) Color(0xFF16A34A) else Color(0xFFE11D48),
                modifier = Modifier.size(14.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = DgNavyDark
            )
            Text(
                text = item.message,
                fontSize = 11.sp,
                color = if (item.isValid) Color(0xFF475569) else Color(0xFFE11D48)
            )
        }

        Text(
            text = if (item.isValid) "PASS" else "FAIL",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (item.isValid) Color(0xFF16A34A) else Color(0xFFE11D48)
        )
    }
}
