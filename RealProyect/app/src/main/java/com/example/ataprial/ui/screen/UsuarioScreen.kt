package com.example.ataprial.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ataprial.data.model.TipoUsuario
import com.example.ataprial.data.model.Usuario
import com.example.ataprial.ui.viewmodel.UsuarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuarioScreen(
    viewModel: UsuarioViewModel,
    onCerrarSesion: () -> Unit
) {
    val estado by viewModel.estado.collectAsState()
    val listaUsuarios by viewModel.listaUsuarios.collectAsState()
    val usuarioLogueado by viewModel.usuarioLogueado.collectAsState()
    val context = LocalContext.current

    var menuExpandido by remember { mutableStateOf(false) }
    var claveVisible by remember { mutableStateOf(false) }

    val rolActivo = usuarioLogueado?.rol ?: TipoUsuario.OPERARIO
    val ocultarFiltroOSesion = usuarioLogueado == null

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp)
    ) {
        // ENCABEZADO SUPERIOR
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (usuarioLogueado != null) {
                Column {
                    Text(text = "Sesión: ${usuarioLogueado?.nombre}", fontWeight = FontWeight.Bold)
                    Text(text = "Rol: ${rolActivo.name}", color = Color.Gray, fontSize = 12.sp)
                }

                Button(
                    onClick = { viewModel.cerrarSesion(onNavigateOut = onCerrarSesion) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Cerrar Sesión", fontSize = 12.sp)
                }
            } else {
                Text(text = "Crea tu cuenta local", fontWeight = FontWeight.Bold, fontSize = 18.sp)

                TextButton(onClick = onCerrarSesion) {
                    Text("Volver al Login")
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(bottom = 16.dp))

        // 🟢 FORMULARIO DE CREACIÓN / AUTOREGISTRO RESTAURADO COMPLETAMENTE
        if (rolActivo == TipoUsuario.JEFATURA || ocultarFiltroOSesion) {
            Text(
                text = if (ocultarFiltroOSesion) "Formulario de Registro" else "Creación de Usuarios (Solo Jefatura)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp),
                color = if (ocultarFiltroOSesion) MaterialTheme.colorScheme.primary else Color(0xFF2E7D32)
            )

            // CAMPO: Nombre completo
            OutlinedTextField(
                value = estado.nombre,
                onValueChange = { viewModel.onNombreChange(it) },
                label = { Text("Nombre completo") },
                isError = estado.errores.nombre != null,
                supportingText = { estado.errores.nombre?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // CAMPO: Correo electrónico
            OutlinedTextField(
                value = estado.correo,
                onValueChange = { viewModel.onCorreoChange(it) },
                label = { Text("Correo electrónico") },
                isError = estado.errores.correo != null,
                supportingText = { estado.errores.correo?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // CAMPO: Contraseña
            OutlinedTextField(
                value = estado.clave,
                onValueChange = { viewModel.onClaveChange(it) },
                label = { Text("Contraseña") },
                isError = estado.errores.clave != null,
                supportingText = { estado.errores.clave?.let { Text(it) } },
                visualTransformation = if (claveVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    val imagenIcono = if (claveVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { claveVisible = !claveVisible }) {
                        Icon(imageVector = imagenIcono, contentDescription = null)
                    }
                }
            )

            // CAMPO: Selector de Categoría / Rol
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
                    modifier = Modifier.menuAnchor().fillMaxWidth()
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

            // BOTÓN: Registrar Usuario / Crear cuenta
            Button(
                onClick = {
                    if (viewModel.registrarUsuario()) {
                        Toast.makeText(context, "¡Usuario registrado exitosamente!", Toast.LENGTH_SHORT).show()
                        if (ocultarFiltroOSesion) {
                            onCerrarSesion() // Redirige al login de inmediato al registrarse desde afuera
                        }
                    } else {
                        Toast.makeText(context, "Por favor revise los campos", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text(if (ocultarFiltroOSesion) "Crear Cuenta" else "Registrar Usuario")
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // CONTROL DE PERMISOS PARA LA LISTA INFERIOR
        if (usuarioLogueado != null && rolActivo == TipoUsuario.OPERARIO) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Área de Operario:\nNo tienes permisos para gestionar o ver cuentas globales.",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            }
        } else if (usuarioLogueado != null) {
            // VISTA PANELES RESTRINGIDOS (Solo Supervisor y Jefatura logueados ven la lista)
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
                    UsuarioItemCard(
                        usuario = usuario,
                        mostrarBotonEliminar = rolActivo == TipoUsuario.JEFATURA,
                        onEliminarClick = {
                            viewModel.eliminarUsuario(usuario, rolDelEjecutor = rolActivo)
                            Toast.makeText(context, "Usuario ${usuario.nombre} eliminado", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun UsuarioItemCard(
    usuario: Usuario,
    mostrarBotonEliminar: Boolean,
    onEliminarClick: () -> Unit
) {
    val colorBadge = when (usuario.rol) {
        TipoUsuario.OPERARIO -> Color(0xFF1976D2)
        TipoUsuario.SUPERVISOR -> Color(0xFFED6C02)
        TipoUsuario.JEFATURA -> Color(0xFF2E7D32)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = usuario.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = usuario.correo, color = Color.Gray, fontSize = 13.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                if (mostrarBotonEliminar) {
                    IconButton(onClick = onEliminarClick) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}
