package pl.kul.espmobileapp.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import io.ktor.client.engine.okhttp.OkHttp
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object NetworkClient {
    val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = false
            })
        }
        install(WebSockets) {
        }
        engine {
            preconfigured = OkHttpClient.Builder()
                .pingInterval(50, TimeUnit.SECONDS)
                .build()
        }
    }

    val apiService by lazy { ApiService(client) }
}