package com.example.ataprial.data.model


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "granjas")
data class Granja(
    @PrimaryKey val id: String,
    val nombreGranja: String,
    val ubicacion: String = ""
)
