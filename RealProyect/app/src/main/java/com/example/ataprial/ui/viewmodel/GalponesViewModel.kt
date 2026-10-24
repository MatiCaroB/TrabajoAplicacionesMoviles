package com.example.ataprial.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ataprial.data.dao.GalponDao
import com.example.ataprial.data.model.EstadoGalpon
import com.example.ataprial.data.model.Galpon
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class GalponesViewModel(private val galponDao: GalponDao) : ViewModel() {

    val galpones: StateFlow<List<Galpon>> = galponDao.obtenerTodosLosGalpones()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun agregarGalpon(nombre: String, tempStr: String, humedadStr: String): Boolean {
        val temp = tempStr.toDoubleOrNull()
        val hum = humedadStr.toDoubleOrNull()

        if (nombre.isBlank() || temp == null || hum == null) {
            return false
        }

        // valores que afectan los estados de la temperatura
        val estado = when {
            temp > 35.0 -> EstadoGalpon.CRITICO
            temp > 30.0 -> EstadoGalpon.ADVERTENCIA
            else -> EstadoGalpon.NORMAL
        }

        val nuevoGalpon = Galpon(
            id = UUID.randomUUID().toString(),
            granjaId = "GRANJA-01",
            nombreGalpon = nombre.trim(),
            temperatura = temp,
            humedad = hum,
            estadoGalpon = estado,
            fechaActualizacion = Date(),
            alertasActivas = temp > 30.0 // Se activa la alerta si pasa de 30°C
        )

        viewModelScope.launch {
            galponDao.insertarGalpon(nuevoGalpon)
        }

        return true
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
