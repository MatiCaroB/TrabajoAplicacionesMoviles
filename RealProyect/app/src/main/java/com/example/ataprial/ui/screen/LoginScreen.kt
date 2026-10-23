package com.example.ataprial.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.ataprial.ui.viewmodel.UsuarioViewModel

@Composable
fun LoginScreen(
    viewModel: UsuarioViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var claveVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "ATAPRIAL",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Inicio de Sesión",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // INPUT: Correo electrónico
        OutlinedTextField(
            value = viewModel.loginCorreo,
            onValueChange = { nuevoTexto: String -> viewModel.loginCorreo = nuevoTexto },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // INPUT: Contraseña
        OutlinedTextField(
            value = viewModel.loginClave,
            onValueChange = { nuevoTexto: String -> viewModel.loginClave = nuevoTexto },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (claveVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val imagen = if (claveVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { claveVisible = !claveVisible }) {
                    Icon(imageVector = imagen, contentDescription = null)
                }
            }
        )

        // Alerta de error con IF clásico (Sin problemas de inferencia de tipos)
        val errorActivo = viewModel.loginError
        if (errorActivo != null) {
            Text(
                text = errorActivo.toString(),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // BOTÓN: Ingresar
        Button(
            onClick = {
                viewModel.iniciarSesion {
                    onLoginSuccess()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Ingresar")
        }
        Spacer(modifier = Modifier.height(16.dp))

        // 💡 NUEVO BOTÓN: Crear cuenta alternativa
        TextButton(
            onClick = onNavigateToRegister
        ) {
            Text(
                text = "¿No tienes una cuenta? Regístrate aquí",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
