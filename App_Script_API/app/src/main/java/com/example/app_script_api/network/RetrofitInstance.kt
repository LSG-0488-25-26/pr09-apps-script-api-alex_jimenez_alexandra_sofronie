package com.example.app_script_api.network

import com.example.app_script_api.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// URL base de la API para conectar con el endpoint de Google Apps Script
object RetrofitInstance {
    private const val DEFAULT_URL = "https://script.google.com/macros/s/AKfycbxv71t4mdguq0cryPab0FRAUJ5PONeelYj-O3D2g0X3m1FPIevgp_vrYMscd1T-1ykkyA/exec"
    
    private val BASE_URL: String
        get() {
            val url = if (BuildConfig.BASE_URL.isNotEmpty()) BuildConfig.BASE_URL else DEFAULT_URL
            
            return if (url.endsWith("/")) url else "$url/"
        }

    //Lazy Singleton: Retrofit + Gson per serialitzar les respostes JSON de l'API.
    val api: FacturaApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FacturaApiService::class.java)
    }
}
