package pl.kul.espmobileapp.components

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import pl.kul.espmobileapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    title: String,
    isConnected: Boolean
) {
    val context = LocalContext.current
    val connectionStatus = if (isConnected) R.string.connected else R.string.disconnected
    val statusColor = if (isConnected) Color(0xFF4CAF50) else Color(0xFFE57373)
    val statusIcon = getStatusIcon(context, title, isConnected)

    val textPadding = 16.dp
    val spacerWidth = 4.dp

    TopAppBar(
        title = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = textPadding/4, end = textPadding),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = title, fontWeight = FontWeight.Bold)
                Row() {
                    Icon(painter = painterResource(statusIcon), contentDescription = null, tint = statusColor)
                    Spacer(modifier = Modifier.width(spacerWidth))
                    Text(text = stringResource(connectionStatus), color = statusColor)
                }
            }
        },
        navigationIcon = {

        },
        actions = {

        },
        windowInsets = WindowInsets(0),
    )
}

private fun getStatusIcon(context: Context, title: String, isConnected: Boolean): Int {
    var statusIcon: Int? = null

    if (title.equals(getString(context, R.string.screen_title_bluetooth))) {
        statusIcon = if (isConnected) R.drawable.bluetooth_connected_24px else R.drawable.bluetooth_disabled_24px
    } else if (title.equals(getString(context, R.string.screen_title_wifi))) {
        statusIcon = if (isConnected) R.drawable.signal_wifi_4_bar_24px else R.drawable.signal_wifi_bad_24px
    }

    return statusIcon!!
}
