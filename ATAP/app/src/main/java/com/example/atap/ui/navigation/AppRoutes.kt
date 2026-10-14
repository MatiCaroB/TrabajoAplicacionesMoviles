package com.example.atap.ui.navigation
//De esta manera evitamos escribir rutas directamente por toda la aplicación.
//En lugar de:
//navController.navigate("productos")
//utilizaremos:
//navController.navigate(AppRoutes.PRODUCTOS)
//Esto hace que las rutas estén centralizadas y sea más sencillo mantenerlas.
object AppRoutes{
    const val HOME = "home"
    const val PRODUCTOS = "productos"
    const val PERFIL = "perfil"
}