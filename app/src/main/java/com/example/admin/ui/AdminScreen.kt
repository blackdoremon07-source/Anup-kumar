package com.example.admin.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.admin.model.AdminSection
import com.example.admin.model.ContentStatus
import com.example.admin.model.ManagedAdmitCard
import com.example.admin.model.ManagedJob
import com.example.admin.model.ManagedRecruitmentRequirement
import com.example.admin.model.ManagedResult
import com.example.admin.model.VerificationStatus
import com.example.auth.model.UserProfile
import com.example.auth.model.UserRole
import com.example.model.JobCategory
import com.example.ui.components.DgBrandLogo
import com.example.ui.components.DgOutlinedButton
import com.example.ui.components.DgPrimaryButton
import com.example.ui.theme.DgBackgroundLight
import com.example.ui.theme.DgBlueLight
import com.example.ui.theme.DgBorderLight
import com.example.ui.theme.DgNavyDark
import com.example.ui.theme.DgNavyPrimary
import com.example.ui.theme.DgSaffron
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    currentUser: UserProfile?,
    adminViewModel: AdminViewModel,
    onNavigateBack: () -> Unit,
    onToggleTestAdminRole: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val currentSection by adminViewModel.currentSection.collectAsState()
    val searchQuery by adminViewModel.searchQuery.collectAsState()
    val statusFilter by adminViewModel.statusFilter.collectAsState()
    val bannerMessage by adminViewModel.notificationBanner.collectAsState()

    val deleteConfirmation by adminViewModel.deleteConfirmation.collectAsState()
    val jobDialogItem by adminViewModel.jobDialogItem.collectAsState()
    val resultDialogItem by adminViewModel.resultDialogItem.collectAsState()
    val admitCardDialogItem by adminViewModel.admitCardDialogItem.collectAsState()
    val recruitmentDialogItem by adminViewModel.recruitmentDialogItem.collectAsState()

    // 1. Strict Server-Side / Role Authorization Check
    val isOwnerAdmin = currentUser != null && currentUser.role == UserRole.OWNER_ADMIN

    if (!isOwnerAdmin) {
        // Access Denied Screen
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Portal Administration", fontWeight = FontWeight.Bold, color = DgNavyDark) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DgNavyDark)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
            },
            modifier = modifier.testTag("admin_access_denied_screen")
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DgBackgroundLight)
                    .padding(paddingValues)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFEE2E2)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Lock",
                        tint = Color(0xFFDC2626),
                        modifier = Modifier.size(44.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Access Denied",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = DgNavyDark
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Owner Admin permission required. You don't have permission to access this page.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DgBorderLight))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Security Verification Notice",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = DgNavyDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "All administrative routes, user management actions, and government data mutations are guarded by Firebase Authentication and Firestore server-side security rules.",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                DgPrimaryButton(
                    text = "Return to Profile",
                    onClick = onNavigateBack,
                    modifier = Modifier.fillMaxWidth().testTag("btn_access_denied_back")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Development convenience test switch
                OutlinedButton(
                    onClick = { onToggleTestAdminRole(true) },
                    modifier = Modifier.fillMaxWidth().testTag("btn_switch_to_admin_demo")
                ) {
                    Text("Developer: Authenticate as OWNER_ADMIN", fontSize = 12.sp, color = DgNavyPrimary)
                }
            }
        }
        return
    }

    // 2. OWNER ADMIN AUTHORIZED VIEW WITH DRAWER
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color.White,
                modifier = Modifier.width(300.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DgNavyDark)
                        .padding(20.dp)
                ) {
                    DgBrandLogo(size = 32.dp, textColor = Color.White)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Owner Admin Studio",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFDC2626))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "OWNER_ADMIN",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = currentUser.name,
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp
                        )
                    }
                }

                HorizontalDivider(color = DgBorderLight)

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    items(AdminSection.values()) { section ->
                        val isSelected = currentSection == section
                        val icon = getSectionIcon(section)

                        NavigationDrawerItem(
                            label = {
                                Text(
                                    text = section.title,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp
                                )
                            },
                            icon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (isSelected) DgNavyPrimary else Color(0xFF64748B)
                                )
                            },
                            selected = isSelected,
                            onClick = {
                                adminViewModel.selectSection(section)
                                coroutineScope.launch { drawerState.close() }
                            },
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = DgBlueLight,
                                selectedTextColor = DgNavyPrimary,
                                unselectedTextColor = DgNavyDark
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.padding(vertical = 2.dp).testTag("drawer_item_${section.name}")
                        )
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = currentSection.title,
                                fontWeight = FontWeight.Bold,
                                color = DgNavyDark,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "DG with Anup • Owner Admin",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { coroutineScope.launch { drawerState.open() } },
                            modifier = Modifier.testTag("admin_menu_drawer_button")
                        ) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = DgNavyDark)
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.testTag("admin_exit_button")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Exit Admin", tint = DgNavyDark)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
            },
            modifier = modifier.testTag("admin_panel_screen")
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DgBackgroundLight)
                    .padding(paddingValues)
            ) {
                // Banner Message if present
                if (bannerMessage != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFECFDF5)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(bannerMessage ?: "", fontSize = 12.sp, color = Color(0xFF065F46), modifier = Modifier.weight(1f))
                            IconButton(
                                onClick = { adminViewModel.clearBanner() },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = Color(0xFF065F46), modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                // Render current section
                when (currentSection) {
                    AdminSection.DASHBOARD -> AdminDashboardView(adminViewModel, currentUser)
                    AdminSection.JOBS -> AdminJobsView(adminViewModel, currentUser)
                    AdminSection.RESULTS -> AdminResultsView(adminViewModel, currentUser)
                    AdminSection.ADMIT_CARDS -> AdminAdmitCardsView(adminViewModel, currentUser)
                    AdminSection.ANSWER_KEYS -> AdminAnswerKeysView(adminViewModel, currentUser)
                    AdminSection.NOTIFICATIONS -> AdminNotificationsView(adminViewModel, currentUser)
                    AdminSection.SCHEMES -> AdminSchemesView(adminViewModel, currentUser)
                    AdminSection.RECRUITMENTS -> AdminRecruitmentsView(adminViewModel, currentUser)
                    AdminSection.USERS -> AdminUsersView(adminViewModel, currentUser)
                    AdminSection.APP_CONTENT -> AdminAppContentView(adminViewModel, currentUser)
                    AdminSection.SETTINGS -> AdminSettingsView(adminViewModel, currentUser, onToggleTestAdminRole)
                    AdminSection.AUDIT_LOG -> AdminAuditLogView(adminViewModel)
                }
            }
        }
    }

    // Generic Delete Confirmation Dialog
    if (deleteConfirmation != null) {
        val (message, onConfirm) = deleteConfirmation!!
        AlertDialog(
            onDismissRequest = { adminViewModel.dismissDeleteConfirmation() },
            title = { Text("Confirm Deletion", fontWeight = FontWeight.Bold, color = DgNavyDark) },
            text = { Text(message, fontSize = 13.sp, color = Color(0xFF475569)) },
            confirmButton = {
                DgPrimaryButton(
                    text = "Delete",
                    onClick = onConfirm,
                    modifier = Modifier.testTag("btn_confirm_delete")
                )
            },
            dismissButton = {
                TextButton(onClick = { adminViewModel.dismissDeleteConfirmation() }) {
                    Text("Cancel", color = Color(0xFF64748B))
                }
            }
        )
    }

    // Job Add/Edit Dialog
    if (jobDialogItem != null) {
        JobEditDialog(
            initialJob = jobDialogItem,
            onDismiss = { adminViewModel.closeJobDialog() },
            onSave = { job -> adminViewModel.saveJob(job, currentUser) }
        )
    }

    // Result Add/Edit Dialog
    if (resultDialogItem != null) {
        ResultEditDialog(
            initialResult = resultDialogItem,
            onDismiss = { adminViewModel.closeResultDialog() },
            onSave = { res -> adminViewModel.saveResult(res, currentUser) }
        )
    }

    // Admit Card Add/Edit Dialog
    if (admitCardDialogItem != null) {
        AdmitCardEditDialog(
            initialCard = admitCardDialogItem,
            onDismiss = { adminViewModel.closeAdmitCardDialog() },
            onSave = { card -> adminViewModel.saveAdmitCard(card, currentUser) }
        )
    }

    // Recruitment Requirement Add/Edit Dialog
    if (recruitmentDialogItem != null) {
        RecruitmentEditDialog(
            initialReq = recruitmentDialogItem,
            onDismiss = { adminViewModel.closeRecruitmentDialog() },
            onSave = { req -> adminViewModel.saveRecruitment(req, currentUser) }
        )
    }
}

// =====================================
// Sub-Views: Section by Section
// =====================================

@Composable
private fun AdminDashboardView(viewModel: AdminViewModel, currentUser: UserProfile) {
    val stats = viewModel.getStats()
    val recentJobs by viewModel.jobs.collectAsState()
    val auditLogs by viewModel.auditLogs.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 60.dp)
    ) {
        item {
            // Welcome Header
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DgNavyPrimary),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Administrator Overview",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "System Status: ${stats.systemStatus}",
                        fontSize = 12.sp,
                        color = Color(0xFF93C5FD)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Stats Grid
        item {
            Text(
                text = "Live Platform Metrics",
                fontWeight = FontWeight.Bold,
                color = DgNavyDark,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatCard("Users", stats.totalUsers.toString(), Icons.Default.People, Modifier.weight(1f))
                    StatCard("Jobs", stats.totalJobs.toString(), Icons.Default.Work, Modifier.weight(1f))
                    StatCard("Results", stats.totalResults.toString(), Icons.Default.Assessment, Modifier.weight(1f))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatCard("Admit Cards", stats.totalAdmitCards.toString(), Icons.Default.Badge, Modifier.weight(1f))
                    StatCard("Specs", stats.totalRecruitments.toString(), Icons.Default.FactCheck, Modifier.weight(1f))
                    StatCard("Saved Items", stats.totalSavedItems.toString(), Icons.Default.Security, Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Recent Updates
        item {
            Text(
                text = "Recent Jobs Managed",
                fontWeight = FontWeight.Bold,
                color = DgNavyDark,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(recentJobs.take(3)) { job ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DgBorderLight))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(job.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DgNavyDark)
                        Text("${job.organization} • ${job.totalVacancies}", fontSize = 11.sp, color = Color(0xFF64748B))
                    }
                    StatusBadge(job.status)
                }
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DgBorderLight))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = DgNavyPrimary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DgNavyDark)
            Text(label, fontSize = 11.sp, color = Color(0xFF64748B))
        }
    }
}

@Composable
private fun AdminJobsView(viewModel: AdminViewModel, currentUser: UserProfile) {
    val jobs by viewModel.jobs.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    val filteredJobs = remember(jobs, searchQuery) {
        if (searchQuery.isBlank()) jobs
        else jobs.filter { it.title.contains(searchQuery, ignoreCase = true) || it.organization.contains(searchQuery, ignoreCase = true) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Search jobs...", fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF64748B)) },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("admin_jobs_search_input")
                )
                Spacer(modifier = Modifier.width(8.dp))
                DgPrimaryButton(
                    text = "Add Job",
                    onClick = { viewModel.openJobDialog(null) },
                    modifier = Modifier.testTag("btn_admin_add_job")
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        items(filteredJobs) { job ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DgBorderLight))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(job.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DgNavyDark, modifier = Modifier.weight(1f))
                        StatusBadge(job.status)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("${job.organization} • Last Date: ${job.lastDate} • Vacancies: ${job.totalVacancies}", fontSize = 11.sp, color = Color(0xFF64748B))
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.openJobDialog(job) },
                            modifier = Modifier.testTag("btn_edit_job_${job.id}")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Edit", fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(
                            onClick = { viewModel.confirmDeleteJob(job, currentUser) },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                            modifier = Modifier.testTag("btn_delete_job_${job.id}")
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Delete", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminResultsView(viewModel: AdminViewModel, currentUser: UserProfile) {
    val results by viewModel.results.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Official Government Results", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DgNavyDark)
                DgPrimaryButton(
                    text = "Add Result",
                    onClick = { viewModel.openResultDialog(null) },
                    modifier = Modifier.testTag("btn_admin_add_result")
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        items(results) { res ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DgBorderLight))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(res.resultTitle, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DgNavyDark)
                    Text("${res.examName} • ${res.organization} • Declared: ${res.resultDate}", fontSize = 11.sp, color = Color(0xFF64748B))
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(onClick = { viewModel.openResultDialog(res) }) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Edit", fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(
                            onClick = { viewModel.confirmDeleteResult(res, currentUser) },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626))
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Delete", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminAdmitCardsView(viewModel: AdminViewModel, currentUser: UserProfile) {
    val cards by viewModel.admitCards.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Admit Cards & Hall Tickets", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DgNavyDark)
                DgPrimaryButton(
                    text = "Add Admit Card",
                    onClick = { viewModel.openAdmitCardDialog(null) },
                    modifier = Modifier.testTag("btn_admin_add_admit_card")
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        items(cards) { card ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DgBorderLight))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(card.admitCardTitle, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DgNavyDark)
                    Text("${card.organization} • Exam Date: ${card.examDate} • Release: ${card.releaseDate}", fontSize = 11.sp, color = Color(0xFF64748B))
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        OutlinedButton(onClick = { viewModel.openAdmitCardDialog(card) }) {
                            Text("Edit", fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(
                            onClick = { viewModel.confirmDeleteAdmitCard(card, currentUser) },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626))
                        ) {
                            Text("Delete", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminAnswerKeysView(viewModel: AdminViewModel, currentUser: UserProfile) {
    val keys by viewModel.answerKeys.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            Text("Official Exam Answer Keys", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DgNavyDark)
            Spacer(modifier = Modifier.height(14.dp))
        }
        items(keys) { key ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DgBorderLight))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(key.answerKeyTitle, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DgNavyDark)
                    Text("${key.organization} • Release Date: ${key.releaseDate}", fontSize = 11.sp, color = Color(0xFF64748B))
                }
            }
        }
    }
}

@Composable
private fun AdminNotificationsView(viewModel: AdminViewModel, currentUser: UserProfile) {
    val notifications by viewModel.notifications.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            Text("Published Career Notifications", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DgNavyDark)
            Spacer(modifier = Modifier.height(14.dp))
        }
        items(notifications) { notif ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DgBorderLight))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(notif.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DgNavyDark)
                    Text("Category: ${notif.category} • Date: ${notif.publishDate}", fontSize = 11.sp, color = Color(0xFF64748B))
                }
            }
        }
    }
}

@Composable
private fun AdminSchemesView(viewModel: AdminViewModel, currentUser: UserProfile) {
    val schemes by viewModel.schemes.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            Text("Government Youth & Career Schemes", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DgNavyDark)
            Spacer(modifier = Modifier.height(14.dp))
        }
        items(schemes) { scheme ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DgBorderLight))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(scheme.schemeName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DgNavyDark)
                    Text("${scheme.department} • Benefits: ${scheme.benefits}", fontSize = 11.sp, color = Color(0xFF64748B))
                }
            }
        }
    }
}

@Composable
private fun AdminRecruitmentsView(viewModel: AdminViewModel, currentUser: UserProfile) {
    val recruitments by viewModel.recruitments.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Recruitment Requirements Manager", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DgNavyDark)
                    Text("Photo, signature & document specifications for Part 3 maker", fontSize = 11.sp, color = Color(0xFF64748B))
                }
                DgPrimaryButton(
                    text = "Add Specs",
                    onClick = { viewModel.openRecruitmentDialog(null) },
                    modifier = Modifier.testTag("btn_admin_add_recruitment")
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        items(recruitments) { req ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DgBorderLight))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(req.recruitmentName, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DgNavyDark, modifier = Modifier.weight(1f))
                        VerificationBadge(req.verificationStatus)
                    }

                    Text("${req.organization} • Category: ${req.category} • Verified: ${req.lastVerifiedDate}", fontSize = 11.sp, color = Color(0xFF64748B))

                    Spacer(modifier = Modifier.height(10.dp))

                    // Photo & Sig specs summary
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DgBackgroundLight, RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("PHOTO REQUIREMENTS", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = DgNavyPrimary)
                            Text("${req.photoWidthPx} × ${req.photoHeightPx} ${req.photoUnit} (${req.photoMinKb}–${req.photoMaxKb} KB)", fontSize = 11.sp, color = DgNavyDark)
                            Text("${req.photoDpi} DPI • ${req.photoBackground}", fontSize = 10.sp, color = Color(0xFF64748B))
                        }
                        Column {
                            Text("SIGNATURE REQUIREMENTS", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = DgSaffron)
                            Text("${req.sigWidthPx} × ${req.sigHeightPx} ${req.sigUnit} (${req.sigMinKb}–${req.sigMaxKb} KB)", fontSize = 11.sp, color = DgNavyDark)
                            Text("${req.sigDpi} DPI • ${req.sigFormat}", fontSize = 10.sp, color = Color(0xFF64748B))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Official Source: ${req.officialSourceUrl}", fontSize = 11.sp, color = Color(0xFF2563EB))

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        OutlinedButton(onClick = { viewModel.openRecruitmentDialog(req) }) {
                            Text("Edit Specs", fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(
                            onClick = { viewModel.confirmDeleteRecruitment(req, currentUser) },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626))
                        ) {
                            Text("Delete", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminUsersView(viewModel: AdminViewModel, currentUser: UserProfile) {
    val users by viewModel.users.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            Text("Registered Candidate Users", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DgNavyDark)
            Spacer(modifier = Modifier.height(14.dp))
        }
        items(users) { u ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DgBorderLight))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(u.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DgNavyDark)
                        Text("${u.email} • ${u.phone}", fontSize = 11.sp, color = Color(0xFF64748B))
                        Text("Role: ${u.role} • Status: ${u.status}", fontSize = 11.sp, color = if (u.status == "ACTIVE") Color(0xFF16A34A) else Color(0xFFDC2626))
                    }
                    if (u.role != "OWNER_ADMIN") {
                        OutlinedButton(onClick = { viewModel.toggleUserStatus(u.uid, currentUser) }) {
                            Text(if (u.status == "ACTIVE") "Disable" else "Enable", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminAppContentView(viewModel: AdminViewModel, currentUser: UserProfile) {
    val contentItems by viewModel.appContent.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            Text("App Content & Official Copy", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DgNavyDark)
            Spacer(modifier = Modifier.height(14.dp))
        }
        items(contentItems) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DgBorderLight))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(item.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DgNavyDark)
                    Text("Key: ${item.key} • Section: ${item.section}", fontSize = 11.sp, color = Color(0xFF64748B))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(item.content, fontSize = 12.sp, color = Color(0xFF334155))
                }
            }
        }
    }
}

@Composable
private fun AdminSettingsView(
    viewModel: AdminViewModel,
    currentUser: UserProfile,
    onToggleTestAdminRole: (Boolean) -> Unit
) {
    val maintenanceMode by viewModel.maintenanceMode.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Admin System Settings", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DgNavyDark)
        Spacer(modifier = Modifier.height(14.dp))

        // Maintenance Mode Toggle
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DgBorderLight))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Maintenance Mode", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DgNavyDark)
                    Text("Temporarily redirect users to maintenance announcement", fontSize = 11.sp, color = Color(0xFF64748B))
                }
                Switch(
                    checked = maintenanceMode,
                    onCheckedChange = { viewModel.toggleMaintenanceMode(currentUser) },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = DgNavyPrimary)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Danger Zone
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFFCA5A5)))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Danger Zone", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF991B1B))
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { viewModel.clearTempCache(currentUser) },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626))
                ) {
                    Text("Clear Temporary Application Cache", fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { onToggleTestAdminRole(false) }
                ) {
                    Text("Switch Back to Normal User Role", fontSize = 12.sp, color = Color(0xFF64748B))
                }
            }
        }
    }
}

@Composable
private fun AdminAuditLogView(viewModel: AdminViewModel) {
    val logs by viewModel.auditLogs.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            Text("Admin Security & Activity Log", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DgNavyDark)
            Text("Immutable audit trail of all administrative updates", fontSize = 11.sp, color = Color(0xFF64748B))
            Spacer(modifier = Modifier.height(14.dp))
        }

        items(logs) { log ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DgBorderLight))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(log.action, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DgNavyDark)
                        Text(SimpleDateFormat("dd MMM HH:mm", Locale.getDefault()).format(Date(log.timestamp)), fontSize = 10.sp, color = Color(0xFF64748B))
                    }
                    Text("Admin: ${log.adminName} (${log.adminUid}) • Target: ${log.collectionType}/${log.targetId}", fontSize = 10.sp, color = Color(0xFF64748B))
                }
            }
        }
    }
}

// =====================================
// Dialogs: Job, Result, Admit Card, Recruitment Specs
// =====================================

@Composable
private fun JobEditDialog(
    initialJob: ManagedJob?,
    onDismiss: () -> Unit,
    onSave: (ManagedJob) -> Unit
) {
    var title by remember { mutableStateOf(initialJob?.title ?: "") }
    var org by remember { mutableStateOf(initialJob?.organization ?: "") }
    var vacancies by remember { mutableStateOf(initialJob?.totalVacancies ?: "") }
    var lastDate by remember { mutableStateOf(initialJob?.lastDate ?: "") }
    var qual by remember { mutableStateOf(initialJob?.qualification ?: "") }
    var status by remember { mutableStateOf(initialJob?.status ?: ContentStatus.PUBLISHED) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialJob == null) "Add Government Job" else "Edit Job", fontWeight = FontWeight.Bold, color = DgNavyDark) },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Job Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("job_dialog_title")
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = org,
                    onValueChange = { org = it },
                    label = { Text("Organization") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("job_dialog_org")
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = vacancies,
                    onValueChange = { vacancies = it },
                    label = { Text("Total Vacancies") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = lastDate,
                    onValueChange = { lastDate = it },
                    label = { Text("Application Last Date") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = qual,
                    onValueChange = { qual = it },
                    label = { Text("Minimum Qualification") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            DgPrimaryButton(
                text = "Save Job",
                onClick = {
                    val job = initialJob?.copy(
                        title = title,
                        organization = org,
                        totalVacancies = vacancies,
                        lastDate = lastDate,
                        qualification = qual,
                        status = status
                    ) ?: ManagedJob(
                        id = "job_${System.currentTimeMillis()}",
                        title = title,
                        organization = org,
                        category = JobCategory.CENTRAL_GOVT,
                        qualification = qual,
                        ageLimit = "18 - 30 Years",
                        totalVacancies = vacancies,
                        lastDate = lastDate,
                        applicationFee = "₹100",
                        officialWebsite = "https://ssc.gov.in",
                        officialNotificationUrl = "https://ssc.gov.in",
                        description = "Official job opening.",
                        status = status
                    )
                    onSave(job)
                },
                enabled = title.isNotBlank() && org.isNotBlank(),
                modifier = Modifier.testTag("btn_save_job_dialog")
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Color(0xFF64748B)) }
        }
    )
}

@Composable
private fun ResultEditDialog(
    initialResult: ManagedResult?,
    onDismiss: () -> Unit,
    onSave: (ManagedResult) -> Unit
) {
    var title by remember { mutableStateOf(initialResult?.resultTitle ?: "") }
    var exam by remember { mutableStateOf(initialResult?.examName ?: "") }
    var org by remember { mutableStateOf(initialResult?.organization ?: "") }
    var date by remember { mutableStateOf(initialResult?.resultDate ?: "") }
    var url by remember { mutableStateOf(initialResult?.officialResultUrl ?: "https://ssc.gov.in") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialResult == null) "Add Exam Result" else "Edit Result", fontWeight = FontWeight.Bold, color = DgNavyDark) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState()).fillMaxWidth()) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Result Title") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = exam, onValueChange = { exam = it }, label = { Text("Exam Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = org, onValueChange = { org = it }, label = { Text("Organization") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Result Date") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = url, onValueChange = { url = it }, label = { Text("Official Result URL") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            DgPrimaryButton(
                text = "Save Result",
                onClick = {
                    val res = initialResult?.copy(
                        resultTitle = title,
                        examName = exam,
                        organization = org,
                        resultDate = date,
                        officialResultUrl = url
                    ) ?: ManagedResult(
                        id = "res_${System.currentTimeMillis()}",
                        examName = exam,
                        organization = org,
                        resultTitle = title,
                        resultDate = date,
                        officialResultUrl = url
                    )
                    onSave(res)
                },
                enabled = title.isNotBlank() && exam.isNotBlank()
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Color(0xFF64748B)) }
        }
    )
}

@Composable
private fun AdmitCardEditDialog(
    initialCard: ManagedAdmitCard?,
    onDismiss: () -> Unit,
    onSave: (ManagedAdmitCard) -> Unit
) {
    var title by remember { mutableStateOf(initialCard?.admitCardTitle ?: "") }
    var exam by remember { mutableStateOf(initialCard?.examName ?: "") }
    var org by remember { mutableStateOf(initialCard?.organization ?: "") }
    var examDate by remember { mutableStateOf(initialCard?.examDate ?: "") }
    var downloadUrl by remember { mutableStateOf(initialCard?.officialDownloadUrl ?: "https://ssc.gov.in") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialCard == null) "Add Admit Card" else "Edit Admit Card", fontWeight = FontWeight.Bold, color = DgNavyDark) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState()).fillMaxWidth()) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Admit Card Title") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = exam, onValueChange = { exam = it }, label = { Text("Exam Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = org, onValueChange = { org = it }, label = { Text("Organization") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = examDate, onValueChange = { examDate = it }, label = { Text("Exam Date") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = downloadUrl, onValueChange = { downloadUrl = it }, label = { Text("Official Download URL") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            DgPrimaryButton(
                text = "Save Admit Card",
                onClick = {
                    val card = initialCard?.copy(
                        admitCardTitle = title,
                        examName = exam,
                        organization = org,
                        examDate = examDate,
                        officialDownloadUrl = downloadUrl
                    ) ?: ManagedAdmitCard(
                        id = "ac_${System.currentTimeMillis()}",
                        examName = exam,
                        organization = org,
                        admitCardTitle = title,
                        releaseDate = "Current",
                        examDate = examDate,
                        officialDownloadUrl = downloadUrl
                    )
                    onSave(card)
                },
                enabled = title.isNotBlank() && exam.isNotBlank()
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Color(0xFF64748B)) }
        }
    )
}

@Composable
private fun RecruitmentEditDialog(
    initialReq: ManagedRecruitmentRequirement?,
    onDismiss: () -> Unit,
    onSave: (ManagedRecruitmentRequirement) -> Unit
) {
    var name by remember { mutableStateOf(initialReq?.recruitmentName ?: "") }
    var org by remember { mutableStateOf(initialReq?.organization ?: "") }
    var category by remember { mutableStateOf(initialReq?.category ?: "SSC") }

    // Photo specs
    var photoWidth by remember { mutableStateOf(initialReq?.photoWidthPx?.toString() ?: "350") }
    var photoHeight by remember { mutableStateOf(initialReq?.photoHeightPx?.toString() ?: "450") }
    var photoMinKb by remember { mutableStateOf(initialReq?.photoMinKb?.toString() ?: "20") }
    var photoMaxKb by remember { mutableStateOf(initialReq?.photoMaxKb?.toString() ?: "50") }
    var photoBackground by remember { mutableStateOf(initialReq?.photoBackground ?: "White / Plain") }

    // Sig specs
    var sigWidth by remember { mutableStateOf(initialReq?.sigWidthPx?.toString() ?: "400") }
    var sigHeight by remember { mutableStateOf(initialReq?.sigHeightPx?.toString() ?: "200") }
    var sigMinKb by remember { mutableStateOf(initialReq?.sigMinKb?.toString() ?: "10") }
    var sigMaxKb by remember { mutableStateOf(initialReq?.sigMaxKb?.toString() ?: "20") }

    // Verification
    var sourceUrl by remember { mutableStateOf(initialReq?.officialSourceUrl ?: "https://ssc.gov.in") }
    var status by remember { mutableStateOf(initialReq?.verificationStatus ?: VerificationStatus.VERIFIED) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialReq == null) "Add Recruitment Specs" else "Edit Recruitment Specs", fontWeight = FontWeight.Bold, color = DgNavyDark) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState()).fillMaxWidth()) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Recruitment Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = org, onValueChange = { org = it }, label = { Text("Organization") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))

                Text("PHOTO REQUIREMENTS", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = DgNavyPrimary)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = photoWidth, onValueChange = { photoWidth = it }, label = { Text("Width (px)") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = photoHeight, onValueChange = { photoHeight = it }, label = { Text("Height (px)") }, modifier = Modifier.weight(1f))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = photoMinKb, onValueChange = { photoMinKb = it }, label = { Text("Min KB") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = photoMaxKb, onValueChange = { photoMaxKb = it }, label = { Text("Max KB") }, modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(8.dp))

                Text("SIGNATURE REQUIREMENTS", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = DgSaffron)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = sigWidth, onValueChange = { sigWidth = it }, label = { Text("Width (px)") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = sigHeight, onValueChange = { sigHeight = it }, label = { Text("Height (px)") }, modifier = Modifier.weight(1f))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = sigMinKb, onValueChange = { sigMinKb = it }, label = { Text("Min KB") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = sigMaxKb, onValueChange = { sigMaxKb = it }, label = { Text("Max KB") }, modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(8.dp))

                Text("VERIFICATION & SOURCE", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF16A34A))
                OutlinedTextField(value = sourceUrl, onValueChange = { sourceUrl = it }, label = { Text("Official Source URL") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            DgPrimaryButton(
                text = "Save Specs",
                onClick = {
                    val req = initialReq?.copy(
                        recruitmentName = name,
                        organization = org,
                        category = category,
                        photoWidthPx = photoWidth.toIntOrNull() ?: 350,
                        photoHeightPx = photoHeight.toIntOrNull() ?: 450,
                        photoMinKb = photoMinKb.toIntOrNull() ?: 20,
                        photoMaxKb = photoMaxKb.toIntOrNull() ?: 50,
                        sigWidthPx = sigWidth.toIntOrNull() ?: 400,
                        sigHeightPx = sigHeight.toIntOrNull() ?: 200,
                        sigMinKb = sigMinKb.toIntOrNull() ?: 10,
                        sigMaxKb = sigMaxKb.toIntOrNull() ?: 20,
                        officialSourceUrl = sourceUrl,
                        verificationStatus = status
                    ) ?: ManagedRecruitmentRequirement(
                        id = "req_${System.currentTimeMillis()}",
                        recruitmentName = name,
                        organization = org,
                        category = category,
                        photoWidthPx = photoWidth.toIntOrNull() ?: 350,
                        photoHeightPx = photoHeight.toIntOrNull() ?: 450,
                        photoMinKb = photoMinKb.toIntOrNull() ?: 20,
                        photoMaxKb = photoMaxKb.toIntOrNull() ?: 50,
                        sigWidthPx = sigWidth.toIntOrNull() ?: 400,
                        sigHeightPx = sigHeight.toIntOrNull() ?: 200,
                        sigMinKb = sigMinKb.toIntOrNull() ?: 10,
                        sigMaxKb = sigMaxKb.toIntOrNull() ?: 20,
                        officialSourceUrl = sourceUrl,
                        verificationStatus = status
                    )
                    onSave(req)
                },
                enabled = name.isNotBlank() && org.isNotBlank()
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Color(0xFF64748B)) }
        }
    )
}

// =====================================
// Helper Badges & Icons
// =====================================

@Composable
private fun StatusBadge(status: ContentStatus) {
    val (bg, fg, label) = when (status) {
        ContentStatus.PUBLISHED -> Triple(Color(0xFFDCFCE7), Color(0xFF15803D), "Published")
        ContentStatus.DRAFT -> Triple(Color(0xFFFEF3C7), Color(0xFFB45309), "Draft")
        ContentStatus.CLOSED -> Triple(Color(0xFFF1F5F9), Color(0xFF64748B), "Closed")
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bg)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(text = label, color = fg, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun VerificationBadge(status: VerificationStatus) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color(status.colorHex).copy(alpha = 0.15f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(text = status.label, color = Color(status.colorHex), fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

private fun getSectionIcon(section: AdminSection): ImageVector = when (section) {
    AdminSection.DASHBOARD -> Icons.Default.Dashboard
    AdminSection.USERS -> Icons.Default.People
    AdminSection.JOBS -> Icons.Default.Work
    AdminSection.RESULTS -> Icons.Default.Assessment
    AdminSection.ADMIT_CARDS -> Icons.Default.Badge
    AdminSection.ANSWER_KEYS -> Icons.Default.Key
    AdminSection.NOTIFICATIONS -> Icons.Default.Notifications
    AdminSection.SCHEMES -> Icons.Default.AccountBalance
    AdminSection.RECRUITMENTS -> Icons.Default.FactCheck
    AdminSection.APP_CONTENT -> Icons.Default.Article
    AdminSection.SETTINGS -> Icons.Default.Settings
    AdminSection.AUDIT_LOG -> Icons.Default.Security
}
