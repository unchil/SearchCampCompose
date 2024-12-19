package com.unchil.searchcampcompose.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BackdropScaffold
import androidx.compose.material.BackdropValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material.rememberBackdropScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.unchil.searchcampcompose.R
import com.unchil.searchcampcompose.data.RepositoryProvider
import com.unchil.searchcampcompose.db.LocalSearchCampDB
import com.unchil.searchcampcompose.model.GoCampingService
import com.unchil.searchcampcompose.model.SnackBarChannelType
import com.unchil.searchcampcompose.model.snackbarChannelList
import com.unchil.searchcampcompose.shared.ChkNetWork
import com.unchil.searchcampcompose.shared.checkInternetConnected
import com.unchil.searchcampcompose.shared.view.CheckPermission
import com.unchil.searchcampcompose.shared.view.PermissionRequiredCompose
import com.unchil.searchcampcompose.ui.theme.SearchCampTheme
import com.unchil.searchcampcompose.viewmodel.SearchScreenViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@SuppressLint("UnrememberedMutableState", "MissingPermission")
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterialApi::class)
@Composable
fun SearchScreen(){

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
        multiplePermissions = permissions
    ) {

        var administrativeDistrictSiDoCode by rememberSaveable {
            mutableStateOf("0")
        }

        var administrativeDistrictSiDo by rememberSaveable {
            mutableStateOf("현위치")
        }

        var administrativeDistrictSiGunGu by rememberSaveable {
            mutableStateOf("")
        }

        val searchTitle: MutableState<String?> = rememberSaveable {
            mutableStateOf(null)
        }

        val context = LocalContext.current
        val db = LocalSearchCampDB.current

        val viewModel = remember {
            SearchScreenViewModel(
                repository = RepositoryProvider.getRepository().apply { database = db },
                administrativeDistrictSiDoCode = administrativeDistrictSiDoCode,
                administrativeDistrictSiGunGu = administrativeDistrictSiGunGu,
                searchTitle = searchTitle.value
            )
        }

        val fusedLocationProviderClient = remember {
            LocationServices.getFusedLocationProviderClient(context)
        }
        var isConnected by remember { mutableStateOf(context.checkInternetConnected()) }

        LaunchedEffect(key1 = isConnected) {
            while (!isConnected) {
                delay(500)
                isConnected = context.checkInternetConnected()
            }
        }

        val currentListDataCntStateFlow = viewModel.currentListDataCntStateFlow.collectAsState()

        val channel = remember { Channel<Int>(Channel.CONFLATED) }

        val snackBarHostState = remember { SnackbarHostState() }

        val coroutineScope = rememberCoroutineScope()

        val scaffoldState = rememberBackdropScaffoldState(BackdropValue.Concealed)

        LaunchedEffect(key1 = viewModel) {
            if (isConnected) {

                fusedLocationProviderClient.lastLocation.addOnCompleteListener(context.mainExecutor) { task ->
                    if (task.isSuccessful && task.result != null) {
                        viewModel.onEvent(
                            SearchScreenViewModel.Event.RecvGoCampingData(
                                GoCampingService.NEARCAMPSITE,
                                mapX = task.result.longitude.toString(),
                                mapY = task.result.latitude.toString()
                            )
                        )
                    }
                }


                viewModel.onEvent(
                    SearchScreenViewModel.Event.RecvGoCampingData(
                        GoCampingService.CAMPSITE
                    )
                )

            }


            viewModel.onEvent(
                SearchScreenViewModel.Event.Search(
                    administrativeDistrictSiDoCode,
                    administrativeDistrictSiGunGu,
                    searchTitle.value
                )
            )



            viewModel.effect.collect {
                when (it) {
                    is SearchScreenViewModel.Effect.QueryResultCount -> {
                        channel.trySend(snackbarChannelList.first {
                            it.channelType == SnackBarChannelType.SEARCH_RESULT
                        }.channel)

                    }

                    else -> {}
                }
            }

        }

        LaunchedEffect(channel) {

            channel.receiveAsFlow().collect { index ->

                val channelData = snackbarChannelList.first {
                    it.channel == index
                }

                //----------
                val message = when (channelData.channelType) {
                    SnackBarChannelType.SEARCH_RESULT -> {
                        val resultString = if (currentListDataCntStateFlow.value == 0) {
                            "가 존재하지 않습니다."
                        } else {
                            " [${currentListDataCntStateFlow.value}]"
                        }
                        context.resources.getString(channelData.message) + resultString
                    }

                    else -> {
                        context.resources.getString(channelData.message)
                    }
                }


                val actionLabel = if (channelData.channelType == SnackBarChannelType.SEARCH_RESULT
                    && currentListDataCntStateFlow.value == 0
                ) {
                    ""
                } else {
                    channelData.actionLabel
                }
                //----------


                val result = snackBarHostState.showSnackbar(
                    message = message,
                    actionLabel = actionLabel,
                    withDismissAction = channelData.withDismissAction,
                    duration = channelData.duration
                )
                when (result) {
                    SnackbarResult.ActionPerformed -> {
                        //----------
                        when (channelData.channelType) {
                            SnackBarChannelType.SEARCH_RESULT -> {

                            }

                            else -> {}
                        }
                        //----------
                    }

                    SnackbarResult.Dismissed -> {

                    }
                }
            }
        }

        val onSearchEventHandler: (siDoCode: String, siDo:String,  siGunGuName: String, siteName: String?) -> Unit =
            { siDoCode, siDoName, siGunGuName, siteName ->
                administrativeDistrictSiDoCode = siDoCode
                administrativeDistrictSiDo = siDoName
                administrativeDistrictSiGunGu = siGunGuName
                searchTitle.value = siteName

                viewModel.onEvent(
                    SearchScreenViewModel.Event.Search(
                        siDoCode,
                        siGunGuName,
                        siteName
                    )
                )

                viewModel.eventHandler(
                    SearchScreenViewModel.Event.Search(
                        siDoCode,
                        siGunGuName,
                        siteName
                    )
                )


            }

        val configuration = LocalConfiguration.current
        var isPortrait by remember { mutableStateOf(false) }
        val peekHeight by remember { mutableStateOf(58.dp) }
        var headerHeight  by remember { mutableStateOf(0.dp) }

        when (configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                isPortrait = true
                headerHeight = 70.dp
            }
            else ->{
                isPortrait = false
            }
        }


        BackdropScaffold(
            scaffoldState = scaffoldState,
            peekHeight = peekHeight,
            headerHeight = headerHeight,
            persistentAppBar =  false,
            backLayerBackgroundColor = Color.Transparent,
            frontLayerShape =  ShapeDefaults.Medium,
            frontLayerBackgroundColor = MaterialTheme.colorScheme.primaryContainer,
            snackbarHost = {
                SnackbarHost(hostState = snackBarHostState) {
                    Snackbar(
                        snackbarData = it,
                        modifier = Modifier,
                        shape = ShapeDefaults.ExtraSmall,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        dismissActionContentColor = MaterialTheme.colorScheme.tertiary
                    )
                }
            },
            appBar = {
                    TopAppBar(
                        title = {

                            val text = if( administrativeDistrictSiDoCode.equals("0")) {
                                "${administrativeDistrictSiDo}\n${context.getString(R.string.mainmenu_result)} ${currentListDataCntStateFlow.value} 건"
                            }else {
                                "${administrativeDistrictSiDo} ${administrativeDistrictSiGunGu}\n${context.getString(R.string.mainmenu_result)} ${currentListDataCntStateFlow.value} 건"
                            }

                            Text(
                                text =  text,
                                color = MaterialTheme.colorScheme.onTertiary,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(end = 80.dp),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                        },
                        navigationIcon = {

                            IconButton(
                                onClick = {
                                    if (scaffoldState.isConcealed) {
                                        coroutineScope.launch { scaffoldState.reveal() }

                                    } else {
                                        coroutineScope.launch { scaffoldState.conceal() }
                                    }
                                }
                            ) {
                                Icon(
                                    Icons.Default.TravelExplore,
                                    contentDescription = "Localized description",
                                    tint = MaterialTheme.colorScheme.onTertiary
                                )
                            }

                        },
                        actions = {

                        },
                        elevation = 2.dp,
                        backgroundColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.onTertiary
                    )



            },
            backLayerContent = {

                Box(
                    modifier = Modifier.fillMaxSize()
                ) {


                    ResultNavScreen(viewModel = viewModel){
                        if(scaffoldState.isConcealed){
                            coroutineScope.launch { scaffoldState.reveal() }
                        }else{
                            coroutineScope.launch { scaffoldState.conceal() }
                        }
                    }

                    AnimatedVisibility(visible = !isConnected) {
                        ChkNetWork(onCheckState = {
                            coroutineScope.launch {
                                isConnected = context.checkInternetConnected()
                            }
                        })
                    }

                }


            },
            frontLayerContent = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.TopCenter,
                ) {
                    SearchCampView(
                        onSearchEventHandler = onSearchEventHandler
                    )
                }
            }
        )





    }

}




@Preview
@Composable
fun PrevSearchScreenNew(){

    /*
    val context = LocalContext.current
    val permissionsManager = PermissionsManager()
    val searchCampDB = SearchCampDB.getInstance(context.applicationContext)
*/

    SearchCampTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {

            BackDropScaffoldTest()
/*
            CompositionLocalProvider(LocalPermissionsManager provides permissionsManager) {
                CompositionLocalProvider(LocalSearchCampDB provides searchCampDB) {
                    SearchScreen()
                }
            }
 */
        }
    }


}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun BackDropScaffoldTest(){

    val scaffoldState = rememberBackdropScaffoldState(
        BackdropValue.Concealed
    )
    val scope = rememberCoroutineScope()
    BackdropScaffold(
        scaffoldState = scaffoldState,
        appBar = {
            TopAppBar(
                title = { Text("Backdrop") },
                navigationIcon = {
                    if (scaffoldState.isConcealed) {
                        IconButton(
                            onClick = {
                                scope.launch { scaffoldState.reveal() }
                            }
                        ) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = "Menu"
                            )
                        }
                    } else {
                        IconButton(
                            onClick = {
                                scope.launch { scaffoldState.conceal() }
                            }
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close"
                            )
                        }
                    }
                },
                elevation = 0.dp,
                backgroundColor = Color.Transparent
            )
        },
        backLayerContent = {
            Box(modifier = Modifier.fillMaxSize())
        },
        frontLayerContent = {
            Box(modifier = Modifier.fillMaxSize() )
        },
        modifier = Modifier,
        gesturesEnabled =  true,
        peekHeight =  56.dp, // default app bar height
        headerHeight = 60.dp, // frontLayer header Height
        persistentAppBar =  true,
        stickyFrontLayer =  true,
        backLayerBackgroundColor = Color.Red,
        backLayerContentColor =  Color.Black,
        frontLayerShape =  ShapeDefaults.Medium,
        frontLayerElevation = 6.dp,
        frontLayerBackgroundColor = Color.Yellow,
        frontLayerContentColor =  Color.White,
        frontLayerScrimColor = Color.Green,
    )
}
