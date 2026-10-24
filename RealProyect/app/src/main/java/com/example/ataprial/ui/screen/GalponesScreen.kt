package com.example.ataprial.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import com.example.ataprial.data.model.EstadoGalpon
import com.example.ataprial.data.model.Galpon
import com.example.ataprial.data.model.TipoUsuario
import com.example.ataprial.ui.viewmodel.GalponesViewModel
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalponesScreen(
    viewModel: GalponesViewModel,
    rolUsuario: TipoUsuario = TipoUsuario.OPERARIO,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    val listaGalpones by viewModel.galpones.collectAsState()

    var nombreGalpon by remember { mutableStateOf("") }
    var temperatura by remember { mutableStateOf("") }
    var humedad by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf<String?>(null) }
    var mensajeConfirmacion by remember { mutableStateOf<String?>(null) }

    val launcherPermiso = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { esOtorgado -> }
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
                        val exito = viewModel.agregarGalpon(nombreGalpon, temperatura, humedad)
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

            // Animación de notificación / mensaje de confirmación
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
                    key = { it.id }
                ) { galpon ->
                    // Estado local para disparar la animación de aparición inicial tipo slide/fade
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
    }
}

@Composable
fun GalponItemCard(
    galpon: Galpon,
    rolUsuario: TipoUsuario,
    onAccionOperario: () -> Unit,
    onAccionSupervisor: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.96f else 1f, label = "card_scale")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
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

            Spacer(modifier = Modifier.height(12.dp))

            when (rolUsuario) {
                TipoUsuario.OPERARIO -> {
                    Button(
                        onClick = {
                            isPressed = true
                            onAccionOperario()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        LaunchedEffect(isPressed) {
                            if (isPressed) {
                                kotlinx.coroutines.delay(100)
                                isPressed = false
                            }
                        }
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
                    OutlinedButton(
                        onClick = {
                            isPressed = true
                            onAccionSupervisor()
                        },
                        modifier = Modifier.fillMaxWidth()
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
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ver Historial de Mediciones")
                    }
                }
                TipoUsuario.JEFATURA -> {
                    // JEFATURA
                }
            }
        }
    }
}

fun dispararNotificacionAlerta(context: Context, nombreGalpon: String, temp: Double) {
    val channelId = "alerta_galpones_channel"
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "Alertas de Temperatura",
            NotificationManager.IMPORTANCE_HIGH
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

    notificationManager.notify(System.currentTimeMillis().toInt(), notification)
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