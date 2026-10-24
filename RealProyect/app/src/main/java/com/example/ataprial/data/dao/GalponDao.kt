package com.example.ataprial.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.ataprial.data.model.Galpon
import com.example.ataprial.data.model.GalponConMediciones
import kotlinx.coroutines.flow.Flow

@Dao
interface GalponDao {

    @Query("SELECT * FROM galpones ORDER BY fechaActualizacion DESC")
    fun obtenerTodosLosGalpones(): Flow<List<Galpon>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarGalpon(galpon: Galpon)

    @Query("DELETE FROM galpones WHERE id = :id")
    suspend fun eliminarGalpon(id: String)
    @Transaction
    @Query("SELECT * FROM galpones WHERE granjaId = :granjaId")
    fun obtenerGalponesPorGranja(granjaId: String):Flow<List<GalponConMediciones>>

    @Transaction
    @Query("SELECT * FROM galpones")
    fun obtenerTodosLosGalponesConMediciones(): Flow<List<GalponConMediciones>>



    @Transaction
    @Query("SELECT * FROM galpones WHERE granjaId = :granjaId")
    fun obtenerGalponesConMedicionesPorGranja(granjaId: String): Flow<List<GalponConMediciones>>




}