package com.koin.ui.coinlist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.koin.domain.model.Coin

import coil.compose.AsyncImage
import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign

@Composable
fun BuyBottomSheet(
    coin: Coin,
    onConfirm: (Double) -> Unit
) {
    var amount by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (coin.imageUrl.isNotEmpty()) {
            AsyncImage(
                model = coin.imageUrl,
                contentDescription = "${coin.name} image",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
            )
        } else {
            Text(
                text = coin.symbol.uppercase(),
                style = MaterialTheme.typography.headlineSmall.copy(fontSize = 24.sp),
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(16.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Buy ${coin.name}", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onConfirm(amount.toDouble()) }) {
            Text("Confirm Purchase")
        }
    }
}