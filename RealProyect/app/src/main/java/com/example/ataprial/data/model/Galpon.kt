package com.example.ataprial.data.model

import java.util.Date

//Datos críticos son propuestos por nosotros

enum class EstadoGalpon{NORMAL, ADVERTENCIA, CRITICO}


data class Galpon(
    val id: String,
    val granjaId: String,
    val nombreGalpon: String,

    val temperatura: Double,
    val humedad: Double,
    val estadoGalpon: EstadoGalpon,
    val fechaActualizacion: Date,
    val alertasActivas: Boolean

    )