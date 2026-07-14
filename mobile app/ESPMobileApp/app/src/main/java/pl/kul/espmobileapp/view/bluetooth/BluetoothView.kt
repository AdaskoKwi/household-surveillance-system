package pl.kul.espmobileapp.view.bluetooth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pl.kul.espmobileapp.R
import pl.kul.espmobileapp.components.CircularIndicator
import pl.kul.espmobileapp.components.TopBar
import pl.kul.espmobileapp.viewmodel.BluetoothViewModel

@Composable
fun BluetoothView(
    viewModel: BluetoothViewModel
) {
    val imageBitmap = viewModel.imageBitmap
    val isPhotoCurrentlyTransmitted = viewModel.isPhotoCurrentlyTransmitted;
    val currentPhotoSize = viewModel.currentByteCount
    val fullPhotoSize = viewModel.photoSize

    val spacerHeight = 32.dp
    val spacerWidth = 8.dp
    val imageSize = 300.dp
    val boxSize = 300.dp
    val cornerSize = 16.dp
    val borderWidth = 2.dp
    val rotationDegrees = 180f
    val circularIndicatorPadding = 16.dp

    DisposableEffect(Unit) {
        viewModel.connectToEsp()

        onDispose {
            viewModel.disconnectFromEsp()
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                title = stringResource(R.string.screen_title_bluetooth),
                isConnected = viewModel.isConnected
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Spacer(modifier = Modifier.height(spacerHeight))

            if (imageBitmap != null) {
                if (!isPhotoCurrentlyTransmitted) {
                    Image(
                        bitmap = imageBitmap,
                        contentDescription = "ESP Image",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .requiredSize(imageSize)
                            .clip(RoundedCornerShape(cornerSize))
                            .border(
                                BorderStroke(borderWidth, MaterialTheme.colorScheme.outline),
                                RoundedCornerShape(cornerSize)
                            )
                            .rotate(rotationDegrees)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(boxSize)
                            .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(cornerSize)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularIndicator(
                            currentValue = currentPhotoSize.toFloat(),
                            maxValue = fullPhotoSize.toFloat(),
                            modifier = Modifier.padding(circularIndicatorPadding)
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(boxSize)
                        .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(cornerSize)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = stringResource(R.string.waiting_for_photo))
                }
            }

            Spacer(modifier = Modifier.height(spacerHeight / 2))

            Column(
                modifier = Modifier
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        viewModel.savePhoto()
                    }
                ) {
                    Text(text = stringResource(R.string.save_photo), fontWeight = FontWeight.SemiBold)

                    Spacer(modifier = Modifier.width(spacerWidth))

                    Icon(
                        painter = painterResource(R.drawable.download_24px),
                        contentDescription = null
                    )
                }

                OutlinedButton(
                    onClick = {
                        viewModel.getPhoto()
                    }
                ) {
                    Text(text = stringResource(R.string.reload_photo))

                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null
                    )
                }
            }
        }
    }
}