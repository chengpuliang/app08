package com.example.myapp01

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import org.json.JSONArray
import org.json.JSONObject


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewModel by viewModels<MainViewModel>()
        setContent {
            viewModel.initialize(LocalContext.current)
            Crossfade(viewModel.screen, label = "") { screen ->
                screen?.invoke() ?: finish()
            }
            // 處理返回鍵
            BackHandler(onBack = viewModel::pop)
        }
    }
}

class MainViewModel : ViewModel() {
    val userCityList = mutableStateListOf<City>()
    val cityListOriginal = mutableStateListOf<City>()
    private val _weatherDataCache = mutableMapOf<String, WeatherData>()
    private val screens = mutableStateListOf<@Composable () -> Unit>({ ListScreen(this) })
    private var appContext: Context? = null
    private var sharedPreferences: SharedPreferences? = null
    var firstStart = true
    val screen get() = screens.lastOrNull()
    fun initialize(context: Context) {
        if (appContext == null) {
            appContext = context.applicationContext
            cityListOriginal.addAll(parseCityXml(context.resources.getXml(R.xml.city_list)))
            sharedPreferences = context.getSharedPreferences("App08",Context.MODE_PRIVATE)
            val jsonArray = JSONArray(sharedPreferences!!.getString("userCityList", "[]"))
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
    }

    @SuppressLint("DiscouragedApi")
    fun getWeatherData(cityName: String): WeatherData {
        if (_weatherDataCache.containsKey(cityName)) {
            println("use cache for $cityName")
            return _weatherDataCache[cityName]!!
        }
        val context = appContext!!
        val resourceId = context.resources.getIdentifier(
            cityName,
            "xml",
            context.packageName
        )
        val weatherData = parseWeatherData(context.resources.getXml(resourceId))
        _weatherDataCache[cityName] = weatherData
        return weatherData
    }
    fun moveCity(from:Int, to:Int) {
        val tmp = userCityList[from]
        userCityList[from] = userCityList[to]
        userCityList[to] = tmp
        saveUserCityList()
    }
    fun addCity(city: City) {
        userCityList.add(city)
        saveUserCityList()
    }
    fun removeCity(index: Int) {
        userCityList.removeAt(index)
        saveUserCityList()
    }
    fun saveUserCityList() {
        val jsonArray = JSONArray()
        userCityList.forEach { city ->
            val jsonObject = JSONObject()
            jsonObject.put("name", city.name)
            jsonObject.put("nameEn", city.nameEn)
            jsonObject.put("fileName", city.fileName)

            jsonArray.put(jsonObject)
        }
        sharedPreferences!!.edit { putString("userCityList", jsonArray.toString()) }
    }
    fun push(targetScreen: @Composable () -> Unit) {
        screens += targetScreen
    }

    fun pop() {
        screens.removeLastOrNull()
    }
}

@Composable
fun VSpacer(height: Dp) {
    Spacer(modifier = Modifier.height(height))
}

@Composable
fun HSpacer(width: Dp) {
    Spacer(modifier = Modifier.width(width))
}
