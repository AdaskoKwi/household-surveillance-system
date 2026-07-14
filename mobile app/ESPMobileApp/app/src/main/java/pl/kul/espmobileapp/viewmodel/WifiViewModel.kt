package pl.kul.espmobileapp.viewmodel

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.kul.espmobileapp.network.ApiService
import pl.kul.espmobileapp.notification.NotificationHelper
import pl.kul.espmobileapp.repository.PhotoRepository

class WifiViewModel(
    private val apiService: ApiService,
    private val notificationHelper: NotificationHelper,
    private val photoRepository: PhotoRepository
): ViewModel() {
    val connectedStatus = "connected"
    val connectionStatus = mutableStateOf("")
    var imageBitmap by mutableStateOf<ImageBitmap?>(null)
    val motionSensorNotification = apiService.photoNotification
    var isConnected by mutableStateOf(false)

    init {
        viewModelScope.launch {
            try {
                apiService.openWebSocket()
            } catch (e: Exception) {
                Log.e("WIFI-VM", e.message.toString())
            }
        }

        viewModelScope.launch {
            try {
                loadConnectionStatus()
                if (connectionStatus.value == connectedStatus) {
                    isConnected = true
                }
            } catch (e: Exception) {
                Log.e("WIFI-VM", e.message.toString())
            }
        }

        viewModelScope.launch {
            motionSensorNotification.collect { notification ->
                if (notification == "ALARM_MOTION") {
                    notificationHelper.showNotification()
                    updatePhoto()
                    savePhoto()
                }
            }
        }
    }

    fun updatePhoto() {
        viewModelScope.launch {
            try {
                val bytes: ByteArray = apiService.fetchPhoto()
                val bitmap = withContext(Dispatchers.Default) {
                    val androidBitmap =  BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    androidBitmap?.asImageBitmap()
                }
                imageBitmap = bitmap
                isConnected = true
            } catch (e: Exception) {
                Log.e("WIFI-VM", e.message.toString())
            }
        }
    }

    fun savePhoto() {
        viewModelScope.launch {
            photoRepository.saveBitmapToGallery(imageBitmap)
        }
    }

    suspend fun loadConnectionStatus() {
        try {
            val result = apiService.fetchConnectionStatus()
            connectionStatus.value = result
        } catch (e: Exception) {
            Log.e("WIFI-VM", e.message.toString())
        }
    }
}

class WifiViewModelFactory(
    private val apiService: ApiService,
    private val notificationHelper: NotificationHelper,
    private val photoRepository: PhotoRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WifiViewModel(apiService, notificationHelper, photoRepository) as T
    }
}