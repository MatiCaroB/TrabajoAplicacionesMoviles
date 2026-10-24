package com.example.ataprial.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ataprial.data.model.Granja
import kotlinx.coroutines.flow.Flow

@Dao
interface GranjaDao {
    @Query("SELECT * FROM granjas")
    fun obtenerTodasLasGranjas(): Flow<List<Granja>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarGranja(granja: Granja)

    @Query("DELETE FROM granjas WHERE id = :id")
    suspend fun eliminarGranja(id: String)
}
