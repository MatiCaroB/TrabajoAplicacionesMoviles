package com.example.crudestudiantes.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "estudiantes")
data class Estudiante(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val nombre: String,
    val correo: String,
    val carrera: String
)
