package com.koin.ui.transactionhistory

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.koin.domain.model.Transaction
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import coil.compose.AsyncImage

@Composable
fun TransactionCard(
    transaction: Transaction,
    onClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick(transaction.id) },
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!transaction.coinImage.isNullOrEmpty()) {
                AsyncImage(
                    model = transaction.coinImage,
                    contentDescription = "${transaction.coinName} image",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
            } else {
                Text(
                    text = transaction.coinSymbol.uppercase(),
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp),
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${transaction.type} ${transaction.coinSymbol}",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (transaction.type == "BUY") Color.Green else Color.Red
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${"%.4f".format(transaction.quantity)} ${transaction.coinName}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "$%.2f".format(transaction.pricePerCoin * transaction.quantity),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date(transaction.timestamp)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}