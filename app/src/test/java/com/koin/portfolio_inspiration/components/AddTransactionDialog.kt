package com.base.features.portfolio.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.base.features.portfolio.data.local.entity.TransactionType
import com.base.features.portfolio.domain.model.Portfolio

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionDialog(
    portfolio: Portfolio,
    onDismiss: () -> Unit,
    onConfirm: (TransactionType, Double, Double, String?) -> Unit
) {
    var transactionType by remember { mutableStateOf(TransactionType.BUY) }
    var quantity by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Add Transaction",
                    style = MaterialTheme.typography.titleLarge
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = transactionType == TransactionType.BUY,
                        onClick = { transactionType = TransactionType.BUY }
                    )
                    Text("Buy")
                    
                    RadioButton(
                        selected = transactionType == TransactionType.SELL,
                        onClick = { transactionType = TransactionType.SELL }
                    )
                    Text("Sell")
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Quantity") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price per ${portfolio.symbol.uppercase()}") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                if (error != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text("Cancel")
                    }
                    
                    Button(
                        onClick = {
                            val quantityDouble = quantity.toDoubleOrNull()
                            val priceDouble = price.toDoubleOrNull()

                            when {
                                quantityDouble == null || quantityDouble <= 0 -> {
                                    error = "Please enter a valid quantity"
                                }
                                priceDouble == null || priceDouble <= 0 -> {
                                    error = "Please enter a valid price"
                                }
                                transactionType == TransactionType.SELL && 
                                    quantityDouble > portfolio.totalQuantity -> {
                                    error = "Cannot sell more than owned quantity"
                                }
                                else -> {
                                    onConfirm(
                                        transactionType,
                                        quantityDouble,
                                        priceDouble,
                                        notes.takeIf { it.isNotBlank() }
                                    )
                                }
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
} 