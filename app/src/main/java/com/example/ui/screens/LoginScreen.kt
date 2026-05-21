package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.KitchenGreen
import com.example.ui.theme.KitchenLightBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(KitchenLightBackground)
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Header Row with Branding Illustration side-by-side
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1.1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Welcome Back!",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        letterSpacing = (-1).sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Sign in to continue",
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }

                // Programmatic grocery basket vector illustration! (Matches Image #1)
                Box(
                    modifier = Modifier
                        .weight(0.9f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    GroceryBasketLogo(modifier = Modifier.size(130.dp))
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Email Field
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = false
                },
                label = { Text("Email Address") },
                placeholder = { Text("Enter your email") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email Icon",
                        tint = if (emailError) MaterialTheme.colorScheme.error else KitchenGreen
                    )
                },
                isError = emailError,
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = textFieldGreenColors(),
                modifier = Modifier.fillMaxWidth()
            )

            if (emailError) {
                Text(
                    text = "Please enter a valid email address.",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, top = 4.dp),
                    textAlign = TextAlign.Start
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Password Field
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = false
                },
                label = { Text("Password") },
                placeholder = { Text("Enter your password") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Password Icon",
                        tint = if (passwordError) MaterialTheme.colorScheme.error else KitchenGreen
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle Password Visibility",
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }
                },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = passwordError,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(16.dp),
                colors = textFieldGreenColors(),
                modifier = Modifier.fillMaxWidth()
            )

            if (passwordError) {
                Text(
                    text = "Password must be at least 6 characters.",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, top = 4.dp),
                    textAlign = TextAlign.Start
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Forgot Password
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "Forgot Password?",
                    color = KitchenGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { /* Handle forgot password simulation */ }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Sign In Button
            Button(
                onClick = {
                    focusManager.clearFocus()
                    val isEmailValid = email.contains("@") && email.length > 3
                    val isPasswordValid = password.length >= 6

                    if (!isEmailValid) emailError = true
                    if (!isPasswordValid) passwordError = true

                    if (isEmailValid && isPasswordValid) {
                        onLoginSuccess(email, password)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = KitchenGreen),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = "Sign In",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Social Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))
                Text(
                    text = " or sign in with ",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Social login row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                // Google Button (Canvas logo representation)
                SocialButton(onClick = { onLoginSuccess("google.user@gmail.com", "oauth_pass") }) {
                    GoogleLogo()
                }

                Spacer(modifier = Modifier.width(28.dp))

                // Apple Button (Canvas logo representation)
                SocialButton(onClick = { onLoginSuccess("apple.user@icloud.com", "oauth_pass") }) {
                    AppleLogo()
                }
            }

            Spacer(modifier = Modifier.height(44.dp))

            // Sign Up Option
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Don't have an account? ",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
                Text(
                    text = "Sign Up",
                    color = KitchenGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable {
                        // Quick populate credentials for rapid user testing!
                        email = "rahul.sharma@email.com"
                        password = "password123"
                    }
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SocialButton(onClick: () -> Unit, content: @Composable () -> Unit) {
    Card(
        onClick = onClick,
        shape = CircleShape,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.size(64.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            content()
        }
    }
}

// Gorgeous Custom Grocery Box Vector drawing to replicate image #1 style!
@Composable
fun GroceryBasketLogo(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Background: Basket items (Mock Tomato Red, Lettuce light green, Milk white bottle)
        // Draw milk bottle
        drawRoundRect(
            color = Color(0xFFEEEEEE),
            topLeft = androidx.compose.ui.geometry.Offset(w * 0.22f, h * 0.15f),
            size = androidx.compose.ui.geometry.Size(w * 0.18f, h * 0.45f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f)
        )
        // Red ripe Tomato
        drawCircle(
            color = Color(0xFFEF4444),
            radius = w * 0.15f,
            center = androidx.compose.ui.geometry.Offset(w * 0.52f, h * 0.38f)
        )
        // Green leaves lettuce popping
        val leafPath = Path().apply {
            moveTo(w * 0.65f, h * 0.4f)
            cubicTo(w * 0.55f, h * 0.15f, w * 0.85f, h * 0.1f, w * 0.82f, h * 0.4f)
            close()
        }
        drawPath(path = leafPath, color = Color(0xFF22C55E))

        // Bread brown top loaf
        val breadPath = Path().apply {
            moveTo(w * 0.62f, h * 0.32f)
            lineTo(w * 0.88f, h * 0.18f)
            lineTo(w * 0.95f, h * 0.35f)
            close()
        }
        drawPath(path = breadPath, color = Color(0xFFD97706))

        // Front basket container (green)
        val basketPath = Path().apply {
            moveTo(w * 0.12f, h * 0.42f)
            lineTo(w * 0.88f, h * 0.42f)
            lineTo(w * 0.82f, h * 0.82f)
            lineTo(w * 0.18f, h * 0.82f)
            close()
        }
        drawPath(path = basketPath, color = Color(0xFF38A169))

        // Nested checkmark circle inside basket
        drawCircle(
            color = Color.White,
            radius = w * 0.15f,
            center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.62f)
        )
        val checkPath = Path().apply {
            moveTo(w * 0.43f, h * 0.62f)
            lineTo(w * 0.48f, h * 0.67f)
            lineTo(w * 0.57f, h * 0.56f)
        }
        drawPath(
            path = checkPath,
            color = Color(0xFF38A169),
            style = Stroke(width = 6f)
        )
    }
}

@Composable
fun GoogleLogo() {
    Canvas(modifier = Modifier.size(28.dp)) {
        val r = size.width / 2
        // Draw standard clean google colored lines/arcs would be awesome, or we can draw standard colored G
        // For simplicity and gorgeous vectoring, draw circular colorful segmented sectors
        drawArc(Color(0xFFEA4335), -145f, 75f, true) // Red top
        drawArc(Color(0xFFFBBC05), -70f, 75f, true)  // Yellow right
        drawArc(Color(0xFF34A853), 5f, 100f, true)   // Green bottom
        drawArc(Color(0xFF4285F4), 105f, 110f, true) // Blue left
        drawCircle(Color.White, r * 0.6f)
    }
}

@Composable
fun AppleLogo() {
    Canvas(modifier = Modifier.size(24.dp)) {
        val w = size.width
        val h = size.height
        // Beautiful programmatically drawn flat stylized Apple logo
        drawCircle(Color.Black, w * 0.43f, center = androidx.compose.ui.geometry.Offset(w * 0.44f, h * 0.62f))
        drawCircle(Color.Black, w * 0.43f, center = androidx.compose.ui.geometry.Offset(w * 0.56f, h * 0.62f))
        // bite
        drawCircle(Color.White, w * 0.22f, center = androidx.compose.ui.geometry.Offset(w * 0.9f, h * 0.54f))
        // stem leaf
        drawArc(
            color = Color.Black,
            startAngle = 0f,
            sweepAngle = 90f,
            useCenter = true,
            topLeft = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.05f),
            size = androidx.compose.ui.geometry.Size(w * 0.25f, h * 0.25f)
        )
    }
}

@Composable
fun textFieldGreenColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = KitchenGreen,
    unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.15f),
    focusedLabelColor = KitchenGreen,
    unfocusedLabelColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
)
