package com.example.ataprial.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

enum class EstadoGalpon {
    NORMAL,
    ADVERTENCIA,
    CRITICO
}
@Entity(
    tableName = "galpones",
    foreignKeys = [
        ForeignKey(
            entity = Granja::class,
            parentColumns = ["id"],
            childColumns = ["granjaId"],
            onDelete = ForeignKey.CASCADE // Si se borra la granja, se borran automáticamente sus galpones
        )
    ]
)

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