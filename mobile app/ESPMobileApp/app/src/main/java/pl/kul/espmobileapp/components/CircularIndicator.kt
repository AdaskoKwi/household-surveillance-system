package pl.kul.espmobileapp.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CircularIndicator(
    currentValue: Float,
    maxValue: Float,
    modifier: Modifier
) {
    val wheelSize = 128.dp
    var loading by remember { mutableStateOf(true) }

    val progress = if (maxValue > 0) currentValue / maxValue else 0f

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if (loading) {
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.size(wheelSize)
            )
        }
    }
}