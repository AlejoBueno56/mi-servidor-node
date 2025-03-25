package com.example.pruebausuario

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// Definir el endpoint de la API
interface ApiService {
    @GET("config") // Ruta en tu backend para obtener la configuración
    suspend fun getConfig(): ConfigResponse
}

// Clase para manejar la configuración remota
class RemoteConfigService(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("AppConfig", Context.MODE_PRIVATE)

    private val api = Retrofit.Builder()
        .baseUrl("https://mi-servidor-node.onrender.com/config") // URL de tu backend en Render
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)

    // Función para obtener datos del backend
    suspend fun fetchConfig(): ConfigResponse? {
        return try {
            val response = withContext(Dispatchers.IO) { api.getConfig() }
            saveConfigLocally(response)
            response
        } catch (e: Exception) {
            loadConfigFromCache()
        }
    }

    // Guardar la configuración localmente en SharedPreferences
    private fun saveConfigLocally(config: ConfigResponse) {
        sharedPreferences.edit().apply {
            putString("promoMessage", config.promoMessage)
            putFloat("price", config.price.toFloat())
            apply()
        }
    }

    // Cargar configuración desde cache
    private fun loadConfigFromCache(): ConfigResponse? {
        val promoMessage = sharedPreferences.getString("promoMessage", null) ?: return null
        val price = sharedPreferences.getFloat("price", 0f)
        return ConfigResponse(promoMessage, price.toDouble())
    }
}

// Modelo de datos que el backend devolverá
data class ConfigResponse(
    val promoMessage: String,
    val price: Double
)
