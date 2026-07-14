package pl.kul.espmobileapp.viewmodel

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import androidx.annotation.RequiresPermission
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
import pl.kul.espmobileapp.notification.NotificationHelper
import pl.kul.espmobileapp.repository.PhotoRepository
import java.util.UUID

class BluetoothViewModel(
    private val context: Context,
    private val bluetoothManager: BluetoothManager,
    private val notificationHelper: NotificationHelper,
    private val photoRepository: PhotoRepository
): ViewModel() {
    private val ESP_MAC = "90:70:69:16:FA:59"
    private val SERVICE_UUID: UUID = UUID.fromString("4fafc201-1fb5-459e-8fcc-c5c9c331914b")
    private val CHARACTERISTIC_UUID: UUID = UUID.fromString("68ef2e0a-1227-421e-b7a9-3c5816c74c62")

    var bluetoothGatt: BluetoothGatt? = null
    private var gattService: BluetoothGattService? = null
    private var gattCharacteristic: BluetoothGattCharacteristic? = null
    val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
    var esp: BluetoothDevice? = bluetoothAdapter?.getRemoteDevice(ESP_MAC)
    var isConnected by mutableStateOf(false)
    var imageByteList = mutableListOf<Byte>()
    var imageBitmap by mutableStateOf<ImageBitmap?>(null)
    var motionHeaderHandled: Boolean = false
    var photoSizeHeaderHandled: Boolean = false
    var photoSize by mutableStateOf(0)
    var currentByteCount by mutableStateOf(0)
    var isPhotoCurrentlyTransmitted by mutableStateOf(false)

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun disconnectFromEsp() {
        bluetoothGatt?.disconnect()
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun connectToEsp() {
        esp?.connectGatt(context, false, object : BluetoothGattCallback() {

            @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
            override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
                when (newState) {
                    BluetoothProfile.STATE_CONNECTED -> {
                        Log.i("BT-VM", "Połączono z serwerem GATT")
                        isConnected = true
                        bluetoothGatt = gatt
                        gatt?.requestMtu(256)
                        gatt?.discoverServices()
                    }

                    BluetoothProfile.STATE_DISCONNECTED -> {
                        Log.i("BT-VM", "Rozłączono z serwerem GATT")
                        isConnected = false

                        bluetoothGatt?.close()
                        bluetoothGatt = null
                    }
                }
            }

            @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
            override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
                gattService = gatt?.getService(SERVICE_UUID)
                val characteristic = gattService?.getCharacteristic(CHARACTERISTIC_UUID)

                if (characteristic != null) {
                    gattCharacteristic = characteristic

                    gatt?.setCharacteristicNotification(characteristic, true)

                    val descriptorUuid = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
                    val descriptor = characteristic.getDescriptor(descriptorUuid)
                    if (descriptor != null) {
                        descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                        gatt?.writeDescriptor(descriptor)
                        Log.i("BT-VM", "Powiadomienia zostały włączone")
                    }
                }

                if (gattService != null) {
                    Log.i("BT-VM", "Gatt service found: ${gattService}")
                } else {
                    Log.e("BT-VM", "Gatt service with UUID: ${SERVICE_UUID} not found")
                }
            }

            override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
                Log.i("BT-VM", "MTU: ${mtu}")
            }

            override fun onCharacteristicChanged(
                gatt: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic,
                value: ByteArray
            ) {
                isPhotoCurrentlyTransmitted = true

                if (!motionHeaderHandled || !photoSizeHeaderHandled) {
                    val motionHeader = String(value)
                    Log.i("BT-VM", motionHeader)

                    if (motionHeader == "Motion") {
                        notificationHelper.showNotification()
                    } else if (motionHeader.startsWith("Photo size")) {
                        val headerArray = motionHeader.split(" ");
                        photoSize = headerArray[2].toInt()
                        photoSizeHeaderHandled = true
                    }
                    motionHeaderHandled = true
                } else {
                    imageByteList.addAll(value.asList())
                    currentByteCount = imageByteList.size
                }

                if (value[value.size - 2] == (-1).toByte() &&
                    value[value.size - 1] == (-39).toByte()) {
                    motionHeaderHandled = false
                    photoSizeHeaderHandled = false
                    isPhotoCurrentlyTransmitted = false
                    updatePhoto()
                }
            }
        })
    }

    fun updatePhoto() {
        viewModelScope.launch {
            try {
                val bitmap = withContext(Dispatchers.Default) {
                    val androidBitmap = BitmapFactory.decodeByteArray(imageByteList.toByteArray(), 0, imageByteList.size)
                    androidBitmap?.asImageBitmap()
                }
                imageBitmap = bitmap
                clearByteList()
            } catch (e: Exception) {
                Log.e("BT-VM", "Błąd dekodowania bitmapy: ${e.message}")
            }
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun getPhoto() {
        gattCharacteristic?.let {
            bluetoothGatt?.readCharacteristic(it)
        }
    }

    fun savePhoto() {
        viewModelScope.launch {
            photoRepository.saveBitmapToGallery(imageBitmap)
        }
    }

    suspend fun clearByteList() {
        imageByteList.clear()
    }
}

class BluetoothViewModelFactory(
    private val context: Context,
    private val bluetoothManager: BluetoothManager,
    private val notificationHelper: NotificationHelper,
    private val photoRepository: PhotoRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BluetoothViewModel(context, bluetoothManager, notificationHelper, photoRepository) as T
    }
}