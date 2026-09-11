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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    onNavigateToAdmin: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

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
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(DgBlueLight)
                            .border(2.dp, Color(0xFFBFDBFE), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Guest User",
                            tint = DgNavyPrimary,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Guest Aspirant",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DgNavyDark,
                        fontSize = 18.sp
                    )

                    Text(
                        text = "Browse jobs, updates & tools anonymously",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF64748B),
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Login / Sign Up button (Placeholder for Part 5)
                    DgPrimaryButton(
                        onClick = {
                            showNotice("Authentication system arriving in Part 5 (Google, Mobile & Email Login)")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_signup_button")
                    ) {
                        Text(
                            text = "Login / Sign Up",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Cloud sync & candidate dashboard will activate in Part 5",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
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
                        subtitle = "Saved job recruitments and exam notifications",
                        icon = Icons.Default.Bookmark,
                        onClick = { showNotice("Saved items list synced locally in Part 1") }
                    ),
                    ProfileMenuItem(
                        title = "My Downloads",
                        subtitle = "Offline syllabus, hall tickets, and admit cards",
                        icon = Icons.Default.Download,
                        onClick = { showNotice("Download repository arriving in Part 4") }
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
                        subtitle = "Notification alerts, language & theme preferences",
                        icon = Icons.Default.Settings,
                        onClick = { showNotice("App preferences settings arriving in Part 5") }
                    ),
                    ProfileMenuItem(
                        title = "Help & FAQ",
                        subtitle = "Frequently asked questions and exam guides",
                        icon = Icons.AutoMirrored.Filled.Help,
                        onClick = { showNotice("DG with Anup Support: blackdoremon07@gmail.com") }
                    ),
                    ProfileMenuItem(
                        title = "Privacy Policy",
                        subtitle = "Data security and zero-watermark transparency",
                        icon = Icons.Default.Lock,
                        onClick = { showNotice("Privacy Policy: DG with Anup strictly safeguards user data.") }
                    ),
                    ProfileMenuItem(
                        title = "Terms & Conditions",
                        subtitle = "Terms of service and government info disclaimers",
                        icon = Icons.Default.Policy,
                        onClick = { showNotice("Terms: DG with Anup is a private career utility & informational portal.") }
                    )
                )
            )
        }

        // Section: Administration
        item {
            ProfileMenuSection(
                title = "Portal Administration",
                items = listOf(
                    ProfileMenuItem(
                        title = "Admin Panel Foundation",
                        subtitle = "Protected administrative dashboard (Coming in Part 6)",
                        icon = Icons.Default.AdminPanelSettings,
                        onClick = onNavigateToAdmin,
                        isHighlight = true
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
                    text = "DG with Anup • v1.0.0",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B)
                )
                Text(
                    text = "Part 1 Foundation Release • Scalable Architecture",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }
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
