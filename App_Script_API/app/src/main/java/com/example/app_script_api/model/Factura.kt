package com.example.app_script_api.model

// Clase Factura para la fila del Google Sheet
data class Factura(
    val id: Int = 0,
    val nombre: String = "",
    val apellidos: String = "",
    val dni: String = "",
    val direccion: String = "",
    val concepto: String = "",
    val cantidad: Int = 0,
    val precioUnitario: Double = 0.0,
    val total: Double = 0.0,
    val fecha: String = ""
)
