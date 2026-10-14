package com.example.formularios.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.formularios.ui.screen.RegistroScreen
import com.example.formularios.ui.screen.ResumenScreen
import com.example.formularios.ui.viewmodel.UsuarioViewModel

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    //Aquí cremos el viewModel una sola vez
    val usuarioViewModel: UsuarioViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "registro"
    ) {

        composable(route = "registro") {
            RegistroScreen(
                navController = navController,
                viewModel = usuarioViewModel
            )
        }

        composable(route = "resumen") {
            ResumenScreen(
                viewModel = usuarioViewModel
            )
        }
    }
}