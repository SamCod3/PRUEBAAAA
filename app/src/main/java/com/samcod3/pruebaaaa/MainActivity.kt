package com.samcod3.pruebaaaa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.samcod3.pruebaaaa.ui.navigation.Screen
import com.samcod3.pruebaaaa.ui.screen.FavoritesScreen
import com.samcod3.pruebaaaa.ui.screen.SearchScreen
import com.samcod3.pruebaaaa.ui.screen.WeatherScreen
import com.samcod3.pruebaaaa.ui.theme.PRUEBAAAATheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PRUEBAAAATheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    
                    NavHost(navController = navController, startDestination = Screen.Home.route) {
                        composable(Screen.Home.route) {
                            FavoritesScreen(
                                onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                                onNavigateToDetail = { id -> navController.navigate(Screen.Detail.createRoute(id)) },
                                onNavigateToCurrentLocation = { 
                                    navController.navigate(Screen.Detail.createRoute("current_location"))
                                }
                            )
                        }
                        
                        composable(Screen.Search.route) {
                            SearchScreen(
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToDetail = { id -> 
                                    navController.navigate(Screen.Detail.createRoute(id)) {
                                        popUpTo(Screen.Search.route) { inclusive = true }
                                    }
                                }
                            )
                        }
                        
                        composable(
                            route = Screen.Detail.route,
                            arguments = listOf(
                                navArgument("municipioId") { type = NavType.StringType }
                            )
                        ) {
                            WeatherScreen()
                        }
                    }
                }
            }
        }
    }
}

