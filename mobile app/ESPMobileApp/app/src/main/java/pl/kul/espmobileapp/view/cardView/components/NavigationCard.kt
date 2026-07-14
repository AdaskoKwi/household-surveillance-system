package pl.kul.espmobileapp.view.cardView.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp


@Composable
fun NavigationCard(
    title: String,
    @DrawableRes
    icon: Int,
    onClick: () -> Unit,
) {
    val cardPadding = 8.dp
    val cardHeight = 140.dp
    val cardElevation = 4.dp
    val spacerHeight = 8.dp
    val iconSize = 40.dp

    Card(
        modifier = Modifier
            .padding(cardPadding)
            .fillMaxWidth()
            .height(cardHeight),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(cardElevation)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(iconSize)
            )

            Spacer(modifier = Modifier.height(spacerHeight))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}