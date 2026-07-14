package pl.kul.espmobileapp.view.cardView.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import pl.kul.espmobileapp.R
import pl.kul.espmobileapp.navigation.Screen

@Composable
fun BottomBar(
    currentScreen: Screen,
    navController: NavController
) {
    val items = listOf(
        Screen.WifiScreen, Screen.BluetoothScreen
    )

    NavigationBar{
        items.forEach { screen ->
            val icon = when (screen) {
                Screen.BluetoothScreen -> R.drawable.bluetooth_24px
                Screen.WifiScreen -> R.drawable.wifi_24px
                else -> R.drawable.error_24px
            }

            NavigationBarItem(selected = currentScreen.route == screen.route, onClick = {
                if (currentScreen.route != screen.route) {
                    navController.navigate(screen.route)
                }
            }, icon = {
                Icon(
                    painter = painterResource(icon), contentDescription = null
                )
            }, label = {
                Text(
                    text = stringResource(screen.title),
                    textAlign = TextAlign.Center
                )
            })
        }
    }
}