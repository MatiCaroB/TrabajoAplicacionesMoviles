package com.example.ataprial.data.database

import android.content.Context

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.ataprial.data.dao.UsuarioDao
import com.example.ataprial.data.model.TipoUsuario
import com.example.ataprial.data.model.Usuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Usuario::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao

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
                val dao = getDatabase(context).usuarioDao()

                // 🔴 1. Cuenta de Jefatura
                dao.insertarUsuario(
                    Usuario(id = "jefe-123", nombre = "Carlos Jefe", correo = "jefe@mail.com", clave ="123456", rol = TipoUsuario.JEFATURA)
                )

                // 🟡 2. Cuenta de Supervisor
                dao.insertarUsuario(
                    Usuario(id = "super-123", nombre = "Ana Supervisora", correo = "supervisor@mail.com", clave ="654321", rol = TipoUsuario.SUPERVISOR)
                )

                // 🔵 3. Cuenta de Operario
                dao.insertarUsuario(
                    Usuario(id = "ope-123", nombre = "Juan Operario", correo = "operario@mail.com", clave ="papoi123", rol = TipoUsuario.OPERARIO)
                )
            }
        }
    }
}



