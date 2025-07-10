package com.koin.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.koin.ui.auth.AuthScreen
import com.koin.ui.auth.AuthViewModel
import com.koin.ui.coindetail.CoinDetailScreen
import com.koin.ui.coindetail.CoinDetailViewModel
import com.koin.ui.coinlist.CoinListScreen
import com.koin.ui.coinlist.CoinListViewModel
import com.koin.ui.portfolio.PortfolioScreen
import com.koin.ui.portfolio.PortfolioViewModel
import com.koin.ui.profile.ProfileScreen
import com.koin.ui.profile.ProfileViewModel
import com.koin.ui.splash.SplashScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    showError: (String?) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        // Splash
        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }

        // Coin List
        composable(Screen.CoinList.route) {
            val viewModel: CoinListViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsState()

            LaunchedEffect(state.error) {
                state.error?.let { showError(it) }
            }

            CoinListScreen(
                state = state,
                onEvent = viewModel::onEvent,
                onCoinClick = { coinId ->
                    navController.navigate(Screen.CoinDetail.createRoute(coinId))
                },
                navController = navController
            )
        }

        // Coin Detail
        composable(Screen.CoinDetail.route) {
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

        // Auth
        composable(Screen.Auth.route) {
            val viewModel: AuthViewModel = hiltViewModel()
            AuthScreen(
                viewModel = viewModel,
                onRegistered = {
                    navController.navigate(Screen.Portfolio.route) {
                        launchSingleTop = true
                        popUpTo(Screen.Portfolio.route) { inclusive = false }
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                }
            )
        }

        // Portfolio
        composable(Screen.Portfolio.route) {
            val viewModel: PortfolioViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsState()
            val selectedCoin by viewModel.selectedCoin.collectAsState()

            LaunchedEffect(state.error) {
                state.error?.let { showError(it) }
            }

            PortfolioScreen(
                state = state,
                onEvent = viewModel::onEvent,
                selectedCoin = selectedCoin,
                navController = navController,
            )
        }

        // Profile
        composable(Screen.Profile.route) {
            val viewModel: ProfileViewModel = hiltViewModel()
            ProfileScreen(
                viewModel = viewModel,
                onLogout = {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                navController = navController
            )
        }
    }
}
