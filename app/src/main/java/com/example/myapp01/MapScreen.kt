package com.example.myapp01

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.delay
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toDrawable


@Composable
fun MapScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    LaunchedEffect(Unit) {
        mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        mapView.setMultiTouchControls(true)
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
        mapView.controller.setZoom(8)
        mapView.controller.setCenter(GeoPoint(22.0, 120.5))
        viewModel.userCityList.forEach {
            val startPoint = getGeoPoint(it.name)
            val startMarker = Marker(mapView)
            val weatherData = viewModel.getWeatherData(it.fileName.dropLast(4))
            startMarker.setPosition(startPoint)
            startMarker.icon = createNumberedMarker(
                context,
                weatherData.airQualityIndex.currentAqi,
                getAqiColor(weatherData.airQualityIndex.currentAqi.toInt()).toArgb(),
                Color.White.toArgb()
            )
            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            mapView.overlays.add(startMarker)
        }
        delay(1000)
        mapView.controller.animateTo(GeoPoint(23.5, 121.0))
    }
    Box {
        AndroidView(factory = { ctx ->
            mapView
        }, onRelease = {
            it.onDetach()
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
                mapView.controller.animateTo(getGeoPoint(viewModel.userCityList.first().name))
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

fun createNumberedMarker(
    context: Context,
    number: String,
    bgColor: Int,
    textColor: Int
): Drawable {
    // 1. 定義尺寸 (可根據需求調整，單位為像素)
    val SIZE = 100 // Marker 圖標的邊長
    val TEXT_SIZE = 40f // 文字大小

    // 2. 創建 Bitmap
    val bitmap = createBitmap(SIZE, SIZE)
    val canvas: Canvas = Canvas(bitmap)

    // 3. 繪製圓形背景
    val circlePaint: Paint = Paint()
    circlePaint.color = bgColor
    circlePaint.isAntiAlias = true // 抗鋸齒

    val radius = SIZE / 2f
    canvas.drawCircle(radius, radius, radius, circlePaint)

    // 4. 繪製數字文字
    val textPaint: Paint = Paint()
    textPaint.color = textColor
    textPaint.textSize = TEXT_SIZE
    textPaint.textAlign = Paint.Align.CENTER
    textPaint.isAntiAlias = true

    // 設置文字基準線，使其垂直居中
    val fm: Paint.FontMetrics = textPaint.fontMetrics
    val x: Float = canvas.width / 2f
    val y: Float = (canvas.height / 2f) - (fm.ascent + fm.descent) / 2f

    canvas.drawText(number, x, y, textPaint)

    return bitmap.toDrawable(context.resources)
}