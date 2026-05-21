package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FoodItem
import com.example.ui.theme.KitchenGreen
import com.example.ui.theme.KitchenOrangeAccent
import com.example.ui.viewmodel.BottomTab
import com.example.ui.viewmodel.FreshNovaViewModel
import java.util.Calendar

@Composable
fun HomeScreen(
    viewModel: FreshNovaViewModel,
    modifier: Modifier = Modifier
) {
    val activeItems by viewModel.activeInventory.collectAsState()
    val profile by viewModel.userProfile.collectAsState()

    val isDark = false

    // Filter items for categories dynamically using calendar-precision getDaysBetween
    val expiringSoonItems = activeItems.filter { item ->
        val days = getDaysBetween(System.currentTimeMillis(), item.expiryDateMillis)
        days in 0..2
    }

    val thisWeekItems = activeItems.filter { item ->
        val days = getDaysBetween(System.currentTimeMillis(), item.expiryDateMillis)
        days in 3..7
    }

    val systemMessage = "Let's track your food and reduce food waste."

    // Theme-driven outer panel layout parameters to match Mockup UI
    val sectionContainerRedColor = if (isDark) Color(0xFF2E1C1D) else Color(0xFFFFF5F5)
    val sectionContainerYellowColor = if (isDark) Color(0xFF2C241B) else Color(0xFFFFFBEB)
    val fontTitleRedColor = if (isDark) Color(0xFFFF8B8C) else Color(0xFFC53030)
    val fontTitleYellowColor = if (isDark) Color(0xFFFBBF24) else Color(0xFF92400E)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        // App bar / Header matching Image #2 exactly (Centered, Hamburg menu left, bell right)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = { /* Simulated Drawer Menu */ },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Navigation drawer menu link",
                    tint = Color.Black,
                    modifier = Modifier.size(28.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Hello, ${profile.name.substringBefore(" ")}! 👋",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Let's track your food and\nreduce food waste.",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 17.sp,
                    textAlign = TextAlign.Center
                )
            }

            // Notification Bell with red badge (Image #2)
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clickable { /* Notifications Alerts panel */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "System notifications alerts panel",
                    tint = Color.Black,
                    modifier = Modifier.size(28.dp)
                )
                // Distinct red alert counter badge
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(Color(0xFFD93025), CircleShape)
                        .align(Alignment.TopEnd)
                        .offset(x = 1.dp, y = (-2).dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "3",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Red "Expiring Soon" Section Box matching Mockup Layout
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF2F3)),
            border = BorderStroke(1.dp, Color(0xFFFCE8E6))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(Color(0xFFC5221F), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("!", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Expiring Soon",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFC5221F)
                        )
                    }
                    Text(
                        text = "View all \u203A",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFC5221F),
                        modifier = Modifier.clickable { viewModel.setBottomTab(BottomTab.Inventory) }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (expiringSoonItems.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No foods expiring in 2 days! \ud83c\udf89",
                            color = Color(0xFFC5221F).copy(alpha = 0.6f),
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(end = 4.dp)
                    ) {
                        items(expiringSoonItems) { item ->
                            ExpiringCard(
                                item = item,
                                onConsume = { viewModel.markAsConsumed(item) },
                                onWaste = { viewModel.markAsWasted(item) }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Yellow "This Week" Section Box matching Mockup Layout
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9ED)),
            border = BorderStroke(1.dp, Color(0xFFFEEFC3))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(Color(0xFFEA8600), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            ClockIcon(
                                color = Color.White,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "This Week",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB06000)
                        )
                    }
                    Text(
                        text = "View all \u203A",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB06000),
                        modifier = Modifier.clickable { viewModel.setBottomTab(BottomTab.Inventory) }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (thisWeekItems.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No other foods expiring this week.",
                            color = Color(0xFFB06000).copy(alpha = 0.6f),
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(end = 4.dp)
                    ) {
                        items(thisWeekItems) { item ->
                            ExpiringCard(
                                item = item,
                                onConsume = { viewModel.markAsConsumed(item) },
                                onWaste = { viewModel.markAsWasted(item) }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tip of the Day Card (Matches image #2 vector illustration beautifully)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF005A36)) // Forest deep emerald green
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1.3f),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFF059669).copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Lightbulb,
                            contentDescription = "General knowledge lightbulb indicator icon",
                            tint = Color(0xFFA7F3D0),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Tip of the Day",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Store milk in the back of the fridge where it's colder to extend its shelf life.",
                            fontSize = 12.sp,
                            color = Color(0xFFE6F4EA),
                            lineHeight = 17.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Refrigerator illustration vector overlay with custom speed/sparkle lines!
                Row(
                    modifier = Modifier.weight(0.7f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    // Sparkling lines drawn next to Refrigerator
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("\\", color = Color(0xFFA7F3D0).copy(alpha = 0.8f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("-", color = Color(0xFFA7F3D0).copy(alpha = 0.8f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("/", color = Color(0xFFA7F3D0).copy(alpha = 0.8f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Box(
                        modifier = Modifier.size(80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        RefrigeratorIllustration(
                            modifier = Modifier.size(76.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Scan QR code pill-shaped dynamic green button
        Button(
            onClick = { viewModel.setBottomTab(BottomTab.Scan) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF137333)),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(56.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = "Code Scanner camera launcher action shortcut",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Scan QR Code",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun ExpiringCard(
    item: FoodItem,
    onConsume: () -> Unit,
    onWaste: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    val isDark = false

    // Colors matched beautifully to the style guidelines
    val emojiBgColor = getPastelBgColor(item.name, item.category, isDark)
    val itemCardContainerColor = Color.White
    val itemCardTextColor = Color.Black
    val itemCardSecondaryTextColor = Color.Gray

    Box(
        modifier = modifier
            .width(136.dp)
            .height(200.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = itemCardContainerColor),
            border = BorderStroke(1.dp, Color(0xFFF1F3F4)),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Circle visual avatar container enclosing food emoji (matching Mockup aesthetic)
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(emojiBgColor, CircleShape)
                        .align(Alignment.CenterHorizontally),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = getFoodEmoji(item.name, item.category),
                        fontSize = 32.sp
                    )
                }

                // Name and Relative expiry date (Matches Image #2 precisely)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = item.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = itemCardTextColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = getRelativeExpirySubtitle(item.expiryDateMillis),
                        fontSize = 11.sp,
                        color = itemCardSecondaryTextColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Normal
                    )
                }

                // Capsule layout containing Stopwatch icon and days left label
                val daysLeft = getDaysBetween(System.currentTimeMillis(), item.expiryDateMillis)
                val badgeText = when {
                    daysLeft < 0 -> "Expired"
                    daysLeft == 0 -> "Today"
                    daysLeft == 1 -> "1 day left"
                    else -> "$daysLeft days left"
                }

                // Match capsule colors according to priority in Image #2
                val pillBgColor = if (daysLeft <= 1) {
                    Color(0xFFFFECEB) // Light red
                } else {
                    Color(0xFFFEF7E0) // Light amber/yellow
                }
                val pillTextColor = if (daysLeft <= 1) {
                    Color(0xFFC5221F) // Red text
                } else {
                    Color(0xFFB06000) // Amber/orange text
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(28.dp)
                        .background(pillBgColor, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        ClockIcon(
                            color = pillTextColor,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = badgeText,
                            color = pillTextColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Distinct, subtle floating meatball overlay trigger button for inventory actions
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(Color.Black.copy(alpha = 0.04f), CircleShape)
                    .clickable { showMenu = true },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "\u2022\u2022\u2022",
                    fontSize = 9.sp,
                    color = Color.Black.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Bold
                )
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Mark Consumed \u2705") },
                    onClick = {
                        onConsume()
                        showMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Mark Wasted \ud83d\uddd1\ufe0f") },
                    onClick = {
                        onWaste()
                        showMenu = false
                    }
                )
            }
        }
    }
}

// Draw a beautiful custom inline ClockIcon vector
@Composable
fun ClockIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val radius = size.minDimension / 2.0f
        val centerPoint = androidx.compose.ui.geometry.Offset(size.width / 2.0f, size.height / 2.0f)

        // Draw outline clock circle
        drawCircle(
            color = color,
            radius = radius - 1f,
            style = Stroke(width = 2.5f)
        )

        // Hour hand
        drawLine(
            color = color,
            start = centerPoint,
            end = androidx.compose.ui.geometry.Offset(centerPoint.x, centerPoint.y - radius * 0.45f),
            strokeWidth = 2.5f
        )

        // Minute hand
        drawLine(
            color = color,
            start = centerPoint,
            end = androidx.compose.ui.geometry.Offset(centerPoint.x + radius * 0.35f, centerPoint.y),
            strokeWidth = 2.5f
        )
    }
}

// Futuristic flat vector refrigerator graphic
@Composable
fun RefrigeratorIllustration(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        val bodyColor = Color(0xFFF1F5F9)
        val shadowColor = Color(0xFFCBD5E1)
        val handleColor = Color(0xFF94A3B8)
        val screenColor = Color(0xFF38BDF8)

        // Main body rounded frame
        drawRoundRect(
            color = bodyColor,
            topLeft = androidx.compose.ui.geometry.Offset(width * 0.12f, height * 0.04f),
            size = androidx.compose.ui.geometry.Size(width * 0.76f, height * 0.92f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f, 12f)
        )

        // Main frame outline shadow
        drawRoundRect(
            color = shadowColor,
            topLeft = androidx.compose.ui.geometry.Offset(width * 0.12f, height * 0.04f),
            size = androidx.compose.ui.geometry.Size(width * 0.76f, height * 0.92f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f, 12f),
            style = Stroke(width = 3.5f)
        )

        // Divider line between dynamic doors
        drawLine(
            color = shadowColor,
            start = androidx.compose.ui.geometry.Offset(width * 0.12f, height * 0.40f),
            end = androidx.compose.ui.geometry.Offset(width * 0.88f, height * 0.40f),
            strokeWidth = 3f
        )

        // Handles
        drawRoundRect(
            color = handleColor,
            topLeft = androidx.compose.ui.geometry.Offset(width * 0.18f, height * 0.18f),
            size = androidx.compose.ui.geometry.Size(width * 0.06f, height * 0.16f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f, 2f)
        )

        drawRoundRect(
            color = handleColor,
            topLeft = androidx.compose.ui.geometry.Offset(width * 0.18f, height * 0.46f),
            size = androidx.compose.ui.geometry.Size(width * 0.06f, height * 0.28f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f, 2f)
        )

        // Smart Display Panel screen
        drawRoundRect(
            color = screenColor,
            topLeft = androidx.compose.ui.geometry.Offset(width * 0.44f, height * 0.16f),
            size = androidx.compose.ui.geometry.Size(width * 0.32f, height * 0.16f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
        )

        // Sheen diagonal line across glass screen dial
        drawLine(
            color = Color.White.copy(alpha = 0.5f),
            start = androidx.compose.ui.geometry.Offset(width * 0.48f, height * 0.28f),
            end = androidx.compose.ui.geometry.Offset(width * 0.72f, height * 0.18f),
            strokeWidth = 2.5f
        )

        // Milk bottle on screen dial
        drawRoundRect(
            color = Color.White,
            topLeft = androidx.compose.ui.geometry.Offset(width * 0.56f, height * 0.20f),
            size = androidx.compose.ui.geometry.Size(width * 0.08f, height * 0.08f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(1.5f, 1.5f)
        )
    }
}

// Precision calendar date differential days calculator
fun getDaysBetween(startMillis: Long, endMillis: Long): Int {
    val startCal = Calendar.getInstance().apply {
        timeInMillis = startMillis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val endCal = Calendar.getInstance().apply {
        timeInMillis = endMillis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val diff = endCal.timeInMillis - startCal.timeInMillis
    return (diff / (24L * 60L * 60L * 1000L)).toInt()
}

// Smart Food Icon / Emoji mapper
fun getFoodEmoji(name: String, category: String): String {
    val lower = name.lowercase()
    return when {
        lower.contains("milk") -> "🥛"
        lower.contains("bread") || lower.contains("loaf") -> "🍞"
        lower.contains("yogurt") || lower.contains("curd") -> "🍨"
        lower.contains("egg") -> "🥚"
        lower.contains("cheese") -> "🧀"
        lower.contains("tomato") -> "🍅"
        lower.contains("lettuce") || lower.contains("cabbage") || lower.contains("spinach") -> "🥬"
        lower.contains("apple") -> "🍎"
        lower.contains("banana") -> "🍌"
        lower.contains("orange") -> "🍊"
        lower.contains("strawberry") -> "🍓"
        lower.contains("grape") -> "🍇"
        lower.contains("potato") -> "🥔"
        lower.contains("carrot") -> "🥕"
        lower.contains("chicken") -> "🍗"
        lower.contains("meat") || lower.contains("beef") -> "🥩"
        lower.contains("fish") -> "🐟"
        lower.contains("chocolate") -> "🍫"
        lower.contains("juice") || lower.contains("drink") -> "🍹"
        else -> {
            when (category) {
                "Dairy" -> "🥛"
                "Fruits & Veggies" -> "🥦"
                "Bakery" -> "🍞"
                "Snacks" -> "🍪"
                "Beverages" -> "🍹"
                else -> "🍕"
            }
        }
    }
}

// Pastels background shades mapped with the food
fun getPastelBgColor(name: String, category: String, isDark: Boolean): Color {
    val lower = name.lowercase()
    val baseColor = when {
        lower.contains("milk") -> if (isDark) Color(0xFF1E3A8A) else Color(0xFFEBF8FF) // Sky Blue
        lower.contains("bread") -> if (isDark) Color(0xFF78350F) else Color(0xFFFFFBEB) // Golden Wheat
        lower.contains("yogurt") -> if (isDark) Color(0xFF581C87) else Color(0xFFF3E8FF) // Lavender Purple
        lower.contains("egg") -> if (isDark) Color(0xFF7C2D12) else Color(0xFFFFF7ED) // Peach Apricot
        lower.contains("cheese") -> if (isDark) Color(0xFF713F12) else Color(0xFFFEF9C3) // Honey Yellow
        lower.contains("tomato") -> if (isDark) Color(0xFF7F1D1D) else Color(0xFFFEE2E2) // Coral Red
        lower.contains("lettuce") || lower.contains("spinach") || category == "Fruits & Veggies" -> if (isDark) Color(0xFF064E3B) else Color(0xFFE8FDF0) // Mint Emerald
        category == "Snacks" -> if (isDark) Color(0xFF4C1D95) else Color(0xFFEDE9FE)
        category == "Beverages" -> if (isDark) Color(0xFF164E63) else Color(0xFFE0F7FA)
        else -> if (isDark) Color(0xFF374151) else Color(0xFFF1F5F9) // Slate grey fallback
    }
    return baseColor
}

fun getRelativeExpirySubtitle(timeMillis: Long): String {
    val daysLeft = getDaysBetween(System.currentTimeMillis(), timeMillis)
    return when {
        daysLeft < 0 -> "Expired"
        daysLeft == 0 -> "Expires today"
        daysLeft == 1 -> "Expires tomorrow"
        else -> "Expires in $daysLeft days"
    }
}

fun getExpiryFormatText(timeMillis: Long): String {
    val cal = Calendar.getInstance().apply { timeInMillis = timeMillis }
    val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    return "${cal.get(Calendar.DAY_OF_MONTH)} ${months[cal.get(Calendar.MONTH)]} ${cal.get(Calendar.YEAR)}"
}

fun getCategoryEmoji(category: String): String {
    return when (category) {
        "Dairy" -> "🥛"
        "Fruits & Veggies" -> "🥦"
        "Bakery" -> "🍞"
        "Snacks" -> "🍪"
        "Beverages" -> "🍹"
        else -> "🍕"
    }
}
