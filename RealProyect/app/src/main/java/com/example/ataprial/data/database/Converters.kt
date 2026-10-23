package com.example.ataprial.data.database

import androidx.room.TypeConverter
import com.example.ataprial.data.model.TipoUsuario

class Converters {
    @TypeConverter
    fun fromTipoUsuario(value: TipoUsuario): String {
        return value.name
    }

    @TypeConverter
    fun toTipoUsuario(value: String): TipoUsuario {
        return runCatching { TipoUsuario.valueOf(value) }.getOrDefault(TipoUsuario.OPERARIO)
    }
}
