package com.example.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inventory
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.KitchenGreen
import com.example.ui.viewmodel.BottomTab
import com.example.ui.viewmodel.FreshNovaViewModel

@Composable
fun DashboardScreen(
    viewModel: FreshNovaViewModel,
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.currentTab.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier.height(72.dp)
            ) {
                // Bottom Tab Items List (Matches mockup aesthetic)
                val navItems = listOf(
                    NavigationItemDetails(
                        tab = BottomTab.Home,
                        label = "Home",
                        selectedIcon = Icons.Default.Home,
                        unselectedIcon = Icons.Outlined.Home
                    ),
                    NavigationItemDetails(
                        tab = BottomTab.Scan,
                        label = "Scan QR",
                        selectedIcon = Icons.Default.QrCodeScanner,
                        unselectedIcon = Icons.Outlined.QrCodeScanner
                    ),
                    NavigationItemDetails(
                        tab = BottomTab.Inventory,
                        label = "Inventory",
                        selectedIcon = Icons.Default.Inventory,
                        unselectedIcon = Icons.Outlined.Inventory
                    ),
                    NavigationItemDetails(
                        tab = BottomTab.Insights,
                        label = "Insights",
                        selectedIcon = Icons.Default.BarChart,
                        unselectedIcon = Icons.Outlined.BarChart
                    ),
                    NavigationItemDetails(
                        tab = BottomTab.Profile,
                        label = "Profile",
                        selectedIcon = Icons.Default.Person,
                        unselectedIcon = Icons.Outlined.Person
                    )
                )

                navItems.forEach { item ->
                    val isSelected = currentTab == item.tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.setBottomTab(item.tab) },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = KitchenGreen,
                            selectedTextColor = KitchenGreen,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = KitchenGreen.copy(alpha = 0.1f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        // Crossfade tab transition
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(
                targetState = currentTab,
                label = "TabTransition"
            ) { tab ->
                when (tab) {
                    is BottomTab.Home -> HomeScreen(viewModel = viewModel)
                    is BottomTab.Scan -> ScanScreen(viewModel = viewModel)
                    is BottomTab.Inventory -> InventoryScreen(viewModel = viewModel)
                    is BottomTab.Insights -> InsightsScreen(viewModel = viewModel)
                    is BottomTab.Profile -> ProfileScreen(viewModel = viewModel)
                }
            }
        }
    }
}

private data class NavigationItemDetails(
    val tab: BottomTab,
    val label: String,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector
)
