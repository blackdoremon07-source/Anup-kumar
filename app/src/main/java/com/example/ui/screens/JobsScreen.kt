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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.WorkOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.example.data.MockDataProvider
import com.example.model.Job
import com.example.model.JobCategory
import com.example.ui.components.DgEmptyState
import com.example.ui.components.DgJobCard
import com.example.ui.components.DgJobDetailDialog
import com.example.ui.components.DgSearchBar
import com.example.ui.theme.DgBackgroundLight
import com.example.ui.theme.DgBorderLight
import com.example.ui.theme.DgNavyDark
import com.example.ui.theme.DgNavyPrimary
import com.example.ui.theme.DgSaffron

@Composable
fun JobsScreen(
    initialCategory: JobCategory? = null,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf(initialCategory ?: JobCategory.ALL) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedJobForModal by remember { mutableStateOf<Job?>(null) }
    var savedJobIds by remember { mutableStateOf(setOf<String>()) }

    val filterOptions = listOf(
        JobCategory.ALL,
        JobCategory.SSC,
        JobCategory.UPSC,
        JobCategory.RAILWAY,
        JobCategory.BANKING,
        JobCategory.POLICE,
        JobCategory.DEFENCE,
        JobCategory.TEACHING,
        JobCategory.STATE_GOVT,
        JobCategory.CENTRAL_GOVT
    )

    // Filter jobs by category and search query
    val filteredJobs = remember(selectedCategory, searchQuery) {
        MockDataProvider.sampleJobs.filter { job ->
            val matchesCategory = (selectedCategory == JobCategory.ALL) || (job.category == selectedCategory)
            val matchesSearch = searchQuery.isBlank() ||
                    job.title.contains(searchQuery, ignoreCase = true) ||
                    job.organization.contains(searchQuery, ignoreCase = true) ||
                    job.qualification.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DgBackgroundLight)
            .testTag("jobs_screen"),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Top Title & Search bar
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Government Job Openings",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = DgNavyDark,
                    fontSize = 20.sp
                )

                Text(
                    text = "Browse Central & State vacancies, notifications, and eligibility",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF64748B),
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                DgSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "Filter by job title, commission, or qualification...",
                    isReadOnly = false
                )
            }
        }

        // Horizontal Category Filter Chips
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filterOptions) { category ->
                    val isSelected = selectedCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = category },
                        label = {
                            Text(
                                text = category.displayName,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DgNavyPrimary,
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFFF1F5F9),
                            labelColor = DgNavyDark
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) DgNavyPrimary else DgBorderLight,
                            selectedBorderColor = DgNavyPrimary,
                            borderWidth = 1.dp
                        ),
                        modifier = Modifier.testTag("job_filter_${category.name}")
                    )
                }
            }
        }

        // Active filter summary info badge
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filteredJobs.size} Recruitments Found",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF475569)
                )

                Text(
                    text = "Showing: ${selectedCategory.displayName}",
                    fontSize = 12.sp,
                    color = DgNavyPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Job Cards List
        if (filteredJobs.isEmpty()) {
            item {
                DgEmptyState(
                    icon = Icons.Default.WorkOff,
                    title = "No jobs found for selected criteria",
                    description = "Try selecting 'All' or clearing your search term to see other official vacancies."
                )
            }
        } else {
            items(filteredJobs) { job ->
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
        }
    }

    // Modal popup for detailed view
    selectedJobForModal?.let { job ->
        DgJobDetailDialog(
            job = job,
            onDismiss = { selectedJobForModal = null }
        )
    }
}
