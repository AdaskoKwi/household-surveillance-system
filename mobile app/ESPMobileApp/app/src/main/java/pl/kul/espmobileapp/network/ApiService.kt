package pl.kul.espmobileapp.network

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.get
import io.ktor.http.HttpMethod
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class ApiService(private val client: HttpClient) {
    private val baseUrl = "http://192.168.4.1"
    private val wsBaseUrl = "192.168.4.1"

    private val _photoNotification = MutableSharedFlow<String>()
    val photoNotification = _photoNotification.asSharedFlow()

    suspend fun openWebSocket() {
        client.webSocket(
            method = HttpMethod.Get,
            host = wsBaseUrl,
            port = 80,
            path = "/ws",
        ) {
            for (frame in incoming) {
                when (frame) {
                    is Frame.Text -> {
                        val text = frame.readText()
                        _photoNotification.emit(text)
                        Log.i("WS", text    )
                    }
                    else -> Log.i("WS", "else")
                }
            }
        }
    }

    suspend fun fetchPhoto(): ByteArray {
        return client.get("$baseUrl/photo").body<ByteArray>()
    }

    suspend fun fetchConnectionStatus(): String {
        return client.get("$baseUrl/is-connected").body()
    }
}