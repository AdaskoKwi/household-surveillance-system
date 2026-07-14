package pl.kul.espmobileapp.navigation

import android.bluetooth.BluetoothManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import pl.kul.espmobileapp.network.NetworkClient
import pl.kul.espmobileapp.notification.NotificationHelper
import pl.kul.espmobileapp.repository.PhotoRepository
import pl.kul.espmobileapp.view.bluetooth.BluetoothView
import pl.kul.espmobileapp.view.cardView.CardView
import pl.kul.espmobileapp.view.wifi.WifiView
import pl.kul.espmobileapp.viewmodel.BluetoothViewModel
import pl.kul.espmobileapp.viewmodel.BluetoothViewModelFactory
import pl.kul.espmobileapp.viewmodel.WifiViewModel
import pl.kul.espmobileapp.viewmodel.WifiViewModelFactory

@Composable
fun AppNavigation(
    navController: NavHostController
) {
    val context = LocalContext.current
    val notificationHelper = NotificationHelper(context)
    val photoRepository = PhotoRepository(context)
    val bluetoothManager = context.getSystemService(BluetoothManager::class.java)

    NavHost(
        navController = navController,
        startDestination = Screen.MainScreen.route
    ) {
        composable(Screen.MainScreen.route) {
            CardView(navController)
        }

        composable(Screen.BluetoothScreen.route) {
            val bluetoothViewModel: BluetoothViewModel = viewModel(
                factory = BluetoothViewModelFactory(
                    context,
                    bluetoothManager,
                    notificationHelper,
                    photoRepository
                )
            )

            BluetoothView(bluetoothViewModel)
        }

        composable(Screen.WifiScreen.route) {
            val wifiViewModel: WifiViewModel = viewModel(
                factory = WifiViewModelFactory(
                    NetworkClient.apiService,
                    notificationHelper,
                    photoRepository
                )
            )

            WifiView(wifiViewModel)
        }
    }
}