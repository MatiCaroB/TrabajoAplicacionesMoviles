package com.example.ataprial.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ataprial.ui.screen.HomeScreen
import com.example.ataprial.ui.screen.UsuarioScreen

object AppRoutes {
    const val HOME = "home"
    const val USUARIOS = "usuarios"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppRoutes.HOME
    ) {
        composable(AppRoutes.HOME) {
            HomeScreen(
                onNavegarAUsuarios = {
                    navController.navigate(AppRoutes.USUARIOS)
                }
            )
        }

        composable(AppRoutes.USUARIOS) {
            UsuarioScreen()
        }
    }
}