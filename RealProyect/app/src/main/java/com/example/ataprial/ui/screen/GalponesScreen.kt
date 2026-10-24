package com.example.ataprial.ui.screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Addchart
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.ataprial.data.model.EstadoGalpon
import com.example.ataprial.data.model.Galpon
import com.example.ataprial.data.model.GalponConMediciones
import com.example.ataprial.data.model.TipoUsuario
import com.example.ataprial.ui.viewmodel.GalponesViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalponesScreen(
    viewModel: GalponesViewModel,
    rolUsuario: TipoUsuario = TipoUsuario.OPERARIO,
    granjaId: String,
    onVolver: () -> Unit,
    onNavegarAHistorial: () -> Unit
) {
    val context = LocalContext.current
    val listaGalpones by viewModel.galpones.collectAsState()

    var nombreGalpon by remember { mutableStateOf("") }
    var temperatura by remember { mutableStateOf("") }
    var humedad by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf<String?>(null) }
    var mensajeConfirmacion by remember { mutableStateOf<String?>(null) }

    // Estados para diálogos
    var galponSeleccionadoParaMedicion by remember { mutableStateOf<Galpon?>(null) }
    var galponSeleccionadoParaHistorial by remember { mutableStateOf<GalponConMediciones?>(null) }
    var nuevaTempInput by remember { mutableStateOf("") }
    var nuevaHumInput by remember { mutableStateOf("") }

    LaunchedEffect(granjaId) {
        viewModel.establecerGranjaActiva(granjaId)
    }

    val launcherPermiso = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { _ -> }
    )

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                launcherPermiso.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

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
                        val exito = viewModel.agregarGalpon(nombreGalpon, temperatura, humedad, granjaId)
                        if (exito) {
                            val tempDouble = temperatura.toDoubleOrNull() ?: 0.0

                            if (tempDouble > 35.0) {
                                hacerVibrarDispositivo(context)
                                dispararNotificacionAlerta(context, nombreGalpon, tempDouble)
                            }

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

            AnimatedVisibility(
                visible = mensajeConfirmacion != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                mensajeConfirmacion?.let { mensaje ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Text(
                            text = mensaje,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

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
                    key = { it.galpon.id }
                ) { galponConMediciones ->
                    var visibleCard by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) {
                        visibleCard = true
                    }

                    AnimatedVisibility(
                        visible = visibleCard,
                        enter = slideInVertically(
                            initialOffsetY = { fullHeight -> fullHeight / 2 },
                            animationSpec = tween(durationMillis = 400)
                        ) + fadeIn(
                            animationSpec = tween(durationMillis = 400)
                        )
                    ) {
                        Box(modifier = Modifier.animateItem()) {
                            GalponItemCard(
                                galpon = galponConMediciones.galpon,
                                rolUsuario = rolUsuario,
                                onVerHistorialClick = {
                                    viewModel.establecerGranjaActiva("")
                                    onNavegarAHistorial()
                                },
                                onMedirClick = {
                                    galponSeleccionadoParaMedicion = galponConMediciones.galpon
                                    nuevaTempInput = ""
                                    nuevaHumInput = ""
                                },
                                onEliminarClick = { idEliminar ->
                                    viewModel.eliminarGalpon(idEliminar)
                                    Toast.makeText(context, "Galpón ${galponConMediciones.galpon.nombreGalpon} eliminado", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // 1. DIÁLOGO PARA REGISTRAR NUEVA MEDICIÓN
    if (galponSeleccionadoParaMedicion != null) {
        AlertDialog(
            onDismissRequest = { galponSeleccionadoParaMedicion = null },
            title = { Text("Nueva Medición: ${galponSeleccionadoParaMedicion?.nombreGalpon}") },
            text = {
                Column {
                    Text("Ingresa los nuevos valores censados:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = nuevaTempInput,
                        onValueChange = { nuevaTempInput = it },
                        label = { Text("Temperatura (°C)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = nuevaHumInput,
                        onValueChange = { nuevaHumInput = it },
                        label = { Text("Humedad (%)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val galpon = galponSeleccionadoParaMedicion!!
                        val exito = viewModel.registrarMedicion(galpon, nuevaTempInput, nuevaHumInput)
                        if (exito) {
                            val tempVal = nuevaTempInput.toDoubleOrNull() ?: 0.0
                            if (tempVal > 35.0) {
                                hacerVibrarDispositivo(context)
                                dispararNotificacionAlerta(context, galpon.nombreGalpon, tempVal)
                            }
                            mensajeConfirmacion = "✅ Medición actualizada en ${galpon.nombreGalpon}"
                        } else {
                            Toast.makeText(context, "Valores inválidos", Toast.LENGTH_SHORT).show()
                        }
                        galponSeleccionadoParaMedicion = null
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { galponSeleccionadoParaMedicion = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // 2. DIÁLOGO PARA VER HISTORIAL DE MEDICIONES
    if (galponSeleccionadoParaHistorial != null) {
        val galponActual = galponSeleccionadoParaHistorial!!.galpon
        val historial = galponSeleccionadoParaHistorial!!.historialMediciones

        AlertDialog(
            onDismissRequest = { galponSeleccionadoParaHistorial = null },
            title = { Text("Historial: ${galponActual.nombreGalpon}") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 350.dp)
                ) {
                    if (historial.isEmpty()) {
                        Text(
                            text = "No se registran mediciones previas.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text(
                            text = "Registros totales: ${historial.size}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(historial.reversed()) { medicion ->
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "🌡️ ${medicion.temperatura} °C",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "💧 ${medicion.humedad} %",
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "📅 ${medicion.fechaHora}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { galponSeleccionadoParaHistorial = null }) {
                    Text("Cerrar")
                }
            }
        )
    }
}

@Composable
fun GalponItemCard(
    galpon: Galpon,
    rolUsuario: TipoUsuario,
    onVerHistorialClick: () -> Unit,
    onMedirClick: () -> Unit,
    onEliminarClick: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isPressed by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }

    val scaleByState by animateFloatAsState(
        targetValue = if (isDeleting) 1.2f else if (isPressed) 0.96f else 1f,
        label = "card_scale"
    )
    val rotation by animateFloatAsState(
        targetValue = if (isDeleting) 25f else 0f,
        animationSpec = tween(durationMillis = 350, easing = androidx.compose.animation.core.FastOutSlowInEasing),
        label = "card_rotation"
    )
    val alpha by animateFloatAsState(
        targetValue = if (isDeleting) 0f else 1f,
        animationSpec = tween(durationMillis = 300, delayMillis = 50),
        label = "card_alpha"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scaleByState)
            .rotate(rotation)
            .alpha(alpha),
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
                Row(verticalAlignment = Alignment.CenterVertically) {
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

                    if (rolUsuario == TipoUsuario.JEFATURA) {
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = {
                            if (!isDeleting) {
                                isDeleting = true
                                hacerVibrarDispositivo(context)
                                scope.launch {
                                    kotlinx.coroutines.delay(350)
                                    onEliminarClick(galpon.id)
                                }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar galpón",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
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

            Spacer(modifier = Modifier.height(12.dp))

            // Botones de acción inferiores
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onMedirClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(imageVector = Icons.Default.Addchart, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Medir", style = MaterialTheme.typography.labelMedium)
                }

                // Botón de historial visible tanto para Operarios como para Supervisores
                if (rolUsuario == TipoUsuario.OPERARIO || rolUsuario == TipoUsuario.SUPERVISOR || rolUsuario == TipoUsuario.JEFATURA) {
                    OutlinedButton(
                        onClick = {
                            isPressed = true
                            onVerHistorialClick()
                        },
                        modifier = Modifier.weight(1.5f)
                    ) {
                        LaunchedEffect(isPressed) {
                            if (isPressed) {
                                kotlinx.coroutines.delay(100)
                                isPressed = false
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Historial", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}

fun dispararNotificacionAlerta(context: Context, nombreGalpon: String, temp: Double) {
    val channelId = "alerta_galpones_channel"
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = android.app.NotificationChannel(
            channelId,
            "Alertas de Temperatura",
            android.app.NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Canal para alertas críticas de galpones"
        }
        notificationManager.createNotificationChannel(channel)
    }
    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.ic_dialog_alert)
        .setContentTitle("⚠️ ¡ALERTA CRÍTICA EN GALPÓN!")
        .setContentText("El galpón '$nombreGalpon' registró $temp °C")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .build()

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
        NotificationManagerCompat.from(context).notify(System.currentTimeMillis().toInt(), notification)
    }
}

fun hacerVibrarDispositivo(context: Context) {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(700, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(700)
    }
}