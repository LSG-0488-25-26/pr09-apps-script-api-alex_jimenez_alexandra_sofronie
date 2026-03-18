package com.example.app_script_api.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_script_api.BuildConfig
import com.example.app_script_api.network.RetrofitInstance
import com.example.app_script_api.model.Estadisticas
import com.example.app_script_api.model.Factura
import com.example.app_script_api.repository.SettingsRepository
import com.example.app_script_api.network.ActualizarRequest
import com.example.app_script_api.network.CrearRequest
import com.example.app_script_api.network.EliminarRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class FacturaViewModel(app: Application) : AndroidViewModel(app) {

    //Repository para SharedPreferences (credenciales, último nombre/concepto)
    private val repository = SettingsRepository("AppSettings", app)

    // GET facturas
    private val _facturas = MutableStateFlow<List<Factura>>(emptyList())
    val facturas: StateFlow<List<Factura>> = _facturas.asStateFlow()

    // GET buscar (resultats filtrats)
    private val _facturasFiltradas = MutableStateFlow<List<Factura>>(emptyList())
    val facturasFiltradas: StateFlow<List<Factura>> = _facturasFiltradas.asStateFlow()

    // GET estadístiques
    private val _estadisticas = MutableStateFlow<Estadisticas?>(null)
    val estadisticas: StateFlow<Estadisticas?> = _estadisticas.asStateFlow()

    // Estats UI
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _enviado = MutableStateFlow(false)
    val enviado: StateFlow<Boolean> = _enviado.asStateFlow()

    private val apiKey: String
        get() = BuildConfig.API_KEY

    // ===================== GET =====================

    fun cargarFacturas() {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null
                val resposta = RetrofitInstance.api.getFacturas(apiKey)
                if (resposta.status == "ok" && resposta.data != null) {
                    _facturas.value = resposta.data
                } else {
                    _error.value = resposta.error ?: "Error desconegut"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error de connexió"
            } finally {
                _loading.value = false
            }
        }
    }

    fun buscarPorNombre(nombre: String) {
        if (nombre.isBlank()) {
            _facturasFiltradas.value = _facturas.value
            return
        }
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null
                val resposta = RetrofitInstance.api.buscarPorNombre(
                    apiKey = apiKey,
                    nombre = nombre
                )
                if (resposta.status == "ok" && resposta.data != null) {
                    _facturasFiltradas.value = resposta.data
                } else {
                    _facturasFiltradas.value = emptyList()
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun cargarEstadisticas() {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null
                val resposta = RetrofitInstance.api.getEstadisticas(apiKey)
                if (resposta.status == "ok" && resposta.data != null) {
                    _estadisticas.value = resposta.data
                } else {
                    _error.value = resposta.error ?: "Error al carregar estadístiques"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    // ===================== POST =====================

    fun crearFactura(
        nombre: String,
        apellidos: String,
        dni: String,
        direccion: String,
        concepto: String,
        cantidad: Int,
        precioUnitario: Double
    ) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null
                _enviado.value = false

                val resposta = RetrofitInstance.api.crearFactura(
                    CrearRequest(
                        apiKey = apiKey,
                        nombre = nombre,
                        apellidos = apellidos,
                        dni = dni,
                        direccion = direccion,
                        concepto = concepto,
                        cantidad = cantidad,
                        precioUnitario = precioUnitario
                    )
                )

                if (resposta.status == "ok") {
                    _enviado.value = true
                    repository.guardarUltimoNombre(nombre)
                    repository.guardarUltimoConcepto(concepto)
                    cargarFacturas()
                } else {
                    _error.value = resposta.error ?: "Error al crear factura"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error de connexió"
            } finally {
                _loading.value = false
            }
        }
    }

    fun eliminarFactura(id: Int) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                val resposta = RetrofitInstance.api.eliminarFactura(
                    EliminarRequest(apiKey = apiKey, id = id)
                )

                if (resposta.status == "ok") {
                    cargarFacturas()
                } else {
                    _error.value = resposta.error ?: "Error al eliminar"
                }

            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun actualizarFactura(
        id: Int,
        nombre: String? = null,
        apellidos: String? = null,
        dni: String? = null,
        direccion: String? = null,
        concepto: String? = null,
        cantidad: Int? = null,
        precioUnitario: Double? = null
    ) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                val resposta = RetrofitInstance.api.actualizarFactura(
                    ActualizarRequest(
                        apiKey = apiKey,
                        id = id,
                        nombre = nombre,
                        apellidos = apellidos,
                        dni = dni,
                        direccion = direccion,
                        concepto = concepto,
                        cantidad = cantidad,
                        precioUnitario = precioUnitario
                    )
                )
                if (resposta.status == "ok") {
                    cargarFacturas()
                } else {
                    _error.value = resposta.error ?: "Error al actualitzar"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    // ===================== SharedPreferences =====================

    fun obtenerUltimoNombre(): String = repository.obtenerUltimoNombre()
    fun obtenerUltimoConcepto(): String = repository.obtenerUltimoConcepto()

    fun guardarCredenciales(user: String, pwd: String) = repository.guardarCredenciales(user, pwd)
    fun obtenerUser(): String = repository.obtenerUser()
    fun obtenerPwd(): String = repository.obtenerPwd()
    fun estaLogueado(): Boolean = repository.estaLogueado()
    fun cerrarSesion() = repository.cerrarSesion()

    fun resetEnviado() {
        _enviado.value = false
    }
}