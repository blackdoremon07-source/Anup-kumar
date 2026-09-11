package com.example.jobphoto.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jobphoto.model.RecruitmentProfile
import com.example.ui.theme.DgEmerald
import com.example.ui.theme.DgNavy
import com.example.ui.theme.DgSaffron

@Composable
fun RequirementProfileCard(
    profile: RecruitmentProfile,
    onChangeExamClick: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_requirement_profile")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with exam name and Change Exam button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "SELECTED RECRUITMENT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DgSaffron,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = profile.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = DgNavy
                    )
                    Text(
                        text = profile.organization,
                        fontSize = 13.sp,
                        color = Color(0xFF64748B)
                    )
                }

                OutlinedButton(
                    onClick = onChangeExamClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DgNavy),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
                    modifier = Modifier.testTag("btn_change_exam")
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Change", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Official Source & Verification Status Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (profile.officialSource.isVerified) Color(0xFFF0FDF4) else Color(0xFFFFFBEB)
                    )
                    .border(
                        1.dp,
                        if (profile.officialSource.isVerified) Color(0xFFBBF7D0) else Color(0xFFFDE68A),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(12.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (profile.officialSource.isVerified) Icons.Default.CheckCircle else Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (profile.officialSource.isVerified) DgEmerald else Color(0xFFD97706),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (profile.officialSource.isVerified) "OFFICIAL VERIFIED SOURCE" else "UNVERIFIED SPECS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (profile.officialSource.isVerified) DgEmerald else Color(0xFFD97706)
                            )
                        }

                        if (profile.officialSource.isVerified) {
                            Text(
                                text = "Verified: ${profile.officialSource.lastVerifiedDate}",
                                fontSize = 11.sp,
                                color = Color(0xFF0F766E),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    if (profile.officialSource.isVerified) {
                        Text(
                            text = "${profile.officialSource.sourceName} • ${profile.officialSource.notificationTitleOrNumber}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = DgNavy
                        )
                        Text(
                            text = "Portal: ${profile.officialSource.sourceUrl}",
                            fontSize = 11.sp,
                            color = Color(0xFF475569)
                        )
                    } else {
                        Text(
                            text = profile.officialSource.unverifiedNotice,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFB45309)
                        )
                        Text(
                            text = "Please check the latest official board notification before submitting.",
                            fontSize = 11.sp,
                            color = Color(0xFF78350F)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Specs Tabs: Photo, Signature, Documents
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0xFFF8FAFC),
                contentColor = DgNavy,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = DgSaffron,
                        height = 3.dp
                    )
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Photo Specs", fontSize = 12.sp, fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Draw, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Signature Specs", fontSize = 12.sp, fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Documents", fontSize = 12.sp, fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (selectedTab) {
                0 -> PhotoSpecsView(profile)
                1 -> SignatureSpecsView(profile)
                2 -> DocumentSpecsView(profile)
            }
        }
    }
}

@Composable
private fun PhotoSpecsView(profile: RecruitmentProfile) {
    val req = profile.photoRequirement
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFAFAFA), RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        SpecRow(label = "Dimensions", value = "${req.displayDimensionString} (${req.width} × ${req.height} px)")
        SpecRow(label = "Aspect Ratio", value = req.aspectRatio)
        SpecRow(label = "File Size Limit", value = "${req.minKb} KB to ${req.maxKb} KB")
        SpecRow(label = "Format", value = req.requiredFormat)
        SpecRow(label = "Resolution DPI", value = "${req.dpi ?: 200} DPI")
        SpecRow(label = "Background", value = req.backgroundRequirement)
        SpecRow(label = "Coverage", value = req.colourRequirement)

        if (req.otherInstructions.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = DgSaffron,
                    modifier = Modifier
                        .size(14.dp)
                        .padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = req.otherInstructions,
                    fontSize = 11.sp,
                    color = Color(0xFF475569),
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun SignatureSpecsView(profile: RecruitmentProfile) {
    val req = profile.signatureRequirement
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFAFAFA), RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        SpecRow(label = "Dimensions", value = "${req.displayDimensionString} (${req.width} × ${req.height} px)")
        SpecRow(label = "Aspect Ratio", value = req.aspectRatio)
        SpecRow(label = "File Size Limit", value = "${req.minKb} KB to ${req.maxKb} KB")
        SpecRow(label = "Format", value = req.requiredFormat)
        SpecRow(label = "Background & Ink", value = req.background)

        if (req.otherInstructions.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = DgSaffron,
                    modifier = Modifier
                        .size(14.dp)
                        .padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = req.otherInstructions,
                    fontSize = 11.sp,
                    color = Color(0xFF475569),
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun DocumentSpecsView(profile: RecruitmentProfile) {
    val req = profile.documentRequirement
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFAFAFA), RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        SpecRow(label = "Document Format", value = req.requiredFormat)
        SpecRow(label = "Max File Size", value = "${req.maxKb} KB (Under ${req.maxKb / 1000f} MB)")
        SpecRow(label = "Max Pages", value = "${req.maxPages} pages")
        SpecRow(label = "Number of Files", value = "${req.numberOfFiles} file(s)")

        if (req.otherInstructions.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = DgSaffron,
                    modifier = Modifier
                        .size(14.dp)
                        .padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = req.otherInstructions,
                    fontSize = 11.sp,
                    color = Color(0xFF475569),
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun SpecRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF64748B)
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = DgNavy
        )
    }
}
