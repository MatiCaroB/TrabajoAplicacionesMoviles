package com.example.ataprial.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ataprial.ui.screen.GalponesScreen
import com.example.ataprial.ui.screen.HomeScreen
import com.example.ataprial.ui.screen.LoginScreen
import com.example.ataprial.ui.screen.UsuarioScreen
import com.example.ataprial.ui.viewmodel.UsuarioViewModel
import com.example.ataprial.data.model.TipoUsuario

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val usuarioViewModel: UsuarioViewModel = viewModel()

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
                        // Limpia de forma segura el Login para que no se pueda volver atrás con el botón físico del teléfono
                        popUpTo(AppRoutes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(AppRoutes.USUARIOS)
                }
            )
        }

        // 🏠 RUTA 2: Menú Principal (Home)
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

        // 👥 RUTA 3: Gestión de Usuarios / Registro Público
        composable(route = AppRoutes.USUARIOS) {
            UsuarioScreen(
                viewModel = usuarioViewModel,
                onCerrarSesion = {
                    // 💡 SOLUCIÓN DEFINTIVA AL CRASH:
                    // Verificamos si hay un historial previo al que regresar de forma segura.
                    // Si el usuario entró al registro de forma anónima desde afuera, popBackStack() lo regresará al Login limpiamente sin romper el enrutador.
                    val regresoExitoso = navController.popBackStack()

                    if (!regresoExitoso) {
                        // Si por algún motivo la pila estaba corrupta, hace el salto forzado seguro
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
            val esJefatura = usuarioActual?.rol == TipoUsuario.JEFATURA

            GalponesScreen(
                esJefatura = esJefatura,
                onVolver = { navController.popBackStack() }
            )
        }
    }
}
