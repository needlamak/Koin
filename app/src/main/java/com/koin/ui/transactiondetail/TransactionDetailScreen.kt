package com.koin.ui.transactiondetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.koin.util.NetworkMonitor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.remember
import com.koin.ui.coinlist.DataEmptyState
import com.koin.ui.coinlist.ErrorEmptyState
import com.koin.ui.coinlist.LoadingState
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailScreen(
    navController: NavController,
    viewModel: TransactionDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val networkMonitor = remember { NetworkMonitor(context) }
    val isNetworkAvailable by networkMonitor.isNetworkAvailable.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Transaction Details") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            when {
                uiState.isLoading -> {
                    LoadingState()
                }
                uiState.error != null -> {
                    ErrorEmptyState(
                        error = uiState.error!!,
                        isNetworkAvailable = isNetworkAvailable,
                        onRetry = { /* Implement retry logic if needed */ }
                    )
                }
                uiState.transaction == null -> {
                    DataEmptyState(
                        isNetworkAvailable = isNetworkAvailable,
                        onRetry = { /* Implement retry logic if needed */ }
                    )
                }
                else -> {
                    val transaction = uiState.transaction!!
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (!transaction.coinImage.isNullOrEmpty()) {
                            AsyncImage(
                                model = transaction.coinImage,
                                contentDescription = "${transaction.coinName} image",
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                            )
                        } else {
                            Text(
                                text = transaction.coinSymbol.uppercase(),
                                style = MaterialTheme.typography.headlineSmall.copy(fontSize = 32.sp),
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                                    .padding(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Type: ${transaction.type}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Coin: ${transaction.coinName} (${transaction.coinSymbol})",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Quantity: ${ "%.4f".format(transaction.quantity)}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Price per Coin: $%.2f".format(transaction.pricePerCoin),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Total Price: $%.2f".format(transaction.quantity * transaction.pricePerCoin),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Transaction Fee: $%.2f".format(transaction.transactionFee),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Timestamp: ${SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault()).format(Date(transaction.timestamp))}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}
