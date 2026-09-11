package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MockDataProvider
import com.example.model.Job
import com.example.model.JobCategory
import com.example.model.Tool
import com.example.ui.components.DgCategoryCard
import com.example.ui.components.DgJobCard
import com.example.ui.components.DgJobDetailDialog
import com.example.ui.components.DgSearchBar
import com.example.ui.components.DgSectionHeader
import com.example.ui.components.DgToolCard
import com.example.ui.components.DgUpdateCard
import com.example.ui.components.UpdateType
import com.example.ui.theme.DgAmber
import com.example.ui.theme.DgBackgroundLight
import com.example.ui.theme.DgBlueAccent
import com.example.ui.theme.DgBorderLight
import com.example.ui.theme.DgEmerald
import com.example.ui.theme.DgNavyDark
import com.example.ui.theme.DgNavyPrimary
import com.example.ui.theme.DgSaffron

data class QuickAction(
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val onClick: () -> Unit
)

@Composable
fun HomeScreen(
    onNavigateToJobs: (JobCategory?) -> Unit,
    onNavigateToTools: () -> Unit,
    onNavigateToToolDetail: (String) -> Unit,
    onNavigateToUpdates: (Int) -> Unit,
    onNavigateToSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedJobForModal by remember { mutableStateOf<Job?>(null) }
    var savedJobIds by remember { mutableStateOf(setOf<String>()) }

    val quickActions = listOf(
        QuickAction("Government Jobs", Icons.Default.Assignment, DgNavyPrimary) { onNavigateToJobs(null) },
        QuickAction("Results", Icons.Default.CheckCircle, DgEmerald) { onNavigateToUpdates(1) },
        QuickAction("Admit Card", Icons.Default.Badge, DgSaffron) { onNavigateToUpdates(2) },
        QuickAction("Answer Key", Icons.Default.VpnKey, Color(0xFF7C3AED)) { onNavigateToUpdates(3) },
        QuickAction("Notifications", Icons.Default.Notifications, Color(0xFF0284C7)) { onNavigateToUpdates(4) },
        QuickAction("Government Schemes", Icons.Default.Policy, Color(0xFF0D9488)) { onNavigateToUpdates(0) }
    )

    val popularTools = MockDataProvider.toolsList
    val latestJobs = MockDataProvider.sampleJobs.take(3)
    val latestResult = MockDataProvider.sampleResults.firstOrNull()
    val latestAdmit = MockDataProvider.sampleAdmitCards.firstOrNull()
    val latestAnswerKey = MockDataProvider.sampleAnswerKeys.firstOrNull()
    val latestNotice = MockDataProvider.sampleNotifications.firstOrNull()
    val latestScheme = MockDataProvider.sampleSchemes.firstOrNull()

    val categories = listOf(
        JobCategory.SSC,
        JobCategory.UPSC,
        JobCategory.RAILWAY,
        JobCategory.BANKING,
        JobCategory.POLICE,
        JobCategory.DEFENCE,
        JobCategory.TEACHING,
        JobCategory.STATE_GOVT,
        JobCategory.CENTRAL_GOVT,
        JobCategory.OTHER
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DgBackgroundLight)
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // HERO SECTION
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White,
                                DgBackgroundLight
                            )
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "DG with Anup",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = DgNavyPrimary,
                        fontSize = 24.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Government Jobs, Results, Admit Cards & Powerful Online Tools",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF475569),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Large Search Box
                    DgSearchBar(
                        query = "",
                        onQueryChange = {},
                        placeholder = "Search jobs, results, admit cards, tools...",
                        isReadOnly = true,
                        onClick = onNavigateToSearch
                    )
                }
            }
        }

        // QUICK ACTION CARDS (6 ITEMS)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Quick Services",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DgNavyDark,
                    fontSize = 17.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                // 2 rows of 3 columns
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        quickActions.take(3).forEach { action ->
                            QuickActionCard(
                                action = action,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        quickActions.drop(3).take(3).forEach { action ->
                            QuickActionCard(
                                action = action,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // POPULAR TOOLS SECTION
        item {
            Spacer(modifier = Modifier.height(10.dp))
            DgSectionHeader(
                title = "Popular Tools",
                subtitle = "Fast utilities for exam forms & certificates",
                actionLabel = "View All (${popularTools.size})",
                onActionClick = onNavigateToTools
            )
        }

        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(popularTools) { tool ->
                    Box(modifier = Modifier.width(220.dp)) {
                        DgToolCard(
                            tool = tool,
                            onClick = { onNavigateToToolDetail(tool.route) }
                        )
                    }
                }
            }
        }

        // LATEST GOVERNMENT UPDATES SECTION
        item {
            Spacer(modifier = Modifier.height(14.dp))
            DgSectionHeader(
                title = "Latest Government Updates",
                subtitle = "Recent exam announcements, keys & results",
                actionLabel = "All Updates",
                onActionClick = { onNavigateToUpdates(0) }
            )
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Mix of top latest updates
                if (latestResult != null) {
                    DgUpdateCard(
                        title = latestResult.title,
                        organization = latestResult.organization,
                        dateOrMeta = "Declared ${latestResult.declaredDate}",
                        updateType = UpdateType.RESULT,
                        status = latestResult.status,
                        onActionClick = { onNavigateToUpdates(1) },
                        actionLabel = "Check Result"
                    )
                }

                if (latestAdmit != null) {
                    DgUpdateCard(
                        title = latestAdmit.title,
                        organization = latestAdmit.organization,
                        dateOrMeta = "Exam: ${latestAdmit.examDate}",
                        updateType = UpdateType.ADMIT_CARD,
                        status = latestAdmit.status,
                        onActionClick = { onNavigateToUpdates(2) },
                        actionLabel = "Get Admit Card"
                    )
                }

                if (latestAnswerKey != null) {
                    DgUpdateCard(
                        title = latestAnswerKey.title,
                        organization = latestAnswerKey.organization,
                        dateOrMeta = latestAnswerKey.objectionLastDate,
                        updateType = UpdateType.ANSWER_KEY,
                        status = latestAnswerKey.status,
                        onActionClick = { onNavigateToUpdates(3) },
                        actionLabel = "View Key"
                    )
                }

                if (latestNotice != null) {
                    DgUpdateCard(
                        title = latestNotice.title,
                        organization = latestNotice.organization,
                        dateOrMeta = "Notice Date: ${latestNotice.date}",
                        updateType = UpdateType.NOTIFICATION,
                        status = latestNotice.importance.name,
                        onActionClick = { onNavigateToUpdates(4) },
                        actionLabel = "Read Notice"
                    )
                }

                if (latestScheme != null) {
                    DgUpdateCard(
                        title = latestScheme.title,
                        organization = latestScheme.ministry,
                        dateOrMeta = latestScheme.deadline,
                        updateType = UpdateType.SCHEME,
                        status = "Active Scheme",
                        onActionClick = { onNavigateToUpdates(0) },
                        actionLabel = "View Scheme"
                    )
                }
            }
        }

        // FEATURED JOBS PREVIEW
        item {
            Spacer(modifier = Modifier.height(14.dp))
            DgSectionHeader(
                title = "Featured Openings",
                subtitle = "Active recruitments accepting applications",
                actionLabel = "View All Jobs",
                onActionClick = { onNavigateToJobs(null) }
            )
        }

        items(latestJobs) { job ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                val isSaved = savedJobIds.contains(job.id)
                DgJobCard(
                    job = job,
                    isSaved = isSaved,
                    onSaveToggle = {
                        savedJobIds = if (isSaved) savedJobIds - job.id else savedJobIds + job.id
                    },
                    onViewDetailsClick = { selectedJobForModal = job }
                )
            }
        }

        // EXAM CATEGORIES SECTION
        item {
            Spacer(modifier = Modifier.height(14.dp))
            DgSectionHeader(
                title = "Exam Categories",
                subtitle = "Browse by conducting board & department"
            )
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.chunked(2).forEach { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        pair.forEach { cat ->
                            val count = MockDataProvider.sampleJobs.count { it.category == cat }
                            DgCategoryCard(
                                category = cat,
                                jobCount = if (count > 0) count else 1,
                                onClick = { onNavigateToJobs(cat) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (pair.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }

    // Job Detail Modal
    selectedJobForModal?.let { job ->
        DgJobDetailDialog(
            job = job,
            onDismiss = { selectedJobForModal = null }
        )
    }
}

@Composable
private fun QuickActionCard(
    action: QuickAction,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable(onClick = action.onClick)
            .testTag("quick_action_${action.title.replace(" ", "_")}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DgBorderLight))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(action.color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = action.icon,
                    contentDescription = action.title,
                    tint = action.color,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = action.title,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = DgNavyDark,
                maxLines = 1
            )
        }
    }
}
