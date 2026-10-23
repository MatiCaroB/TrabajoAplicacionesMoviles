package com.example.ataprial.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ataprial.data.model.TipoUsuario
import com.example.ataprial.data.model.Usuario
import com.example.ataprial.ui.viewmodel.UsuarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuarioScreen(
    viewModel: UsuarioViewModel = viewModel()
) {
    val estado by viewModel.estado.collectAsState()
    val listaUsuarios by viewModel.listaUsuarios.collectAsState()
    val context = LocalContext.current
    var menuExpandido by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = "Creación de Usuarios",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // CAMPO: Nombre
        OutlinedTextField(
            value = estado.nombre,
            onValueChange = { viewModel.onNombreChange(it) },
            label = { Text("Nombre completo") },
            isError = estado.errores.nombre != null,
            supportingText = { estado.errores.nombre?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // CAMPO: Correo
        OutlinedTextField(
            value = estado.correo,
            onValueChange = { viewModel.onCorreoChange(it) },
            label = { Text("Correo electrónico") },
            isError = estado.errores.correo != null,
            supportingText = { estado.errores.correo?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // CAMPO: Clave
        OutlinedTextField(
            value = estado.clave,
            onValueChange = { viewModel.onClaveChange(it) },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            isError = estado.errores.clave != null,
            supportingText = { estado.errores.clave?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // CAMPO: Selector de Rol / Categoría
        ExposedDropdownMenuBox(
            expanded = menuExpandido,
            onExpandedChange = { menuExpandido = !menuExpandido }
        ) {
            OutlinedTextField(
                value = estado.rol,
                onValueChange = {},
                readOnly = true,
                label = { Text("Categoría / Rol") },
                isError = estado.errores.rol != null,
                supportingText = { estado.errores.rol?.let { Text(it) } },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuExpandido) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = menuExpandido,
                onDismissRequest = { menuExpandido = false }
            ) {
                TipoUsuario.values().forEach { tipo ->
                    DropdownMenuItem(
                        text = { Text(tipo.name) },
                        onClick = {
                            viewModel.onRolChange(tipo.name)
                            menuExpandido = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // BOTÓN: Registrar
        Button(
            onClick = {
                if (viewModel.registrarUsuario()) {
                    Toast.makeText(context, "¡Usuario creado exitosamente!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Por favor revise los campos", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Registrar Usuario")
        }

        Spacer(modifier = Modifier.height(20.dp))

        // LISTA DE USUARIOS REGISTRADOS
        Text(
            text = "Usuarios Registrados (${listaUsuarios.size})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(listaUsuarios) { usuario ->
                UsuarioItemCard(usuario = usuario)
            }
        }
    }
}
@Composable
fun UsuarioItemCard(usuario: Usuario) {
    val colorBadge = when (usuario.rol) {
        TipoUsuario.OPERARIO -> Color(0xFF1976D2)   // Azul
        TipoUsuario.SUPERVISOR -> Color(0xFFED6C02) // Naranja
        TipoUsuario.JEFATURA -> Color(0xFF2E7D32)   // Verde
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = usuario.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = usuario.correo, color = Color.Gray, fontSize = 13.sp)
            }

            Box(
                modifier = Modifier
                    .background(color = colorBadge, shape = RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = usuario.rol.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }
    }
}