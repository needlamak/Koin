package com.koin.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object CoinList : Screen("coin_list")
    object CoinDetail : Screen("coin_detail/{coinId}") {
        fun createRoute(coinId: String) = "coin_detail/$coinId"
    }
    object Portfolio : Screen("portfolio")
    object Auth : Screen("auth")
    object Profile : Screen("profile")
}
