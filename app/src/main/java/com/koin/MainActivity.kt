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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.koin.components.BottomNavBar
import com.koin.navigation.Screen
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
                val hideBottomBarRoutes =
                    setOf(Screen.Splash.route, Screen.Auth.route, "coin_detail")
                if (currentRoute !in hideBottomBarRoutes) {
                    BottomNavBar(navController)
                }
            }

        ) { innerPadding ->
            com.koin.navigation.NavGraph(
                navController = navController,
                modifier = Modifier.padding(innerPadding),
                showError = ::showError
            )
        }
    }
}

