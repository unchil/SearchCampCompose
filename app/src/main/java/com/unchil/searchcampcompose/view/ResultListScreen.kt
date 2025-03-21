package com.unchil.searchcampcompose.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Publish
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.size.Size
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.unchil.searchcampcompose.LocalUsableHaptic
import com.unchil.searchcampcompose.R
import com.unchil.searchcampcompose.db.entity.CampSite_TBL
import com.unchil.searchcampcompose.model.GoCampingService
import com.unchil.searchcampcompose.model.SiteDefaultData
import com.unchil.searchcampcompose.shared.ChkNetWork
import com.unchil.searchcampcompose.shared.checkInternetConnected
import com.unchil.searchcampcompose.shared.chromeIntent
import com.unchil.searchcampcompose.shared.hapticProcessing
import com.unchil.searchcampcompose.shared.view.CheckPermission
import com.unchil.searchcampcompose.shared.view.ImageViewer
import com.unchil.searchcampcompose.shared.view.PermissionRequiredCompose
import com.unchil.searchcampcompose.viewmodel.SearchScreenViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun UpButton(
    modifier:Modifier,
    isPortrait:Boolean,
    listState:Any
){

    val showButton by remember {
        derivedStateOf {
            if (isPortrait) {
                ( listState as LazyListState).firstVisibleItemIndex > 0
            } else {
                ( listState as LazyGridState).firstVisibleItemIndex > 0
            }
        }
    }


    val isUsableHaptic = LocalUsableHaptic.current
    val hapticFeedback = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()



    if( showButton) {
        FloatingActionButton(
            modifier = Modifier.then(modifier),
            elevation =  FloatingActionButtonDefaults.elevation(defaultElevation = 2.dp),
            onClick = {
                coroutineScope.launch {
                    if (isPortrait) {
                        ( listState as LazyListState).animateScrollToItem(0)
                    } else {
                        ( listState as LazyGridState).animateScrollToItem(0)
                    }
                    hapticProcessing(coroutineScope, hapticFeedback, isUsableHaptic)
                }
            }
        ) {
            Icon(
                modifier = Modifier.scale(1f),
                imageVector = Icons.Outlined.Publish,
                contentDescription = "Up",

                )

        }
    }

}

@Composable
fun SearchingProgressIndicator(
    isVisibility:Boolean
){
    if(isVisibility) {
        Box(modifier = Modifier.fillMaxSize()) {

            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )

        }
    }
}

@Composable
fun ErrorProgressView(){
    Box(modifier = Modifier.fillMaxSize()) {
        Text( "Error",
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ResultListScreen(
    viewModel: SearchScreenViewModel
){
    val permissions = listOf(
        Manifest.permission.INTERNET,
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
        val campSiteStream = viewModel.campSiteListPaging.collectAsLazyPagingItems()
        var isLoading by remember { mutableStateOf(true) }
        val isUsableHaptic = LocalUsableHaptic.current
        val hapticFeedback = LocalHapticFeedback.current
        val coroutineScope = rememberCoroutineScope()

        val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState =  SheetState(
            skipPartiallyExpanded = false,
            density = LocalDensity.current,
            initialValue = SheetValue.PartiallyExpanded,
            skipHiddenState = true
        ))

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
        val context = LocalContext.current
        val sheetDragHandle: @Composable (() -> Unit)? = {
            Box(
                modifier = Modifier
                    .height(60.dp)
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.tertiary),
                contentAlignment = Alignment.Center
            ) {

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
        }
        var isFirstTab by remember {mutableStateOf(true)}
        val sheetContent: @Composable (ColumnScope.() -> Unit) = {
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
                        SiteImagePagerView(viewModel = viewModel )
                    }
                }
            }
        }
        var isPortrait by remember { mutableStateOf(false) }
        val lazyListState = rememberLazyListState(initialFirstVisibleItemIndex = 0)
        val lazyGridState = rememberLazyGridState(initialFirstVisibleItemIndex = 0)
        var isConnected by remember { mutableStateOf(context.checkInternetConnected()) }
        val onClickHandler: (data: SiteDefaultData) -> Unit = {
            isConnected = context.checkInternetConnected()
            currentCampSiteData.value = it
            isFirstTab = true
            dragHandlerAction.invoke()
            hapticProcessing(coroutineScope, hapticFeedback, isUsableHaptic)
        }
        val onClickPhotoHandler: (data: SiteDefaultData) -> Unit = {
            isConnected = context.checkInternetConnected()
            viewModel.onEvent(SearchScreenViewModel.Event.InitRecvSiteImageList)
            currentCampSiteData.value = it
            isFirstTab = false
            dragHandlerAction.invoke()
            hapticProcessing(coroutineScope, hapticFeedback, isUsableHaptic)

            viewModel.onEvent(
                SearchScreenViewModel.Event.RecvGoCampingData(
                    servicetype = GoCampingService.SITEIMAGE,
                    contentId = it.contentId
                )
            )
        }
        val content: @Composable (PaddingValues) -> Unit = { innerPadding ->
            Box(
                modifier = Modifier.padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {


                if (isPortrait) {
                    LazyColumn(
                        modifier = Modifier
                            .align(Alignment.TopCenter),
                        state = lazyListState,
                        userScrollEnabled = true,
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        contentPadding = PaddingValues(
                            horizontal = 0.dp,
                            vertical = 1.dp
                        )
                    ) {

                        items(campSiteStream.itemCount) {

                            campSiteStream[it]?.let {

                                val siteDefaultData =
                                    CampSite_TBL.toSiteDefaultData(it)
                                SiteDefaultView(
                                    siteData = siteDefaultData,
                                    onClick = {
                                        onClickHandler.invoke(
                                            siteDefaultData
                                        )
                                    },
                                    onClickPhoto = {

                                        onClickPhotoHandler.invoke(
                                            siteDefaultData
                                        )
                                    },
                                    onLongClick = {

                                        onClickHandler.invoke(
                                            siteDefaultData
                                        )
                                    }
                                )

                                Spacer(modifier = Modifier.padding(bottom = 1.dp))

                            }


                        }

                    }

                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .align(Alignment.TopCenter),
                        state = lazyGridState,
                        userScrollEnabled = true,
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalArrangement = Arrangement.Center,
                        contentPadding = PaddingValues(
                            horizontal = 2.dp,
                            vertical = 2.dp
                        )
                    ) {

                        items(campSiteStream.itemCount) {

                            campSiteStream[it]?.let {

                                val siteDefaultData =
                                    CampSite_TBL.toSiteDefaultData(it)
                                SiteDefaultView(
                                    siteData = siteDefaultData,
                                    onClick = {
                                        onClickHandler.invoke(
                                            siteDefaultData
                                        )
                                    },
                                    onClickPhoto = {
                                        onClickPhotoHandler.invoke(
                                            siteDefaultData
                                        )
                                    },
                                    onLongClick = {
                                        onClickHandler.invoke(
                                            siteDefaultData
                                        )
                                    }
                                )

                                Spacer(modifier = Modifier.padding(10.dp))

                            }


                        }

                    }
                }

                UpButton(
                    modifier = Modifier
                        .padding(end = 10.dp, bottom = 10.dp)
                        .align(Alignment.BottomEnd),
                    isPortrait= isPortrait,
                    listState = if(isPortrait) lazyListState else lazyGridState
                )

                if(campSiteStream.itemCount == 0) {
                    ImageViewer(data = R.drawable.forest, size = Size.ORIGINAL)
                }


                if( !isConnected) {
                    ChkNetWork(onCheckState = {
                        coroutineScope.launch {
                            isConnected = context.checkInternetConnected()
                        }
                    })
                }




            }// Box
        }

        when (campSiteStream.loadState.source.refresh) {
            is LoadState.Error -> {
                isLoading = false
                SearchingProgressIndicator(false)
                ErrorProgressView()
            }
            LoadState.Loading -> {
                SearchingProgressIndicator(isLoading)
            }
            is LoadState.NotLoading -> {

                isLoading = false
                SearchingProgressIndicator(false)

                val configuration = LocalConfiguration.current

                isPortrait = when (configuration.orientation) {
                    Configuration.ORIENTATION_PORTRAIT -> { true }
                    else -> { false }
                }

                var columnWidth by remember { mutableStateOf(1f) }
                var columnHeight by remember { mutableStateOf(1f) }

                when (configuration.orientation) {
                    Configuration.ORIENTATION_PORTRAIT -> {
                        isPortrait = true
                        columnWidth = 1f
                        columnHeight = 1f
                    }

                    else -> {
                        isPortrait = false
                        columnWidth = 0.9f
                        columnHeight = 1f
                    }
                }

                LaunchedEffect(key1 = isConnected) {
                    while (!isConnected) {
                        delay(500)
                        isConnected = context.checkInternetConnected()
                    }
                }

                BottomSheetScaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding(),
                    scaffoldState = scaffoldState,
                    sheetPeekHeight = sheetPeekHeightValue,
                    sheetShape = ShapeDefaults.Small,
                    sheetDragHandle = sheetDragHandle,
                    sheetContent = sheetContent,
                    content = content
                )// BottomSheetScaffold

            }

        }// when
    }

}