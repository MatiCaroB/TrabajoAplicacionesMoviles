package com.example.ataprial.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ataprial.data.model.EstadoGalpon
import com.example.ataprial.data.model.Galpon
import com.example.ataprial.ui.viewmodel.GalponesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalponesScreen(
    esJefatura: Boolean = true, // Controla si se muestra el formulario de registro
    onVolver: () -> Unit,       // Callback para la flecha de regreso
    viewModel: GalponesViewModel = viewModel()
) {
    val listaGalpones by viewModel.galpones.collectAsState()

    var nombreGalpon by remember { mutableStateOf("") }
    var temperatura by remember { mutableStateOf("") }
    var humedad by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf<String?>(null) }

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
            // SOLO JEFATURA: Formulario para registrar
            if (esJefatura) {
                Text(
                    text = "Registrar Nuevo Galpón",
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

            // VISTA COMÚN (Jefatura y Operario): Lista de Galpones
            Text(
                text = "Galpones Registrados (${listaGalpones.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(listaGalpones) { galpon ->
                    GalponItemCard(galpon)
                }
            }
        }
    }
}

@Composable
fun GalponItemCard(galpon: Galpon) {
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
                Text(
                    text = "Temp: ${galpon.temperatura} °C",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Humedad: ${galpon.humedad} %",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = if (galpon.alertasActivas) "⚠️ Alerta" else "✅ OK",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
// me imagino que aquí podríamos hacer la interfaz respecto a galpones, se me ocurre un menú donde aparezcan todos los galpones y al presionar en cada uno te puedas
// ir a sus detalles, serían como tarjetas, pero puedes filtrarlos para llegar a específicos más rápido
// y en las tarjetas de los galpones información resumida de mayor importancia, como temperaturas o que si alguno esté en alerta siempre aparezca de los primeros you know