package com.unchil.searchcampcompose.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bungalow
import androidx.compose.material.icons.outlined.Cabin
import androidx.compose.material.icons.outlined.Cottage
import androidx.compose.material.icons.outlined.Countertops
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material.icons.outlined.Forest
import androidx.compose.material.icons.outlined.NaturePeople
import androidx.compose.material.icons.outlined.OutdoorGrill
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Signpost
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.navigation.compose.rememberNavController
import coil3.size.Size
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.unchil.searchcampcompose.LocalUsableHaptic
import com.unchil.searchcampcompose.R
import com.unchil.searchcampcompose.db.LocalSearchCampDB
import com.unchil.searchcampcompose.db.SearchCampDB
import com.unchil.searchcampcompose.model.SiteDefaultData
import com.unchil.searchcampcompose.model.getDesc
import com.unchil.searchcampcompose.shared.LocalPermissionsManager
import com.unchil.searchcampcompose.shared.PermissionsManager
import com.unchil.searchcampcompose.shared.view.CheckPermission
import com.unchil.searchcampcompose.shared.view.PermissionRequiredCompose
import com.unchil.searchcampcompose.ui.theme.SearchCampTheme
import com.unchil.searchcampcompose.viewmodel.SearchScreenViewModel
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@SuppressLint("UnrememberedMutableState")
@OptIn( ExperimentalPermissionsApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun SiteDefaultView(
    siteData:SiteDefaultData,
    allowHardware:Boolean = true,
    onClick:()->Unit,
    onClickPhoto:()->Unit,
    onLongClick:()->Unit
){

    val permissions = listOf(
        Manifest.permission.INTERNET
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


        Box(
            modifier = Modifier
                /*
                .combinedClickable(
                    interactionSource = interactionSource,
                    indication = rememberRipple(bounded = true),
                    onClick = onClick
                )

                 */
                .fillMaxWidth()
                .height(170.dp)
                .clip(ShapeDefaults.ExtraSmall)
                .background(color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f))  ,
            contentAlignment = Alignment.Center
        ){

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center

                ) {

                    Icon(
                        imageVector = Icons.Outlined.Cabin,
                        contentDescription = "cabin",
                        modifier = Modifier.scale(0.7f)
                    )

                    Spacer(modifier = Modifier.size(10.dp))

                    Text(
                        text = siteData.facltNm,
                        modifier = Modifier,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.bodyMedium
                    )

                }


                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center

                ) {


                    Column (
                        modifier = Modifier
                            .clickable {
                                 onClick.invoke()
                            }
                            .fillMaxWidth(0.5f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start
                    ){



                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center

                        ) {

                            Icon(
                                imageVector = Icons.Outlined.Description,
                                contentDescription = "description",
                                modifier = Modifier.scale(0.7f)
                            )


                            Text(
                                text =  siteData.lineIntro,
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Start,
                                style = MaterialTheme.typography.bodySmall
                            )

                        }



                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center

                        ) {

                            Icon(
                                imageVector = Icons.Outlined.Cottage,
                                contentDescription = "Cottage",
                                modifier = Modifier.scale(0.7f)
                            )



                            Text(
                                text =  siteData.induty,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Start,
                                style = MaterialTheme.typography.bodySmall
                            )

                        }



                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center

                        ) {

                            Icon(
                                imageVector = Icons.Outlined.NaturePeople,
                                contentDescription = "NaturePeople",
                                modifier = Modifier.scale(0.7f)
                            )




                            Text(
                                text =  siteData.facltDivNm,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Start,
                                style = MaterialTheme.typography.bodySmall
                            )

                        }



                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center

                        ) {

                            Icon(
                                imageVector = Icons.Outlined.Forest,
                                contentDescription = "Forest",
                                modifier = Modifier.scale(0.7f)
                            )

                            Text(
                                text =  siteData.lctCl,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Start,
                                style = MaterialTheme.typography.bodySmall
                            )

                        }


                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center

                        ) {

                            Icon(
                                imageVector = Icons.Outlined.Pets,
                                contentDescription = "Pets",
                                modifier = Modifier.scale(0.7f)
                            )

                            Text(
                                text =  siteData.animalCmgCl,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Start,
                                style = MaterialTheme.typography.bodySmall
                            )


                        }



                    }



                    PhotoPreview(
                        data = if(siteData.firstImageUrl.isNotEmpty()){siteData.firstImageUrl} else {
                            R.drawable.forest} ,
                        allowHardware = allowHardware,
                        onClick = onClickPhoto,
                    )


                }
            }
        }


    }

}



@Composable
fun SiteIntroductionView(
    siteData:SiteDefaultData
){

    val scrollState = rememberScrollState()

    val configuration = LocalConfiguration.current
    var isPortrait by remember { mutableStateOf(false) }
    isPortrait = when (configuration.orientation) {
        Configuration.ORIENTATION_PORTRAIT -> {
            true
        }

        else -> {
            false
        }
    }

    var columnHeight by remember { mutableStateOf(0.dp) }

    when (configuration.orientation) {
        Configuration.ORIENTATION_PORTRAIT -> {
            isPortrait = true
            columnHeight = configuration.screenHeightDp.dp / 2
        }

        else -> {
            isPortrait = false
            columnHeight = configuration.screenHeightDp.dp
        }
    }


    val hapticFeedback = LocalHapticFeedback.current

    var isHapticProcessing by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = isHapticProcessing) {
        if (isHapticProcessing) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            isHapticProcessing = false
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
                 ,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {


        ImageViewer(
            data = if (siteData.firstImageUrl.isNotEmpty()) {
                siteData.firstImageUrl
            } else {
                R.drawable.forest
            },
            size = Size.ORIGINAL,
            contentScale = ContentScale.FillWidth
        )


        Column(
            modifier = Modifier
                .padding(all = 10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {



            if( siteData.tel.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start

                ) {
                    Icon(
                        imageVector = Icons.Outlined.Phone,
                        contentDescription = "전화",
                        modifier = Modifier.scale(0.7f)
                    )
                    Text(
                        text = siteData.tel,
                        modifier = Modifier,
                        //    fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

            }


            if( siteData.addr1.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Signpost,
                        contentDescription = "주소",
                        modifier = Modifier.scale(0.7f)
                    )
                    Text(
                        text = siteData.addr1,
                        modifier = Modifier,
                        //   fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            if( siteData.resveCl.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Icon(
                        imageVector = Icons.Outlined.EventAvailable,
                        contentDescription = "예약",
                        modifier = Modifier.scale(0.7f)
                    )
                    Text(
                        text = siteData.resveCl,
                        modifier = Modifier,
                        //       fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            if( siteData.sbrsCl.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Countertops,
                        contentDescription = "시설",
                        modifier = Modifier.scale(0.7f)
                    )
                    Text(
                        text = siteData.sbrsCl,
                        modifier = Modifier,
                        //     fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

            }


            if( siteData.eqpmnLendCl.isNotEmpty()) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Icon(
                        imageVector = Icons.Outlined.OutdoorGrill,
                        contentDescription = "대여장비",
                        modifier = Modifier.scale(0.7f)
                    )
                    Text(
                        text = siteData.eqpmnLendCl,
                        modifier = Modifier,
                        //        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

            }

            if( siteData.glampInnerFclty.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Bungalow,
                        contentDescription = "글램핑시설",
                        modifier = Modifier.scale(0.7f)
                    )
                    Text(
                        text = siteData.glampInnerFclty,
                        modifier = Modifier,
                        //        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

            }

        }

        if( siteData.intro.isNotEmpty()) {
            Text(
                text = siteData.intro,
                modifier = Modifier.padding(horizontal = 10.dp),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyMedium
            )

        }

    }
}



@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalPermissionsApi::class, ExperimentalFoundationApi::class)
@Composable
fun SiteImagePagerView(
    viewModel: SearchScreenViewModel,
    contentId: String? = null
) {

    val configuration = LocalConfiguration.current
    var isPortrait by remember { mutableStateOf(false) }
    isPortrait = when (configuration.orientation) {
        Configuration.ORIENTATION_PORTRAIT -> {
            true
        }
        else -> {
            false
        }
    }

    var cardWidthDp by remember { mutableStateOf(0.dp) }
    var cardHeightDp by remember { mutableStateOf(0.dp) }

    val landscapeBottomSheetWidth = 640.dp

    when (configuration.orientation) {
        Configuration.ORIENTATION_PORTRAIT -> {
            isPortrait = true
            cardWidthDp = configuration.screenWidthDp.dp
            cardHeightDp = cardWidthDp
        }

        else -> {
            isPortrait = false
            cardWidthDp = landscapeBottomSheetWidth * 0.7f
            cardHeightDp = cardWidthDp
        }
    }


    // 한 패이지 의 패딩값
    val paddingValues by mutableStateOf(
        if (isPortrait) {

            PaddingValues(vertical = configuration.screenHeightDp.dp / 2 - cardHeightDp / 2)
        } else {
            PaddingValues(horizontal = landscapeBottomSheetWidth / 2 - cardWidthDp / 2)
        }
    )


    val pagesPerViewport = object : PageSize {
        override fun Density.calculateMainAxisPageSize(
            availableSpace: Int,
            pageSpacing: Int
        ): Int {
            return availableSpace
        }
    }

    val permissions = listOf( Manifest.permission.INTERNET )

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

        val resultState = viewModel.recvSiteImageListState.collectAsState()

        when(resultState.value){
            is SearchScreenViewModel.RecvSiteImageListState.Error -> {
                val result = (resultState.value as SearchScreenViewModel.RecvSiteImageListState.Error)
                Box( modifier = Modifier
                    .fillMaxSize()
                ) {
                    Text(
                        text = result.responseStatus.getDesc().second,
                        modifier = Modifier
                            .align(Alignment.Center),
                        textAlign = TextAlign.Center
                    )
                }
            }
            SearchScreenViewModel.RecvSiteImageListState.Loading -> {
                Box( modifier = Modifier
                    .fillMaxSize()
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                    )
                }
            }
            is SearchScreenViewModel.RecvSiteImageListState.Success -> {

                val result = (resultState.value as SearchScreenViewModel.RecvSiteImageListState.Success)

                if(result.dataList.size == 0){
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ){
                        Text(
                            text = "데이터가 존재하지 않습니다.",
                            modifier = Modifier
                                .align(Alignment.Center),
                            textAlign = TextAlign.Center
                        )
                    }
                }else {

                    val density = LocalDensity.current
                    val context = LocalContext.current

                    val isUsableHaptic = LocalUsableHaptic.current
                    val hapticFeedback = LocalHapticFeedback.current
                    val coroutineScope = rememberCoroutineScope()

                    fun hapticProcessing(){
                        if(isUsableHaptic){
                            coroutineScope.launch {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                        }
                    }

                    var isVisibleImage by remember{ mutableStateOf(false) }
                    var imageUrl by remember { mutableStateOf("") }

                    val pagerState  =   rememberPagerState(
                        initialPage = 0,
                        initialPageOffsetFraction = 0f,
                        pageCount = {  result.dataList.size } )

                    /*
                    LaunchedEffect(key1 = pagerState.currentPage ){
                        hapticProcessing()
                    }

                     */

                    Box (
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {

                        if(isPortrait){

                            VerticalPager(
                                modifier = Modifier,
                                state = pagerState,
                                pageSpacing = 0.dp,
                                pageSize = pagesPerViewport,
                                beyondViewportPageCount = 1,
                                contentPadding = paddingValues,
                            ) { page ->

                                Card(
                                    Modifier
                                        .height(cardHeightDp)
                                        .width(cardWidthDp)
                                        .graphicsLayer {
                                            val pageOffset = (
                                                    (pagerState.currentPage - page) + pagerState
                                                        .currentPageOffsetFraction
                                                    ).absoluteValue

                                            alpha = lerp(
                                                start = 0.7f,
                                                stop = 1f,
                                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                                            )
                                            scaleX = lerp(
                                                start = 0.8f,
                                                stop = 1f,
                                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                                            )
                                            scaleY = lerp(
                                                start = 0.8f,
                                                stop = 1f,
                                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                                            )


                                        }
                                        .combinedClickable(
                                            onLongClick = {
                                                hapticProcessing()
                                                isVisibleImage = true
                                                imageUrl = result.dataList[page].imageUrl
                                            },
                                            onClick = {
                                                hapticProcessing()
                                                isVisibleImage = true
                                                imageUrl = result.dataList[page].imageUrl
                                            }
                                        ),
                                    shape =  ShapeDefaults.ExtraSmall,


                                    ) {


                                    ImageViewer(
                                        data = result.dataList[page].imageUrl,
                                        size = Size(600,600),
                                        isZoomable = false,
                                        contentScale = ContentScale.Crop
                                    )


                                }

                            }

                        } else {

                            HorizontalPager(
                                modifier = Modifier,
                                state = pagerState,
                                pageSpacing = 0.dp,
                                pageSize = pagesPerViewport,
                                beyondViewportPageCount = 1,
                                contentPadding = paddingValues,
                            ) { page ->

                                Card(
                                    Modifier
                                        .width(cardWidthDp)
                                        .height(cardHeightDp)
                                        .graphicsLayer {
                                            val pageOffset = (
                                                    (pagerState.currentPage - page) + pagerState
                                                        .currentPageOffsetFraction
                                                    ).absoluteValue

                                            alpha = lerp(
                                                start = 0.7f,
                                                stop = 1f,
                                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                                            )
                                            scaleX = lerp(
                                                start = 0.7f,
                                                stop = 1f,
                                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                                            )
                                            scaleY = lerp(
                                                start = 0.7f,
                                                stop = 1f,
                                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                                            )


                                        }
                                        .combinedClickable(
                                            onLongClick = {
                                                hapticProcessing()
                                                isVisibleImage = true
                                                imageUrl = result.dataList[page].imageUrl
                                            },
                                            onClick = {
                                                hapticProcessing()
                                                isVisibleImage = true
                                                imageUrl = result.dataList[page].imageUrl
                                            }
                                        ),
                                    shape =  ShapeDefaults.ExtraSmall,
                                ) {

                                    ImageViewer(
                                        data = result.dataList[page].imageUrl,
                                        size = Size(600,600),
                                        isZoomable = false,
                                        contentScale = ContentScale.Crop
                                    )

                                }

                            }

                        }

                        AnimatedVisibility(visible = isVisibleImage,
                            enter = slideInVertically {
                                // Slide in from 40 dp from the top.
                                with(density) { 40.dp.roundToPx() }
                            } + expandVertically(
                                // Expand from the top.
                                expandFrom = Alignment.Top
                            ) + fadeIn(
                                // Fade in with the initial alpha of 0.3f.
                                initialAlpha = 0.3f
                            ),
                            exit = slideOutVertically() + shrinkVertically() + fadeOut()
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(ShapeDefaults.ExtraSmall)
                                    .fillMaxSize()
                                    .background(color = MaterialTheme.colorScheme.background)
                                    .clickable {
                                        hapticProcessing()
                                        isVisibleImage = false
                                    }
                            ) {
                                ImageViewer(
                                    data = imageUrl,
                                    size = Size.ORIGINAL,
                                    isZoomable = true,
                                    contentScale = ContentScale.Fit
                                )
                            }
                        }



                    }


                }

            }
        }

    }
}




@Preview
@Composable
fun PrevSiteView(){

    val context = LocalContext.current
    val permissionsManager = PermissionsManager()
    val searchCampDB = SearchCampDB.getInstance(context.applicationContext)

    val scrollState = rememberScrollState()
    val navController = rememberNavController()

    SearchCampTheme {
        Surface(modifier = Modifier,  color = MaterialTheme.colorScheme.background     ) {


            CompositionLocalProvider(LocalPermissionsManager provides permissionsManager) {
                CompositionLocalProvider(LocalSearchCampDB provides searchCampDB) {

                    SiteIntroductionView(SiteDefaultData.setInitValue())

                }
            }


        }
    }


}