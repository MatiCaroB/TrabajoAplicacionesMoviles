package com.example.ataprial.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.ataprial.data.model.EstadoGalpon
import com.example.ataprial.data.model.Galpon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date
import java.util.UUID

class GalponesViewModel : ViewModel() {

    private val _galpones = MutableStateFlow<List<Galpon>>(emptyList())
    val galpones: StateFlow<List<Galpon>> = _galpones.asStateFlow()

    init {
        // Datos iniciales de prueba
        _galpones.value = listOf(
            Galpon(
                id = UUID.randomUUID().toString(),
                granjaId = "GRANJA-01",
                nombreGalpon = "Galpón 1 - Sector Norte",
                temperatura = 24.5,
                humedad = 60.0,
                estadoGalpon = EstadoGalpon.NORMAL,
                fechaActualizacion = Date(),
                alertasActivas = false
            ),
            Galpon(
                id = UUID.randomUUID().toString(),
                granjaId = "GRANJA-01",
                nombreGalpon = "Galpón 2 - Sector Sur",
                temperatura = 32.1,
                humedad = 75.0,
                estadoGalpon = EstadoGalpon.ADVERTENCIA,
                fechaActualizacion = Date(),
                alertasActivas = true
            )
        )
    }

    fun agregarGalpon(nombre: String, tempStr: String, humedadStr: String): Boolean {
        val temp = tempStr.toDoubleOrNull()
        val hum = humedadStr.toDoubleOrNull()

        if (nombre.isBlank() || temp == null || hum == null) {
            return false
        }

        val nuevoGalpon = Galpon(
            id = UUID.randomUUID().toString(),
            granjaId = "GRANJA-01",
            nombreGalpon = nombre.trim(),
            temperatura = temp,
            humedad = hum,
            estadoGalpon = if (temp > 30.0) EstadoGalpon.ADVERTENCIA else EstadoGalpon.NORMAL,
            fechaActualizacion = Date(),
            alertasActivas = temp > 30.0
        )

        _galpones.value = _galpones.value + nuevoGalpon
        return true
    }
}