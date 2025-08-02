package com.koin.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.koin.authentication.presentation.CognitoAuthViewModel
import com.koin.authentication.presentation.SignUpScreen
//import com.koin.authentication.presentation.AuthViewModel
import com.koin.authentication.presentation.ConfirmationScreen
import com.koin.authentication.presentation.LoginScreen
import com.koin.ui.coindetail.CoinDetailScreen
import com.koin.ui.coindetail.CoinDetailViewModel
import com.koin.ui.coinlist.CoinListScreen
import com.koin.ui.coinlist.CoinListViewModel
import com.koin.ui.notification.NotificationDetailScreen
import com.koin.ui.notification.NotificationScreen
import com.koin.ui.portfolio.PortfolioScreen
import com.koin.ui.portfolio.PortfolioViewModel
import com.koin.ui.portfoliodetail.PortfolioDetailScreen
import com.koin.ui.portfoliodetail.PortfolioDetailViewModel
import com.koin.ui.profile.EditProfileScreen
import com.koin.ui.profile.ProfileScreen
import com.koin.ui.profile.ProfileViewModel
import com.koin.ui.settings.SettingsScreen
import com.koin.ui.splash.SplashScreen
import com.koin.ui.totalbalance.TotalBalanceScreen
import com.koin.ui.transactiondetail.TransactionDetailScreen
import com.koin.ui.transactionhistory.TransactionHistoryScreen
import com.koin.ui.transactionsuccess.TransactionSuccessScreen

// Define reusable transitions at the top of your file or in a separate file
val slideInFromRight = slideInHorizontally(initialOffsetX = { it })
val slideOutToLeft = slideOutHorizontally(targetOffsetX = { -it })
val slideInFromLeft = slideInHorizontally(initialOffsetX = { -it })
val slideOutToRight = slideOutHorizontally(targetOffsetX = { it })

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    showError: (String?) -> Unit
) {
    val authViewModel = hiltViewModel<CognitoAuthViewModel>()
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier,
        // Apply default transitions to the entire NavHost
        enterTransition = { slideInFromRight },
        exitTransition = { slideOutToLeft },
        popEnterTransition = { slideInFromLeft },
        popExitTransition = { slideOutToRight }
    ) {
        // Now all composables inherit the transitions automatically
        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }

        composable(Screen.Login.route) {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToSignUp = { navController.navigate("signup") },
                onNavigateToDashboard = {
                    navController.navigate(Screen.Portfolio.route)
                })
        }

        composable("signup") {
            SignUpScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onNavigateToConfirmation = { email ->
                    navController.navigate("confirmation/$email")
                }
            )
        }

        composable(
            "confirmation/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            ConfirmationScreen(
                email = email,
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

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
                onPortfolioCoinClick = { coinId ->
                    navController.navigate(Screen.PortfolioCoinDetail.createRoute(coinId))
                }
            )
        }

        composable(Screen.PortfolioCoinDetail.route) {
            val viewModel: PortfolioDetailViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsState()

            LaunchedEffect(state.error) {
                state.error?.let { showError(it) }
            }

            PortfolioDetailScreen(
                state = state,
                onEvent = viewModel::onEvent,
                onBackClick = { navController.popBackStack() },
                navigateToTransactionSuccess = {
                    navController.navigate(Screen.TransactionSuccess.route)
                }
            )
        }

        composable(Screen.Profile.route) {
            val viewModel: ProfileViewModel = hiltViewModel()
            ProfileScreen(
                viewModel = viewModel,
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                navController = navController
            )
        }

        composable(Screen.TransactionSuccess.route) {
            TransactionSuccessScreen(navController)
        }

        composable(Screen.TransactionHistory.route) {
            TransactionHistoryScreen(navController)
        }

        composable(Screen.TransactionDetail.route) {
            TransactionDetailScreen(navController)
        }

        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController, onLogout = {
                navController.navigate(Screen.Login.route) {
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    }
                }
            })
        }

        composable(Screen.EditProfile.route) {
            EditProfileScreen(navController)
        }

        composable(Screen.Notification.route) {
            NotificationScreen(navController)
        }

        composable(Screen.NotificationDetail.route) {
            val notificationId = it.arguments?.getString("notificationId")?.toLongOrNull()
            if (notificationId != null) {
                NotificationDetailScreen(navController, notificationId = notificationId)
            }
        }

        composable(Screen.TotalBalance.route) {
            TotalBalanceScreen(navController = navController)
        }
    }
}