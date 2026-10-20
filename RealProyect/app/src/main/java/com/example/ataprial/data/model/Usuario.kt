package com.example.ataprial.data.model

enum class TipoUsuario{
    OPERARIO,
    SUPERVISOR,
    JEFATURA
}

data class Usuario(
    val id: String,
    val nombre: String,
    val correo: String,
    val rol: TipoUsuario
)

data class UsuarioUiState(
    val nombre: String = "",
    val correo: String = "",
    val clave: String = "",
    val rol: String = "",
    val errores: UsuarioErrores = UsuarioErrores()
)

data class UsuarioErrores(
    val nombre : String? = null,
    val correo : String? = null,
    val clave : String? = null,
    val rol : String? = null,
)