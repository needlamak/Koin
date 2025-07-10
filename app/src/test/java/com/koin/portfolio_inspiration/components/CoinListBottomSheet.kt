package com.base.features.portfolio.presentation.components

import android.R.attr.scaleX
import android.R.attr.scaleY
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.base.features.portfolio.domain.model.Portfolio
import kotlinx.coroutines.delay
import java.text.NumberFormat
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun CoinListBottomSheet(
    portfolios: List<Portfolio>,
    onCoinClick: (Portfolio) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Your Holdings",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(portfolios) { portfolio ->
                HoldingItem(
                    portfolio = portfolio,
                    onClick = { onCoinClick(portfolio) }
                )
            }
            item {
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun AnimatedCoinListItem(
    portfolio: Portfolio,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { offset ->
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onLongPress = { onLongPress() }
                )
            }
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = "https://assets.coingecko.com/coins/images/1/small/bitcoin.png?1547033579",
                    contentDescription = portfolio.name,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
                Column {
                    Text(
                        text = portfolio.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = portfolio.symbol.uppercase(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = NumberFormat.getCurrencyInstance().format(portfolio.totalValue),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (portfolio.profitLoss >= 0)
                            Icons.Default.TrendingUp
                        else
                            Icons.Default.TrendingDown,
                        contentDescription = null,
                        tint = if (portfolio.profitLoss >= 0)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${String.format("%.2f", portfolio.profitLossPercentage)}%",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (portfolio.profitLoss >= 0)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}


//@Composable
//private fun CoinListItem(
//    portfolio: Portfolio,
//    onClick: () -> Unit
//) {
//    val formatter = java.text.NumberFormat.getCurrencyInstance(java.util.Locale.US)
//
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 4.dp),
//        shape = RoundedCornerShape(12.dp),
//        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
//        onClick = onClick
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            // Coin icon
//            AsyncImage(
//                model = "https://raw.githubusercontent.com/spothq/cryptocurrency-icons/master/128/color/${portfolio.symbol.lowercase()}.png",
//                contentDescription = "Coin icon",
//                modifier = Modifier.size(40.dp)
//            )
//
//            Spacer(modifier = Modifier.width(16.dp))
//
//            // Coin info
//            Column(modifier = Modifier.weight(1f)) {
//                Text(
//                    text = portfolio.name,
//                    style = MaterialTheme.typography.bodyLarge,
//                    fontWeight = FontWeight.Bold
//                )
//                Text(
//                    text = portfolio.symbol.uppercase(),
//                    style = MaterialTheme.typography.bodyMedium,
//                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
//                )
//            }
//
//            // Price info
//            Column(horizontalAlignment = Alignment.End) {
//                Text(
//                    text = formatter.format(portfolio.totalValue),
//                    style = MaterialTheme.typography.bodyLarge,
//                    fontWeight = FontWeight.Bold
//                )
//                Text(
//                    text = "${String.format("%.2f", portfolio.profitLossPercentage)}%",
//                    style = MaterialTheme.typography.bodyMedium,
//                    color = if (portfolio.profitLossPercentage >= 0)
//                        MaterialTheme.colorScheme.primary
//                    else
//                        MaterialTheme.colorScheme.error
//                )
//            }
//        }
//    }
//}