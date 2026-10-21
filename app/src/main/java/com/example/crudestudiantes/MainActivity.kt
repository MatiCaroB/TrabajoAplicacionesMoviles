package com.example.crudestudiantes



import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.crudestudiantes.data.EstudianteDatabase
import com.example.crudestudiantes.data.EstudianteRepository
import com.example.crudestudiantes.ui.screen.EstudianteScreen
import com.example.crudestudiantes.ui.viewmodel.EstudianteViewModel

class EstudianteViewModelFactory(
    private val repository: EstudianteRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(
                EstudianteViewModel::class.java
            )
        ) {
            @Suppress("UNCHECKED_CAST")
            return EstudianteViewModel(repository) as T
        }

        throw IllegalArgumentException(
            "ViewModel desconocido"
        )
    }
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = EstudianteDatabase.getDatabase(this)
        val repository = EstudianteRepository(
            database.estudianteDao()
        )

        val factory = EstudianteViewModelFactory(repository)

        setContent {
            val viewModel: EstudianteViewModel = viewModel(
                factory = factory
            )

            EstudianteScreen(viewModel)
        }
    }
}
