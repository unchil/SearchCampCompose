package com.unchil.searchcampcompose.api

import com.unchil.searchcampcompose.model.CurrentWeather
import com.unchil.searchcampcompose.model.GoCampingResponse
import com.unchil.searchcampcompose.model.GoCampingResponseImage
import com.unchil.searchcampcompose.model.VWorldResponse_LT_C_ADSIDO_INFO
import com.unchil.searchcampcompose.model.VWorldResponse_LT_C_ADSIGG_INFO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class SearchCampApi {

    private val OPENWEATHER_URL = "https://api.openweathermap.org/data/2.5"
    private val VWORLD_URL = "https://api.vworld.kr/req"
    private val GOCAMPING_URL = "https://apis.data.go.kr/B551011/GoCamping"

    private val httpClient = HttpClient {

        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                useAlternativeNames = false
            })
        }

        install(Logging) {
            logger = Logger.DEFAULT
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 10000
            connectTimeoutMillis = 3000
            socketTimeoutMillis = 3000
        }
    }

    suspend fun getWeatherData(
        lat:String,
        lon:String,
        units:String,
        appid:String
    ): CurrentWeather {
        val url = "${OPENWEATHER_URL}/weather?lat=${lat}&lon=${lon}&units=${units}&appid=${appid}"
        return httpClient.get(urlString = url).body()
    }

    suspend fun recvVWORLD_LT_C_ADSIDO_INFO (
        apiKey:String,
        request:String,
        data:String,
        geomfilter:String,
        size:String,
        geometry:String,
        crs:String,
    ): VWorldResponse_LT_C_ADSIDO_INFO {
        val url = "${VWORLD_URL}/data?key=${apiKey}&request=${request}&data=${data}&crs=${crs}&geomfilter=${geomfilter}&geometry=${geometry}&size=${size}"
        val result = httpClient.get(urlString = url)
        return result.body()
    }

    suspend fun recvVWORLD_LT_C_ADSIGG_INFO (
        apiKey:String,
        request:String,
        data:String,
        geomfilter:String,
        size:String,
        geometry:String,
        crs:String,
    ): VWorldResponse_LT_C_ADSIGG_INFO {
        val url = "${VWORLD_URL}/data?key=${apiKey}&request=${request}&data=${data}&crs=${crs}&geomfilter=${geomfilter}&geometry=${geometry}&size=${size}"
        val result = httpClient.get(urlString = url)
        return result.body()
    }


    suspend fun getDefault(
        serviceKey:String,
        numOfRows:String,
        pageNo:String,
        MobileOS:String,
        MobileApp:String,
        _type:String
    ): GoCampingResponse {
        val url = "${GOCAMPING_URL}/req?serviceKey=${serviceKey}&numOfRows=${numOfRows}&pageNo=${pageNo}&MobileOS=${MobileOS}&MobileApp=${MobileApp}&_type=${_type}"
        return httpClient.get(urlString = url).body()
    }

    suspend fun getLocation(
        serviceKey:String,
        numOfRows:String,
        pageNo:String,
        MobileOS:String,
        MobileApp:String,
        _type:String,
        mapX:String,
        mapY:String,
        radius:String
    ):GoCampingResponse{
        val url = "${GOCAMPING_URL}/req?serviceKey=${serviceKey}&numOfRows=${numOfRows}&pageNo=${pageNo}&MobileOS=${MobileOS}&MobileApp=${MobileApp}&_type=${_type}&mapX=${mapX}&mapY=${mapY}&radius=${radius}"
        return httpClient.get(urlString = url).body()
    }

    suspend fun getSearch(
        serviceKey:String,
        numOfRows:String,
        pageNo:String,
        MobileOS:String,
        MobileApp:String,
        _type:String,
        keyword:String,
    ):GoCampingResponse{
        val url = "${GOCAMPING_URL}/req?serviceKey=${serviceKey}&numOfRows=${numOfRows}&pageNo=${pageNo}&MobileOS=${MobileOS}&MobileApp=${MobileApp}&_type=${_type}&keyword=${keyword}"
        return httpClient.get(urlString = url).body()
    }

    suspend fun getSync(
        serviceKey:String,
        numOfRows:String,
        pageNo:String,
        MobileOS:String,
        MobileApp:String,
        _type:String,
        syncModTime:String,
    ):GoCampingResponse{
        val url = "${GOCAMPING_URL}/req?serviceKey=${serviceKey}&numOfRows=${numOfRows}&pageNo=${pageNo}&MobileOS=${MobileOS}&MobileApp=${MobileApp}&_type=${_type}&syncModTime=${syncModTime}"
        return httpClient.get(urlString = url).body()
    }

    suspend fun getImage(
        serviceKey:String,
        numOfRows:String,
        pageNo:String,
        MobileOS:String,
        MobileApp:String,
        _type:String,
        contentId:String,
    ): GoCampingResponseImage {
        val url = "${GOCAMPING_URL}/req?serviceKey=${serviceKey}&numOfRows=${numOfRows}&pageNo=${pageNo}&MobileOS=${MobileOS}&MobileApp=${MobileApp}&_type=${_type}&contentId=${contentId}"
        return httpClient.get(urlString = url).body()
    }

}