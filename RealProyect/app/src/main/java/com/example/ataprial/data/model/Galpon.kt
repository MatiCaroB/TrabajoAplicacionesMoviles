package com.example.ataprial.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.util.Date

enum class EstadoGalpon {
    NORMAL,
    ADVERTENCIA,
    CRITICO
}
@Entity(
    tableName = "galpones",
    indices = [androidx.room.Index("granjaId")],
    foreignKeys = [
        ForeignKey(
            entity = Granja::class,
            parentColumns = ["id"],
            childColumns = ["granjaId"],
            onDelete = ForeignKey.CASCADE
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
    val alertasActivas: Boolean,

)
@Entity(
    tableName = "mediciones_galpon",
    foreignKeys = [
        ForeignKey(
            entity = Galpon::class,
            parentColumns = ["id"],
            childColumns = ["galponId"],
            onDelete = ForeignKey.CASCADE)
        ]
    )

data class MedicionGalpon(
    @PrimaryKey
    val id: String = java.util.UUID.randomUUID().toString(),
    val galponId: String,
    val temperatura: Double,
    val humedad: Double,
    val fechaHora: String = android.text.format.DateFormat.format("dd/MM/yyyy HH:mm", java.util.Date()).toString()
)
data class GalponConMediciones(
    @Embedded val galpon: Galpon,
    @Relation(
        parentColumn = "id",
        entityColumn = "galponId"
    )
    val historialMediciones: List<MedicionGalpon>
)