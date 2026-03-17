package com.example.app_script_api.repository

import android.content.Context
import android.content.SharedPreferences

//Class SettingsRepository para gestionar SharedPreferences: para guardar y leer datos pequeños en el dispositivo (clave-valor).
class SettingsRepository(nomFitxer: String, private val context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(nomFitxer, Context.MODE_PRIVATE)
    fun <T> saveSettingValue(key: String, value: T) {
        with(sharedPreferences.edit()) {
            when (value) {
                is String -> putString(key, value)
                is Int -> putInt(key, value)
                is Boolean -> putBoolean(key, value)
                is Float -> putFloat(key, value)
                is Long -> putLong(key, value)
                else -> throw IllegalArgumentException("Unsupported type")
            }
            apply()
        }
    }
    fun <T> getSettingValue(key: String, defaultValue: T): T {
        @Suppress("UNCHECKED_CAST")
        return when (defaultValue) {
            is String -> sharedPreferences.getString(key, defaultValue) as T
            is Int -> sharedPreferences.getInt(key, defaultValue) as T
            is Boolean -> sharedPreferences.getBoolean(key, defaultValue) as T
            is Float -> sharedPreferences.getFloat(key, defaultValue) as T
            is Long -> sharedPreferences.getLong(key, defaultValue) as T
            else -> throw IllegalArgumentException("Unsupported type")
        }
    }
    
    fun guardarUltimoNombre(nombre: String) {
        sharedPreferences.edit().putString("ultimo_nombre", nombre).apply()
    }
    fun obtenerUltimoNombre(): String {
        return sharedPreferences.getString("ultimo_nombre", "") ?: ""
    }
    fun guardarUltimoConcepto(concepto: String) {
        sharedPreferences.edit().putString("ultimo_concepto", concepto).apply()
    }
    fun obtenerUltimoConcepto(): String {
        return sharedPreferences.getString("ultimo_concepto", "") ?: ""
    }

    // Credencials de login (Usuario -> 1234) per SharedPreferences
    fun guardarCredenciales(user: String, pwd: String) {
        sharedPreferences.edit()
            .putString("Usuario", user)
            .putString("1234", pwd)
            .putBoolean("logueado", true)
            .apply()
    }
    fun obtenerUser(): String = sharedPreferences.getString("Usuario", "") ?: ""
    fun obtenerPwd(): String = sharedPreferences.getString("1234", "") ?: ""
    fun estaLogueado(): Boolean = sharedPreferences.getBoolean("logueado", false)

    fun cerrarSesion() {
        sharedPreferences.edit()
            .remove("Usuario")
            .remove("1234")
            .remove("logueado")
            .apply()
    }
}