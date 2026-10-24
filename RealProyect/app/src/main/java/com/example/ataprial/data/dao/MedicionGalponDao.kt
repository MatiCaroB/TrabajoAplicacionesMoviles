package com.example.ataprial.data.dao



import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ataprial.data.model.MedicionGalpon
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicionGalponDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarMedicion(medicion: MedicionGalpon)

    @Query("SELECT * FROM mediciones_galpon WHERE galponId = :galponId")
    fun obtenerMedicionesPorGalpon(galponId: String): Flow<List<MedicionGalpon>>
}