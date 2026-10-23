package com.example.ataprial.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.ataprial.data.model.TipoUsuario
import com.example.ataprial.data.model.Usuario
import com.example.ataprial.data.model.UsuarioErrores
import com.example.ataprial.data.model.UsuarioUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class UsuarioViewModel : ViewModel() {

    // Estado del formulario
    private val _estado = MutableStateFlow(UsuarioUiState())
    val estado: StateFlow<UsuarioUiState> = _estado.asStateFlow()

    // Lista en memoria de usuarios registrados
    private val _listaUsuarios = MutableStateFlow<List<Usuario>>(emptyList())
    val listaUsuarios: StateFlow<List<Usuario>> = _listaUsuarios.asStateFlow()

    // Actualizar campos
    fun onNombreChange(valor: String) {
        _estado.update { it.copy(nombre = valor, errores = it.errores.copy(nombre = null)) }
    }

    fun onCorreoChange(valor: String) {
        _estado.update { it.copy(correo = valor, errores = it.errores.copy(correo = null)) }
    }

    fun onClaveChange(valor: String) {
        _estado.update { it.copy(clave = valor, errores = it.errores.copy(clave = null)) }
    }

    fun onRolChange(valor: String) {
        _estado.update { it.copy(rol = valor, errores = it.errores.copy(rol = null)) }
    }

    // Validar entradas
    fun validarFormulario(): Boolean {
        val estadoActual = _estado.value
        val errores = UsuarioErrores(
            nombre = if (estadoActual.nombre.isBlank()) "Campo obligatorio" else null,
            correo = if (!estadoActual.correo.contains("@")) "Correo inválido" else null,
            clave = if (estadoActual.clave.length < 6) "Contraseña de al menos 6 caracteres" else null,
            rol = if (estadoActual.rol.isBlank()) "Debe seleccionar un rol" else null
        )

        val hayErrores = listOfNotNull(
            errores.nombre,
            errores.correo,
            errores.clave,
            errores.rol
        ).isNotEmpty()

        _estado.update { it.copy(errores = errores) }
        return !hayErrores
    }

    // Guardar usuario y limpiar formulario
    fun registrarUsuario(): Boolean {
        if (!validarFormulario()) return false

        val estadoActual = _estado.value
        val tipoUsuario = runCatching { TipoUsuario.valueOf(estadoActual.rol) }
            .getOrDefault(TipoUsuario.OPERARIO)

        val nuevoUsuario = Usuario(
            id = UUID.randomUUID().toString(),
            nombre = estadoActual.nombre,
            correo = estadoActual.correo,
            rol = tipoUsuario
        )

        // Agregar a la lista
        _listaUsuarios.update { it + nuevoUsuario }

        // Limpiar el formulario
        _estado.value = UsuarioUiState()

        return true
    }
}


