package com.example.myapp01

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapp01.ui.theme.MyApp01Theme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DetailScreen(weatherData: WeatherData) {
    val nowHour = remember { SimpleDateFormat("HH:00", Locale.getDefault()).format(Date()) }
    val nowHourlyForecast =
        remember {
            weatherData.hourlyForecast.find { it.time == nowHour } ?: HourlyForecast(
                "NA",
                "NA",
                "NA"
            )
        }
    val todayForecast = remember { weatherData.tenDayForecast.first() }
    Box {
        Image(
            painter = painterResource(
                WeatherRes.valueOf(nowHourlyForecast.weatherCondition).background
            ),
            nowHourlyForecast.weatherCondition, contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp, 0.dp)
                .verticalScroll(rememberScrollState())
        ) {
            VSpacer(32.dp)
            if (weatherData.isCurrent) {
                Text(
                    text = "當前位置",
                    color = getColorOnBg(nowHourlyForecast.weatherCondition),
                    fontSize = 24.sp
                )
            }
            Text(
                text = weatherData.currentWeather.city,
                color = getColorOnBg(nowHourlyForecast.weatherCondition),
                fontSize = if (weatherData.isCurrent) 16.sp else 28.sp
            )
            VSpacer(16.dp)
            Row {
                HSpacer(24.dp)
                Text(
                    text = nowHourlyForecast.temperature,
                    color = getColorOnBg(nowHourlyForecast.weatherCondition),
                    fontSize = 72.sp
                )
            }
            Text(
                text = stringResource(
                    id = WeatherRes.valueOf(nowHourlyForecast.weatherCondition).chName
                ),
                color = getColorOnBg(nowHourlyForecast.weatherCondition),
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row {
                Text(
                    text = "H: ${todayForecast.highTemperature}",
                    color = getColorOnBg(nowHourlyForecast.weatherCondition),
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.width(20.dp))
                Text(
                    text = "L: ${todayForecast.lowTemperature}",
                    color = getColorOnBg(nowHourlyForecast.weatherCondition),
                    fontSize = 18.sp
                )
            }
            VSpacer(12.dp)
            Card(
                modifier = Modifier.alpha(0.7f).fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState(0))
                ) {
                    weatherData.hourlyForecast.filter {
                        it.time.replace(":", "").toInt() >= nowHour.replace(":", "")
                            .toInt()
                    }.forEach { hourlyForecast ->
                        Column(
                            modifier = Modifier.padding(16.dp, 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(if (hourlyForecast.time == nowHour) "現在" else hourlyForecast.time)
                            Image(
                                painter = painterResource(
                                    WeatherRes.valueOf(hourlyForecast.weatherCondition).icon
                                ),
                                hourlyForecast.weatherCondition,
                                modifier = Modifier.padding(8.dp)
                            )
                            Text(hourlyForecast.temperature)
                        }
                    }
                }
            }
            VSpacer(12.dp)
            Card(
                modifier = Modifier.alpha(0.7f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(R.drawable.baseline_calendar_month_24),
                            "",
                            modifier = Modifier.width(18.dp)
                        )
                        Text(
                            text = "  10天內天氣預報",
                            fontSize = 14.sp
                        )
                    }
                    val startTemp = remember {
                        weatherData.tenDayForecast.minOf { it.lowTemperature.removeSuffix("°C") }
                            .toInt()
                    }
                    val endTemp = remember {
                        weatherData.tenDayForecast.maxOf { it.highTemperature.removeSuffix("°C") }
                            .toInt()
                    }
                    val todayDate = remember { weatherData.tenDayForecast.first().date }
                    weatherData.tenDayForecast.forEach { day ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (day.date == todayDate) "今天" else day.date.drop(
                                    5
                                ).replace("-", "/"),
                                modifier = Modifier.weight(.2f)
                            )
                            Image(
                                painter = painterResource(
                                    WeatherRes.valueOf(day.weatherCondition).icon
                                ),
                                "",
                                modifier = Modifier
                                    .padding(6.dp)
                                    .weight(.3f)
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.weight(.5f)
                            ) {
                                Text(
                                    day.lowTemperature,
                                    fontSize = 12.sp
                                )
                                Box(
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .height(6.dp)
                                        .width(100.dp)
                                        .clip(RoundedCornerShape(50))
                                        .background(
                                            Brush.horizontalGradient(
                                                colors = listOf(
                                                    Color(0xff90D5FF),
                                                    Color(0xff1e90ff),
                                                    Color.Yellow,
                                                    Color(0xffFFA500),
                                                    Color(0xffff0000)
                                                )
                                            )
                                        )
                                        .clipToBounds()
                                ) {
                                    val gradientWidth = 100.dp
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .width(
                                                gradientWidth * ((day.lowTemperature.dropLast(2)
                                                    .toInt() - startTemp)) / (endTemp - startTemp)
                                            )
                                            .background(
                                                Color.Gray
                                            )
                                    )
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .width(
                                                gradientWidth - (gradientWidth * ((day.highTemperature.dropLast(
                                                    2
                                                ).toInt() - startTemp)) / (endTemp - startTemp))
                                            )
                                            .offset(
                                                gradientWidth * ((day.highTemperature.dropLast(2)
                                                    .toInt() - startTemp)) / (endTemp - startTemp)
                                            )
                                            .background(
                                                Color.Gray
                                            )
                                    )
                                }
                                Text(
                                    day.highTemperature,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
            VSpacer(12.dp)
            Card(
                modifier = Modifier.alpha(0.7f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                )
                {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(R.drawable.baseline_show_chart_24),
                            "",
                            modifier = Modifier.width(18.dp)
                        )
                        Text(
                            text = "  空氣指標",
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    )
                    {
                        Box {
                            Canvas(
                                modifier = Modifier
                                    .width(250.dp)
                                    .height(250.dp)
                                    .padding(16.dp)
                            ) {
                                drawArc(
                                    brush = SolidColor(Color.LightGray),
                                    startAngle = 150f,
                                    sweepAngle = 240f,
                                    useCenter = false,
                                    style = Stroke(35f, cap = StrokeCap.Round)
                                )
                                drawArc(
                                    brush = SolidColor(
                                        when (weatherData.airQualityIndex.currentAqi.toInt()) {
                                            in 0..24 -> (Color(0xff33767d))
                                            in 25..49 -> (Color(0xff44996b))
                                            in 50..74 -> (Color(0xff91bc5d))
                                            in 75..99 -> (Color(0xfffadf5b))
                                            in 100..124 -> (Color(0xfff4bcb2))
                                            in 125..149 -> (Color(0xfff0994c))
                                            in 150..175 -> (Color(0xffd4563f))
                                            else -> Color.Gray
                                        }
                                    ),
                                    startAngle = 150f,
                                    sweepAngle = 240f * (weatherData.airQualityIndex.currentAqi.toFloat() / 175),
                                    useCenter = false,
                                    style = Stroke(35f, cap = StrokeCap.Round)
                                )
                            }
                            Text(
                                weatherData.airQualityIndex.currentAqi,
                                textAlign = TextAlign.Center,
                                fontSize = 48.sp,
                                modifier = Modifier
                                    .width(250.dp)
                                    .padding(0.dp, 90.dp, 0.dp, 0.dp)
                            )
                            Text(
                                "AQI",
                                textAlign = TextAlign.Center,
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .width(250.dp)
                                    .padding(0.dp, 140.dp, 0.dp, 0.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .width(250.dp)
                                    .padding(25.dp, 190.dp, 20.dp, 0.dp)
                            ) {
                                Text(
                                    text = "0",
                                    modifier = Modifier.weight(.5f)
                                )
                                Text(
                                    text = "175",
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.weight(.5f)
                                )
                            }
                        }
                        Column(
                            modifier = Modifier
                                .offset(0.dp, (-35).dp)
                                .background(
                                    when (weatherData.airQualityIndex.currentAqi.toInt()) {
                                        in 0..24 -> (Color(0xff33767d))
                                        in 25..49 -> (Color(0xff44996b))
                                        in 50..74 -> (Color(0xff91bc5d))
                                        in 75..99 -> (Color(0xfffadf5b))
                                        in 100..124 -> (Color(0xfff4bcb2))
                                        in 125..149 -> (Color(0xfff0994c))
                                        in 150..175 -> (Color(0xffd4563f))
                                        else -> Color.Gray
                                    }
                                )
                                .width(80.dp)
                                .height(35.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = when (weatherData.airQualityIndex.currentAqi.toInt()) {
                                    in 0..50 -> "良"
                                    in 51..100 -> "普通"
                                    else -> "不健康"
                                },
                            )
                        }
                    }
                }
            }
            VSpacer(12.dp)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DetailScreenPreview() {
    MyApp01Theme {
        DetailScreen(MainViewModel().apply { initialize(LocalContext.current) }.getWeatherData("taipei"))
    }
}
