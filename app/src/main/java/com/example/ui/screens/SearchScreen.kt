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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.InMemoryAppRepository
import com.example.model.Job
import com.example.ui.components.DgEmptyState
import com.example.ui.components.DgJobCard
import com.example.ui.components.DgJobDetailDialog
import com.example.ui.components.DgSearchBar
import com.example.ui.components.DgToolCard
import com.example.ui.components.DgUpdateCard
import com.example.ui.components.UpdateType
import com.example.ui.theme.DgBackgroundLight
import com.example.ui.theme.DgNavyDark
import com.example.ui.theme.DgNavyPrimary

@Composable
fun SearchScreen(
    onBack: () -> Unit,
    onNavigateToTool: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    val repository = remember { InMemoryAppRepository() }
    val searchResults = remember(searchQuery) { repository.searchAll(searchQuery) }
    var selectedJobForModal by remember { mutableStateOf<Job?>(null) }
    var savedJobIds by remember { mutableStateOf(setOf<String>()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DgBackgroundLight)
            .statusBarsPadding()
            .testTag("search_screen")
    ) {
        // Top Search Bar Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("search_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = DgNavyPrimary
                    )
                }

                DgSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "Search jobs, admit cards, tools...",
                    isReadOnly = false,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Search Results List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 60.dp)
        ) {
            if (searchQuery.isBlank()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp, start = 20.dp, end = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Color(0xFFCBD5E1),
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Search across DG with Anup",
                            fontWeight = FontWeight.Bold,
                            color = DgNavyDark,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Type an exam name (e.g. 'SSC', 'UPSC'), commission, or utility tool.",
                            color = Color(0xFF64748B),
                            fontSize = 12.sp
                        )
                    }
                }
            } else if (searchResults.totalCount == 0) {
                item {
                    DgEmptyState(
                        title = "No matches for \"$searchQuery\"",
                        description = "Check spelling or search by department name such as SSC, Railway, Banking, or Police."
                    )
                }
            } else {
                item {
                    Text(
                        text = "Found ${searchResults.totalCount} results for \"$searchQuery\"",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF475569),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }

                // Matched Jobs
                if (searchResults.matchedJobs.isNotEmpty()) {
                    item {
                        Text(
                            text = "Recruitments & Jobs (${searchResults.matchedJobs.size})",
                            fontWeight = FontWeight.Bold,
                            color = DgNavyDark,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }
                    items(searchResults.matchedJobs) { job ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
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
                }

                // Matched Tools
                if (searchResults.matchedTools.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Matching Tools (${searchResults.matchedTools.size})",
                            fontWeight = FontWeight.Bold,
                            color = DgNavyDark,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }
                    items(searchResults.matchedTools) { tool ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                            DgToolCard(
                                tool = tool,
                                onClick = { onNavigateToTool(tool.route) }
                            )
                        }
                    }
                }

                // Matched Results
                if (searchResults.matchedResults.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Results (${searchResults.matchedResults.size})",
                            fontWeight = FontWeight.Bold,
                            color = DgNavyDark,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }
                    items(searchResults.matchedResults) { result ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                            DgUpdateCard(
                                title = result.title,
                                organization = result.organization,
                                dateOrMeta = result.declaredDate,
                                updateType = UpdateType.RESULT,
                                status = result.status,
                                onActionClick = {}
                            )
                        }
                    }
                }

                // Matched Admit Cards
                if (searchResults.matchedAdmitCards.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Admit Cards (${searchResults.matchedAdmitCards.size})",
                            fontWeight = FontWeight.Bold,
                            color = DgNavyDark,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }
                    items(searchResults.matchedAdmitCards) { admit ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                            DgUpdateCard(
                                title = admit.title,
                                organization = admit.organization,
                                dateOrMeta = admit.examDate,
                                updateType = UpdateType.ADMIT_CARD,
                                status = admit.status,
                                onActionClick = {}
                            )
                        }
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
