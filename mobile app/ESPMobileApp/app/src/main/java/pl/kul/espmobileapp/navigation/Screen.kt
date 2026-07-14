package pl.kul.espmobileapp.navigation

import androidx.annotation.StringRes
import pl.kul.espmobileapp.R

sealed class Screen(
    val route: String,
    @StringRes
    val title: Int
) {
    data object BluetoothScreen : Screen("bluetooth_screen", R.string.screen_title_bluetooth)
    data object WifiScreen : Screen("wifi_screen", R.string.screen_title_wifi)
    data object MainScreen : Screen("main_screen", R.string.screen_title_main)

    companion object {
        val allScreen by lazy {
            listOf(
                BluetoothScreen,
                WifiScreen,
                MainScreen
            )
        }
    }
}