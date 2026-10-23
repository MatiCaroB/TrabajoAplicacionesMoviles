package com.example.ataprial.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TipoUsuario {
    OPERARIO,
    SUPERVISOR,
    JEFATURA
}

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey val id: String,
    val nombre: String,
    val correo: String,
    val clave: String,
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
    val nombre: String? = null,
    val correo: String? = null,
    val clave: String? = null,
    val rol: String? = null,
)
