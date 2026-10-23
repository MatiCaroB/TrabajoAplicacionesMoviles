package com.example.ataprial.data.database

import androidx.room.TypeConverter
import com.example.ataprial.data.model.EstadoGalpon
import com.example.ataprial.data.model.TipoUsuario
import java.util.Date

class Converters {

    // --- CONVERSORES DE USUARIO ---
    @TypeConverter
    fun fromTipoUsuario(value: TipoUsuario): String {
        return value.name
    }

    @TypeConverter
    fun toTipoUsuario(value: String): TipoUsuario {
        return runCatching { TipoUsuario.valueOf(value) }.getOrDefault(TipoUsuario.OPERARIO)
    }

    // --- CONVERSORES DE GALPÓN ---
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromEstadoGalpon(value: EstadoGalpon?): String? {
        return value?.name
    }

    @TypeConverter
    fun toEstadoGalpon(value: String?): EstadoGalpon {
        return runCatching { value?.let { EstadoGalpon.valueOf(it) } }.getOrNull() ?: EstadoGalpon.NORMAL
    }
}
