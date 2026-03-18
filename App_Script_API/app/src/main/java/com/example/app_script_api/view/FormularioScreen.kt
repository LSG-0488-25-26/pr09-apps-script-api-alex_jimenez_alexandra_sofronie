package com.example.app_script_api.view

import androidx.benchmark.traceprocessor.Row
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.app_script_api.viewmodel.FacturaViewModel

/**
 * Pantalla del formulari per crear factures/tíquets.
 * Layout d'una sola columna optimitzat per mòbil.
 */
@Composable
fun FormularioScreen(
    viewModel: FacturaViewModel,
    navController: NavHostController? = null
) {
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val enviado by viewModel.enviado.collectAsState()

    var nombre by remember { mutableStateOf(viewModel.obtenerUltimoNombre()) }
    var apellidos by remember { mutableStateOf("") }
    var dni by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var concepto by remember { mutableStateOf(viewModel.obtenerUltimoConcepto()) }
    var cantidad by remember { mutableStateOf("") }
    var precioUnitario by remember { mutableStateOf("") }

    var nombreError by remember { mutableStateOf("") }
    var conceptoError by remember { mutableStateOf("") }
    var cantidadError by remember { mutableStateOf("") }
    var precioError by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF6200EE)),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    "🧾 Generador de Factures",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Crea tíquets i factures des de la teva app",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "Formulari de Factura",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = nombre,
                        onValueChange = {
                            nombre = it
                            nombreError = ""
                        },
                        label = { Text("Nombre") },
                        isError = nombreError.isNotEmpty(),
                        supportingText = {
                            if (nombreError.isNotEmpty()) {
                                Text(nombreError, color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = apellidos,
                        onValueChange = { apellidos = it },
                        label = { Text("Cognoms") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = dni,
                        onValueChange = { dni = it },
                        label = { Text("DNI") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = direccion,
                        onValueChange = { direccion = it },
                        label = { Text("Adreça") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = concepto,
                        onValueChange = {
                            concepto = it
                            conceptoError = ""
                        },
                        label = { Text("Concepte") },
                        isError = conceptoError.isNotEmpty(),
                        supportingText = {
                            if (conceptoError.isNotEmpty()) {
                                Text(conceptoError, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = cantidad,
                            onValueChange = {
                                cantidad = it
                                cantidadError = ""
                            },
                            label = { Text("Quantitat") },
                            isError = cantidadError.isNotEmpty(),
                            supportingText = {
                                if (cantidadError.isNotEmpty()) {
                                    Text(cantidadError, color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        OutlinedTextField(
                            value = precioUnitario,
                            onValueChange = {
                                precioUnitario = it
                                precioError = ""
                            },
                            label = { Text("Preu unitari (€)") },
                            isError = precioError.isNotEmpty(),
                            supportingText = {
                                if (precioError.isNotEmpty()) {
                                    Text(precioError, color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    if (error != null) {
                        Text(
                            "Error: $error",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    if (enviado) {
                        Text(
                            "Factura enviada correctament!",
                            color = Color(0xFF2E7D32),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    Button(
                        onClick = {
                            nombreError = if (nombre.isBlank()) "Introdueix el nom" else ""
                            conceptoError = if (concepto.isBlank()) "Introdueix el concepte" else ""
                            cantidadError = if (cantidad.toIntOrNull()?.let { it > 0 } != true) "Quantitat vàlida" else ""
                            precioError = if (precioUnitario.toDoubleOrNull()?.let { it > 0 } != true) "Preu vàlid" else ""

                            if (nombreError.isEmpty() && conceptoError.isEmpty() && cantidadError.isEmpty() && precioError.isEmpty()) {
                                viewModel.crearFactura(
                                    nombre = nombre.trim(),
                                    apellidos = apellidos.trim(),
                                    dni = dni.trim(),
                                    direccion = direccion.trim(),
                                    concepto = concepto.trim(),
                                    cantidad = cantidad.toIntOrNull() ?: 0,
                                    precioUnitario = precioUnitario.toDoubleOrNull() ?: 0.0
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
                        enabled = !loading
                    ) {
                        if (loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.height(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Enviar factura", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}