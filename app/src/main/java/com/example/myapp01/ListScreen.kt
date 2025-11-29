package com.example.myapp01

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.time.Duration.Companion.minutes

@Composable
fun ListScreen(viewModel: MainViewModel) {
    var changeOrderMode by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    val filteredCityList by remember {
        derivedStateOf {
            if (searchText.isBlank()) {
                emptyList()
            } else {
                viewModel.cityListOriginal.filter {
                    it.name.contains(searchText) ||
                            it.nameEn.contains(searchText, ignoreCase = true)
                }
            }
        }
    }
    var nowTime by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        if (viewModel.firstStart) {
            viewModel.firstStart = false
            viewModel.push { DetailPager(viewModel, viewModel.userCityList, 0) }
        }
        while (true) {
            nowTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            delay(1.minutes)
        }
    }
    Column(
        modifier = Modifier
            .padding(18.dp, 0.dp)
            .verticalScroll(rememberScrollState())
    ) {
        VSpacer(12.dp)
        var expanded by remember { mutableStateOf(false) }
        Box(
            modifier = Modifier
                .padding(0.dp, 24.dp, 0.dp, 0.dp)
                .align(AbsoluteAlignment.Right)
        ) {
            if (changeOrderMode) {
                IconButton(onClick = { changeOrderMode = false }) {
                    Icon(Icons.Default.Done, contentDescription = "Done")
                }
            } else {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More options")
                }
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text(getLangText("編輯列表", "Edit List")) },
                    onClick = { changeOrderMode = true; expanded = false }
                )
                DropdownMenuItem(
                    text = { Text(getLangText("設定", "Settings")) },
                    onClick = { viewModel.push { SettingsScreen(viewModel) }; expanded = false }
                )
                HorizontalDivider()
                DropdownMenuItem(
                    text = { Text(getLangText("攝氏 °C", "Celsius °C")) },
                    onClick = {
                        GlobalSettings.setTempMode(TempMode.C)
                        viewModel.saveGlobalSettings()
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text(getLangText("華氏 °F", "Fahrenheit °F")) },
                    onClick = {
                        GlobalSettings.setTempMode(TempMode.F)
                        viewModel.saveGlobalSettings()
                        expanded = false
                    }
                )
            }
        }
        Text(
            text = getLangText("天氣", "Weather"),
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp
        )
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text(getLangText("輸入城市地點來搜尋", "Search for a city")) },
            trailingIcon = {
                Icon(
                    painter = painterResource(R.drawable.baseline_search_24),
                    contentDescription = ""
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        if (searchText.isBlank()) {
            viewModel.userCityList.forEachIndexed { index, city ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (changeOrderMode && index != 0) {
                        Button(
                            onClick = {
                                viewModel.removeCity(index)
                            },
                            modifier = Modifier
                                .height(140.dp)
                                .padding(6.dp)
                                .width(40.dp),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(0.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_remove_24),
                                contentDescription = "",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        WeatherListItem(
                            city = city,
                            weatherData = viewModel.getWeatherData(city.fileName.dropLast(4)),
                            onSwitch = {
                                viewModel.push {
                                    DetailPager(viewModel, viewModel.userCityList, index)
                                }
                            }, nowTime = nowTime
                        )
                    }
                    if (changeOrderMode && index != 0) {
                        Column {
                            Button(
                                onClick = {
                                    viewModel.moveCity(index, index - 1)
                                },
                                modifier = Modifier
                                    .height(70.dp)
                                    .padding(6.dp, 6.dp, 6.dp, 2.dp)
                                    .width(40.dp),
                                shape = RoundedCornerShape(12.dp),
                                enabled = index > 1,
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_keyboard_arrow_up_24),
                                    contentDescription = "",
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Button(
                                onClick = {
                                    viewModel.moveCity(index, index + 1)
                                },
                                modifier = Modifier
                                    .height(70.dp)
                                    .padding(6.dp, 2.dp, 6.dp, 6.dp)
                                    .width(40.dp),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(0.dp),
                                enabled = index != viewModel.userCityList.count() - 1
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_keyboard_arrow_down_24),
                                    contentDescription = "",
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }
            }
        } else {
            filteredCityList.forEach { city ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        WeatherListItem(
                            city = city,
                            weatherData = viewModel.getWeatherData(city.fileName.dropLast(4)),
                            onSwitch = {},
                            nowTime = nowTime
                        )
                    }
                    if (!viewModel.userCityList.contains(city)) {
                        Button(
                            onClick = {
                                viewModel.addCity(city)
                                searchText = ""

                            },
                            modifier = Modifier
                                .height(140.dp)
                                .padding(6.dp)
                                .width(70.dp),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_add_24),
                                contentDescription = "",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("DiscouragedApi")
@Composable
fun WeatherListItem(city: City, weatherData: WeatherData, onSwitch: () -> Unit, nowTime: String) {
    val currentDate = Date()
    val nowHour = SimpleDateFormat("HH:00", Locale.getDefault()).format(currentDate)
    val nowHourlyForecast =
        weatherData.hourlyForecast.find { it.time == nowHour } ?: HourlyForecast("NA", "NA", "NA")
    Box(
        modifier = Modifier
            .height(140.dp)
            .fillMaxWidth()
            .padding(0.dp, 8.dp)
            .clickable {
                onSwitch()
            }
    ) {
        Image(
            painter = painterResource(WeatherRes.valueOf(nowHourlyForecast.weatherCondition).background),
            "",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(10.dp)
                )
                .border(
                    1.dp, Color.Black,
                    RoundedCornerShape(10.dp)
                )
        )
        Row {
            Column(
                modifier = Modifier
                    .weight(.5f)
                    .padding(12.dp),
            ) {
                if (weatherData.isCurrent) {
                    Text(
                        text = getLangText("當前位置", "My location"),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (nowHourlyForecast.weatherCondition == "sunny") Color.Black else Color.White,
                    )
                    Text(
                        text = getLangText(city.name, city.nameEn),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (nowHourlyForecast.weatherCondition == "sunny") Color.Black else Color.White,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Text(
                        text = getLangText(city.name, city.nameEn),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (nowHourlyForecast.weatherCondition == "sunny") Color.Black else Color.White
                    )
                    Text(
                        text = nowTime,
                        color = if (nowHourlyForecast.weatherCondition == "sunny") Color.Black else Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
                Text(
                    text = getLangText(
                        stringResource(
                            id = WeatherRes.valueOf(nowHourlyForecast.weatherCondition).chName
                        ), nowHourlyForecast.weatherCondition.uppercase()
                    ),
                    color = if (nowHourlyForecast.weatherCondition == "sunny") Color.Black else Color.White,
                    fontSize = 14.sp
                )
            }
            Column(
                modifier = Modifier
                    .weight(.5f)
                    .padding(12.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${nowHourlyForecast.temperature}°",
                    color = Color.White,
                    fontSize = 54.sp,
                    modifier = Modifier.weight(1f)
                )
                Row {
                    Text(
                        text = "H: ${weatherData.tenDayForecast.first().highTemperature}°",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "L: ${weatherData.tenDayForecast.first().lowTemperature}°",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ListPreview() {
    ListScreen(MainViewModel().apply { initialize(LocalContext.current); userCityList.add(this.cityListOriginal.last()) })
}