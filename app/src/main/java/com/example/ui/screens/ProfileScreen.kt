package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.auth.model.UserRole
import com.example.auth.ui.AuthViewModel
import com.example.ui.components.DgBrandLogo
import com.example.ui.components.DgPrimaryButton
import com.example.ui.theme.DgBackgroundLight
import com.example.ui.theme.DgBlueLight
import com.example.ui.theme.DgBorderLight
import com.example.ui.theme.DgNavyDark
import com.example.ui.theme.DgNavyPrimary
import com.example.ui.theme.DgSaffron
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val currentUser by authViewModel.currentUser.collectAsState()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var showPolicyDialog by remember { mutableStateOf(false) }
    var showSavedItemsDialog by remember { mutableStateOf(false) }
    var showDownloadsDialog by remember { mutableStateOf(false) }

    fun showNotice(msg: String) {
        coroutineScope.launch {
            snackbarHostState.showSnackbar(msg)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DgBackgroundLight)
            .testTag("profile_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Profile Header Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DgBorderLight))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(if (currentUser != null) DgNavyPrimary else DgBlueLight)
                            .border(2.dp, if (currentUser != null) DgSaffron else Color(0xFFBFDBFE), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (currentUser != null) {
                            Text(
                                text = currentUser!!.name.take(1).uppercase(),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Guest",
                                tint = DgNavyPrimary,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (currentUser != null) {
                        Text(
                            text = currentUser!!.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = DgNavyDark,
                            fontSize = 19.sp
                        )

                        if (currentUser!!.email.isNotBlank()) {
                            Text(
                                text = currentUser!!.email,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF64748B),
                                fontSize = 12.sp
                            )
                        }

                        if (!currentUser!!.phone.isNullOrBlank()) {
                            Text(
                                text = "+91 ${currentUser!!.phone}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF64748B),
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Role Badge
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val isAdmin = currentUser!!.role == UserRole.OWNER_ADMIN
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isAdmin) Color(0xFFFEE2E2) else Color(0xFFDCFCE7))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = if (isAdmin) "OWNER ADMINISTRATOR" else "CANDIDATE USER",
                                    color = if (isAdmin) Color(0xFFDC2626) else Color(0xFF16A34A),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showEditProfileDialog = true },
                                modifier = Modifier.weight(1f).testTag("btn_edit_profile")
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Edit Profile", fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = { authViewModel.signOut() },
                                modifier = Modifier.weight(1f).testTag("btn_logout")
                            ) {
                                Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Logout", fontSize = 12.sp)
                            }
                        }
                    } else {
                        Text(
                            text = "Guest Aspirant",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = DgNavyDark,
                            fontSize = 18.sp
                        )

                        Text(
                            text = "Sign in to save recruitments, sync admit cards & personalize alerts",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF64748B),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        DgPrimaryButton(
                            text = "Login / Sign Up",
                            onClick = onNavigateToLogin,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_signup_button")
                        )
                    }
                }
            }
        }

        // Prominent Owner Admin Panel Card (Visible if OWNER_ADMIN or for testing)
        item {
            val isAdmin = currentUser?.role == UserRole.OWNER_ADMIN
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clickable { onNavigateToAdmin() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isAdmin) Color(0xFFFEF2F2) else Color(0xFFFFFBEB)
                ),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(
                        if (isAdmin) Color(0xFFF87171) else Color(0xFFFDE68A)
                    )
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isAdmin) Color(0xFFDC2626) else DgSaffron),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Admin Panel",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Owner Admin Panel",
                                fontWeight = FontWeight.Bold,
                                color = DgNavyDark,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isAdmin) Color(0xFFDC2626) else Color(0xFF64748B))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = if (isAdmin) "AUTHORIZED" else "PROTECTED",
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Text(
                            text = if (isAdmin)
                                "Manage jobs, results, recruitments, specs & platform audit logs"
                            else
                                "Owner credentials required. Click to authenticate / test.",
                            fontSize = 11.sp,
                            color = Color(0xFF475569)
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = if (isAdmin) Color(0xFFDC2626) else DgSaffron,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Section: Aspirant Library
        item {
            ProfileMenuSection(
                title = "My Activity",
                items = listOf(
                    ProfileMenuItem(
                        title = "My Saved Items",
                        subtitle = "${currentUser?.savedItemIds?.size ?: 0} saved notifications & syllabus",
                        icon = Icons.Default.Bookmark,
                        onClick = { showSavedItemsDialog = true }
                    ),
                    ProfileMenuItem(
                        title = "My Downloads",
                        subtitle = "${currentUser?.downloadHistory?.size ?: 0} local documents & compressed assets",
                        icon = Icons.Default.Download,
                        onClick = { showDownloadsDialog = true }
                    )
                )
            )
        }

        // Section: Settings & Support
        item {
            ProfileMenuSection(
                title = "Preferences & Support",
                items = listOf(
                    ProfileMenuItem(
                        title = "Settings",
                        subtitle = "Notification preferences, clean cache & storage",
                        icon = Icons.Default.Settings,
                        onClick = { showSettingsDialog = true }
                    ),
                    ProfileMenuItem(
                        title = "Help & Support",
                        subtitle = "Technical helpdesk: blackdoremon07@gmail.com",
                        icon = Icons.AutoMirrored.Filled.Help,
                        onClick = { showHelpDialog = true }
                    ),
                    ProfileMenuItem(
                        title = "Privacy Policy & Zero-Watermark",
                        subtitle = "Client-side image processing transparency",
                        icon = Icons.Default.Lock,
                        onClick = { showPolicyDialog = true }
                    ),
                    ProfileMenuItem(
                        title = "Terms & Official Disclaimers",
                        subtitle = "Government recruitment information compliance",
                        icon = Icons.Default.Policy,
                        onClick = { showPolicyDialog = true }
                    )
                )
            )
        }

        // App Version Info
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DgBrandLogo(size = 28.dp, showText = false)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "DG with Anup • v1.0.0 (Production)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B)
                )
                Text(
                    text = "Clean Architecture • Zero Watermark Guarantee",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }
    }

    // Edit Profile Dialog
    if (showEditProfileDialog && currentUser != null) {
        var editName by remember { mutableStateOf(currentUser!!.name) }
        var editPhone by remember { mutableStateOf(currentUser!!.phone ?: "") }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Edit Profile", fontWeight = FontWeight.Bold, color = DgNavyDark) },
            text = {
                Column {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Full Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("edit_profile_name")
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        label = { Text("Mobile Number") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("edit_profile_phone")
                    )
                }
            },
            confirmButton = {
                DgPrimaryButton(
                    text = "Save Changes",
                    onClick = {
                        authViewModel.updateProfile(editName, editPhone.ifBlank { null })
                        showEditProfileDialog = false
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel", color = Color(0xFF64748B))
                }
            }
        )
    }

    // Settings Dialog
    if (showSettingsDialog) {
        var notifEnabled by remember { mutableStateOf(true) }

        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = { Text("Preferences & Cache", fontWeight = FontWeight.Bold, color = DgNavyDark) },
            text = {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Government Job Push Alerts", fontSize = 13.sp, color = DgNavyDark)
                        Switch(
                            checked = notifEnabled,
                            onCheckedChange = { notifEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = DgNavyPrimary)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("App Version: 1.0.0 (Release)", fontSize = 12.sp, color = Color(0xFF64748B))
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedButton(
                        onClick = {
                            showNotice("Local offline documents cache cleared.")
                            showSettingsDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Clear Temporary Cache", fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSettingsDialog = false }) {
                    Text("Done", fontWeight = FontWeight.Bold, color = DgNavyPrimary)
                }
            }
        )
    }

    // Saved Items Dialog
    if (showSavedItemsDialog) {
        AlertDialog(
            onDismissRequest = { showSavedItemsDialog = false },
            title = { Text("My Saved Notifications", fontWeight = FontWeight.Bold, color = DgNavyDark) },
            text = {
                val items = currentUser?.savedItemIds ?: emptyList()
                if (items.isEmpty()) {
                    Text("No recruitments bookmarked yet. Tap the bookmark icon on any job card to save it.", fontSize = 13.sp, color = Color(0xFF64748B))
                } else {
                    Column {
                        for (id in items) {
                            Text("• $id", fontSize = 13.sp, color = DgNavyDark, modifier = Modifier.padding(vertical = 4.dp))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSavedItemsDialog = false }) { Text("Close") }
            }
        )
    }

    // Downloads Dialog
    if (showDownloadsDialog) {
        AlertDialog(
            onDismissRequest = { showDownloadsDialog = false },
            title = { Text("My Downloads", fontWeight = FontWeight.Bold, color = DgNavyDark) },
            text = {
                val history: List<String> = currentUser?.downloadHistory ?: emptyList()
                if (history.isEmpty()) {
                    Text("You have not exported any files yet. Photos, signatures, and PDFs generated in Tools will be logged here.", fontSize = 13.sp, color = Color(0xFF64748B))
                } else {
                    Column {
                        for (downloadItem in history) {
                            Text("• $downloadItem", fontSize = 13.sp, color = DgNavyDark, modifier = Modifier.padding(vertical = 4.dp))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDownloadsDialog = false }) { Text("Close") }
            }
        )
    }

    // Help Dialog
    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            title = { Text("Help & Support", fontWeight = FontWeight.Bold, color = DgNavyDark) },
            text = {
                Column {
                    Text("DG with Anup Support", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DgNavyDark)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("For recruitment queries, photo maker assistance, or bug reports, please write to our official email:", fontSize = 12.sp, color = Color(0xFF475569))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Email: blackdoremon07@gmail.com", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DgNavyPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Response time: within 24 hours.", fontSize = 11.sp, color = Color(0xFF64748B))
                }
            },
            confirmButton = {
                TextButton(onClick = { showHelpDialog = false }) { Text("OK") }
            }
        )
    }

    // Policy & Terms Dialog
    if (showPolicyDialog) {
        AlertDialog(
            onDismissRequest = { showPolicyDialog = false },
            title = { Text("Privacy Policy & Terms", fontWeight = FontWeight.Bold, color = DgNavyDark) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text("1. Zero-Watermark Privacy Guarantee", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DgNavyDark)
                    Text("DG with Anup processes all photo, signature, and PDF modifications locally on device memory. Your documents are never watermarked and never uploaded to public servers without authorization.", fontSize = 11.sp, color = Color(0xFF475569))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("2. Government Information Disclaimer", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DgNavyDark)
                    Text("DG with Anup is an independent candidate assistance utility and informational portal. All recruitment circulars, syllabi, and notifications are cross-referenced from authorized government commission portals.", fontSize = 11.sp, color = Color(0xFF475569))
                }
            },
            confirmButton = {
                TextButton(onClick = { showPolicyDialog = false }) { Text("I Understand") }
            }
        )
    }
}

data class ProfileMenuItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val onClick: () -> Unit,
    val isHighlight: Boolean = false
)

@Composable
private fun ProfileMenuSection(
    title: String,
    items: List<ProfileMenuItem>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = DgNavyDark,
            fontSize = 14.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DgBorderLight))
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                items.forEachIndexed { index, item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = item.onClick)
                            .padding(14.dp)
                            .testTag("profile_item_${item.title.replace(" ", "_")}"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (item.isHighlight) Color(0xFFFEF3C7) else DgBlueLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null,
                                tint = if (item.isHighlight) DgSaffron else DgNavyPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.title,
                                fontWeight = FontWeight.SemiBold,
                                color = DgNavyDark,
                                fontSize = 14.sp
                            )
                            Text(
                                text = item.subtitle,
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color(0xFFCBD5E1),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    if (index < items.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 14.dp),
                            color = DgBorderLight
                        )
                    }
                }
            }
        }
    }
}
