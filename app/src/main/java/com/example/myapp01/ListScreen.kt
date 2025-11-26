package com.example.myapp01

import android.content.Context
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
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ListScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("App08",Context.MODE_PRIVATE)
    val cityListOriginal = remember {
        parseCityXml(context.resources.getXml(R.xml.city_list))
    }
    val userCityList = remember { mutableStateListOf<City>() }
    var changeOrderMode by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        val jsonArray = JSONArray(sharedPreferences.getString("userCityList", "[]"))
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            userCityList.add(
                City(
                    obj.getString("name"),
                    obj.getString("nameEn"),
                    obj.getString("fileName")
                )
            )
        }
        userCityList.apply { if (isEmpty()) userCityList.add(cityListOriginal.first())}
    }
    var searchText by remember { mutableStateOf("") }
    val filteredCityList by remember {
        derivedStateOf {
            if (searchText.isBlank()) {
                emptyList()
            } else {
                cityListOriginal.filter {
                    it.name.contains(searchText) ||
                            it.nameEn.contains(searchText, ignoreCase = true)
                }
            }
        }
    }
    Column(
        modifier = Modifier
            .padding(18.dp, 0.dp)
            .verticalScroll(rememberScrollState())
    ) {
        var expanded by remember { mutableStateOf(false) }
        Box(
            modifier = Modifier
                .padding(0.dp,24.dp,0.dp,0.dp).align(AbsoluteAlignment.Right)
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
                    text = { Text("編輯列表") },
                    onClick = { changeOrderMode = true;expanded = false}
                )
                DropdownMenuItem(
                    text = { Text("Option 2") },
                    onClick = { /* Do something... */ }
                )
            }
        }
        Text(
            text = "天氣",
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp
        )
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("輸入城市地點來搜尋") },
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
            userCityList.forEachIndexed { index, city ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (changeOrderMode && index != 0) {
                        Button(
                            onClick = {
                                userCityList.removeAt(index)
                                val jsonArray = JSONArray()
                                userCityList.forEach { city ->
                                    val jsonObject = JSONObject()
                                    jsonObject.put("name", city.name)
                                    jsonObject.put("nameEn", city.nameEn)
                                    jsonObject.put("fileName", city.fileName)

                                    jsonArray.put(jsonObject)
                                }
                                sharedPreferences.edit().putString("userCityList", jsonArray.toString()).apply()
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
                            context = context,
                            item = city,
                            onSwitch = {
                                viewModel.push {
                                    DetailPager(viewModel, userCityList, index)
                                }
                            }
                        )
                    }
                    if (changeOrderMode && index != 0) {
                        Column {
                            Button(
                                onClick = {
                                    val tmp = city
                                    userCityList[index] = userCityList[index-1]
                                    userCityList[index-1] = tmp
                                    val jsonArray = JSONArray()
                                    userCityList.forEach { city ->
                                        val jsonObject = JSONObject()
                                        jsonObject.put("name", city.name)
                                        jsonObject.put("nameEn", city.nameEn)
                                        jsonObject.put("fileName", city.fileName)

                                        jsonArray.put(jsonObject)
                                    }
                                    sharedPreferences.edit().putString("userCityList", jsonArray.toString()).apply()
                                },
                                modifier = Modifier
                                    .height(70.dp)
                                    .padding(6.dp,6.dp,6.dp,2.dp)
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
                                    val tmp = city
                                    userCityList[index] = userCityList[index+1]
                                    userCityList[index+1] = tmp
                                    val jsonArray = JSONArray()
                                    userCityList.forEach { city ->
                                        val jsonObject = JSONObject()
                                        jsonObject.put("name", city.name)
                                        jsonObject.put("nameEn", city.nameEn)
                                        jsonObject.put("fileName", city.fileName)

                                        jsonArray.put(jsonObject)
                                    }
                                    sharedPreferences.edit().putString("userCityList", jsonArray.toString()).apply()
                                },
                                modifier = Modifier
                                    .height(70.dp)
                                    .padding(6.dp,2.dp,6.dp,6.dp)
                                    .width(40.dp),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(0.dp),
                                enabled = index != userCityList.count()-1
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
                            context = context,
                            item = city,
                            onSwitch = {}
                        )
                    }
                    if (!userCityList.contains(city)) {
                        Button(
                            onClick = {
                                userCityList.add(city)
                                val jsonArray = JSONArray()
                                userCityList.forEach { city ->
                                    val jsonObject = JSONObject()
                                    jsonObject.put("name", city.name)
                                    jsonObject.put("nameEn", city.nameEn)
                                    jsonObject.put("fileName", city.fileName)

                                    jsonArray.put(jsonObject)
                                }
                                sharedPreferences.edit().putString("userCityList", jsonArray.toString()).apply()
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

@Composable
fun WeatherListItem(context: Context,item: City,onSwitch: () -> Unit) {
    val weatherData = parseWeatherData(context.resources.getXml(
        context.resources.getIdentifier(
            item.fileName.dropLast(4),
            "xml",
            context.packageName
        )
    ))
    val currentDate = Date()
    val nowHour = SimpleDateFormat("HH:00", Locale.getDefault()).format(currentDate)
    val nowHourlyForecast =
        weatherData.hourlyForecast.find{it.time == nowHour} ?: HourlyForecast("NA","NA","NA")
    Box (
        modifier = Modifier.height(140.dp).fillMaxWidth().padding(0.dp,8.dp).clickable {
            onSwitch()
        }
    ){
        Image (
            painter = painterResource(
                context.resources.getIdentifier(
                    nowHourlyForecast.weatherCondition,
                    "drawable",
                    context.packageName
                )
            ),"",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth().clip(
                RoundedCornerShape(10.dp)).border(1.dp, Color.Black,
                RoundedCornerShape(10.dp)
            )
        )
        Row {
            Column (
                modifier = Modifier.weight(.5f).padding(12.dp),
            ){
                if (item.fileName == "current.xml") {
                    Text(
                        text = "當前位置",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (nowHourlyForecast.weatherCondition == "sunny") Color.Black else Color.White,
                    )
                    Text(
                        text = item.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (nowHourlyForecast.weatherCondition == "sunny") Color.Black else Color.White,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Text(
                        text = item.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (nowHourlyForecast.weatherCondition == "sunny") Color.Black else Color.White
                    )
                    Text(
                        text = nowHour,
                        color = if (nowHourlyForecast.weatherCondition == "sunny") Color.Black else Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f))
                }
                Text(
                    text = stringResource(
                        id =
                        context.resources.getIdentifier(
                            nowHourlyForecast.weatherCondition,
                            "string",
                            context.packageName
                        )
                    ),
                    color = if (nowHourlyForecast.weatherCondition == "sunny") Color.Black else Color.White,
                    fontSize = 14.sp
                )
            }
            Column (
                modifier = Modifier.weight(.5f).padding(12.dp),
                horizontalAlignment = Alignment.End
            ){
                Text (
                    text = nowHourlyForecast.temperature,
                    color = Color.White,
                    fontSize = 54.sp,
                    modifier = Modifier.weight(1f)
                )
                Row {
                    Text(
                        text = "H: ${weatherData.tenDayForecast.first().highTemperature}",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "L: ${weatherData.tenDayForecast.first().lowTemperature}",
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
    ListScreen(MainViewModel())
}