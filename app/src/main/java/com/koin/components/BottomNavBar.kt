package com.koin.components

import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

// Modified NavItem data class to hold both selected and unselected icons
data class NavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)
@Composable
fun BottomNavBar(navController: NavHostController) {
    val items = listOf(
        // Provide both filled and outlined icons for each item
        NavItem(
            "portfolio",
            "Portfolio",
            Icons.Filled.AccountBalanceWallet,
            Icons.Outlined.AccountBalanceWallet
        ),
        NavItem(
            "coin_list",
            "Coin list",
            Icons.Filled.Home,
            Icons.Outlined.Home
        ),
        NavItem(
            "profile",
            "Profile",
            Icons.Filled.AccountCircle,
            Icons.Outlined.AccountCircle
        )
    )

    NavigationBar(
        //modifier = Modifier.height(70.dp),
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        items.forEach { item ->
            val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(item.route) {
                            // Pop up to the first bottom nav destination (portfolio)
                            // This prevents multiple instances of bottom nav screens
                            popUpTo("portfolio") {
                                saveState = true
                                inclusive = false // Keep portfolio as the base
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    // Choose the icon based on the 'selected' state
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = null // Consider adding a meaningful content description
                    )
                },
                // If you want to show labels, uncomment this:
                 label = { Text(item.label) } // Using route as a placeholder, you'd probably want a more user-friendly label
            )
        }
    }
}

