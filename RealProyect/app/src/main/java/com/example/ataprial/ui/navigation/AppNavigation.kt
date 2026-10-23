package com.example.ataprial.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ataprial.data.database.AppDatabase
import com.example.ataprial.data.model.TipoUsuario
import com.example.ataprial.ui.screen.GalponesScreen
import com.example.ataprial.ui.screen.HomeScreen
import com.example.ataprial.ui.screen.LoginScreen
import com.example.ataprial.ui.screen.UsuarioScreen
import com.example.ataprial.ui.viewmodel.GalponesViewModel
import com.example.ataprial.ui.viewmodel.GalponesViewModelFactory
import com.example.ataprial.ui.viewmodel.UsuarioViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val usuarioViewModel: UsuarioViewModel = viewModel()

    // 1. Obtención del contexto y base de datos SQLite antes de las rutas
    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)

    // 2. Creación del GalponesViewModel usando su fábrica
    val galponesViewModel: GalponesViewModel = viewModel(
        factory = GalponesViewModelFactory(database.galponDao())
    )

    NavHost(
        navController = navController,
        startDestination = AppRoutes.LOGIN
    ) {
        // 🔐 RUTA 1: Pantalla de Login
        composable(route = AppRoutes.LOGIN) {
            LoginScreen(
                viewModel = usuarioViewModel,
                onLoginSuccess = {
                    navController.navigate(AppRoutes.HOME) {
                        popUpTo(AppRoutes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(AppRoutes.USUARIOS)
                }
            )
        }

        // 🏠 RUTA 2: Menú Principal (Home)
        composable(route = AppRoutes.HOME) {
            HomeScreen(
                onNavegarAUsuarios = {
                    navController.navigate(AppRoutes.USUARIOS)
                },
                onNavegarAGalpones = {
                    navController.navigate(AppRoutes.GALPONES)
                }
            )
        }

        // 👥 RUTA 3: Gestión de Usuarios / Registro Público
        composable(route = AppRoutes.USUARIOS) {
            UsuarioScreen(
                viewModel = usuarioViewModel,
                onCerrarSesion = {
                    val regresoExitoso = navController.popBackStack()
                    if (!regresoExitoso) {
                        navController.navigate(AppRoutes.LOGIN) {
                            popUpTo(AppRoutes.LOGIN) { inclusive = true }
                        }
                    }
                }
            )
        }

        // 🏗️ RUTA 4: Galpones
        composable(route = AppRoutes.GALPONES) {
            val usuarioActual = usuarioViewModel.usuarioLogueado.collectAsState().value
            val rol = usuarioActual?.rol ?: TipoUsuario.OPERARIO // Por defecto OPERARIO si no hay sesión

            GalponesScreen(
                viewModel = galponesViewModel,
                rolUsuario = rol, // 👈 Le pasamos el ROL (OPERARIO, SUPERVISOR o JEFATURA)
                onVolver = { navController.popBackStack() }
            )
        }
    }
}
