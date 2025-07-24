package com.koin.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.koin.R
import com.koin.navigation.Screen
import com.koin.ui.session.SessionViewModel
import kotlinx.coroutines.delay

/**
 * Shows a splash while we decide where to go (auth vs main).
 * Uses the system splash on Android 12+, so keep this quick (<800ms ideally).
 */
@Composable
fun SplashScreen(navController: NavController) {
    val sessionViewModel: SessionViewModel = hiltViewModel()
    val isLoggedIn = sessionViewModel.isLoggedIn.collectAsState().value

    // Navigate after small delay to allow first collect.
    LaunchedEffect(isLoggedIn) {
        // Give the ViewModel a frame to emit.
        delay(150)
        val target = if (isLoggedIn) Screen.Portfolio.route else Screen.Login.route
        navController.navigate(target) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(modifier = Modifier
            .size(200.dp)
            .clip(RoundedCornerShape(20))) {

            Image(
                painter = painterResource(id = R.drawable.koin),
                contentDescription = null,
                contentScale = ContentScale.Crop

                )
        }
    }
}
