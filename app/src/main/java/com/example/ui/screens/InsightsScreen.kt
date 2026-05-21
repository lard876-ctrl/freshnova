package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FoodItem
import com.example.ui.theme.KitchenGreen
import com.example.ui.theme.KitchenOrangeAccent
import com.example.ui.viewmodel.AIRecommendationsState
import com.example.ui.viewmodel.BottomTab
import com.example.ui.viewmodel.FreshNovaViewModel

@Composable
fun InsightsScreen(
    viewModel: FreshNovaViewModel,
    modifier: Modifier = Modifier
) {
    val activeItems by viewModel.activeInventory.collectAsState()
    val consumedItems by viewModel.historicalConsumed.collectAsState()
    val wastedItems by viewModel.historicalWasted.collectAsState()
    val profile by viewModel.userProfile.collectAsState()

    val aiState by viewModel.aiState.collectAsState()

    var showTipsDialog by remember { mutableStateOf(false) }

    // Dynamic calculations based on DB
    val totalWastedCost = wastedItems.sumOf { it.estimatedCost }
    val displayWasted = if (totalWastedCost > 0.0) totalWastedCost else 1248.0
    val displaySaved = profile.moneySaved
    val displaySavedCount = profile.itemsSaved

    val categoryWastes = listOf(
        CategoryWaste("Dairy", 398.0, 0.32f, Color(0xFF3B82F6), "🥛"),
        CategoryWaste("Fruits & Veggies", 299.0, 0.24f, Color(0xFF10B981), "🥦"),
        CategoryWaste("Bakery", 224.0, 0.18f, Color(0xFFF59E0B), "🍞"),
        CategoryWaste("Snacks", 174.0, 0.14f, Color(0xFFEF4444), "🍪"),
        CategoryWaste("Beverages", 100.0, 0.08f, Color(0xFF8B5CF6), "🍹"),
        CategoryWaste("Others", 53.0, 0.04f, Color(0xFFEC4899), "🍕")
    )

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(scrollState)
    ) {
        // Stats Title Matching Image #4 precisely
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    InsightsHeaderIcon()
                    Text(
                        text = "Insights",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Track your impact and build better habits",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }
            
            ThisMonthDropdown()
        }

        // Section Overview Label matching mockup
        Text(
            text = "Overview",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
        )

        // Overview Cards row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OverviewStatCard(
                title = "Food Wasted",
                amount = "₹${displayWasted.toInt()}",
                trendText = "12%",
                icon = "🗑️",
                isPositiveTrend = false,
                containerColor = Color(0xFFFFF2F2),
                accentColor = Color(0xFFC5221F),
                modifier = Modifier.weight(1f)
            )

            OverviewStatCard(
                title = "Money Saved",
                amount = "₹${displaySaved.toInt()}",
                trendText = "18%",
                icon = "💰",
                isPositiveTrend = true,
                containerColor = Color(0xFFE6F4EA),
                accentColor = Color(0xFF137333),
                modifier = Modifier.weight(1f)
            )

            OverviewStatCard(
                title = "Items Saved",
                amount = "$displaySavedCount",
                trendText = "14%",
                icon = "🌱",
                isPositiveTrend = true,
                containerColor = Color(0xFFE8F0FE),
                accentColor = Color(0xFF1A73E8),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Chef Nova-AI Smart Recipes recommendation block (LEGENDRY COMPONENT - White Theme Style!)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE6F4EA)), // Beautiful fresh mint white background
            border = BorderStroke(1.5.dp, KitchenGreen.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(KitchenGreen.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "AI Assistant",
                                tint = KitchenGreen
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Chef Nova-AI Chef",
                            color = Color.Black,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(KitchenGreen, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text("Beta", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Chef Nova scans your active pantry items is real-time and recommends delicious meals to cook before ingredients expire!",
                    color = Color.DarkGray,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // AI Recommendations State Machine
                when (aiState) {
                    is AIRecommendationsState.Idle -> {
                        Button(
                            onClick = { viewModel.loadAIFoodRecommendations() },
                            colors = ButtonDefaults.buttonColors(containerColor = KitchenGreen),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = "", tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Generate Zero-Waste Recipes", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                    is AIRecommendationsState.Loading -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = KitchenGreen, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Chef Nova is cooking up recipes...", color = Color.DarkGray, fontSize = 12.sp)
                        }
                    }
                    is AIRecommendationsState.Success -> {
                        val text = (aiState as AIRecommendationsState.Success).recipes
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 240.dp)
                                .background(Color.Black.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                                .verticalScroll(rememberScrollState())
                                .padding(12.dp)
                        ) {
                            Text(
                                text = text,
                                color = Color.Black,
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TextButton(
                                onClick = { viewModel.loadAIFoodRecommendations() },
                                colors = ButtonDefaults.textButtonColors(contentColor = KitchenGreen)
                            ) {
                                Text("🔄 Regenerate", fontSize = 12.sp)
                            }
                            TextButton(
                                onClick = { viewModel.setBottomTab(BottomTab.Inventory) },
                                colors = ButtonDefaults.textButtonColors(contentColor = Color.Gray)
                            ) {
                                Text("Check Pantry", fontSize = 12.sp)
                            }
                        }
                    }
                    is AIRecommendationsState.Error -> {
                        Text(
                            text = (aiState as AIRecommendationsState.Error).message,
                            color = Color.Red,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.loadAIFoodRecommendations() },
                            colors = ButtonDefaults.buttonColors(containerColor = KitchenGreen)
                        ) {
                            Text("Retry Suggestions")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Wastage Breakdown Section with Custom Donut Canvas Chart (Matches Image #4 with Inline Percentage Labels!)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.2.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Wastage Breakdown",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    var breakdownDropdownExpanded by remember { mutableStateOf(false) }
                    var selectedBreakdownBy by remember { mutableStateOf("Category") }
                    
                    Box {
                        Text(
                            text = "By $selectedBreakdownBy ∨",
                            fontSize = 12.sp,
                            color = KitchenGreen,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { breakdownDropdownExpanded = true }
                        )
                        DropdownMenu(
                            expanded = breakdownDropdownExpanded,
                            onDismissRequest = { breakdownDropdownExpanded = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            listOf("Category", "Pantry Area", "Week", "Shelf Life").forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option, fontSize = 13.sp) },
                                    onClick = {
                                        selectedBreakdownBy = option
                                        breakdownDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Chart row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1. Custom Donut drawing Canvas with text labels inside slices!
                    Box(
                        modifier = Modifier
                            .size(136.dp)
                            .weight(1.0f),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val strokeWidthVal = 40f
                            var currentAngle = -90f
                            val sizeBox = Size(size.width - strokeWidthVal, size.height - strokeWidthVal)
                            val offsetStart = strokeWidthVal / 2
                            val centerX = size.width / 2f
                            val centerY = size.height / 2f
                            val textRadius = (size.width - strokeWidthVal) / 2f

                            categoryWastes.forEach { entry ->
                                val sweep = entry.percent * 360f
                                drawArc(
                                    color = entry.color,
                                    startAngle = currentAngle,
                                    sweepAngle = sweep,
                                    useCenter = false,
                                    style = Stroke(width = strokeWidthVal),
                                    topLeft = androidx.compose.ui.geometry.Offset(offsetStart, offsetStart),
                                    size = sizeBox
                                )
                                
                                // Draw percentage text inside the donut slice segment
                                if (entry.percent > 0.05f) { // only for slices large enough to hold text
                                    val midAngle = currentAngle + sweep / 2f
                                    val midAngleRad = Math.toRadians(midAngle.toDouble())
                                    val tx = centerX + textRadius * Math.cos(midAngleRad).toFloat()
                                    val ty = centerY + textRadius * Math.sin(midAngleRad).toFloat()
                                    
                                    val percentText = "${(entry.percent * 100).toInt()}%"
                                    val textPaint = android.graphics.Paint().apply {
                                        color = android.graphics.Color.WHITE
                                        textSize = 21f
                                        textAlign = android.graphics.Paint.Align.CENTER
                                        typeface = android.graphics.Typeface.create(
                                            android.graphics.Typeface.DEFAULT,
                                            android.graphics.Typeface.BOLD
                                        )
                                    }
                                    // ty + 7f adjusts baseline slightly downwards for visual center alignment
                                    drawContext.canvas.nativeCanvas.drawText(
                                        percentText,
                                        tx,
                                        ty + 7f,
                                        textPaint
                                    )
                                }
                                
                                currentAngle += sweep
                            }
                        }

                        // Inner circular metrics count label
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Total",
                                fontSize = 11.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "₹${displayWasted.toInt()}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // 2. Legend Items lists matching Image #4 exactly
                    Column(
                        modifier = Modifier.weight(1.23f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categoryWastes.forEach { entry ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(entry.color, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${entry.emoji}  ${entry.name}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.DarkGray
                                    )
                                }
                                Text(
                                    text = "₹${entry.cost.toInt()} (${(entry.percent * 100).toInt()}%)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // MOST WASTED ITEMS Segment (Matches image #4 perfectly: Milk, Bread, Yogurt, Tomatoes, Bananas)
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Most Wasted Items",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Text(
                    text = "View all >",
                    color = KitchenGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.clickable { /* View all log */ }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Carousel list
            val mostWastedStatic = listOf(
                MostWastedItem("Milk", "🥛", 456, 8),
                MostWastedItem("Bread", "🍞", 289, 6),
                MostWastedItem("Yogurt", "🥛", 210, 5),
                MostWastedItem("Tomatoes", "🍅", 152, 4),
                MostWastedItem("Bananas", "🍌", 141, 4)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 6.dp)
            ) {
                items(mostWastedStatic) { card ->
                    Card(
                        modifier = Modifier
                            .width(115.dp)
                            .height(152.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Food visual rounded container
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .background(Color(0xFFF1F3F4), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(card.emoji, fontSize = 26.sp)
                            }
                            
                            Text(
                                text = card.name,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                textAlign = TextAlign.Center
                            )
                            
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(1.dp)) {
                                Text(
                                    text = "₹${card.cost}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFC5221F)
                                )
                                Text(
                                    text = "${card.times} times",
                                    fontSize = 9.sp,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Progress motivational card matching mockup with beautiful Outlined indicator button!
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE6F4EA)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            border = BorderStroke(1.dp, Color(0xFFCEEAD6))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1.3f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFFCEEAD6), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🌱", fontSize = 18.sp)
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "You're on the right track! 🎉",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF137333),
                            fontSize = 13.sp
                        )
                        Text(
                            text = "You wasted 12% less food compared to last month. Keep it up and save more!",
                            color = Color(0xFF137333).copy(alpha = 0.82f),
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                OutlinedButton(
                    onClick = { showTipsDialog = true },
                    border = BorderStroke(1.5.dp, Color(0xFF137333)),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF137333)),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.weight(0.7f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("💡", fontSize = 11.sp)
                        Text("View Tips", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // WASTAGE TREND GRAPH Section (Nov-Apr, matches mockup Image #4 perfectly)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.2.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Wastage Trend",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = "vs last 6 months",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }

                    Text(
                        text = "View Details >",
                        fontSize = 11.sp,
                        color = KitchenGreen,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { /* Trend history detail details */ }
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Custom Curvature line-graph Canvas plotted on screen (Nov to Apr with values & months!)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(188.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val h = size.height
                        val w = size.width
                        val paddingLeftRight = 60f
                        val graphW = w - paddingLeftRight * 2f
                        
                        val points = listOf(
                            PlotPoint("Nov", 1620f, 0f),
                            PlotPoint("Dec", 1810f, 1f),
                            PlotPoint("Jan", 1540f, 2f),
                            PlotPoint("Feb", 1370f, 3f),
                            PlotPoint("Mar", 1420f, 4f),
                            PlotPoint("Apr", 1248f, 5f)
                        )

                        val maxVal = 2000f
                        val minVal = 1000f
                        val diffVal = maxVal - minVal
                        val spacing = graphW / 5f

                        val path = Path()
                        val fillPath = Path()

                        // 1. Precalculate coordinates so spline runs perfectly smoothly
                        val coords = points.mapIndexed { i, p ->
                            val cx = paddingLeftRight + i * spacing
                            val scaledYFraction = (p.value - minVal) / diffVal
                            // Scale down height slightly to keep text and labels from clipping (0.45 height multiplier + 0.25 vertical offset)
                            val cy = h * (1f - (scaledYFraction * 0.45f + 0.25f))
                            androidx.compose.ui.geometry.Offset(cx, cy)
                        }

                        // 2. Build curve path
                        coords.forEachIndexed { i, pt ->
                            if (i == 0) {
                                path.moveTo(pt.x, pt.y)
                                fillPath.moveTo(pt.x, h - 35f) // offset by 35f for bottom label text area
                                fillPath.lineTo(pt.x, pt.y)
                            } else {
                                val prev = coords[i - 1]
                                val contr1X = prev.x + (pt.x - prev.x) / 2f
                                val contr1Y = prev.y
                                val contr2X = prev.x + (pt.x - prev.x) / 2f
                                val contr2Y = pt.y
                                
                                path.cubicTo(contr1X, contr1Y, contr2X, contr2Y, pt.x, pt.y)
                                fillPath.cubicTo(contr1X, contr1Y, contr2X, contr2Y, pt.x, pt.y)
                            }

                            if (i == coords.size - 1) {
                                fillPath.lineTo(pt.x, h - 35f)
                                fillPath.close()
                            }
                        }

                        // 3. Draw gradient under path
                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    KitchenGreen.copy(alpha = 0.16f),
                                    Color.White.copy(alpha = 0.0f)
                                )
                            )
                        )

                        // 4. Draw curve stroke line
                        drawPath(
                            path = path,
                            color = KitchenGreen,
                            style = Stroke(width = 6f)
                        )

                        // 5. Draw plotted markers, values, and month labels
                        coords.forEachIndexed { i, pt ->
                            val p = points[i]
                            
                            // Plotted vertex marker
                            drawCircle(
                                color = KitchenGreen,
                                radius = 10f,
                                center = pt
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 5f,
                                center = pt
                            )

                            // Cost Value Label (above point)
                            val valueText = "₹${p.value.toInt()}"
                            val valuePaint = android.graphics.Paint().apply {
                                color = android.graphics.Color.BLACK
                                textSize = 25f
                                textAlign = android.graphics.Paint.Align.CENTER
                                typeface = android.graphics.Typeface.create(
                                    android.graphics.Typeface.DEFAULT,
                                    android.graphics.Typeface.BOLD
                                )
                            }
                            drawContext.canvas.nativeCanvas.drawText(
                                valueText,
                                pt.x,
                                pt.y - 18f,
                                valuePaint
                            )

                            // Month Label (at bottom of canvas aligned with node)
                            val monthPaint = android.graphics.Paint().apply {
                                color = android.graphics.Color.GRAY
                                textSize = 25f
                                textAlign = android.graphics.Paint.Align.CENTER
                                typeface = android.graphics.Typeface.create(
                                    android.graphics.Typeface.DEFAULT,
                                    android.graphics.Typeface.BOLD
                                )
                            }
                            drawContext.canvas.nativeCanvas.drawText(
                                p.label,
                                pt.x,
                                h - 10f,
                                monthPaint
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(100.dp))
    }

    // Interactive popup tips to ensure rich UI
    if (showTipsDialog) {
        AlertDialog(
            onDismissRequest = { showTipsDialog = false },
            title = { Text("Kitchen Waste Hacks 💡", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("1. **Freeze near-expiry items**: Veggies, berries, meat, and grated cheese can easily be stored in the freezer for months.")
                    Text("2. **First In, First Out**: Keep older items at the front of your shelves so they get eaten first.")
                    Text("3. **Stock cooking**: Simmer leftover vegetables, carcasses, and herbs to create delicious soups and stocks.")
                }
            },
            confirmButton = {
                Button(onClick = { showTipsDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = KitchenGreen)) {
                    Text("Awesome!")
                }
            }
        )
    }
}

@Composable
fun OverviewStatCard(
    title: String,
    amount: String,
    trendText: String,
    icon: String,
    isPositiveTrend: Boolean,
    containerColor: Color,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(136.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Icon Badge and Title next to it
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(accentColor.copy(alpha = 0.12f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = icon, fontSize = 12.sp)
                }
                Text(
                    text = title,
                    fontSize = 11.sp,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }

            // Big Bold colored value
            Text(
                text = amount,
                fontSize = 19.sp,
                fontWeight = FontWeight.Black,
                color = accentColor,
                maxLines = 1
            )

            // Trend capsule badge matching Image #4!
            Box(
                modifier = Modifier
                    .background(accentColor.copy(alpha = 0.10f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    // Small Arrow indicator
                    Text(
                        text = "↑",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                    Text(
                        text = trendText,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                    Text(
                        text = " vs last month",
                        fontSize = 8.sp,
                        color = accentColor.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun InsightsHeaderIcon() {
    Row(
        modifier = Modifier.size(20.dp, 20.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        Box(
            modifier = Modifier
                .width(5.dp)
                .height(9.dp)
                .background(Color(0xFF34A853), RoundedCornerShape(1.5.dp))
        )
        Box(
            modifier = Modifier
                .width(5.dp)
                .height(18.dp)
                .background(Color(0xFF34A853), RoundedCornerShape(1.5.dp))
        )
        Box(
            modifier = Modifier
                .width(5.dp)
                .height(13.dp)
                .background(Color(0xFF34A853), RoundedCornerShape(1.5.dp))
        )
    }
}

@Composable
fun ThisMonthDropdown() {
    var expanded by remember { mutableStateOf(false) }
    var selectedPeriod by remember { mutableStateOf("This Month") }
    
    Box {
        Row(
            modifier = Modifier
                .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                .clickable { expanded = true }
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text("📅", fontSize = 11.sp)
            Text(
                text = selectedPeriod,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )
            Text("▼", fontSize = 7.sp, color = Color.Gray)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White)
        ) {
            listOf("This Week", "This Month", "Last 3 Months", "Yearly").forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, fontSize = 13.sp) },
                    onClick = {
                        selectedPeriod = option
                        expanded = false
                    }
                )
            }
        }
    }
}

data class CategoryWaste(
    val name: String,
    val cost: Double,
    val percent: Float,
    val color: Color,
    val emoji: String
)

data class MostWastedItem(
    val name: String,
    val emoji: String,
    val cost: Int,
    val times: Int
)

data class PlotPoint(
    val label: String,
    val value: Float,
    val fraction: Float
)
