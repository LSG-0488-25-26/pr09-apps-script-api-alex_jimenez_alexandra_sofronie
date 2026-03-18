package com.example.app_script_api.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    }
}