package com.koin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.koin.ui.coindetail.CoinDetailScreen
import com.koin.ui.coindetail.CoinDetailViewModel
import com.koin.ui.coinlist.CoinListScreen
import com.koin.ui.coinlist.CoinListViewModel
import com.koin.ui.theme.KoinTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KoinApp()
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KoinApp() {
    KoinTheme {
        val navController = rememberNavController()
        val snackbarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()
        LocalContext.current

        // Show error messages as Snackbar
        fun showError(message: String?) {
            if (!message.isNullOrBlank()) {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = message,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "coin_list",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("coin_list") {
                    val viewModel: CoinListViewModel = hiltViewModel()
                    val state by viewModel.uiState.collectAsState()

                    // Show error if any
                    LaunchedEffect(state.error) {
                        state.error?.let { showError(it) }
                    }

                    CoinListScreen(
                        state = state,
                        onEvent = viewModel::onEvent,
                        onCoinClick = { coinId ->
                            navController.navigate("coin_detail/$coinId")
                        }
                    )
                }

                composable("coin_detail/{coinId}") { backStackEntry ->
                    val viewModel: CoinDetailViewModel = hiltViewModel()
                    val state by viewModel.uiState.collectAsState()

                    // Show error if any
                    LaunchedEffect(state.error) {
                        state.error?.let { showError(it) }
                    }

                    CoinDetailScreen(
                        state = state,
                        onEvent = viewModel::onEvent,
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    KoinApp()
}