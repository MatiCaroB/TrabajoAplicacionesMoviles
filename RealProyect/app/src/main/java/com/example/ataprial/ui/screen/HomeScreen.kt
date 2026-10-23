package com.example.ataprial.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(
    onNavegarAUsuarios: () -> Unit,
    onNavegarAGalpones: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Sistema Ataprial",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Seleccione una opción para continuar",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
        )

        // OPCIÓN 1: Gestión de Usuarios (ACTIVA)
        OpcionHomeCard(
            titulo = "Gestión de Usuarios",
            descripcion = "Crear y administrar cuentas con roles",
            icono = Icons.Default.PersonAdd,
            onClick = onNavegarAUsuarios,
            habilitado = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // OPCIÓN 2: Galpones (Deshabilitada por ahora)
        OpcionHomeCard(
            titulo = "Control de Galpones",
            descripcion = "Monitorear galpones y estado ambiental",
            icono = Icons.Default.Warehouse,
            onClick = onNavegarAGalpones,
            habilitado = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // OPCIÓN 3: Monitoreo (Deshabilitada por ahora)
        OpcionHomeCard(
            titulo = "Monitoreo de Sensores",
            descripcion = "Próximamente",
            icono = Icons.Default.Sensors,
            onClick = {},
            habilitado = false
        )
    }
}

@Composable
fun OpcionHomeCard(
    titulo: String,
    descripcion: String,
    icono: ImageVector,
    onClick: () -> Unit,
    habilitado: Boolean
) {
    Card(
        onClick = onClick,
        enabled = habilitado,
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (habilitado) 4.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = if (habilitado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = titulo,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (habilitado) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
                )
                Text(
                    text = descripcion,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}