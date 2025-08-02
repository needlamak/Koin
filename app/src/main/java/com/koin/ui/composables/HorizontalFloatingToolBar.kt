package com.koin.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HorizontalFloatingToolBar(
    onSellClick: () -> Unit,
    onCreatePriceAlertClick: () -> Unit,
    onBuyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBuyClick) {
                Icon(Icons.Default.AddShoppingCart, contentDescription = "Buy")
                Spacer(Modifier.width(8.dp))
                Text("Buy")
            }
            TextButton(onClick = onCreatePriceAlertClick) {
                Icon(Icons.Default.AddAlert, contentDescription = "Create Price Alert")
                Spacer(Modifier.width(8.dp))
                Text("Price Alert")
            }

            TextButton(onClick = onSellClick) {
                Icon(
                    Icons.Default.Sell,
                    contentDescription = "Sell",
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(Modifier.width(8.dp))
                Text("Sell", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}