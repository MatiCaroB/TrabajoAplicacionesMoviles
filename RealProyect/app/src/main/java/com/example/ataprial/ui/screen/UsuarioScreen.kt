package com.example.ataprial.ui.screen

import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        // ENCABEZADO SUPERIOR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (usuarioLogueado != null) {
                Column {
                    Text(
                        text = "Sesión: ${usuarioLogueado?.nombre}",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Rol: ${rolActivo.name}",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }

                Button(
                    onClick = {
                        viewModel.cerrarSesion(onNavigateOut = onCerrarSesion)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Cerrar Sesión", fontSize = 12.sp)
                }
            } else {
                Column {
                    Text(
                        text = "Sin Sesión Activa",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "Crea una cuenta o regresa al inicio",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }

                OutlinedButton(onClick = onCerrarSesion) {
                    Text("Ir al Login", fontSize = 12.sp)
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(bottom = 16.dp))

        // FORMULARIO DE CREACIÓN / REGISTRO
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
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
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
                singleLine = true,
                trailingIcon = {
                    val imagenIcono = if (claveVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val descripcion = if (claveVisible) "Ocultar contraseña" else "Mostrar contraseña"
                    IconButton(onClick = { claveVisible = !claveVisible }) {
                        Icon(imageVector = imagenIcono, contentDescription = descripcion)
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
                    modifier = Modifier
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = menuExpandido,
                    onDismissRequest = { menuExpandido = false }
                ) {
                    TipoUsuario.entries.forEach { tipo ->
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
                            onCerrarSesion()
                        }
                    } else {
                        Toast.makeText(context, "Por favor revise los campos", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(if (ocultarFiltroOSesion) "Crear Cuenta e Iniciar Sesión" else "Registrar Usuario")
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
                items(
                    items = listaUsuarios,
                    key = { it.correo }
                ) { usuario ->
                    Box(modifier = Modifier.animateItem()) {
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
}

@Composable
fun UsuarioItemCard(
    usuario: Usuario,
    mostrarBotonEliminar: Boolean,
    onEliminarClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isDeleting by remember { mutableStateOf(false) }

    // Animación de ruptura (escala, rotación y alpha)
    val scale by animateFloatAsState(
        targetValue = if (isDeleting) 1.2f else 1f,
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
        label = "scale_break"
    )
    val rotation by animateFloatAsState(
        targetValue = if (isDeleting) 25f else 0f,
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
        label = "rotation_break"
    )
    val alpha by animateFloatAsState(
        targetValue = if (isDeleting) 0f else 1f,
        animationSpec = tween(durationMillis = 300, delayMillis = 50),
        label = "alpha_break"
    )

    val colorBadge = when (usuario.rol) {
        TipoUsuario.OPERARIO -> Color(0xFF1976D2)
        TipoUsuario.SUPERVISOR -> Color(0xFFED6C02)
        TipoUsuario.JEFATURA -> Color(0xFF2E7D32)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .rotate(rotation)
            .alpha(alpha),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
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
                    IconButton(onClick = {
                        if (!isDeleting) {
                            isDeleting = true


                            hacerVibrarEliminacion(context)
                            reproducirSonidoEliminacion(context)

                            scope.launch {
                                delay(350)
                                onEliminarClick()
                            }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar usuario",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

// Funciones auxiliares para vibración y sonido
fun hacerVibrarEliminacion(context: Context) {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(80)
    }
}

fun reproducirSonidoEliminacion(context: Context) {
    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE, 1.0f)
}