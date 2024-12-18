package com.unchil.searchcampcompose.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeviceThermostat
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material.icons.outlined.Storm
import androidx.compose.material.icons.outlined.WbTwilight
import androidx.compose.material.icons.outlined.WindPower
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorFilter.Companion.tint
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unchil.searchcampcompose.LocalUsableDarkMode
import com.unchil.searchcampcompose.data.RepositoryProvider
import com.unchil.searchcampcompose.db.*
import com.unchil.searchcampcompose.shared.*
import com.unchil.searchcampcompose.ui.theme.SearchCampTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.unchil.searchcampcompose.shared.checkInternetConnected
import com.unchil.searchcampcompose.R
import com.unchil.searchcampcompose.db.entity.CURRENTWEATHER_TBL
import com.unchil.searchcampcompose.model.*
import com.unchil.searchcampcompose.shared.view.CheckPermission
import com.unchil.searchcampcompose.shared.view.PermissionRequiredCompose
import com.unchil.searchcampcompose.shared.view.PermissionRequiredComposeFuncName
import com.unchil.searchcampcompose.viewmodel.WeatherViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


private fun getWeatherIcon(type:String):Int {
    return when(type){
        "01d" -> R.drawable.ic_openweather_01d
        "01n" -> R.drawable.ic_openweather_01n
        "02d" -> R.drawable.ic_openweather_02d
        "02n" -> R.drawable.ic_openweather_02n
        "03d" -> R.drawable.ic_openweather_03d
        "03n" -> R.drawable.ic_openweather_03n
        "04d" -> R.drawable.ic_openweather_04d
        "04n" -> R.drawable.ic_openweather_04n
        "09d" -> R.drawable.ic_openweather_09d
        "09n" -> R.drawable.ic_openweather_09n
        "10d" -> R.drawable.ic_openweather_10d
        "10n" -> R.drawable.ic_openweather_10n
        "11d" -> R.drawable.ic_openweather_11d
        "11n" -> R.drawable.ic_openweather_11n
        "13d" -> R.drawable.ic_openweather_13d
        "13n" -> R.drawable.ic_openweather_13n
        "50d" -> R.drawable.ic_openweather_50d
        "50n" -> R.drawable.ic_openweather_50n
        else -> R.drawable.ic_openweather_unknown
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("MissingPermission", "SuspiciousIndentation", "UnrememberedMutableState")
@Composable
fun WeatherContent(isSticky:Boolean = false ){

    val permissions = listOf(
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    val multiplePermissionsState = rememberMultiplePermissionsState( permissions)
    CheckPermission(multiplePermissionsState = multiplePermissionsState)

    var isGranted by mutableStateOf(true)
    permissions.forEach { chkPermission ->
        isGranted =   isGranted && multiplePermissionsState.permissions.find { it.permission == chkPermission  }?.status?.isGranted ?: false
    }

    PermissionRequiredCompose(
        isGranted = isGranted,
        multiplePermissions = permissions ,
        viewType = PermissionRequiredComposeFuncName.Weather
    ) {

        val context = LocalContext.current
        val configuration = LocalConfiguration.current
        val coroutineScope = rememberCoroutineScope()
        val db = LocalSearchCampDB.current

        val viewModel = remember {
            WeatherViewModel(repository = RepositoryProvider.getRepository().apply { database = db }  )
        }

        val fusedLocationProviderClient = remember {
            LocationServices.getFusedLocationProviderClient(context)
        }

        var isSuccessfulTask by rememberSaveable { mutableStateOf( false ) }

        var isConnected  by  remember { mutableStateOf(context.checkInternetConnected()) }

        LaunchedEffect(key1 = isConnected ){
            while(!isConnected) {
                delay(500)
                isConnected = context.checkInternetConnected()
            }
        }

        val checkCurrentWeather:()->Unit = {
            coroutineScope.launch {
                fusedLocationProviderClient.lastLocation.addOnCompleteListener( context.mainExecutor) { task ->
                    if (task.isSuccessful && task.result != null ) {
                        isSuccessfulTask = true
                        if(isConnected) {
                            viewModel.searchWeather(task.result.toLatLng())
                        }
                    }
                }
            }
        }

        LaunchedEffect(key1 = viewModel ){
            checkCurrentWeather.invoke()
        }


        val weatherData = viewModel.currentWeatheStaterFlow.collectAsState()

        Box(
            modifier = Modifier
                .width(400.dp)
                .clip(shape = ShapeDefaults.ExtraSmall)
                .background(
                    color = Color.Transparent
                ),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility  (!isSuccessfulTask  ) {
                IconButton(
                    onClick = checkCurrentWeather,
                    content = {
                        Row(
                            modifier = Modifier,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                modifier = Modifier.padding(end = 10.dp),
                                imageVector = Icons.Outlined.SearchOff,
                                contentDescription = "LocationSearching"
                            )
                            Text(text = context.resources.getString(R.string.weather_location_searching))
                        }
                    })
            }

            AnimatedVisibility (weatherData.value.dt != 0L){
                when (configuration.orientation) {
                    Configuration.ORIENTATION_PORTRAIT -> {
                        WeatherView(weatherData.value)
                    }
                    else -> {
                        if (isSticky) {
                            WeatherView(weatherData.value)
                        } else {
                            WeatherViewLandScape(weatherData.value)
                        }
                    }
                }
            }

            if (!isConnected) {
                ChkNetWork(
                    onCheckState = {
                        coroutineScope.launch {
                            isConnected =  context.checkInternetConnected()
                        }
                    }
                )
            }

        }

    }

}



@Composable
fun WeatherView(
    item: CURRENTWEATHER_TBL,
    modifier:Modifier = Modifier
){

    val context = LocalContext.current
    val isUsableDarkMode = LocalUsableDarkMode.current
    val colorFilter:ColorFilter? = if(isUsableDarkMode){
        tint(Color.LightGray.copy(alpha = 0.3f), blendMode = BlendMode.Darken)
    }else {
        null
    }



    Column(
        modifier = modifier
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Text( item.toTextHeadLine()
            , modifier = Modifier.fillMaxWidth()
            , textAlign = TextAlign.Center
            , style  = MaterialTheme.typography.titleSmall
        )

        Text(item.toTextWeatherDesc()
            , modifier = Modifier.fillMaxWidth()
            , textAlign = TextAlign.Center
            , style  = MaterialTheme.typography.titleSmall
        )

        Row(
            modifier = Modifier.align(Alignment.Start)

        ) {

            Image(
                painter =  painterResource(id = getWeatherIcon(item.icon)),
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .clip(ShapeDefaults.Small)
                    .fillMaxWidth(0.2f),
                contentDescription = "weather",
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center,
                colorFilter = colorFilter
            )

            Column (modifier = Modifier.padding(start= 10.dp)){
                WeatherItem(id =  Icons.Outlined.WbTwilight, desc = item.toTextSun(context.resources::getString))
                WeatherItem(id = Icons.Outlined.DeviceThermostat, desc = item.toTextTemp(context.resources::getString))
                WeatherItem(id = Icons.Outlined.WindPower, desc = item.toTextWind(context.resources::getString))
                WeatherItem(id = Icons.Outlined.Storm, desc = item.toTextWeather(context.resources::getString))
            }


        }

    }

}

@Composable
fun WeatherViewLandScape(
    item: CURRENTWEATHER_TBL,
    modifier:Modifier = Modifier
) {
    val context = LocalContext.current

    val isUsableDarkMode = LocalUsableDarkMode.current
    val colorFilter:ColorFilter? = if(isUsableDarkMode){
        tint(Color.LightGray.copy(alpha = 0.3f), blendMode = BlendMode.Darken)
    }else {
        null
    }

    Column(
        modifier = modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            item.toTextHeadLine(),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleSmall
        )

        Text(
            item.toTextWeatherDesc(),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleSmall
        )

        Image(
            painter =  painterResource(id = getWeatherIcon(item.icon)),
            modifier = Modifier
                .padding(vertical = 10.dp)
                .clip(ShapeDefaults.Small)
                .fillMaxWidth(0.5f),
            contentDescription = "weather",
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center,
            colorFilter = colorFilter
            )


        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp),
            horizontalAlignment = Alignment.Start
        ){
            WeatherItem(id =  Icons.Outlined.WbTwilight, desc = item.toTextSun(context.resources::getString))
            WeatherItem(id = Icons.Outlined.DeviceThermostat, desc = item.toTextTemp(context.resources::getString))
            WeatherItem(id = Icons.Outlined.WindPower, desc = item.toTextWind(context.resources::getString))
            WeatherItem(id = Icons.Outlined.Storm, desc = item.toTextWeather(context.resources::getString))
        }


    }

}

@Composable
fun WeatherItem(id: ImageVector, desc: String){

    Row( modifier = Modifier, verticalAlignment = Alignment.CenterVertically) {

        Icon(  imageVector = id
            , contentDescription = "desc"
            , modifier = Modifier
                .height(20.dp)
                .padding(end = 10.dp)

        )

        Text( desc
            , modifier = Modifier
            , textAlign = TextAlign.Start
            , style  = MaterialTheme.typography.bodySmall
        )
    }


}

@Preview
@Composable
fun PrevWeatherContent(){
    val context = LocalContext.current
    val permissionsManager = PermissionsManager()
    val searchCampDB = SearchCampDB.getInstance(context.applicationContext)

    SearchCampTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            CompositionLocalProvider(LocalPermissionsManager provides permissionsManager) {
                CompositionLocalProvider(LocalSearchCampDB provides searchCampDB) {
                    WeatherContent(isSticky = false)
                }
            }
        }
    }
}
