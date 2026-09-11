package com.example.jobphoto.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.example.jobphoto.model.JobPhotoValidationReport
import com.example.jobphoto.model.ValidationCheckStatus
import com.example.jobphoto.model.ValidationRuleResult
import com.example.ui.theme.DgEmerald
import com.example.ui.theme.DgNavy

@Composable
fun JobValidationChecklistCard(
    report: JobPhotoValidationReport?
) {
    if (report == null) return

    val isCompliant = report.isFullyCompliant

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_validation_checklist")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFFF0FDF4), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FactCheck,
                            contentDescription = null,
                            tint = DgEmerald,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Portal Compliance Checklist",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = DgNavy
                        )
                        Text(
                            text = "Automated verification against official guidelines",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                // Overall Compliance Badge
                Box(
                    modifier = Modifier
                        .background(
                            if (isCompliant) Color(0xFFDCFCE7) else Color(0xFFFEF2F2),
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isCompliant) "100% READY" else "NEEDS FIX",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCompliant) DgEmerald else Color(0xFFEF4444)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Checklist Items
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                ChecklistItemRow(report.photoDimensions)
                ChecklistItemRow(report.photoAspectRatio)
                ChecklistItemRow(report.photoFileSize)
                ChecklistItemRow(report.photoFormat)
                ChecklistItemRow(report.photoBackground)
                ChecklistItemRow(report.photoDpi)
                ChecklistItemRow(report.signatureDimensions)
                ChecklistItemRow(report.signatureSize)
                ChecklistItemRow(report.signatureFormat)
            }
        }
    }
}

@Composable
private fun ChecklistItemRow(item: ValidationRuleResult) {
    val (bgColor, iconColor, icon) = when (item.status) {
        ValidationCheckStatus.PASS -> Triple(Color(0xFFF0FDF4), DgEmerald, Icons.Default.Check)
        ValidationCheckStatus.WARNING -> Triple(Color(0xFFFFFBEB), Color(0xFFD97706), Icons.Default.Warning)
        ValidationCheckStatus.FAIL -> Triple(Color(0xFFFEF2F2), Color(0xFFEF4444), Icons.Default.Close)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(iconColor.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(12.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = DgNavy
            )
            Text(
                text = item.detail,
                fontSize = 11.sp,
                color = Color(0xFF475569)
            )
        }

        // Status Tag
        Text(
            text = item.status.name,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = iconColor,
            modifier = Modifier
                .background(iconColor.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}
