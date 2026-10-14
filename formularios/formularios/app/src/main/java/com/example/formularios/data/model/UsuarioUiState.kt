package com.example.formularios.data.model

//Modelo principal que representa el estado del formulario del usuario
data class UsuarioUiState(
    val nombre: String = "",
    val correo: String = "",
    val clave: String = "",
    val direccion: String = "",
    val aceptaTerminos: Boolean = false,
    //Objeto que contiene los errores por campo
    val errores: UsuarioErrores = UsuarioErrores()
)

//Modelo que almacena posibles errores individuales del formulario
data class UsuarioErrores(
    val nombre: String? = null,
    val correo: String? = null,
    val clave: String? = null,
    val direccion: String? = null
)