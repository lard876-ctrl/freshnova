package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FoodItem
import com.example.ui.theme.KitchenGreen
import com.example.ui.theme.KitchenOrangeAccent
import com.example.ui.viewmodel.FreshNovaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    viewModel: FreshNovaViewModel,
    modifier: Modifier = Modifier
) {
    val activeItems by viewModel.activeInventory.collectAsState()
    val consumedItems by viewModel.historicalConsumed.collectAsState()
    val wastedItems by viewModel.historicalWasted.collectAsState()

    val currentSortBy by viewModel.inventorySortBy.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilterTab by remember { mutableStateOf("All Items") } // "All Items", "Expiring Soon", "Expired", "Used"
    var showAddDialog by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }

    // State of tapped item to show actions
    var selectedItemForDetail by remember { mutableStateOf<FoodItem?>(null) }

    // Live Metrics Calculations based on REAL Room Data
    val totalItemsCount = activeItems.size
    val expiringTodayCount = activeItems.count { getDaysBetween(System.currentTimeMillis(), it.expiryDateMillis) == 0 }
    val expiringThisWeekCount = activeItems.count { getDaysBetween(System.currentTimeMillis(), it.expiryDateMillis) in 1..7 }
    val freshItemsCount = activeItems.count { getDaysBetween(System.currentTimeMillis(), it.expiryDateMillis) > 7 }

    // Determine target list to render based on top tabs
    val sourceList = when (selectedFilterTab) {
        "Expiring Soon" -> activeItems.filter { getDaysBetween(System.currentTimeMillis(), it.expiryDateMillis) in 0..7 }
        "Expired" -> activeItems.filter { getDaysBetween(System.currentTimeMillis(), it.expiryDateMillis) < 0 }
        "Used" -> consumedItems
        else -> activeItems
    }

    // Filter by search query
    val filteredList = sourceList.filter {
        it.name.contains(searchQuery, ignoreCase = true) || it.category.contains(searchQuery, ignoreCase = true)
    }

    // Sort accordingly
    val sortedList = when (currentSortBy) {
        "Alphabetical" -> filteredList.sortedBy { it.name }
        "Date Added" -> filteredList.sortedByDescending { it.insertedDateMillis }
        else -> filteredList.sortedBy { it.expiryDateMillis } // Sorting by Expiry Date
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding(),
        floatingActionButton = {
            if (selectedFilterTab != "Used") {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = KitchenGreen,
                    contentColor = Color.White,
                    shape = CircleShape,
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Food")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header Title Matching Image #3
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Inventory",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "All your items in one place",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }

            // Search Bar & Filter Button row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search ingredients, products...", fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Gray) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = textFieldGreenColors(),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .border(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                        .clickable { showSortMenu = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filter",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                    
                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Sort by Expiry Date 📅") },
                            onClick = {
                                viewModel.setInventorySortBy("Expiry Date")
                                showSortMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Sort Alphabetically 🔤") },
                            onClick = {
                                viewModel.setInventorySortBy("Alphabetical")
                                showSortMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Sort by Date Added 🆕") },
                            onClick = {
                                viewModel.setInventorySortBy("Date Added")
                                showSortMenu = false
                            }
                        )
                    }
                }
            }

            // Top Filter Tabs (Matches image #3 exactly)
            val tabs = listOf("All Items", "Expiring Soon", "Expired", "Used")
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(tabs) { tab ->
                    val isSelected = selectedFilterTab == tab
                    val tabIcon = when (tab) {
                        "Expiring Soon" -> "⚠️"
                        "Expired" -> "🗑️"
                        "Used" -> "✅"
                        else -> "🛍️"
                    }

                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilterTab = tab },
                        label = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(tabIcon, modifier = Modifier.padding(end = 4.dp))
                                Text(tab)
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = KitchenGreen.copy(alpha = 0.12f),
                            selectedLabelColor = KitchenGreen,
                            selectedTrailingIconColor = KitchenGreen
                        ),
                        border = if (isSelected) FilterChipDefaults.filterChipBorder(enabled = true, selected = true, borderColor = KitchenGreen) else FilterChipDefaults.filterChipBorder(enabled = true, selected = false, borderColor = Color.LightGray.copy(alpha = 0.5f))
                    )
                }
            }

            // Metrics Summary Grid (Matches image #3 exactly: Total, Exp today, Exp week, Fresh)
            if (selectedFilterTab == "All Items") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        count = totalItemsCount.toString(),
                        label = "Total Items",
                        color = Color(0xFFE6F4EA),
                        textColor = Color(0xFF137333),
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        count = expiringTodayCount.toString(),
                        label = "Expiring Today",
                        color = Color(0xFFFCE8E6),
                        textColor = Color(0xFFC5221F),
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        count = expiringThisWeekCount.toString(),
                        label = "Expiring Week",
                        color = Color(0xFFFEF7E0),
                        textColor = Color(0xFFB06000),
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        count = freshItemsCount.toString(),
                        label = "Fresh Items",
                        color = Color(0xFFE8F0FE),
                        textColor = Color(0xFF1A73E8),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Sort indicator subtitle Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your Items",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { showSortMenu = true }
                ) {
                    Text(
                        text = "Sort by: $currentSortBy",
                        fontSize = 13.sp,
                        color = KitchenGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Dropdown",
                        tint = KitchenGreen,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Central Items List (LazyColumn of Food Cards, matching mockup Image #3)
            if (sortedList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🥬", fontSize = 64.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No matching food items!",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Try adding items or changing your search filter.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(sortedList) { foodItem ->
                        FoodItemCard(
                            foodItem = foodItem,
                            onClick = { selectedItemForDetail = foodItem }
                        )
                    }
                }
            }
        }

        // Action details panel (Bottom sheet context menu on tapping an item)
        if (selectedItemForDetail != null) {
            val item = selectedItemForDetail!!
            val days = getDaysBetween(System.currentTimeMillis(), item.expiryDateMillis)

            AlertDialog(
                onDismissRequest = { selectedItemForDetail = null },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(getCategoryEmoji(item.category), fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(item.name, fontWeight = FontWeight.Bold)
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Quantity: ${item.quantity}", fontWeight = FontWeight.SemiBold)
                        Text("Category: ${item.category}")
                        Text("Expiry Date: ${getExpiryFormatText(item.expiryDateMillis)}")
                        Text("Storage Zone: ${item.storageLocation}")
                        Text("Est. Cost: ₹${item.estimatedCost.toInt()}")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (days < 0) "Status: Expired 🚨" else "Status: $days days remaining",
                            fontWeight = FontWeight.Bold,
                            color = if (days in 0..2) Color.Red else if (days in 3..7) KitchenOrangeAccent else KitchenGreen
                        )
                    }
                },
                confirmButton = {
                    if (!item.isConsumed && !item.isWasted) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilledTonalButton(
                                onClick = {
                                    viewModel.markAsConsumed(item)
                                    selectedItemForDetail = null
                                },
                                colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color(0xFFE6F4EA), contentColor = KitchenGreen)
                            ) {
                                Text("Consumed")
                            }
                            FilledTonalButton(
                                onClick = {
                                    viewModel.markAsWasted(item)
                                    selectedItemForDetail = null
                                },
                                colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color(0xFFFCE8E6), contentColor = Color.Red)
                            ) {
                                Text("Wasted")
                            }
                        }
                    } else {
                        Button(onClick = { selectedItemForDetail = null }, colors = ButtonDefaults.buttonColors(containerColor = KitchenGreen)) {
                            Text("OK")
                        }
                    }
                },
                dismissButton = {
                    IconButton(
                        onClick = {
                            viewModel.deleteItem(item)
                            selectedItemForDetail = null
                        },
                        colors = IconButtonDefaults.iconButtonColors(contentColor = Color.Red)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete item")
                    }
                }
            )
        }

        // Add Dialog Mount
        if (showAddDialog) {
            AddGroceryDialog(
                onDismiss = { showAddDialog = false },
                onAdd = { name, quantity, category, days, location, price ->
                    viewModel.addCustomItem(name, quantity, category, days, location, price)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun MetricCard(
    count: String,
    label: String,
    color: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(72.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = textColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = textColor.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                lineHeight = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun FoodItemCard(
    foodItem: FoodItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val daysLeft = getDaysBetween(System.currentTimeMillis(), foodItem.expiryDateMillis)

    val (badgeText, badgeColor, textColor) = when {
        foodItem.isConsumed -> Triple("Consumed", Color(0xFFE6F4EA), Color(0xFF137333))
        foodItem.isWasted -> Triple("Wasted", Color(0xFFFCE8E6), Color(0xFFC5221F))
        daysLeft < 0 -> Triple("Expired", Color(0xFFFCE8E6), Color(0xFFC5221F))
        daysLeft == 0 -> Triple("Expiring Today", Color(0xFFFFECEB), Color(0xFFD63031))
        daysLeft <= 2 -> Triple("Expiring Soon", Color(0xFFFFECEB), Color(0xFFD63031))
        daysLeft <= 7 -> Triple("This Week", Color(0xFFFEF7E0), Color(0xFFB06000))
        else -> Triple("Fresh", Color(0xFFE8F0FE), Color(0xFF1A73E8))
    }

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1.3f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Emoji
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(KitchenGreen.copy(alpha = 0.05f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = getCategoryEmoji(foodItem.category), fontSize = 22.sp)
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = foodItem.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = foodItem.quantity,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )

                        // Storage Location pill
                        val isRefrig = foodItem.storageLocation == "Refrigerated"
                        Box(
                            modifier = Modifier
                                .background(
                                    if (isRefrig) Color(0xFFE8F0FE) else Color(0xFFF1F3F4),
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isRefrig) "❄️ Refrigerated" else "🧺 Pantry",
                                fontSize = 10.sp,
                                color = if (isRefrig) Color(0xFF1A73E8) else Color.DarkGray,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Expiry timeline statistics
            Column(
                modifier = Modifier.weight(0.7f),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "Expires ${getExpiryFormatText(foodItem.expiryDateMillis)}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                
                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Quick state badge
                    Box(
                        modifier = Modifier
                            .background(badgeColor, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = badgeText,
                            fontSize = 10.sp,
                            color = textColor,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Details",
                        tint = Color.LightGray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
