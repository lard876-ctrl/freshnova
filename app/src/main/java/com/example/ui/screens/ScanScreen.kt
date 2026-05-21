package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FoodItem
import com.example.ui.theme.KitchenGreen
import com.example.ui.theme.KitchenOrangeAccent
import com.example.ui.viewmodel.BottomTab
import com.example.ui.viewmodel.FreshNovaViewModel

@Composable
fun ScanScreen(
    viewModel: FreshNovaViewModel,
    modifier: Modifier = Modifier
) {
    val scannedBill by viewModel.scannedBill.collectAsState()
    val isFlashOn by viewModel.isFlashlightOn.collectAsState()
    val showOverlay by viewModel.showScannerOverlay.collectAsState()

    var showManualAddDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Soft sleek white-green theme background
    ) {
        // Light background camera simulation view
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Bar Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { viewModel.closeQRScan() },
                    modifier = Modifier.background(Color.Black.copy(alpha = 0.05f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }

                Text(
                    text = "Scan QR Code",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                IconButton(
                    onClick = { viewModel.toggleFlashlight() },
                    modifier = Modifier.background(Color.Black.copy(alpha = 0.05f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (isFlashOn) Icons.Default.FlashlightOn else Icons.Default.FlashlightOff,
                        contentDescription = "Flashlight",
                        tint = if (isFlashOn) Color(0xFFD97706) else Color.Black
                    )
                }
            }

            Text(
                text = "Align the QR code within the frame\nto scan your bill",
                fontSize = 14.sp,
                color = Color.Black.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Immersive Camera Scan Viewfinder frame (Matches Image #2 but styled for White Theme)
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .border(2.dp, Color.Black.copy(alpha = 0.1f), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Background simulated white receipt with active scanner line overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .clickable { viewModel.startQRScanSimulation() },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "GREEN MART",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = "Thank you for shopping!",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Receipt QR icon drawing
                        Icon(
                            imageVector = Icons.Default.QrCode,
                            contentDescription = "Receipt QR",
                            tint = Color.Black,
                            modifier = Modifier.size(100.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Scan to track your items\n& expiry dates",
                            fontSize = 11.sp,
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Sliding active green laser line in camera viewfinder
                    val infiniteTransition = rememberInfiniteTransition(label = "Laser")
                    val laserOffset by infiniteTransition.animateFloat(
                        initialValue = 0.1f,
                        targetValue = 0.9f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(2000, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "LaserOffset"
                    )

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val h = size.height
                        val w = size.width
                        val y = h * laserOffset
                        drawLine(
                            color = Color(0xFF22C55E),
                            start = androidx.compose.ui.geometry.Offset(w * 0.05f, y),
                            end = androidx.compose.ui.geometry.Offset(w * 0.95f, y),
                            strokeWidth = 6f
                        )
                    }
                }

                // Four corner focus ticks
                FocusCornersOverlay()
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "⚡ Tap on the receipt card to trigger scan simulation",
                fontSize = 12.sp,
                color = KitchenGreen,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isFlashOn) "Flashlight active" else "Tap to turn on flashlight",
                fontSize = 13.sp,
                color = Color.Black.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Manual entries alternative trigger
            OutlinedButton(
                onClick = { showManualAddDialog = true },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = KitchenGreen),
                border = BorderStroke(1.5.dp, KitchenGreen.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(50.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Edit")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Type Grocery Items Manually")
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Expanded interactive sliding bottom sheet (Scanned receipt result)
        AnimatedVisibility(
            visible = scannedBill != null,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            val bill = scannedBill
            if (bill != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 520.dp),
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    ) {
                        // Header panel
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(0xFFE6F4EA), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Success check",
                                    tint = KitchenGreen
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "QR Code Scanned!",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Text(
                                    text = "We found ${bill.items.size} items in this bill",
                                    fontSize = 13.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 16.dp),
                            color = Color.LightGray.copy(alpha = 0.5f)
                        )

                        // Grocery items lazy review scroller (Matches Image #2 items perfectly)
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(bill.items) { item ->
                                ScannedItemRow(item = item)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Decision buttons block
                        Button(
                            onClick = { viewModel.saveScannedBillToInventory() },
                            colors = ButtonDefaults.buttonColors(containerColor = KitchenGreen),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                        ) {
                            Text(
                                "Save Items to Inventory",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedButton(
                            onClick = {
                                viewModel.closeQRScan()
                                showManualAddDialog = true
                            },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = KitchenGreen),
                            border = BorderStroke(1.dp, KitchenGreen.copy(alpha = 0.4f))
                        ) {
                            Text("Review & Add Custom Items", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // Overlay instructions when scan simulation hasn't been clicked
        if (scannedBill == null && !showOverlay) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Color.Black.copy(alpha = 0.4f))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2E7D32)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "👉 Press the QR bill above to simulate a camera scan decodification!",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        // Mount the standard custom Add Grocery modal
        if (showManualAddDialog) {
            AddGroceryDialog(
                onDismiss = { showManualAddDialog = false },
                onAdd = { name, quantity, category, days, location, price ->
                    viewModel.addCustomItem(name, quantity, category, days, location, price)
                    showManualAddDialog = false
                    // Auto redirect to inventory to check additions
                    viewModel.setBottomTab(BottomTab.Inventory)
                }
            )
        }
    }
}

@Composable
fun FocusCornersOverlay() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val gap = 36f
        val thick = 8f

        // Draw top left corner
        drawPath(
            path = Path().apply {
                moveTo(gap, gap + 30f)
                lineTo(gap, gap)
                lineTo(gap + 30f, gap)
            },
            color = KitchenGreen,
            style = Stroke(width = thick)
        )

        // Draw top right corner
        drawPath(
            path = Path().apply {
                moveTo(w - gap - 30f, gap)
                lineTo(w - gap, gap)
                lineTo(w - gap, gap + 30f)
            },
            color = KitchenGreen,
            style = Stroke(width = thick)
        )

        // Draw bottom left corner
        drawPath(
            path = Path().apply {
                moveTo(gap, h - gap - 30f)
                lineTo(gap, h - gap)
                lineTo(gap + 30f, h - gap)
            },
            color = KitchenGreen,
            style = Stroke(width = thick)
        )

        // Draw bottom right corner
        drawPath(
            path = Path().apply {
                moveTo(w - gap - 30f, h - gap)
                lineTo(w - gap, h - gap)
                lineTo(w - gap, h - gap - 30f)
            },
            color = KitchenGreen,
            style = Stroke(width = thick)
        )
    }
}

@Composable
fun ScannedItemRow(item: FoodItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FBF9)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1.2f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category emoji icon
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.LightGray.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = getCategoryEmoji(item.category), fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = item.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = item.quantity,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            // Expiry stats
            Column(
                modifier = Modifier.weight(0.8f),
                horizontalAlignment = Alignment.End
            ) {
                val days = getDaysBetween(System.currentTimeMillis(), item.expiryDateMillis)
                val (txtColor, txtDays) = when {
                    days < 0 -> Pair(Color.Red, "Expired")
                    days == 0 -> Pair(Color.Red, "Today")
                    days == 1 -> Pair(Color.Red, "1 day left")
                    days < 4 -> Pair(KitchenOrangeAccent, "$days days left")
                    else -> Pair(KitchenGreen, "$days days left")
                }

                Text(
                    text = "Exp: ${getExpiryFormatText(item.expiryDateMillis)}",
                    fontSize = 11.sp,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = txtDays,
                    fontSize = 11.sp,
                    color = txtColor,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Green tick check indicator
            RadioButton(
                selected = true,
                onClick = {},
                colors = RadioButtonDefaults.colors(selectedColor = KitchenGreen)
            )
        }
    }
}

// Dialog Component for adding new items manually (Reuses layout beautifully)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGroceryDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String, Int, String, Double) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Dairy") }
    var shelfLifeDays by remember { mutableStateOf("5") }
    var location by remember { mutableStateOf("Refrigerated") }
    var priceInput by remember { mutableStateOf("100") }

    val categoriesList = listOf("Dairy", "Fruits & Veggies", "Bakery", "Snacks", "Beverages", "Others")
    val locationsList = listOf("Refrigerated", "Pantry", "Freezer")

    var isCatExpanded by remember { mutableStateOf(false) }
    var isLocExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Food Item", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(androidx.compose.foundation.rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Food Name") },
                    placeholder = { Text("e.g. Milk, Apples") },
                    singleLine = true,
                    colors = textFieldGreenColors(),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Quantity") },
                    placeholder = { Text("e.g. 1 Litre, 4 pieces") },
                    singleLine = true,
                    colors = textFieldGreenColors(),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = shelfLifeDays,
                        onValueChange = { shelfLifeDays = it },
                        label = { Text("Shelf Life (Days)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = textFieldGreenColors(),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = priceInput,
                        onValueChange = { priceInput = it },
                        label = { Text("Estimated Cost (₹)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = textFieldGreenColors(),
                        modifier = Modifier.weight(1.1f)
                    )
                }

                // Category dropdown menu trigger
                Box {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        label = { Text("Category") },
                        readOnly = true,
                        colors = textFieldGreenColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isCatExpanded = true },
                        trailingIcon = {
                            Text(
                                "▼",
                                modifier = Modifier
                                    .clickable { isCatExpanded = true }
                                    .padding(8.dp)
                            )
                        }
                    )
                    DropdownMenu(
                        expanded = isCatExpanded,
                        onDismissRequest = { isCatExpanded = false }
                    ) {
                        categoriesList.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    category = cat
                                    isCatExpanded = false
                                }
                            )
                        }
                    }
                }

                // Location selector
                Box {
                    OutlinedTextField(
                        value = location,
                        onValueChange = {},
                        label = { Text("Storage Location") },
                        readOnly = true,
                        colors = textFieldGreenColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isLocExpanded = true },
                        trailingIcon = {
                            Text(
                                "▼",
                                modifier = Modifier
                                    .clickable { isLocExpanded = true }
                                    .padding(8.dp)
                            )
                        }
                    )
                    DropdownMenu(
                        expanded = isLocExpanded,
                        onDismissRequest = { isLocExpanded = false }
                    ) {
                        locationsList.forEach { loc ->
                            DropdownMenuItem(
                                text = { Text(loc) },
                                onClick = {
                                    location = loc
                                    isLocExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && quantity.isNotBlank()) {
                        val days = shelfLifeDays.toIntOrNull() ?: 5
                        val cost = priceInput.toDoubleOrNull() ?: 100.0
                        onAdd(name, quantity, category, days, location, cost)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = KitchenGreen)
            ) {
                Text("Add Item", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, colors = ButtonDefaults.textButtonColors(contentColor = Color.Gray)) {
                Text("Cancel")
            }
        }
    )
}
