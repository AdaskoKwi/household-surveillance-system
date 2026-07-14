package pl.kul.espmobileapp.view.cardView

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import pl.kul.espmobileapp.R
import pl.kul.espmobileapp.navigation.Screen
import pl.kul.espmobileapp.view.cardView.components.NavigationCard

@Composable
fun CardView(
    navController: NavController
) {
    val columnPadding = 16.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(columnPadding),
        verticalArrangement = Arrangement.Center
    ) {
        NavigationCard(
            title = stringResource(R.string.screen_title_wifi),
            icon = R.drawable.wifi_24px,
            onClick = { navController.navigate(Screen.WifiScreen.route)}
        )

        NavigationCard(
            title = stringResource(R.string.screen_title_bluetooth),
            icon = R.drawable.bluetooth_24px,
            onClick = { navController.navigate(Screen.BluetoothScreen.route)}
        )
    }
}