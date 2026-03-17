package com.example.app_script_api.network

import com.example.app_script_api.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// URL base de la API para conectar con el endpoint de Google Apps Script
object RetrofitInstance {
    private const val DEFAULT_URL = "ttps://script.google.com/macros/s/AKfycbyLkk7XXxCuqueCyvYlaB7z1nZFlpaK7lA5ItVRSEFv_zdtrjuXT3BAifmCRnPWWP7c/exec"
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
