package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.KitchenGreen
import com.example.ui.theme.KitchenOrangeAccent
import com.example.ui.viewmodel.FreshNovaViewModel

@Composable
fun ProfileScreen(
    viewModel: FreshNovaViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.userProfile.collectAsState()
    val isNotifOn by viewModel.isNotificationEnabled.collectAsState()
    val currentLang by viewModel.selectedLanguage.collectAsState()

    var showLangMenu by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        // Stats Title Matching Image #6
        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp)) {
            Text(
                text = "Profile",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Manage your preferences and impact metrics",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Professional Photo / Avatar card Header panel (Matches Image #6)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // User Avatar Profile circular icon frame
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(Color(0xFFE6F4EA), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "User Avatar",
                        tint = KitchenGreen,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.width(18.dp))

                Column {
                    Text(
                        text = profile.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = profile.email,
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // Green Rank Badge "Eco Saver"
                    Box(
                        modifier = Modifier
                            .background(KitchenGreen.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "🏆 ${profile.statusBadge}",
                            color = KitchenGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Eco Impact Metrics Banner Card Grid layout (Matches Image #6 exactly but styled for White Theme!)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE6F4EA)), // Beautiful fresh mint white background
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Metric 1: Money Saved
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("💰", fontSize = 24.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("₹${profile.moneySaved.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = KitchenGreen)
                    Text("Money Saved", fontSize = 10.sp, color = Color.DarkGray)
                }

                // Divider vertical line
                Box(modifier = Modifier.width(1.dp).height(44.dp).background(Color.Black.copy(alpha = 0.10f)))

                // Metric 2: Items Saved
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("🛒", fontSize = 24.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("${profile.itemsSaved}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = KitchenGreen)
                    Text("Items Saved", fontSize = 10.sp, color = Color.DarkGray)
                }

                // Divider vertical line
                Box(modifier = Modifier.width(1.dp).height(44.dp).background(Color.Black.copy(alpha = 0.10f)))

                // Metric 3: Active Streak (Glow flames!)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1.1f)
                ) {
                    Text("🔥", fontSize = 24.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("${profile.dayStreak} Days", fontSize = 16.sp, fontWeight = FontWeight.Black, color = KitchenOrangeAccent)
                    Text("Active Streak", fontSize = 10.sp, color = Color.DarkGray)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Kitchen Preferences Settings Block Header
        Text(
            text = "KITCHEN PREFERENCES",
            fontSize = 11.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)
        )

        // General settings list card matching Image #6
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column {
                // Preferred Language (Interactive menu!)
                SettingsRow(
                    icon = Icons.Default.Language,
                    title = "App Language",
                    description = currentLang,
                    onClick = { showLangMenu = true }
                )
                // Dropdown layout
                Box(modifier = Modifier.padding(start = 64.dp)) {
                    DropdownMenu(
                        expanded = showLangMenu,
                        onDismissRequest = { showLangMenu = false }
                    ) {
                        viewModel.languages.forEach { lang ->
                            DropdownMenuItem(
                                text = { Text(lang) },
                                onClick = {
                                    viewModel.setLanguage(lang)
                                    showLangMenu = false
                                }
                            )
                        }
                    }
                }

                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f), modifier = Modifier.padding(start = 64.dp))

                // Notifications Alerts Toggle switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1.2f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFFFEF3C7), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = "Alerts icon",
                                tint = Color(0xFFD97706)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                "Expiry Alerts",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                "Push messages before foods expire",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    Switch(
                        checked = isNotifOn,
                        onCheckedChange = { viewModel.toggleNotifications() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = KitchenGreen,
                            uncheckedThumbColor = Color.LightGray,
                            uncheckedTrackColor = Color.LightGray.copy(alpha = 0.3f)
                        )
                    )
                }

                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f), modifier = Modifier.padding(start = 64.dp))

                // Simulated Household Size
                SettingsRow(
                    icon = Icons.Default.People,
                    title = "Household Size",
                    description = "4 Members",
                    onClick = { /* Simulated */ }
                )

                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f), modifier = Modifier.padding(start = 64.dp))

                // Primary Storage zone preference
                SettingsRow(
                    icon = Icons.Default.Storage,
                    title = "Primary Storage",
                    description = "Refrigerated & Pantry",
                    onClick = { /* Simulated */ }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Red sign out button (Actionable, exits dashboard safely)
        Button(
            onClick = { viewModel.logout() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFCE8E6)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(54.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Logout icon",
                    tint = Color.Red
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Sign Out Account",
                    color = Color.Red,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
fun SettingsRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1.2f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(KitchenGreen.copy(alpha = 0.08f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = KitchenGreen,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Forward Icon",
            tint = Color.LightGray.copy(alpha = 0.8f),
            modifier = Modifier.size(18.dp)
        )
    }
}
