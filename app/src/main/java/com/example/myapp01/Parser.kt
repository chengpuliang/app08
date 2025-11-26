package com.example.myapp01

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import org.xmlpull.v1.XmlPullParser

data class WeatherData (
    val currentWeather: CurrentWeather,
    val hourlyForecast: List<HourlyForecast>,
    val tenDayForecast: List<TenDayForecast>,
    val airQualityIndex: AirQualityIndex
)

data class CurrentWeather (
    val city: String,
    val latitude: String,
    val longitude: String
)

data class HourlyForecast (
    val time: String,
    val weatherCondition: String,
    val temperature: String
)

data class TenDayForecast (
    val date: String,
    val weatherCondition: String,
    val highTemperature: String,
    val lowTemperature: String
)

data class AirQualityIndex (
    val currentAqi: String
)

data class City (
    val name: String,
    val nameEn: String,
    val fileName: String
)

fun parseWeatherData(parser: XmlPullParser): WeatherData {
    var eventType = parser.eventType
    var cityName = ""
    var aqi = ""
    var latitude = ""
    var longitude = ""
    var hourTime = ""
    var weatherCondition = ""
    var temperature = ""
    var dayDate = ""
    var dayHigh = ""
    var dayLow = ""
    var hourlyForecast = mutableListOf<HourlyForecast>()
    var tenDayForecast = mutableListOf<TenDayForecast>()
    while (eventType != XmlPullParser.END_DOCUMENT) {
        when (eventType) {
            XmlPullParser.START_TAG -> {
                when (parser.name) {
                    "city" -> cityName = parser.nextText()
                    "current_aqi" -> aqi = parser.nextText()
                    "latitude" -> latitude = parser.nextText()
                    "longitude" -> longitude = parser.nextText()
                    "time" -> hourTime = parser.nextText()
                    "weather_condition" -> weatherCondition = parser.nextText()
                    "temperature" -> temperature = parser.nextText()
                    "date" -> dayDate = parser.nextText()
                    "high_temperature" -> dayHigh = parser.nextText()
                    "low_temperature" -> dayLow = parser.nextText()
                }
            }
            XmlPullParser.END_TAG -> {
                when (parser.name) {
                    "hour" -> {
                        hourlyForecast.add(
                            HourlyForecast(hourTime,weatherCondition, temperature)
                        )
                    }
                    "day" -> {
                        tenDayForecast.add(
                            TenDayForecast(dayDate,weatherCondition,dayHigh,dayLow)
                        )
                    }
                }
            }

        }
        eventType = parser.next()
    }
    return WeatherData(
        currentWeather = CurrentWeather(cityName,latitude,longitude),
        hourlyForecast = hourlyForecast,
        tenDayForecast = tenDayForecast,
        airQualityIndex = AirQualityIndex(aqi)
    )
}

fun parseCityXml(parser: XmlPullParser): List<City> {
    val cityList = mutableStateListOf<City>()
    var eventType = parser.eventType
    var name = ""
    var nameEn = ""
    var fileName = ""
    while (eventType != XmlPullParser.END_DOCUMENT) {
        when (eventType) {
            XmlPullParser.START_TAG -> {
                when (parser.name) {
                    "name" -> name = parser.nextText()
                    "nane_en" -> nameEn = parser.nextText()
                    "file_name" -> fileName = parser.nextText()
                }
            }
            XmlPullParser.END_TAG -> {
                if (parser.name == "city") {
                    cityList.add(
                        City(name,nameEn,fileName)
                    )
                }
            }
        }
        eventType = parser.next()
    }
    return cityList
}

enum class WeatherRes (
    val icon: Int,
    val background: Int,
    val chName: Int
) {
    cloudy(R.drawable.ic_cloudy,R.drawable.cloudy,R.string.cloudy),
    overcast(R.drawable.ic_overcast,R.drawable.overcast, R.string.overcast),
    rain(R.drawable.ic_rain,R.drawable.rain, R.string.rain),
    sunny(R.drawable.ic_sunny,R.drawable.sunny, R.string.sunny),
    thunder(R.drawable.ic_thunder,R.drawable.thunder, R.string.thunder)
}

fun getColorOnBg(weatherCondition: String): Color {
    return if (weatherCondition == "sunny")
        Color.Black
    else
        Color.White
}