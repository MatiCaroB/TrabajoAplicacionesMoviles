package com.example.ataprial.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

enum class EstadoGalpon {
    NORMAL,
    ADVERTENCIA,
    CRITICO
}

@Entity(tableName = "galpones")
data class Galpon(
    @PrimaryKey
    val id: String,
    val granjaId: String,
    val nombreGalpon: String,
    val temperatura: Double,
    val humedad: Double,
    val estadoGalpon: EstadoGalpon,
    val fechaActualizacion: Date,
    val alertasActivas: Boolean
)