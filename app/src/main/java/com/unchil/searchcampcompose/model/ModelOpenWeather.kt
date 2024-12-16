package com.unchil.searchcampcompose.model


import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.unchil.searchcampcompose.R
import com.unchil.searchcampcompose.db.entity.CURRENTWEATHER_TBL
import com.unchil.searchcampcompose.shared.HHmmss
import com.unchil.searchcampcompose.shared.UnixTimeToString
import com.unchil.searchcampcompose.shared.yyyyMMddHHmm


data class CurrentWeather(
    @SerializedName("coord") var coord: Coord,
    @SerializedName("weather") var weather: List<Weather>,
    @SerializedName("base") var base: String, //Internal parameter
    @SerializedName("main") var main: Main,
    @SerializedName("visibility") var visibility: Int, // visibility
    @SerializedName("wind") var wind: Wind,
    @SerializedName("clouds") var clouds: Clouds,
    @SerializedName("dt") var dt: Long, //Time of data calculation, unix, UTC
    @SerializedName("sys") var sys: Sys,
    @SerializedName("timezone") var timezone: Long, //Shift in seconds from UTC
    @SerializedName("id") var id: Long, //City ID
    @SerializedName("name") var name: String, //City name
    @SerializedName("cod") var cod: Int // Return Result Code
//   var rain: Rain,
//   var snow: Snow,

)

data class Coord (
    @SerializedName("lon") var lon: Float, //City geo location, longitude
    @SerializedName("lat") var lat: Float //City geo location, latitude
)


data class Weather ( //more info Weather condition codes
    
    @SerializedName("id") var id: Int, //Weather condition id
    @SerializedName("main") var main: String, //Group of weather parameters (Rain, Snow, Extreme etc.)
    @SerializedName("description") var description: String, //Weather condition within the group. You can get the
    @SerializedName("icon") var icon : String //Weather icon id
)

data class Main (
    @SerializedName("temp") var temp: Float, //Temperature. Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
    @SerializedName("feels_like") var feels_like: Float, //Temperature. This temperature parameter accounts for the human perception of weather. Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
    @SerializedName("pressure") var pressure: Float, //Atmospheric pressure (on the sea level, if there is no sea_level or grnd_level data), hPa
    @SerializedName("humidity") var humidity: Float, //Humidity, %
    @SerializedName("temp_min") var temp_min: Float, //Minimum temperature at the moment. This is minimal currently observed temperature (within large megalopolises and urban areas). Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
    @SerializedName("temp_max") var temp_max: Float //Maximum temperature at the moment. This is maximal currently observed temperature (within large megalopolises and urban areas). Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
    //  var sea_level: String, //Atmospheric pressure on the sea level, hPa
    //  var grnd_level: String //Atmospheric pressure on the ground level, hPa
)

data class Wind (
    @SerializedName("speed") var speed : Float, //Wind speed. Unit Default: meter/sec, Metric: meter/sec, Imperial: miles/hour.
    @SerializedName("deg") var deg : Float //Wind direction, degrees (meteorological)
    //  var gust : String //Wind gust. Unit Default: meter/sec, Metric: meter/sec, Imperial:miles/hour
)

data class Clouds (
    @SerializedName("all") var all  : Int //Cloudiness, %
)

data class Sys (
    //   var type : Int, //Internal parameter
//    var id : Int, //Internal parameter
    @SerializedName("country") var country : String, //Country code (GB, JP etc.)
    @SerializedName("sunrise") var sunrise : Long, //Sunrise time, unix, UTC
    @SerializedName("sunset") var sunset : Long //Sunset time, unix, UTC
    //    var message : Float, //Internal parameter
)


fun CurrentWeather.toCURRENTWEATHER_TBL(): CURRENTWEATHER_TBL {

    val currentWeather = CURRENTWEATHER_TBL(
        dt = this.dt,
        base = this.base,
        visibility = this.visibility,
        timezone = this.timezone,
        name = this.name,
        latitude = this.coord.lat,
        longitude = this.coord.lon,
        main = this.weather[0].main,
        description = this.weather[0].description,
        icon = this.weather[0].icon,
        temp = this.main.temp,
        feels_like = this.main.feels_like,
        pressure = this.main.pressure,
        humidity = this.main.humidity,
        temp_min = this.main.temp_min,
        temp_max = this.main.temp_max,
        speed = this.wind.speed,
        deg = this.wind.deg,
        all = this.clouds.all,
        //  type = this.sys.type,
        type = 0,
        country = this.sys.country,
        sunrise = this.sys.sunrise,
        sunset = this.sys.sunset
    )

    return currentWeather
}




const val WEATHER_TEXT_SUN = "sunrise:%s sunset:%s"
const val WEATHER_TEXT_TEMP = "temp:%,.0f°C  min:%,.0f°C  max:%,.0f°C"
const val WEATHER_TEXT_WEATHER = "pressure:%,.0fhPa humidity:%,.0f"
const val WEATHER_TEXT_WIND = "wind:%,.0fm/s deg:%,.0f° visibility:%dkm"

const  val TAG_M_KM = 1000


fun CURRENTWEATHER_TBL.toTextHeadLine(): String {
    return UnixTimeToString(this.dt, yyyyMMddHHmm) + "  ${this.name}/${this.country}"
}


fun CURRENTWEATHER_TBL.toTextWeatherDesc(): String {
    return  "${this.main} : ${this.description}"
}


fun CURRENTWEATHER_TBL.toTextSun(getString: (Int)->String ): String {
    return String.format( getString(R.string.weather_desc_sun),
        UnixTimeToString(this.sunrise, HHmmss),
        UnixTimeToString(this.sunset, HHmmss)
    )
}

fun CURRENTWEATHER_TBL.toTextTemp(getString: (Int)->String): String {
    return String.format ( getString(R.string.weather_desc_temp),
        this.temp,
        this.temp_min,
        this.temp_max
    )
}


fun CURRENTWEATHER_TBL.toTextWeather(getString: (Int)->String): String {
    return String.format( getString(R.string.weather_desc_weather),
        this.pressure,
        this.humidity
    ) + "%"
}


fun CURRENTWEATHER_TBL.toTextWind(getString: (Int)->String): String {
    return   String.format(
        getString(R.string.weather_desc_wind),
        this.speed,
        this.deg,
        this.visibility/ TAG_M_KM )
}
