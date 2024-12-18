package com.unchil.searchcampcompose.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BedtimeOff
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ModeOfTravel
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.widgets.ScaleBar
import com.unchil.searchcampcompose.LocalUsableHaptic
import com.unchil.searchcampcompose.R
import com.unchil.searchcampcompose.data.GoCampingService
import com.unchil.searchcampcompose.db.LocalSearchCampDB
import com.unchil.searchcampcompose.db.entity.CampSite_TBL
import com.unchil.searchcampcompose.model.SiteDefaultData
import com.unchil.searchcampcompose.shared.ChkNetWork
import com.unchil.searchcampcompose.shared.checkInternetConnected
import com.unchil.searchcampcompose.shared.chromeIntent
import com.unchil.searchcampcompose.shared.view.CheckPermission
import com.unchil.searchcampcompose.shared.view.PermissionRequiredCompose
import com.unchil.searchcampcompose.viewmodel.SearchScreenViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("UnrememberedMutableState", "MissingPermission")
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class,
    MapsComposeExperimentalApi::class
)
@Composable
fun ResultMapScreen(
    viewModel: SearchScreenViewModel
){


    val permissions = listOf(
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    val multiplePermissionsState = rememberMultiplePermissionsState( permissions)

    CheckPermission(multiplePermissionsState = multiplePermissionsState)

    var isGranted by mutableStateOf(true)

    val  interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }

    permissions.forEach { chkPermission ->
        isGranted =   isGranted && multiplePermissionsState.permissions.find { it.permission == chkPermission  }?.status?.isGranted ?: false
    }

    PermissionRequiredCompose(
        isGranted = isGranted,
        multiplePermissions = permissions
    ) {

        val context = LocalContext.current


        val configuration = LocalConfiguration.current
        var isPortrait by remember { mutableStateOf(false) }
        isPortrait = when (configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                true
            }
            else ->{
                false
            }
        }

        var columnWidth by remember { mutableStateOf(1f) }
        var bottomPadding by remember { mutableStateOf(0.dp) }

        when (configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                isPortrait = true
                columnWidth = 9f
                bottomPadding = 60.dp
            }
            else ->{
                isPortrait = false
                columnWidth = 0.5f
                bottomPadding = 0.dp
            }
        }



        val db = LocalSearchCampDB.current
        val isUsableHaptic = LocalUsableHaptic.current
        val hapticFeedback = LocalHapticFeedback.current
        val coroutineScope = rememberCoroutineScope()

        var isDarkMode by remember { mutableStateOf(false) }

        /*
        // Event Handler 와 충돌
        var isHapticProcessing by remember { mutableStateOf(false) }

        LaunchedEffect(key1 = isHapticProcessing) {
            if (isHapticProcessing) {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                isHapticProcessing = false
            }
        }

         */


        fun hapticProcessing(){
            if(isUsableHaptic){
                coroutineScope.launch {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }
            }
        }



        val fusedLocationProviderClient = remember {
            LocationServices.getFusedLocationProviderClient(context)
        }


        val currentSiteDataList = viewModel.currentListDataStateFlow.collectAsState()

        var isSetCurrentLocation by remember { mutableStateOf(false) }

        var currentLocation by remember {
            mutableStateOf(LatLng(0.0,0.0))
        }


        LaunchedEffect( key1 =  currentLocation){
            if( currentLocation == LatLng(0.0,0.0)) {
                fusedLocationProviderClient.lastLocation.addOnCompleteListener( context.mainExecutor) { task ->
                    if (task.isSuccessful && task.result != null ) {
                        currentLocation = task.result.toLatLng()
                        isSetCurrentLocation = true
                    }
                }
            }else {
                isSetCurrentLocation = true
            }
        }

        var isGoCampSiteLocation by remember { mutableStateOf(false) }

        val  campSiteBound: (List<CampSite_TBL>)-> LatLngBounds = {

            if(it.isNotEmpty()) {
                val latitudes = it.map {
                    it.mapY.toDouble()
                }

                val longitude = it.map {
                    it.mapX.toDouble()
                }

                LatLngBounds(
                    LatLng((latitudes.min()), longitude.min()),  // SW bounds
                    LatLng((latitudes.max()), longitude.max()) // NE bounds
                )
            } else {
                LatLngBounds(
                    currentLocation,
                    currentLocation
                )
            }

        }


        // No ~~~~ remember
        val markerState =  MarkerState( position = currentLocation )
        val defaultCameraPosition =  CameraPosition.fromLatLngZoom( currentLocation, 12f)
        val cameraPositionState =  CameraPositionState(position = defaultCameraPosition)


        val isUsableDarkMode by remember { mutableStateOf(false) }

        var mapProperties by remember {
            mutableStateOf(
                MapProperties(
                    mapType = MapType.NORMAL,
                    isMyLocationEnabled = true,
                    mapStyleOptions = if(isUsableDarkMode) {
                        MapStyleOptions.loadRawResourceStyle(
                            context,
                            R.raw.mapstyle_night
                        )
                    } else { null }
                )
            )
        }

        val uiSettings by remember {
            mutableStateOf(
                MapUiSettings(
                    compassEnabled = true,
                    myLocationButtonEnabled = true,
                    mapToolbarEnabled = true,
                    zoomControlsEnabled = false

                )
            )
        }


        val onMapLongClickHandler: (LatLng) -> Unit = {
            //       markerState.position = it
            //        cameraPositionState =   CameraPositionState(position = CameraPosition.fromLatLngZoom(it, 12f))
        }

        var mapTypeIndex by rememberSaveable { mutableStateOf(0) }

        var isVisibleSiteDefaultView by remember { mutableStateOf(false) }
        var currentSiteDefaultData: SiteDefaultData? by remember {
            mutableStateOf(null)
        }

        val sheetState = SheetState(
            skipPartiallyExpanded = false,
            density = LocalDensity.current,
            initialValue = SheetValue.PartiallyExpanded,
            skipHiddenState = true
        )

        val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = sheetState)
        val currentCampSiteData: MutableState<SiteDefaultData?> = remember { mutableStateOf(null) }
        val sheetPeekHeightValue by remember { mutableStateOf(0.dp) }

        val dragHandlerAction:()->Unit = {
            coroutineScope.launch {
                if (scaffoldState.bottomSheetState.currentValue == SheetValue.PartiallyExpanded) {
                    scaffoldState.bottomSheetState.expand()
                } else {
                    scaffoldState.bottomSheetState.partialExpand()
                }
            }
        }

        var isConnected by remember { mutableStateOf(context.checkInternetConnected()) }


        LaunchedEffect(key1 = isConnected) {
            while (!isConnected) {
                delay(500)
                isConnected = context.checkInternetConnected()
            }
        }

        var isFirstTab by remember {mutableStateOf(true)}



        val onClickHandler: (data: SiteDefaultData) -> Unit = {
            isConnected = context.checkInternetConnected()
            currentCampSiteData.value = it
            isFirstTab = true
            dragHandlerAction.invoke()
            hapticProcessing()
        }


        val onClickPhotoHandler: (data: SiteDefaultData) -> Unit = {
            isConnected = context.checkInternetConnected()
            viewModel.onEvent(SearchScreenViewModel.Event.InitRecvSiteImageList)
            currentCampSiteData.value = it
            isFirstTab = false
            dragHandlerAction.invoke()
            hapticProcessing()

            viewModel.onEvent(
                SearchScreenViewModel.Event.RecvGoCampingData(
                    servicetype = GoCampingService.SITEIMAGE,
                    contentId = it.contentId
                )
            )

        }


        BottomSheetScaffold(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            scaffoldState = scaffoldState,
            sheetPeekHeight = sheetPeekHeightValue,
            sheetShape = ShapeDefaults.Small,
            sheetDragHandle = {
                Box(
                    modifier = Modifier
                        .height(60.dp)
                        .fillMaxWidth()
                        .background(color = MaterialTheme.colorScheme.tertiary),
                    contentAlignment = Alignment.Center
                ) {
                    /*
                    Icon(
                        modifier = Modifier
                            .scale(1f)
                            .clickable {
                                dragHandlerAction.invoke()
                            },
                        imageVector =
                        if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded)
                            Icons.Outlined.KeyboardArrowDown
                        else Icons.Outlined.KeyboardArrowUp,
                        contentDescription = "SiteDetailScreen",
                    )

                     */

                    currentCampSiteData.value?.let {

                        Row (
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ){

                            Text(
                                text = it.facltNm,
                                modifier = Modifier.padding(vertical = 2.dp),
                                color = MaterialTheme.colorScheme.onTertiary,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.titleMedium
                            )

                            if(it.homepage.isNotEmpty()){
                                IconButton(onClick = {  chromeIntent.invoke(context, it.homepage)  }    ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Home,
                                        contentDescription = "홈페이지",
                                        modifier = Modifier,
                                        tint = MaterialTheme.colorScheme.onTertiary
                                    )
                                }
                            }




                        }




                    }


                }
            },
            sheetContent = {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(1f),
                    contentAlignment = Alignment.Center

                ) {
                    currentCampSiteData.value?.let {
                        if (isFirstTab) {
                            SiteIntroductionView(it)
                        } else {
                            SiteImagePagerView(viewModel = viewModel)
                        }
                    }
                }
            }
        ) { innerPadding ->

            Box(
                modifier = Modifier.padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {


                GoogleMap(
                    cameraPositionState = cameraPositionState,
                    properties = mapProperties,
                    uiSettings = uiSettings,
                    onMapLongClick = onMapLongClickHandler,
                    onMapClick = {
                        isVisibleSiteDefaultView = false }
                ) {

                    Marker(
                        state = markerState,
                        title = "lat/lng:(${String.format("%.5f", markerState.position.latitude)},${String.format("%.5f", markerState.position.longitude)})",
                    )


                    MapEffect(key1 = isGoCampSiteLocation, key2 = isSetCurrentLocation){ googleMap ->

                        if(isSetCurrentLocation){

                            val cameraPosition = CameraPosition.Builder()
                                .target(campSiteBound(currentSiteDataList.value).center)
                                .zoom(10f)
                                .bearing(0f)
                                .tilt(30f)
                                .build()

                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

                            isSetCurrentLocation = false
                        }

                        if(isGoCampSiteLocation) {

                            val cameraPosition = CameraPosition.Builder()
                                .target(campSiteBound(currentSiteDataList.value).center)
                                .zoom(10f)
                                .bearing(0f)
                                .tilt(30f)
                                .build()

                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

                            isGoCampSiteLocation = false
                        }

                    }



                    currentSiteDataList.value.forEach { it ->

                        val state = MarkerState(position = LatLng(it.mapY.toDouble(), it.mapX.toDouble()))
                        Marker(
                            state = state,
                            title =  it.facltNm,
                            onClick = {marker ->
                                isVisibleSiteDefaultView = true
                                currentSiteDefaultData = CampSite_TBL.toSiteDefaultData(it)
                                false
                            },
                            onInfoWindowClick = {
                                isVisibleSiteDefaultView = false
                            }
                        )

                    }

                }

                Column(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 10.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp),
                            shape = ShapeDefaults.ExtraSmall
                        )
                ) {


                    IconButton(
                        onClick = {
                            hapticProcessing()
                            isDarkMode = !isDarkMode
                            if (isDarkMode) {
                                mapProperties = mapProperties.copy(
                                    mapStyleOptions = MapStyleOptions.loadRawResourceStyle(
                                        context,
                                        R.raw.mapstyle_night
                                    )
                                )
                            } else {
                                mapProperties = mapProperties.copy(mapStyleOptions = null)
                            }
                        }
                    ) {
                        Icon(
                            modifier = Modifier.scale(1f),
                            imageVector = if (isDarkMode) Icons.Outlined.BedtimeOff else Icons.Outlined.DarkMode,
                            contentDescription = "DarkMode",
                        )
                    }


                    IconButton(
                        onClick = {
                            hapticProcessing()
                            isGoCampSiteLocation = true
                        }
                    ) {
                        Icon(
                            modifier = Modifier.scale(1f),
                            imageVector = Icons.Outlined.ModeOfTravel,
                            contentDescription = "GoCampSiteLocationl",
                        )
                    }


                }


                ScaleBar(
                    modifier = Modifier
                        .padding(bottom = 30.dp)
                        .align(Alignment.BottomStart),
                    cameraPositionState = cameraPositionState
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 10.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp),
                            shape = ShapeDefaults.ExtraSmall
                        )
                ) {
                    MapTypeMenuList.forEachIndexed { index, it ->
                        AnimatedVisibility(
                            visible = true,
                        ) {
                            IconButton(
                                onClick = {
                                    hapticProcessing()
                                    val mapType = MapType.values().first { mapType ->
                                        mapType.name == it.name
                                    }
                                    mapProperties = mapProperties.copy(mapType = mapType)
                                    mapTypeIndex = index

                                }) {

                                Icon(
                                    imageVector = it.getDesc().first,
                                    contentDescription = it.name,
                                )
                            }
                        }
                    }
                }


                currentSiteDefaultData?.let {

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth(columnWidth)
                            .padding(bottom = bottomPadding)
                            .padding(horizontal = 10.dp),
                        contentAlignment = Alignment.Center,
                    ) {

                        AnimatedVisibility(visible = isVisibleSiteDefaultView) {
                            SiteDefaultView(
                                siteData = it,
                                onClick = {
                                    onClickHandler.invoke(it)
                                },
                                onClickPhoto = {
                                    onClickPhotoHandler.invoke(it)
                                },
                                onLongClick = {
                                    onClickHandler.invoke(it)
                                }
                            )
                        }

                    }
                }

                if( !isConnected) {
                    ChkNetWork(onCheckState = {
                        coroutineScope.launch {
                            isConnected = context.checkInternetConnected()
                        }
                    })
                }


            }



        }//BottomSheetScaffold


    }// permission
}