package com.example.atap.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.atap.data.model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel(){

    private val _productos = MutableStateFlow(
        listOf(
            Producto(
            id=1,
            nombre = "Notebook",
            precio = 599990
            ),

            Producto(
            id =2,
            nombre = "Mouse",
            precio = 19990
            ),

            Producto(
            id =3,
            nombre = "Teclado",
            precio = 39990
            )
        )
    )

    val productos = _productos.asStateFlow()

}
//El ViewModel será responsable de mantener los datos de nuestra aplicación.
//La interfaz solamente los observará.

