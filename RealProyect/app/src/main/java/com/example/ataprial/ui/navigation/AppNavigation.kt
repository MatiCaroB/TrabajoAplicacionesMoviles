package com.example.ataprial.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ataprial.data.database.AppDatabase
import com.example.ataprial.data.model.TipoUsuario
import com.example.ataprial.ui.screen.GalponesScreen
import com.example.ataprial.ui.screen.GranjasScreen
import com.example.ataprial.ui.screen.HomeScreen
import com.example.ataprial.ui.screen.LoginScreen
import com.example.ataprial.ui.screen.UsuarioScreen
import com.example.ataprial.ui.viewmodel.GalponesViewModel
import com.example.ataprial.ui.viewmodel.GalponesViewModelFactory
import com.example.ataprial.ui.viewmodel.GranjasViewModel
import com.example.ataprial.ui.viewmodel.GranjasViewModelFactory
import com.example.ataprial.ui.viewmodel.UsuarioViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val usuarioViewModel: UsuarioViewModel = viewModel()

    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)

    val galponesViewModel: GalponesViewModel = viewModel(
        factory = GalponesViewModelFactory(database.galponDao())
    )


    val granjasViewModel: GranjasViewModel = viewModel(
        factory = GranjasViewModelFactory(database.granjaDao())
    )

    val usuarioActual = usuarioViewModel.usuarioLogueado.collectAsState().value
    val rol = usuarioActual?.rol ?: TipoUsuario.OPERARIO

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
                    // Ahora la opción de Galpones te dirige primero a seleccionar la Granja
                    navController.navigate(AppRoutes.GRANJAS)
                }
            )
        }

        // 👥 RUTA 3: Gestión de Usuarios
        composable(route = AppRoutes.USUARIOS) {
            UsuarioScreen(
                viewModel = usuarioViewModel,
                onCerrarSesion = {
                    navController.navigate(AppRoutes.LOGIN) {
                        popUpTo(AppRoutes.HOME) { inclusive = true }
                    }
                }
            )
        }

        // 🚜 RUTA 4: Gestión de Granjas
        composable(route = AppRoutes.GRANJAS) {
            GranjasScreen(
                viewModel = granjasViewModel,
                rolUsuario = rol,
                onGranjaClick = { idGranja ->

                    navController.navigate("${AppRoutes.GALPONES}/$idGranja")
                },
                onVolver = { navController.popBackStack() }
            )
        }

        // 🏗️ RUTA 5: Galpones dinámicos asociados por Granja
        composable(
            route = "${AppRoutes.GALPONES}/{granjaId}",
            arguments = listOf(navArgument("granjaId") { type = NavType.StringType })
        ) { backStackEntry ->
            val granjaId = backStackEntry.arguments?.getString("granjaId") ?: "GRANJA-01"

            GalponesScreen(
                viewModel = galponesViewModel,
                rolUsuario = rol,
                granjaId = granjaId,
                onVolver = { navController.popBackStack() }
            )
        }
    }
}
