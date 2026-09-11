package com.example.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.auth.model.AuthStatus
import com.example.ui.components.DgBrandLogo
import com.example.ui.components.DgOutlinedButton
import com.example.ui.components.DgPrimaryButton
import com.example.ui.theme.DgBackgroundLight
import com.example.ui.theme.DgNavyDark
import com.example.ui.theme.DgNavyPrimary
import com.example.ui.theme.DgSaffron

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onNavigateBack: () -> Unit,
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val authStatus by authViewModel.authStatus.collectAsState()
    val phoneOtpState by authViewModel.phoneOtpState.collectAsState()
    val isSignUpMode by authViewModel.isSignUpMode.collectAsState()
    val forgotPasswordOpen by authViewModel.forgotPasswordOpen.collectAsState()
    val statusMessage by authViewModel.statusMessage.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Google, 1: Mobile OTP, 2: Email

    // Form inputs
    var nameInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var phoneInput by remember { mutableStateOf("") }
    var otpInput by remember { mutableStateOf("") }

    var forgotEmailInput by remember { mutableStateOf("") }

    // If authenticated, trigger callback
    if (authStatus is AuthStatus.Authenticated) {
        onLoginSuccess()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isSignUpMode) "Create Account" else "Sign In",
                        fontWeight = FontWeight.Bold,
                        color = DgNavyDark
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("login_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = DgNavyDark
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        modifier = modifier.testTag("login_screen")
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DgBackgroundLight)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: DG with Anup Logo & Welcome
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    DgBrandLogo(modifier = Modifier.height(56.dp))

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Welcome to DG with Anup",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = DgNavyDark,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Sign in to access personalized recruitment profiles, save target exams, and track download history.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Status message banner
            if (statusMessage != null) {
                val isError = authStatus is AuthStatus.Error || phoneOtpState.error != null
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isError) Color(0xFFFEF2F2) else Color(0xFFF0FDF4)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(
                            if (isError) Color(0xFFFCA5A5) else Color(0xFF86EFAC)
                        )
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isError) Icons.Default.ErrorOutline else Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = if (isError) Color(0xFFDC2626) else Color(0xFF16A34A),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = statusMessage ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isError) Color(0xFF991B1B) else Color(0xFF166534),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Method Selector Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = DgNavyPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = DgNavyPrimary,
                        height = 3.dp
                    )
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0; authViewModel.clearStatusMessage() },
                    text = { Text("Google", fontWeight = FontWeight.SemiBold, fontSize = 13.sp) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1; authViewModel.clearStatusMessage() },
                    text = { Text("Mobile OTP", fontWeight = FontWeight.SemiBold, fontSize = 13.sp) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2; authViewModel.clearStatusMessage() },
                    text = { Text("Email", fontWeight = FontWeight.SemiBold, fontSize = 13.sp) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tab 0: Continue with Google
            if (selectedTab == 0) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Fast One-Tap Login",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = DgNavyDark
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Sign in securely using your authorized Google Account. No passwords required.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF64748B),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        DgOutlinedButton(
                            text = "Continue with Google",
                            onClick = {
                                authViewModel.signInWithGoogle(
                                    idToken = "demo_google_token",
                                    displayName = "Aspirant Candidate",
                                    email = "candidate.aspirant@gmail.com",
                                    photoUrl = null
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_google_signin")
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Encrypted with Firebase Security Rules",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }
            }

            // Tab 1: Mobile Phone + OTP
            if (selectedTab == 1) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = if (!phoneOtpState.isOtpSent) "Mobile Number Verification" else "Enter 6-Digit OTP",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = DgNavyDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (!phoneOtpState.isOtpSent)
                                "Enter your 10-digit mobile number to receive a secure SMS OTP."
                            else
                                "OTP sent to +91 ${phoneOtpState.phoneNumber}. Code expires in 5 minutes.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF64748B)
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        if (!phoneOtpState.isOtpSent) {
                            OutlinedTextField(
                                value = phoneInput,
                                onValueChange = { if (it.length <= 10) phoneInput = it.filter { c -> c.isDigit() } },
                                label = { Text("Mobile Number") },
                                placeholder = { Text("9876543210") },
                                prefix = { Text("+91 ", fontWeight = FontWeight.Bold, color = DgNavyPrimary) },
                                leadingIcon = {
                                    Icon(Icons.Default.Phone, contentDescription = null, tint = DgNavyPrimary)
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("phone_number_input")
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            DgPrimaryButton(
                                text = "Send OTP",
                                onClick = { authViewModel.sendPhoneOtp(phoneInput) },
                                enabled = phoneInput.length == 10,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("btn_send_otp")
                            )
                        } else {
                            OutlinedTextField(
                                value = otpInput,
                                onValueChange = { if (it.length <= 6) otpInput = it.filter { c -> c.isDigit() } },
                                label = { Text("6-Digit OTP Code") },
                                placeholder = { Text("123456") },
                                leadingIcon = {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = DgNavyPrimary)
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("otp_code_input")
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            DgPrimaryButton(
                                text = "Verify OTP & Continue",
                                onClick = { authViewModel.verifyPhoneOtp(otpInput) },
                                enabled = otpInput.length == 6,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("btn_verify_otp")
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (phoneOtpState.resendCooldownSeconds > 0) {
                                    Text(
                                        text = "Resend in ${phoneOtpState.resendCooldownSeconds}s",
                                        fontSize = 12.sp,
                                        color = Color(0xFF64748B)
                                    )
                                } else {
                                    TextButton(
                                        onClick = { authViewModel.sendPhoneOtp(phoneOtpState.phoneNumber) },
                                        modifier = Modifier.testTag("btn_resend_otp")
                                    ) {
                                        Text("Resend OTP", color = DgNavyPrimary, fontWeight = FontWeight.Bold)
                                    }
                                }

                                TextButton(
                                    onClick = {
                                        authViewModel.sendPhoneOtp("") // reset
                                    }
                                ) {
                                    Text("Change Number", color = Color(0xFF64748B), fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Tab 2: Email & Password
            if (selectedTab == 2) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = if (isSignUpMode) "Create Candidate Account" else "Candidate Login",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = DgNavyDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isSignUpMode)
                                "Register with your name and official candidate email."
                            else
                                "Enter your registered credentials to sign in.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF64748B)
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        if (isSignUpMode) {
                            OutlinedTextField(
                                value = nameInput,
                                onValueChange = { nameInput = it },
                                label = { Text("Full Name") },
                                placeholder = { Text("e.g. Rahul Sharma") },
                                leadingIcon = {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = DgNavyPrimary)
                                },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("signup_name_input")
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = { emailInput = it },
                            label = { Text("Email Address") },
                            placeholder = { Text("candidate@example.com") },
                            leadingIcon = {
                                Icon(Icons.Default.Email, contentDescription = null, tint = DgNavyPrimary)
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("email_input")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = passwordInput,
                            onValueChange = { passwordInput = it },
                            label = { Text("Password (min 6 chars)") },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = DgNavyPrimary)
                            },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                                    )
                                }
                            },
                            singleLine = true,
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("password_input")
                        )

                        if (!isSignUpMode) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = {
                                        forgotEmailInput = emailInput
                                        authViewModel.setForgotPasswordOpen(true)
                                    },
                                    modifier = Modifier.testTag("btn_forgot_password")
                                ) {
                                    Text("Forgot Password?", fontSize = 12.sp, color = DgNavyPrimary)
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        DgPrimaryButton(
                            text = if (isSignUpMode) "Sign Up" else "Login",
                            onClick = {
                                authViewModel.submitEmailAuth(nameInput, emailInput, passwordInput)
                            },
                            enabled = emailInput.isNotBlank() && passwordInput.length >= 6 && (!isSignUpMode || nameInput.isNotBlank()),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_email_submit")
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Toggle between Sign In and Create Account
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isSignUpMode) "Already have an account?" else "New to DG with Anup?",
                                fontSize = 13.sp,
                                color = Color(0xFF64748B)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isSignUpMode) "Sign In" else "Create Account",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = DgNavyPrimary,
                                modifier = Modifier
                                    .clickable {
                                        authViewModel.setSignUpMode(!isSignUpMode)
                                    }
                                    .testTag("toggle_signup_mode")
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Privacy Assurance Note
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = DgNavyPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Privacy Guaranteed: Your documents, photos, and signatures are processed locally and never stored without explicit request.",
                        fontSize = 11.sp,
                        color = Color(0xFF475569),
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }

    // Forgot Password Dialog
    if (forgotPasswordOpen) {
        AlertDialog(
            onDismissRequest = { authViewModel.setForgotPasswordOpen(false) },
            title = { Text("Reset Password", fontWeight = FontWeight.Bold, color = DgNavyDark) },
            text = {
                Column {
                    Text(
                        text = "Enter your registered email address to receive password reset instructions.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF64748B)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = forgotEmailInput,
                        onValueChange = { forgotEmailInput = it },
                        label = { Text("Email Address") },
                        placeholder = { Text("candidate@example.com") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("forgot_password_email_input")
                    )
                }
            },
            confirmButton = {
                DgPrimaryButton(
                    text = "Send Reset Link",
                    onClick = { authViewModel.requestPasswordReset(forgotEmailInput) },
                    enabled = forgotEmailInput.isNotBlank(),
                    modifier = Modifier.testTag("btn_send_reset_link")
                )
            },
            dismissButton = {
                TextButton(
                    onClick = { authViewModel.setForgotPasswordOpen(false) }
                ) {
                    Text("Cancel", color = Color(0xFF64748B))
                }
            }
        )
    }
}
