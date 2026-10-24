package com.example.ataprial.ui.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ataprial.data.dao.GalponDao
import com.example.ataprial.data.dao.MedicionGalponDao
import com.example.ataprial.data.model.EstadoGalpon
import com.example.ataprial.data.model.Galpon
import com.example.ataprial.data.model.GalponConMediciones
import com.example.ataprial.data.model.MedicionGalpon
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class GalponesViewModel(
    private val galponDao: GalponDao,
    private val medicionGalponDao: MedicionGalponDao
) : ViewModel() {

    private val idGranjaActiva = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val galpones: StateFlow<List<GalponConMediciones>> = idGranjaActiva
        .flatMapLatest { id ->
            if (id.isBlank()) {
                // 👈 Si no hay granja seleccionada (como al entrar desde Monitoreo), trae TODOS los galpones
                galponDao.obtenerTodosLosGalponesConMediciones()
            } else {
                galponDao.obtenerGalponesConMedicionesPorGranja(id)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Configura el filtro actual de la pantalla apenas entras a una granja
    fun establecerGranjaActiva(granjaId: String) {
        idGranjaActiva.value = granjaId
    }

    fun agregarGalpon(
        nombre: String,
        tempStr: String,
        humidityStr: String,
        granjaId: String
    ): Boolean {
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

    // 🌡️ Función unificada: Registra en la tabla de historial Y actualiza el estado actual del galpón
    fun registrarMedicion(galpon: Galpon, nuevaTempStr: String, nuevaHumStr: String): Boolean {
        val temp = nuevaTempStr.toDoubleOrNull()
        val hum = nuevaHumStr.toDoubleOrNull()

        if (temp == null || hum == null) return false

        val nuevoEstado = when {
            temp > 35.0 -> EstadoGalpon.CRITICO
            temp > 30.0 -> EstadoGalpon.ADVERTENCIA
            else -> EstadoGalpon.NORMAL
        }

        // 1. Instancia del historial
        val nuevaMedicion = MedicionGalpon(
            galponId = galpon.id,
            temperatura = temp,
            humedad = hum
        )

        // 2. Entidad del galpón actualizada
        val galponActualizado = galpon.copy(
            temperatura = temp,
            humedad = hum,
            estadoGalpon = nuevoEstado,
            fechaActualizacion = Date(),
            alertasActivas = temp > 30.0
        )

        viewModelScope.launch {
            try {
                println("=== INTENTANDO INSERTAR MEDICIÓN ===")

            // Guarda la nueva medición en el historial
            medicionGalponDao.insertarMedicion(nuevaMedicion)
            println("=== MEDICIÓN INSERTADA CON ÉXITO: ${nuevaMedicion.id} ===")
            // Actualiza los valores actuales del galpón
            galponDao.insertarGalpon(galponActualizado)
        } catch (e: Exception) {
            println("=== ERROR AL INSERTAR EN ROOM: ${e.message} ===")
            e.printStackTrace()
        }
    }

        return true
    }
}

class GalponesViewModelFactory(
    private val galponDao: GalponDao,
    private val medicionGalponDao: MedicionGalponDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GalponesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GalponesViewModel(galponDao, medicionGalponDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}