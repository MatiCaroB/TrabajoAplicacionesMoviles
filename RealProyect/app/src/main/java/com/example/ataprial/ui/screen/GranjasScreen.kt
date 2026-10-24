package com.example.ataprial.ui.screen


import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn

import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ataprial.data.model.Granja
import com.example.ataprial.data.model.TipoUsuario
import com.example.ataprial.ui.viewmodel.GranjasViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GranjasScreen(
    viewModel: GranjasViewModel,
    rolUsuario: TipoUsuario,
    onGranjaClick: (String) -> Unit,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    val listaGranjas by viewModel.granjas.collectAsState(initial = emptyList())

    var nombreGranja by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Granjas") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
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
                Text("Registrar Nueva Granja (Jefatura)", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = nombreGranja,
                    onValueChange = { nombreGranja = it; mensajeError = null },
                    label = { Text("Nombre de la Granja") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = ubicacion,
                    onValueChange = { ubicacion = it },
                    label = { Text("Ubicación (Opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                mensajeError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        if (nombreGranja.isNotBlank()) {
                            viewModel.agregarGranja(nombreGranja, ubicacion)
                            nombreGranja = ""
                            ubicacion = ""
                        } else {
                            mensajeError = "El nombre no puede estar vacío."
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar Granja")
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            }

            Text("Granjas Disponibles (${listaGranjas.size})", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                items(items = listaGranjas, key = { it.id }) { granja ->
                    var visibleCard by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) { visibleCard = true }

                    AnimatedVisibility(
                        visible = visibleCard,
                        enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(tween(400))
                    ) {
                        Box(modifier = Modifier.animateItem()) {
                            GranjaItemCard(
                                granja = granja,
                                mostrarEliminar = rolUsuario == TipoUsuario.JEFATURA,
                                onClick = { onGranjaClick(granja.id) },
                                onEliminar = {
                                    viewModel.eliminarGranja(granja.id)
                                    Toast.makeText(context, "Granja eliminada", Toast.LENGTH_SHORT).show()
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
fun GranjaItemCard(granja: Granja, mostrarEliminar: Boolean, onClick: () -> Unit, onEliminar: () -> Unit) {
    val scope = rememberCoroutineScope()
    var isDeleting by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isDeleting) 1.2f else 1f, label = "scale")
    val rotation by animateFloatAsState(if (isDeleting) 25f else 0f, label = "rotation")
    val alpha by animateFloatAsState(if (isDeleting) 0f else 1f, label = "alpha")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .rotate(rotation)
            .alpha(alpha)
            .clickable { if (!isDeleting) onClick() },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(granja.nombreGranja, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                if (granja.ubicacion.isNotBlank()) {
                    Text(granja.ubicacion, color = Color.Gray, fontSize = 13.sp)
                }
            }
            if (mostrarEliminar) {
                IconButton(onClick = {
                    isDeleting = true
                    scope.launch { delay(350); onEliminar() }
                }) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Borrar", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}