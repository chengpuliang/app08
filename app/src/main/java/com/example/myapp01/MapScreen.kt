package com.example.myapp01

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.delay
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView

@Composable
fun MapScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val mapView = remember { MapView(context)}
    LaunchedEffect(Unit) {
        mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        mapView.setUseDataConnection(false)
        mapView.setTileSource(
            XYTileSource(
                "Google Maps HD",
                7,
                12,
                256,
                ".png",
                arrayOf("")
            )
        )
        mapView.controller.setZoom(10)
        mapView.controller.setCenter(GeoPoint((21 * 1E6).toInt(),(120 * 1E6).toInt()))

        delay(1000)
        mapView.controller.animateTo(GeoPoint((23.5 * 1E6).toInt(),(121 * 1E6).toInt()))
    }
    Box {
        AndroidView(factory = { ctx ->
            mapView
        })
        Button(
            onClick = {
                viewModel.pop()
            },
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier
                .padding(12.dp, 42.dp)
                .width(60.dp)
                .height(60.dp)
                .shadow(24.dp)
        ) {
            Icon(
                Icons.Default.Close, "",
                modifier = Modifier.size(32.dp)
            )
        }
        Button(
            onClick = {
                viewModel.pop()
            },
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp, 42.dp)
                .width(60.dp)
                .height(60.dp)
                .shadow(24.dp)
        ) {
            Icon(
                Icons.Default.LocationOn, "",
                modifier = Modifier.size(32.dp)
            )
        }
    }

}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun MapPreview() {
    MapScreen(MainViewModel())
}