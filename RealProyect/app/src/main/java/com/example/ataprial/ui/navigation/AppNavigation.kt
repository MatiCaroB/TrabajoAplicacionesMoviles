package com.example.ataprial.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ataprial.ui.screen.GalponesScreen
import com.example.ataprial.ui.screen.HomeScreen
import com.example.ataprial.ui.screen.UsuarioScreen

object AppRoutes {
    const val HOME = "home"
    const val USUARIOS = "usuarios"
    const val GALPONES = "galpones"
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
                },
                onNavegarAGalpones = {
                    navController.navigate(AppRoutes.GALPONES)
                }
            )
        }

        composable(AppRoutes.USUARIOS) {
            UsuarioScreen()
        }

        composable(AppRoutes.GALPONES) {
            GalponesScreen()
        }
    }
}