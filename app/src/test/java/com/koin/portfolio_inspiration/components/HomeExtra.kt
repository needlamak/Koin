package com.kyro.ui.theme.presentation.components.home

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

enum class BottomSheetType {
    ADD_FUNDS,
    SCANNER,
    SEND_MONEY
}

// BottomSheetScreens.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFundsBottomSheetContent(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    var selectedAmount by remember { mutableStateOf("") }
    var customAmount by remember { mutableStateOf("") }

    val configuration = LocalConfiguration.current
    val maxHeight = configuration.screenHeightDp.dp - 100.dp
    val animatedHeight by animateDpAsState(
        targetValue = if (isExpanded) maxHeight else 400.dp,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "bottom_sheet_height"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(animatedHeight)
            .animateContentSize(
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            )
    ) {

        // Header with expand/collapse button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Add Funds",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),

                )

            IconButton(
                onClick = { isExpanded = !isExpanded },
                modifier = Modifier
                    .size(32.dp)

            ) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Scrollable content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Quick amount selection
                Text(
                    text = "Quick Amount",
                    style = MaterialTheme.typography.titleMedium,

                    modifier = Modifier.padding(vertical = 8.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(listOf("$50", "$100", "$250", "$500", "$1000")) { amount ->
                        FilterChip(
                            onClick = { selectedAmount = amount },
                            label = {
                                Text(
                                    text = amount,
                                    color = if (selectedAmount == amount) Color.Black else Color.White
                                )
                            },
                            selected = selectedAmount == amount,
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = Color.White.copy(alpha = 0.1f),
                                selectedContainerColor = Color.White,
                                labelColor = Color.White,
                                selectedLabelColor = Color.Black
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selectedAmount == amount,
                                borderColor = Color.White.copy(alpha = 0.3f),
                                selectedBorderColor = Color.White
                            )
                        )
                    }
                }
            }

            item {
                // Custom amount input
                Text(
                    text = "Custom Amount",
                    style = MaterialTheme.typography.titleMedium,

                    modifier = Modifier.padding(vertical = 8.dp)
                )

                OutlinedTextField(
                    value = customAmount,
                    onValueChange = {
                        customAmount = it
                        selectedAmount = ""
                    },
                    label = { Text("Enter amount") },
                    prefix = { Text("$") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        cursorColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                // Payment methods
                Text(
                    text = "Payment Method",
                    style = MaterialTheme.typography.titleMedium,

                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    PaymentMethodCard(
                        title = "Bank Transfer",
                        subtitle = "Free • 1-3 business days",
                        icon = Icons.Default.AccountBalance
                    )
                    PaymentMethodCard(
                        title = "Debit Card",
                        subtitle = "Instant • 2.9% fee",
                        icon = Icons.Default.CreditCard
                    )
                    PaymentMethodCard(
                        title = "Apple Pay",
                        subtitle = "Instant • 2.9% fee",
                        icon = Icons.Default.Phone
                    )
                }
            }

            if (isExpanded) {
                item {
                    // Additional expanded content
                    Text(
                        text = "Recent Transactions",
                        style = MaterialTheme.typography.titleMedium,

                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        repeat(5) { index ->
                            TransactionItem(
                                title = "Added $${100 + index * 50}",
                                subtitle = "Dec ${15 + index}, 2024",
                                amount = "+$${100 + index * 50}"
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "Security & Limits",
                        style = MaterialTheme.typography.titleMedium,

                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    InfoCard(
                        title = "Daily Limit",
                        value = "$2,500 remaining"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    InfoCard(
                        title = "Weekly Limit",
                        value = "$10,000 remaining"
                    )
                }
            }

            item {
                // Continue button
                val finalAmount = customAmount.ifEmpty { selectedAmount.removePrefix("$") }
                Button(
                    onClick = {
                        if (finalAmount.isNotEmpty()) {
                            // Handle continue with amount
                            onDismiss()
                        }
                    },
                    enabled = finalAmount.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black,
                        disabledContainerColor = Color.White.copy(alpha = 0.3f),
                        disabledContentColor = Color.Black.copy(alpha = 0.6f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Continue",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerBottomSheetContent(
    onDismiss: () -> Unit,
    onScanFromGallery: () -> Unit,
    onShowMyCode: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val maxHeight = configuration.screenHeightDp.dp - 100.dp
    val animatedHeight by animateDpAsState(
        targetValue = if (isExpanded) maxHeight else 350.dp,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "scanner_sheet_height"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(animatedHeight)
            .animateContentSize(
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            )
    ) {


        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Scan QR Code",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            IconButton(
                onClick = { isExpanded = !isExpanded },
                modifier = Modifier
                    .size(32.dp)

            ) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",

                    modifier = Modifier.size(20.dp)
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Scanner preview placeholder
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Black.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = "QR Scanner",
                                tint = Color.White.copy(alpha = 0.6f),
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = "Point camera at QR code",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            item {
                // Quick actions
                Text(
                    text = "Quick Actions",
                    style = MaterialTheme.typography.titleMedium,

                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionCard(
                        title = "Gallery",
                        icon = Icons.Default.PhotoLibrary,
                        onClick = {
                            onScanFromGallery()
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionCard(
                        title = "My Code",
                        icon = Icons.Default.QrCode,
                        onClick = {
                            onShowMyCode()
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (isExpanded) {
                item {
                    Text(
                        text = "Scan History",
                        style = MaterialTheme.typography.titleMedium,

                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        repeat(3) { index ->
                            ScanHistoryItem(
                                address = "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa",
                                date = "Dec ${15 + index}, 2024",
                                type = if (index % 2 == 0) "Received" else "Sent"
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendMoneyBottomSheetContent(
    onDismiss: () -> Unit,
    onContactSelected: (String) -> Unit,
    onScanQR: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    var amount by remember { mutableStateOf("") }
    var recipient by remember { mutableStateOf("") }

    val configuration = LocalConfiguration.current
    val maxHeight = configuration.screenHeightDp.dp - 100.dp
    val animatedHeight by animateDpAsState(
        targetValue = if (isExpanded) maxHeight else 450.dp,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "send_money_sheet_height"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(animatedHeight)
            .animateContentSize(
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            )
    ) {


        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Send Money",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            IconButton(
                onClick = { isExpanded = !isExpanded },
                modifier = Modifier
                    .size(32.dp)

            ) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",

                    modifier = Modifier.size(20.dp)
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Recent contacts
                Text(
                    text = "Recent",
                    style = MaterialTheme.typography.titleMedium,

                    modifier = Modifier.padding(vertical = 8.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(5) { index ->
                        val contactName = "User ${index + 1}"
                        ContactItem(
                            name = contactName,
                            avatar = "U${index + 1}",
                            onClick = {
                                onContactSelected(contactName)
                                recipient = contactName
                            }
                        )
                    }
                }
            }

            item {
                // Amount input
                Text(
                    text = "Amount",
                    style = MaterialTheme.typography.titleMedium,

                    modifier = Modifier.padding(vertical = 8.dp)
                )

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Enter amount") },
                    prefix = { Text("$") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        cursorColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                // Recipient input
                Text(
                    text = "Send To",
                    style = MaterialTheme.typography.titleMedium,

                    modifier = Modifier.padding(vertical = 8.dp)
                )

                OutlinedTextField(
                    value = recipient,
                    onValueChange = { recipient = it },
                    label = { Text("Address or username") },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                onScanQR()
                                onDismiss()
                            }
                        ) {
                            Icon(
                                Icons.Default.QrCodeScanner,
                                contentDescription = "Scan QR",
                                tint = Color.White.copy(alpha = 0.6f)
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        cursorColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (isExpanded) {
                item {
                    Text(
                        text = "Transaction History",
                        style = MaterialTheme.typography.titleMedium,

                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        repeat(4) { index ->
                            TransactionItem(
                                title = "Sent to User ${index + 1}",
                                subtitle = "Dec ${15 + index}, 2024",
                                amount = "-$${50 + index * 25}"
                            )
                        }
                    }
                }
            }

            item {
                // Send button
                val canSend = amount.isNotEmpty() && recipient.isNotEmpty()
                Button(
                    onClick = {
                        if (canSend) {
                            // Handle send
                            onDismiss()
                        }
                    },
                    enabled = canSend,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black,
                        disabledContainerColor = Color.White.copy(alpha = 0.3f),
                        disabledContentColor = Color.Black.copy(alpha = 0.6f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Continue",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}

/// Helper Composables
@Composable
private fun PaymentMethodCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { /* Handle selection */ },
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.05f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,

                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Icon(
                Icons.Default.KeyboardArrowRight,
                contentDescription = "Select",
                tint = Color.White.copy(alpha = 0.6f)
            )
        }
    }
}


@Composable
fun QuickActionCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(100.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,

                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun ScanHistoryItem(
    address: String,
    date: String,
    type: String, // e.g., "Received", "Sent"
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = address,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "$date • $type",
                style = MaterialTheme.typography.bodySmall
            )
        }
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "View Details",
            tint = Color.White.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun ContactItem(
    name: String,
    avatar: String, // For simplicity, a single character or initials
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(
                    Color.White.copy(alpha = 0.1f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = avatar,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
fun TransactionItem(
    title: String,
    subtitle: String,
    amount: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        Text(
            text = amount,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = if (amount.startsWith("+")) Color.Green else Color.Red
        )
    }
}

@Composable
fun InfoCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.05f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            )
        }
    }
}
