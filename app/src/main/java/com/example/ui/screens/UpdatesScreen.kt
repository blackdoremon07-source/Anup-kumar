package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MockDataProvider
import com.example.model.Job
import com.example.ui.components.DgEmptyState
import com.example.ui.components.DgJobDetailDialog
import com.example.ui.components.DgUpdateCard
import com.example.ui.components.UpdateType
import com.example.ui.theme.DgBackgroundLight
import com.example.ui.theme.DgNavyDark
import com.example.ui.theme.DgNavyPrimary
import com.example.ui.theme.DgSaffron

@Composable
fun UpdatesScreen(
    initialTabIndex: Int = 0,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(initialTabIndex) }
    var selectedJobForModal by remember { mutableStateOf<Job?>(null) }

    val tabs = listOf("Jobs", "Results", "Admit Card", "Answer Key", "Notifications")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DgBackgroundLight)
            .testTag("updates_screen"),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Latest Government Updates",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = DgNavyDark,
                    fontSize = 20.sp
                )
                Text(
                    text = "Live feeds, official result declarations, and admit card releases",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF64748B),
                    fontSize = 12.sp
                )
            }
        }

        // Scrollable Tabs
        item {
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.White,
                contentColor = DgNavyPrimary,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = DgNavyPrimary,
                        height = 3.dp
                    )
                },
                modifier = Modifier.testTag("updates_tabs")
            ) {
                tabs.forEachIndexed { index, title ->
                    val isSelected = selectedTabIndex == index
                    Tab(
                        selected = isSelected,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp,
                                color = if (isSelected) DgNavyPrimary else Color(0xFF64748B)
                            )
                        },
                        modifier = Modifier.testTag("update_tab_$index")
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Tab Content
        when (selectedTabIndex) {
            0 -> {
                // Jobs Tab
                items(MockDataProvider.sampleJobs) { job ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)) {
                        DgUpdateCard(
                            title = job.title,
                            organization = job.organization,
                            dateOrMeta = "Last Date: ${job.lastDate} • ${job.totalVacancies}",
                            updateType = UpdateType.JOB,
                            status = job.status,
                            onActionClick = { selectedJobForModal = job },
                            actionLabel = "View Notice"
                        )
                    }
                }
            }

            1 -> {
                // Results Tab
                items(MockDataProvider.sampleResults) { result ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)) {
                        DgUpdateCard(
                            title = result.title,
                            organization = result.organization,
                            dateOrMeta = "${result.examDate} • ${result.declaredDate}",
                            updateType = UpdateType.RESULT,
                            status = result.status,
                            onActionClick = { /* Handled in Part 7 with direct PDF link */ },
                            actionLabel = "View Scorecard"
                        )
                    }
                }
            }

            2 -> {
                // Admit Card Tab
                items(MockDataProvider.sampleAdmitCards) { admit ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)) {
                        DgUpdateCard(
                            title = admit.title,
                            organization = admit.organization,
                            dateOrMeta = "Exam Date: ${admit.examDate} • ${admit.releaseDate}",
                            updateType = UpdateType.ADMIT_CARD,
                            status = admit.status,
                            onActionClick = { /* Handled in Part 7 with portal login link */ },
                            actionLabel = "Download Hall Ticket"
                        )
                    }
                }
            }

            3 -> {
                // Answer Key Tab
                items(MockDataProvider.sampleAnswerKeys) { key ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)) {
                        DgUpdateCard(
                            title = key.title,
                            organization = key.organization,
                            dateOrMeta = "${key.examDate} • ${key.objectionLastDate}",
                            updateType = UpdateType.ANSWER_KEY,
                            status = key.status,
                            onActionClick = { /* Handled in Part 7 */ },
                            actionLabel = "View Key & Answers"
                        )
                    }
                }
            }

            4 -> {
                // Notifications Tab
                items(MockDataProvider.sampleNotifications) { notice ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)) {
                        DgUpdateCard(
                            title = notice.title,
                            organization = notice.organization,
                            dateOrMeta = "Published: ${notice.date} • Category: ${notice.category.displayName}",
                            updateType = UpdateType.NOTIFICATION,
                            status = notice.importance.name,
                            onActionClick = { /* Handled in Part 7 */ },
                            actionLabel = "Read Circular"
                        )
                    }
                }
            }
        }
    }

    selectedJobForModal?.let { job ->
        DgJobDetailDialog(
            job = job,
            onDismiss = { selectedJobForModal = null }
        )
    }
}
