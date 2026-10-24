package com.example.ataprial.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ataprial.data.dao.GalponDao
import com.example.ataprial.data.model.EstadoGalpon
import com.example.ataprial.data.model.Galpon
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class GalponesViewModel(private val galponDao: GalponDao) : ViewModel() {

    private val idGranjaActiva = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val galpones: StateFlow<List<Galpon>> = idGranjaActiva
        .flatMapLatest { id ->
            if (id.isBlank()) {
                kotlinx.coroutines.flow.flowOf(emptyList())
            } else {
                galponDao.obtenerGalponesPorGranja(id)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Configura el filtro actual de la pantalla apenas entras
    fun establecerGranjaActiva(granjaId: String) {
        idGranjaActiva.value = granjaId
    }

    fun agregarGalpon(nombre: String, tempStr: String, humidityStr: String, granjaId: String): Boolean {
        val temp = tempStr.toDoubleOrNull()
        val hum = humidityStr.toDoubleOrNull()

        if (nombre.isBlank() || temp == null || hum == null || granjaId.isBlank()) {
            return false
        }

        val estado = when {
            temp > 35.0 -> EstadoGalpon.CRITICO
            temp > 30.0 -> EstadoGalpon.ADVERTENCIA
            else -> EstadoGalpon.NORMAL
        }

        val nuevoGalpon = Galpon(
            id = UUID.randomUUID().toString(),
            granjaId = granjaId.trim(),
            nombreGalpon = nombre.trim(),
            temperatura = temp,
            humedad = hum,
            estadoGalpon = estado,
            fechaActualizacion = Date(),
            alertasActivas = temp > 30.0
        )

        viewModelScope.launch {
            galponDao.insertarGalpon(nuevoGalpon)
        }

        return true
    }


    fun eliminarGalpon(id: String) {
        viewModelScope.launch {
            galponDao.eliminarGalpon(id)
        }
    }
}

class GalponesViewModelFactory(private val galponDao: GalponDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GalponesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GalponesViewModel(galponDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
