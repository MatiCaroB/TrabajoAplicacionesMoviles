package com.example.ataprial.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.ataprial.data.dao.GalponDao
import com.example.ataprial.data.dao.UsuarioDao
import com.example.ataprial.data.model.EstadoGalpon
import com.example.ataprial.data.model.Galpon
import com.example.ataprial.data.model.TipoUsuario
import com.example.ataprial.data.model.Usuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

@Database(entities = [Usuario::class, Galpon::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun galponDao(): GalponDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ataprial_database"
                )
                    .addCallback(AppDatabaseCallback(context))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback(
        private val context: Context
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            // Usamos una Coroutine en segundo plano para escribir en SQLite sin congelar la app
            CoroutineScope(Dispatchers.IO).launch {
                val database = getDatabase(context)
                val usuarioDao = database.usuarioDao()
                val galponDao = database.galponDao()

                // 🔴 1. Cuenta de Jefatura
                usuarioDao.insertarUsuario(
                    Usuario(id = "jefe-123", nombre = "Carlos Jefe", correo = "jefe@mail.com", clave ="123456", rol = TipoUsuario.JEFATURA)
                )

                // 🟡 2. Cuenta de Supervisor
                usuarioDao.insertarUsuario(
                    Usuario(id = "super-123", nombre = "Ana Supervisora", correo = "supervisor@mail.com", clave ="654321", rol = TipoUsuario.SUPERVISOR)
                )

                // 🔵 3. Cuenta de Operario
                usuarioDao.insertarUsuario(
                    Usuario(id = "ope-123", nombre = "Juan Operario", correo = "operario@mail.com", clave ="papoi123", rol = TipoUsuario.OPERARIO)
                )

                // 🐔 Galpones iniciales de prueba en SQLite
                galponDao.insertarGalpon(
                    Galpon(
                        id = UUID.randomUUID().toString(),
                        granjaId = "GRANJA-01",
                        nombreGalpon = "Galpón 1 - Sector Norte",
                        temperatura = 24.5,
                        humedad = 60.0,
                        estadoGalpon = EstadoGalpon.NORMAL,
                        fechaActualizacion = Date(),
                        alertasActivas = false
                    )
                )
            }
        }
    }
}



