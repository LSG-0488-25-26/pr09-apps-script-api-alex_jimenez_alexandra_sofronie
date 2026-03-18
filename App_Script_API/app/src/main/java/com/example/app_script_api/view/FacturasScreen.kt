package com.example.app_script_api.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.app_script_api.model.Estadisticas
import com.example.app_script_api.model.Factura
import com.example.app_script_api.routes.Routes
import com.example.app_script_api.viewmodel.FacturaViewModel

/**
 * Pantalla que mostra les factures obtingudes de l'API (GET).
 * Inclou cerca per nom i estadístiques.
 */

@Composable
fun FacturasScreen(
    viewModel: FacturaViewModel,
    navController: NavHostController? = null
) {
    val facturas by viewModel.facturas.collectAsState()
    val facturasFiltradas by viewModel.facturasFiltradas.collectAsState()
    val estadisticas by viewModel.estadisticas.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    var buscarNombre by remember { mutableStateOf("") }
    var mostrarEstadisticas by remember { mutableStateOf(false) }
    var facturaAEditar by remember { mutableStateOf<Factura?>(null) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        viewModel.cargarFacturas()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Card de capçalera (compacta per mòbil)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF6200EE)),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    "📋 Llistat de Factures",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Dades actualitzades des del Google Sheet",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }

        // Card de búsqueda i formulario
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = buscarNombre,
                onValueChange = { buscarNombre = it },
                label = { Text("Cercar per nom") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Cercar")
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        focusManager.clearFocus()
                        if (buscarNombre.isNotBlank()) {
                            viewModel.buscarPorNombre(buscarNombre.trim())
                        }
                    }
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        if (buscarNombre.isNotBlank()) {
                            viewModel.buscarPorNombre(buscarNombre.trim())
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cercar")
                }
                IconButton(
                    onClick = {
                        buscarNombre = ""
                        viewModel.cargarFacturas()
                    }
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Actualitzar", tint = Color(0xFF6200EE))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    mostrarEstadisticas = !mostrarEstadisticas
                    if (mostrarEstadisticas) viewModel.cargarEstadisticas()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (mostrarEstadisticas) "Amagar stats" else "Estadístiques")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        when {
            loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF6200EE))
                }
            }
            error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                "Error: $error",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(onClick = { viewModel.cargarFacturas() }) {
                                Text("Tornar a intentar")
                            }
                        }
                    }
                }
            }

            else -> {
                val listado = if (buscarNombre.isNotEmpty()) facturasFiltradas else facturas
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    if (mostrarEstadisticas && estadisticas != null) {
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            EstadisticasCard(estadisticas!!)
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                    when {
                        listado.isEmpty() -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .padding(horizontal = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(32.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("📭", fontSize = 40.sp)
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            if (buscarNombre.isNotEmpty())
                                                "No s'han trobat factures per a \"$buscarNombre\""
                                            else
                                                "No hi ha factures encara",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color.Gray
                                        )
                                        Text(
                                            if (buscarNombre.isNotEmpty())
                                                "Prova amb un altre nom"
                                            else
                                                "Crea la primera des del formulari",
                                            fontSize = 14.sp,
                                            color = Color.Gray,
                                            modifier = Modifier.padding(top = 8.dp)
                                        )
                                    }
                                }
                            }
                        }

                        else -> {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .padding(horizontal = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(bottom = 16.dp)
                            ) {
                                items(listado, key = { it.id }) { factura ->
                                    FacturaCard(
                                        factura = factura,
                                        viewModel = viewModel,
                                        onEditar = { facturaAEditar = it }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (navController != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navController.navigate(Routes.Formulario.route) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text("Tornar al formulari")
            }
        }

        //Editamos la factura
        facturaAEditar?.let { factura ->
                var nombre by remember(factura.id) { mutableStateOf(factura.nombre) }
                var apellidos by remember(factura.id) { mutableStateOf(factura.apellidos) }
                var dni by remember(factura.id) { mutableStateOf(factura.dni) }
                var direccion by remember(factura.id) { mutableStateOf(factura.direccion) }
                var concepto by remember(factura.id) { mutableStateOf(factura.concepto) }
                var cantidad by remember(factura.id) { mutableStateOf(factura.cantidad.toString()) }
                var precioUnitario by remember(factura.id) { mutableStateOf(factura.precioUnitario.toString()) }

                //AlertDialog para editar la factura
                AlertDialog(
                    onDismissRequest = { facturaAEditar = null },
                    title = { Text("Editar factura #${factura.id}") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(apellidos, { apellidos = it }, label = { Text("Cognoms") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(dni, { dni = it }, label = { Text("DNI") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(direccion, { direccion = it }, label = { Text("Adreça") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(concepto, { concepto = it }, label = { Text("Concepte") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(cantidad, { cantidad = it }, label = { Text("Quantitat") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                            OutlinedTextField(precioUnitario, { precioUnitario = it }, label = { Text("Preu unitari (€)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.actualizarFactura(
                                    id = factura.id,
                                    nombre = nombre.trim().ifEmpty { null },
                                    apellidos = apellidos.trim().ifEmpty { null },
                                    dni = dni.trim().ifEmpty { null },
                                    direccion = direccion.trim().ifEmpty { null },
                                    concepto = concepto.trim().ifEmpty { null },
                                    cantidad = cantidad.toIntOrNull(),
                                    precioUnitario = precioUnitario.toDoubleOrNull()
                                )
                                facturaAEditar = null
                            }
                        ) {
                            Text("Guardar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { facturaAEditar = null }) {
                            Text("Cancel·lar")
                        }
                    }
                )
            }
    }
}

@Composable
fun FacturaCard(factura: Factura, viewModel: FacturaViewModel, onEditar: (Factura) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#${factura.id} - ${factura.nombre}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6200EE)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = factura.fecha,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    IconButton(onClick = { onEditar(factura) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color(0xFF6200EE))
                    }
                    IconButton(onClick = { viewModel.eliminarFactura(factura.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFB00020))
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            Text("Concepte: ${factura.concepto}", fontSize = 16.sp)

            if (factura.apellidos.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text("Cognoms: ${factura.apellidos}", fontSize = 14.sp, color = Color.Gray)
            }
            if (factura.dni.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text("DNI: ${factura.dni}", fontSize = 14.sp, color = Color.Gray)
            }
            if (factura.direccion.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text("Adreça: ${factura.direccion}", fontSize = 14.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Quantitat: ${factura.cantidad}", fontSize = 14.sp, color = Color.Gray)
                Text("Preu unit.: %.2f €".format(factura.precioUnitario), fontSize = 14.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Total: %.2f €".format(factura.total),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )
        }
    }
}

@Composable
fun EstadisticasCard(estadisticas: Estadisticas) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            //Estadisticas de factures
            Text(
                "📊 Estadístiques",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                //TOTAL facturas
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Total factures", fontSize = 12.sp, color = Color.Gray)
                    Text("${estadisticas.totalFacturas}", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                }

                //Suma total
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Suma total", fontSize = 12.sp, color = Color.Gray)
                    Text("%.2f €".format(estadisticas.sumaTotal), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                }

                //Mitjana
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Mitjana", fontSize = 12.sp, color = Color.Gray)
                    Text("%.2f €".format(estadisticas.mediaFactura), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                }
            }
        }
    }
}