package com.example.ataprial.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.ataprial.data.model.UsuarioErrores
import com.example.ataprial.data.model.UsuarioUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class UsuarioViewModel : ViewModel(){
    //Estado interno que puede mutar
    private val _estado = MutableStateFlow(UsuarioUiState())

    //Estado expuesto para la ui
    val estado : StateFlow<UsuarioUiState> = _estado

    //Actualizar cada campo y limpiar su error
    fun onNombreChange(valor: String) {
        _estado.update { it.copy(nombre = valor, errores = it.errores.copy (nombre = null))}
    }

    fun onCorreoChange(valor: String) {
        _estado.update { it.copy(correo = valor, errores = it.errores.copy ( correo = null))}
    }

    fun onClaveChange(valor: String) {
        _estado.update { it.copy(clave = valor, errores = it.errores.copy ( clave = null))}
    }

    fun validarFormulario():Boolean {
        val estadoActual = _estado.value
        val errores = UsuarioErrores(
            //si el campo nombre se encuentra en blanco se gatilla este String
            nombre = if(estadoActual.nombre.isBlank()) "Campo obligatorio" else null,
            //si este campo de correo NO contiene el arroba se gatilla el String owo
            correo = if(!estadoActual.correo.contains("@")) "Correo invalido" else null,
            clave = if(estadoActual.correo.length < 6) "Contraseña debe contener al menos 6 carácteres" else null,

        )
        val hayErrores =listOfNotNull(
            errores.nombre,
            errores.correo,
            errores.clave
        ).isNotEmpty()

        _estado.update { it.copy(errores = errores)}

        return !hayErrores
    }
}


