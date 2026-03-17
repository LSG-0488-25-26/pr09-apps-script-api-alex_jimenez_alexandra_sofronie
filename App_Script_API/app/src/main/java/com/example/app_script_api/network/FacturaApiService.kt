package com.example.app_script_api.network

import com.example.app_script_api.model.Estadisticas
import com.example.app_script_api.model.Factura
import com.example.app_script_api.model.GetResponse
import com.example.app_script_api.model.PostResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface FacturaApiService {

    @GET("exec")     //Mostramos las facturas
    suspend fun getFacturas(
        @Query("apiKey") apiKey: String,
        @Query("type") type: String = "facturas"
    ): GetResponse<List<Factura>>

    @GET("exec")    //Buscamos por nombre
    suspend fun buscarPorNombre(
        @Query("apiKey") apiKey: String,
        @Query("type") type: String = "buscar",
        @Query("nombre") nombre: String
    ): GetResponse<List<Factura>>

    @GET("exec")    //Mostramos las estadisticas
    suspend fun getEstadisticas(
        @Query("apiKey") apiKey: String,
        @Query("type") type: String = "estadisticas"
    ): GetResponse<Estadisticas>

    @POST("exec")   //Creamos la factura
    suspend fun crearFactura(
        @Body body: CrearRequest
    ): PostResponse

    @POST("exec")   //Eliminamos la factura
    suspend fun eliminarFactura(
        @Body body: EliminarRequest
    ): PostResponse

    @POST("exec")   //Actualizamos la factura
    suspend fun actualizarFactura(
        @Body body: ActualizarRequest
    ): PostResponse
}

//Body de la petició POST per crear una factura a l'API d'Apps Script
data class CrearRequest(
    //Creamos la factura
    val apiKey: String,
    val type: String = "crear",
    val nombre: String,
    val apellidos: String,
    val dni: String,
    val direccion: String,
    val concepto: String,
    val cantidad: Int,
    val precioUnitario: Double
)

//Eliminamos la factura
data class EliminarRequest(
    val apiKey: String,
    val type: String = "eliminar",
    val id: Int
)

//Actualizamos la factura
data class ActualizarRequest(
    val apiKey: String,
    val type: String = "actualizar",
    val id: Int,
    val nombre: String? = null,
    val apellidos: String? = null,
    val dni: String? = null,
    val direccion: String? = null,
    val concepto: String? = null,
    val cantidad: Int? = null,
    val precioUnitario: Double? = null
)
