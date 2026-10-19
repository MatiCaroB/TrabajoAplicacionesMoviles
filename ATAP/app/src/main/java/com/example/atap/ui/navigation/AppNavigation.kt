package com.example.atap.ui.navigation

import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.atap.ui.screeen.HomeScreen
import com.example.atap.ui.screeen.PerfilScreen
import com.example.atap.ui.screeen.ProductosScreen
import com.example.atap.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {

    val backStackEntry by navController
        .currentBackStackEntryAsState()

    val currentRoute =
        backStackEntry?.destination?.route

    val navController = rememberNavController()
    val viewModel: MainViewModel = viewModel()



    Scaffold (
        topBar = {

            TopAppBar(
                title = {
                    Text("Mi Tienda")
                }
            )
        },
        bottomBar = {
            NavigationBar {

                NavigationBarItem(
                    selected = false,
                    onClick = {
                        navController.navigate(AppRoutes.HOME)
                    },
                    icon = {},
                    label = {
                        Text("Inicio")
                    }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = {
                        navController.navigate(AppRoutes.PRODUCTOS)
                    },
                    icon = {},
                    label = {
                        Text("Productos")
                    }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = {
                        navController.navigate(AppRoutes.PERFIL)
                    },
                    icon = {},
                    label = {
                        Text("Perfil")
                    }
                )
            }
        }



    ) { paddingValues ->

        NavHost(
            navController = navController,
            startDestination = AppRoutes.HOME,
            modifier = Modifier.padding(paddingValues)
        ) {

            composable(AppRoutes.HOME) {
                HomeScreen()
            }

            composable(AppRoutes.PRODUCTOS) {
                ProductosScreen(
                    viewModel = viewModel
                )
            }

            composable(AppRoutes.PERFIL) {
                PerfilScreen()
            }
        }
    }
}
