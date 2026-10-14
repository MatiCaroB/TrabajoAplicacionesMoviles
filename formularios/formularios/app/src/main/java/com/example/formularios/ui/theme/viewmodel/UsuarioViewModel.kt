package com.example.formularios.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.formularios.data.model.UsuarioErrores
import com.example.formularios.data.model.UsuarioUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class UsuarioViewModel : ViewModel() {
    //ESTADO INTERNO MUTABLE
    private val _estado = MutableStateFlow(UsuarioUiState())

    //ESTADO EXPUESTO PARA LA UI
    val estado: StateFlow<UsuarioUiState> = _estado

    //ACTUALIZAR CADA CAMPO Y LIMPIAR SU ERROR
    fun onNombreChange(valor: String) {
        _estado.update { it.copy(nombre = valor, errores = it.errores.copy(nombre = null)) }
    }

    fun onCorreoChange(valor: String) {
        _estado.update { it.copy(correo = valor, errores = it.errores.copy(correo = null)) }
    }

    fun onClaveChange(valor: String) {
        _estado.update { it.copy(clave = valor, errores = it.errores.copy(clave = null)) }
    }

    fun onDireccionChange(valor: String) {
        _estado.update { it.copy(direccion = valor, errores = it.errores.copy(direccion = null)) }
    }

    fun onAceptaTerminosChange(valor: Boolean) {
        _estado.update { it.copy(aceptaTerminos = valor) }
    }
    fun validarFormulario(): Boolean {
        val estadoActual = _estado.value
        val errores = UsuarioErrores(
            nombre = if(estadoActual.nombre.isBlank()) "Campo obligatorio" else null,
            correo = if(!estadoActual.correo.contains("@")) "Correo inválido" else null,
            clave = if(estadoActual.clave.length < 6) "Al menos 6 caracteres" else null,
            direccion = if(estadoActual.direccion.isBlank()) "Campo obligatorio" else null
        )
        val hayErrores = listOfNotNull(
            errores.nombre,
            errores.correo,
            errores.clave,
            errores.direccion
        ).isNotEmpty()

        _estado.update { it.copy(errores = errores) }

        return !hayErrores
    }
}
