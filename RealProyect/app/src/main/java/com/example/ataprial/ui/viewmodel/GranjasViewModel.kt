package com.example.ataprial.ui.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ataprial.data.dao.GranjaDao
import com.example.ataprial.data.model.Granja
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class GranjasViewModel(private val granjaDao: GranjaDao) : ViewModel() {


    val granjas: StateFlow<List<Granja>> = granjaDao.obtenerTodasLasGranjas()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )


    fun agregarGranja(nombre: String, ubicacion: String): Boolean {
        if (nombre.isBlank()) {
            return false
        }

        val nuevaGranja = Granja(
            id = UUID.randomUUID().toString(),
            nombreGranja = nombre.trim(),
            ubicacion = ubicacion.trim()
        )

        viewModelScope.launch {
            granjaDao.insertarGranja(nuevaGranja)
        }

        return true
    }


    fun eliminarGranja(id: String) {
        viewModelScope.launch {
            granjaDao.eliminarGranja(id)
        }
    }
}

class GranjasViewModelFactory(private val granjaDao: GranjaDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GranjasViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GranjasViewModel(granjaDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
