package com.samcod3.pruebaaaa.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Search : Screen("search")
    data object Detail : Screen("detail/{municipioId}") {
        fun createRoute(municipioId: String) = "detail/$municipioId"
    }
}
