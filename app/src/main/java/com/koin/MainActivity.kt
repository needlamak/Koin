package com.koin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.koin.ui.auth.AuthScreen
import com.koin.ui.auth.AuthViewModel
import com.koin.ui.coindetail.CoinDetailScreen
import com.koin.ui.coindetail.CoinDetailViewModel
import com.koin.ui.coinlist.CoinListScreen
import com.koin.ui.coinlist.CoinListViewModel
import com.koin.components.BottomNavBar
import com.koin.ui.profile.ProfileScreen
import com.koin.ui.profile.ProfileViewModel
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
        val sessionViewModel: com.koin.ui.session.SessionViewModel = hiltViewModel()
        val isLoggedIn by sessionViewModel.isLoggedIn.collectAsState()
        val startDestination = "splash"
        val snackbarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()

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
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            // In MainActivity.kt, update the Scaffold's bottomBar condition
            bottomBar = {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val hideBottomBarRoutes = setOf("splash", "auth", "coin_detail")
                if (currentRoute !in hideBottomBarRoutes) {
                    BottomNavBar(navController)
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.padding(innerPadding)
            ) {
                // 🚀 Splash
                composable("splash") {
                    com.koin.ui.splash.SplashScreen(navController)
                }
                composable("coin_list") {
                    val viewModel: CoinListViewModel = hiltViewModel()
                    val state by viewModel.uiState.collectAsState()

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

                    LaunchedEffect(state.error) {
                        state.error?.let { showError(it) }
                    }

                    CoinDetailScreen(
                        state = state,
                        onEvent = viewModel::onEvent,
                        onBackClick = { navController.popBackStack() }
                    )
                }
                // ✅ Auth destination
                composable("auth") {
                    val viewModel: AuthViewModel = hiltViewModel()
                    AuthScreen(
                        viewModel = viewModel,
                        onRegistered = {
                            navController.navigate("coin_list") {
                                launchSingleTop = true
                                popUpTo("coin_list") { inclusive = false }
                                popUpTo("auth") { inclusive = true }
                            }
                        }
                    )
                }

                // ✅ Profile screen after login
                composable("profile") {
                    val viewModel: ProfileViewModel = hiltViewModel()
                    ProfileScreen(viewModel = viewModel, onLogout = {
                        navController.navigate("profile") {
                            launchSingleTop = true
                            popUpTo("profile") { inclusive = false }
                            launchSingleTop = true
                            popUpTo("auth") { inclusive = false }
                        }
                    }
                    )
                }
            }
        }
    }
}
