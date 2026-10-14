package com.example.formularios

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.formularios.ui.navigation.AppNavigation
import com.example.formularios.ui.theme.FormulariosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FormulariosTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    //Aqui insertamos el flujo de navegación en lugar del Greeting()
                    Box(
                        modifier = Modifier.padding(innerPadding)
                    ){
                        AppNavigation()
                    }
                }
            }
        }
    }
}