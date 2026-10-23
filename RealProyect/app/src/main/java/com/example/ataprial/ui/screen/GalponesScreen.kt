package com.example.ataprial.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.ataprial.data.model.EstadoGalpon
import com.example.ataprial.data.model.Galpon
import com.example.ataprial.data.model.TipoUsuario
import com.example.ataprial.ui.viewmodel.GalponesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalponesScreen(
    viewModel: GalponesViewModel,
    rolUsuario: TipoUsuario = TipoUsuario.OPERARIO, // Rol del usuario logueado
    onVolver: () -> Unit
) {
    val listaGalpones by viewModel.galpones.collectAsState()

    var nombreGalpon by remember { mutableStateOf("") }
    var temperatura by remember { mutableStateOf("") }
    var humedad by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf<String?>(null) }
    var mensajeConfirmacion by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Control de Galpones") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // 🔴 SOLO JEFATURA: Formulario para registrar nuevos galpones
            if (rolUsuario == TipoUsuario.JEFATURA) {
                Text(
                    text = "Registrar Nuevo Galpón (Jefatura)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = nombreGalpon,
                    onValueChange = {
                        nombreGalpon = it
                        mensajeError = null
                    },
                    label = { Text("Nombre del galpón") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = temperatura,
                        onValueChange = {
                            temperatura = it
                            mensajeError = null
                        },
                        label = { Text("Temp (°C)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = humedad,
                        onValueChange = {
                            humedad = it
                            mensajeError = null
                        },
                        label = { Text("Humedad (%)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                if (mensajeError != null) {
                    Text(
                        text = mensajeError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        val exito = viewModel.agregarGalpon(nombreGalpon, temperatura, humedad)
                        if (exito) {
                            nombreGalpon = ""
                            temperatura = ""
                            humedad = ""
                            mensajeError = null
                        } else {
                            mensajeError = "Completa el nombre, temperatura y humedad válidos."
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar Galpón")
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            }

            // Mensaje informativo temporal al realizar una acción de Operario o Supervisor
            if (mensajeConfirmacion != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Text(
                        text = mensajeConfirmacion!!,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // VISTA DE GALPONES PARA TODOS LOS ROLES
            Text(
                text = "Galpones Registrados (${listaGalpones.size}) - Rol: ${rolUsuario.name}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(
                    items = listaGalpones,
                    key = { it.id }
                ) { galpon ->
                    GalponItemCard(
                        galpon = galpon,
                        rolUsuario = rolUsuario,
                        onAccionOperario = {
                            mensajeConfirmacion = "✅ Operario confirmó acción en ${galpon.nombreGalpon}"
                        },
                        onAccionSupervisor = {
                            mensajeConfirmacion = "📊 Supervisor consultó el historial de ${galpon.nombreGalpon}"
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun GalponItemCard(
    galpon: Galpon,
    rolUsuario: TipoUsuario,
    onAccionOperario: () -> Unit,
    onAccionSupervisor: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = galpon.nombreGalpon,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                val colorEstado = when (galpon.estadoGalpon) {
                    EstadoGalpon.NORMAL -> MaterialTheme.colorScheme.primaryContainer
                    EstadoGalpon.ADVERTENCIA -> MaterialTheme.colorScheme.errorContainer
                    EstadoGalpon.CRITICO -> MaterialTheme.colorScheme.error
                }

                Surface(
                    color = colorEstado,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = galpon.estadoGalpon.name,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Temp: ${galpon.temperatura} °C", style = MaterialTheme.typography.bodySmall)
                Text(text = "Humedad: ${galpon.humedad} %", style = MaterialTheme.typography.bodySmall)
                Text(
                    text = if (galpon.alertasActivas) "⚠️ Alerta" else "✅ OK",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }

            // ACCIONES DIFERENCIADAS POR ROL
            Spacer(modifier = Modifier.height(12.dp))

            when (rolUsuario) {
                TipoUsuario.OPERARIO -> {
                    // 🔵 OPERARIO: Registrar/Confirmar acción realizada
                    Button(
                        onClick = onAccionOperario,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Confirmar Acción / Atender Alerta")
                    }
                }
                TipoUsuario.SUPERVISOR -> {
                    // 🟡 SUPERVISOR: Revisar historial de eventos e inspecciones
                    OutlinedButton(
                        onClick = onAccionSupervisor,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ver Historial de Mediciones")
                    }
                }
                TipoUsuario.JEFATURA -> {
                    // 🔴 JEFATURA: Ya administra el formulario arriba, así que ve el estado compacto sin saturar botones
                }
            }
        }
    }
}