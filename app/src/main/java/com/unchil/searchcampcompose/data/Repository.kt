package com.unchil.searchcampcompose.data


import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.room.withTransaction
import com.google.android.gms.maps.model.LatLng
import com.unchil.searchcampcompose.BuildConfig
import com.unchil.searchcampcompose.api.SearchCampApi
import com.unchil.searchcampcompose.db.SearchCampDB
import com.unchil.searchcampcompose.db.entity.CURRENTWEATHER_TBL
import com.unchil.searchcampcompose.db.entity.CampSite_TBL
import com.unchil.searchcampcompose.db.entity.NearCampSite_TBL
import com.unchil.searchcampcompose.db.entity.SiDo_TBL
import com.unchil.searchcampcompose.db.entity.SiGunGu_TBL
import com.unchil.searchcampcompose.db.entity.SiteImage_TBL
import com.unchil.searchcampcompose.model.AdministrativeDistrictSiDoList
import com.unchil.searchcampcompose.model.CollectType
import com.unchil.searchcampcompose.model.CollectTypeList
import com.unchil.searchcampcompose.model.GoCampingRecvItem
import com.unchil.searchcampcompose.model.GoCampingResponse
import com.unchil.searchcampcompose.model.GoCampingResponseImage
import com.unchil.searchcampcompose.model.GoCampingResponseStatus
import com.unchil.searchcampcompose.model.GoCampingResponseStatusList
import com.unchil.searchcampcompose.model.GoCampingService
import com.unchil.searchcampcompose.model.GoCampingSyncStatus
import com.unchil.searchcampcompose.model.VWorldService
import com.unchil.searchcampcompose.model.getCodeString
import com.unchil.searchcampcompose.model.getDesc
import com.unchil.searchcampcompose.model.getExtentionNameList
import com.unchil.searchcampcompose.model.toCURRENTWEATHER_TBL
import com.unchil.searchcampcompose.shared.UnixTimeToString
import com.unchil.searchcampcompose.shared.yyyyMMdd
import com.unchil.searchcampcompose.viewmodel.SearchScreenViewModel
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.logging.Logger
import io.ktor.util.logging.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest




class Repository {

    internal val LOGGER = KtorSimpleLogger("com.unchil.searchcampcompose")

    lateinit var database: SearchCampDB

    private val api = SearchCampApi()

    val request = "getfeature"
    val geomfilter = "BOX(13663271.680031825,3894007.9689600193,14817776.555251127,4688953.0631258525)"
    val size = "1000"
    val geometry = "false"
    val crs = "EPSG:3857"
    val numOfRows = "10000"
    val pageNo = "1"
    val MobileOS = "AND"
    val MobileApp = "campsite"
    val datatype = "json"
    val nearMaxRadius = "15000"  // 반경 15Km

    val currentLatLng: MutableStateFlow<LatLng> = MutableStateFlow(LatLng(0.0, 0.0))

    val _currentWeather: MutableStateFlow<CURRENTWEATHER_TBL> = MutableStateFlow(
        CURRENTWEATHER_TBL( 0L, "", 0, 0L,"",0f, 0f,
            "", "", "", 0f,0f,0f,0f,0f,
            0f,0f,0f, 0, 0, "", 0L , 0L)
    )

    val sidoListStateFlow:MutableStateFlow<List<SiDo_TBL>>  = MutableStateFlow(listOf())

    val sigunguListStateFlow:MutableStateFlow<List<SiGunGu_TBL>>  = MutableStateFlow(listOf())

    val siteImageListStateFlow:MutableStateFlow<List<SiteImage_TBL>> = MutableStateFlow(listOf())



     val _recvSiteImageListState:MutableStateFlow<SearchScreenViewModel.RecvSiteImageListState>
     = MutableStateFlow(SearchScreenViewModel.RecvSiteImageListState.Success(emptyList()))


    var currentListDataStateFlow:MutableStateFlow<List<CampSite_TBL>> = MutableStateFlow(listOf())


    fun setLoadingStateRecvSiteImageListState(){
        _recvSiteImageListState.value = SearchScreenViewModel.RecvSiteImageListState.Loading
    }


    suspend fun getCampSiteListFlow(
        administrativeDistrictSiDoCode:String,
        administrativeDistrictSiGunGu:String,
        searchTitle:String? = null): Int{


        val facltNm =  if(searchTitle.isNullOrEmpty()){
            "%%"
        } else {
            "%" +  searchTitle.replace(' ','%' )  + "%"
        }

        val donm = if (administrativeDistrictSiDoCode == "0"){
            emptyArray()
        } else {
            AdministrativeDistrictSiDoList.first{
                it.getCodeString() == administrativeDistrictSiDoCode
            }.getExtentionNameList()
        }

        val administrativeDistrictSiGunGuSplitArray = administrativeDistrictSiGunGu.split(" ", limit = 2)

        //세종특별자치시 예외사항
         val sigungNm = if(administrativeDistrictSiDoCode == "36"){"%"} else {administrativeDistrictSiGunGuSplitArray[0] }


         val addr1 = if(administrativeDistrictSiGunGuSplitArray.size == 2) {
            "%" +  administrativeDistrictSiGunGuSplitArray[1] + "%"
        } else {
            "%%"
        }

         val result =  if(administrativeDistrictSiDoCode == "0"){
             database.nearCampSiteDao.select_Search(facltNm)
         }else {
             database.campSiteDao.select_Search(
                 donm,
                 sigungNm,
                 addr1,
                 facltNm
             )
         }
         currentListDataStateFlow.emit(result)

         return result.size
    }



    fun getNearCampSiteListPaging( facltNm:String): Flow<PagingData<CampSite_TBL>> {

        return  Pager(
            config = PagingConfig(pageSize = 5, enablePlaceholders = false),
            pagingSourceFactory = {
                database.nearCampSiteDao.select_Search_Paging(facltNm)
            }
        ).flow
    }


    fun getCampSiteListPaging(
        donm: Array<String>,
        sigungNm: String,
        addr1: String,
        facltNm:String
    ): Flow<PagingData<CampSite_TBL>> {

        return  Pager(
            config = PagingConfig(pageSize = 5, enablePlaceholders = false),
            pagingSourceFactory = {
                database.campSiteDao.select_Search_Paging(
                    donm,
                    sigungNm,
                    addr1,
                    facltNm
                )
            }
        ).flow

    }



    fun getCampSiteListStream(
        administrativeDistrictSiDoCode:String,
        administrativeDistrictSiGunGu:String,
        searchTitle:String? = null):Flow<PagingData<CampSite_TBL>>{

        val facltNm =  if(searchTitle.isNullOrEmpty()){
            "%%"
        } else {
            "%" +  searchTitle.replace(' ','%' )  + "%"
        }

        val donm = if (administrativeDistrictSiDoCode == "0"){
            emptyArray()
        }  else {
            AdministrativeDistrictSiDoList.first{
                it.getCodeString() == administrativeDistrictSiDoCode
            }.getExtentionNameList()
        }

        val administrativeDistrictSiGunGuSplitArray = administrativeDistrictSiGunGu.split(" ", limit = 2)

        //세종특별자치시 예외사항
        val sigungNm = if(administrativeDistrictSiDoCode == "36"){"%"} else {administrativeDistrictSiGunGuSplitArray[0] }

        val addr1 = if(administrativeDistrictSiGunGuSplitArray.size == 2) {
           "%" +  administrativeDistrictSiGunGuSplitArray[1] + "%"
        } else {
            "%%"
        }

        return  if(administrativeDistrictSiDoCode == "0"){
            getNearCampSiteListPaging(   facltNm)
        }else {

            getCampSiteListPaging(
                donm,
                sigungNm,
                addr1,
                facltNm
            )
        }

    }





    suspend fun setCurrentLatLng(data:LatLng){
        currentLatLng.emit(data)
    }



    suspend fun getSiDoList() {
        database.sidoDao.select_All_Flow().collectLatest {
            sidoListStateFlow.value = it
        }
    }

    suspend fun getSiGunGuList(upCode:String) {
        database.sigunguDao.select_Flow(upCode = upCode, length = upCode.length).collectLatest {
            sigunguListStateFlow.value = it
        }
    }




    suspend fun getWeatherData(latitude: String, longitude: String){

        if( chkCollectTime(CollectType.WEATHER)  == true  ) {
            try {
                val apiResponse = api.getWeatherData(
                    lat=latitude,
                    lon=longitude,
                    units="metric",
                    appid= BuildConfig.OPENWEATHER_KEY
                )
                _currentWeather.value = apiResponse.toCURRENTWEATHER_TBL()
                database.withTransaction {
                    database.currentWeatherDao.trancate()
                    database.currentWeatherDao.insert(apiResponse.toCURRENTWEATHER_TBL())
                }
                updateCollectTime(CollectType.WEATHER.name)

            } catch (e:Exception){
                e.localizedMessage?.let {
                    LOGGER.error(it)
                }
                database.currentWeatherDao.select_Flow().collect{
                    _currentWeather.value = it
                }
            }
        }else {
            database.currentWeatherDao.select_Flow().collect{
                _currentWeather.value = it
            }

        }

    }


    fun chkCollectTime(type: CollectType): Boolean {

        val intervalTime = when(type){
            CollectType.LT_C_ADSIDO_INFO -> 86400000
            CollectType.LT_C_ADSIGG_INFO -> 86400000
            CollectType.CAMPSITE ->  86400000
            CollectType.NEARCAMPSITE -> 0
            CollectType.SITEIMAGE -> 0
            CollectType.SYNC -> 86400000
            CollectType.WEATHER -> 3600000
        }

        val beforeCollectTime = database.collectTimeDao.select(type.name)
        return if (beforeCollectTime + intervalTime <= System.currentTimeMillis()) true else false
    }

    suspend fun updateCollectTime(type:String){
        database.collectTimeDao.update(
            type = type,
            time = System.currentTimeMillis()
        )
    }

    suspend fun recvVWorldData(serviceType: VWorldService){

        if( CollectTypeList.find {
                it.name == serviceType.name}?.let { chkCollectTime(it) } == true  ) {

            try {
                when (serviceType) {
                    VWorldService.LT_C_ADSIDO_INFO -> {
                        val apiResponse = api.recvVWORLD_LT_C_ADSIDO_INFO(
                            apiKey = BuildConfig.WORLD_API_KEY,
                            request = request,
                            data = serviceType.name,
                            geomfilter = geomfilter,
                            size = size,
                            geometry = geometry,
                            crs = crs
                        )
                        val resultList: MutableList<SiDo_TBL> = mutableListOf()
                        apiResponse.response.result?.featureCollection?.features?.forEach {
                            resultList.add(
                                SiDo_TBL(
                                    ctprvn_cd = it.properties.ctprvn_cd ?: "",
                                    ctp_kor_nm = it.properties.ctp_kor_nm ?: "",
                                    ctp_eng_nm = it.properties.ctp_eng_nm ?: ""
                                )
                            )
                        }


                        database.withTransaction {
                            database.sidoDao.trancate()
                            database.sidoDao.insert_List(resultList)
                            updateCollectTime(serviceType.name)

                            /****
                            Transaction 내에  동일한 테이블을 로드하는 Flow 를 넣으면  Transaction 이 종료 되지 않음 (무한 루프)
                            getSiDoList()
                             ***/
                        }
                    }

                    VWorldService.LT_C_ADSIGG_INFO -> {
                        val apiResponse = api.recvVWORLD_LT_C_ADSIGG_INFO(
                            apiKey = BuildConfig.WORLD_API_KEY,
                            request = request,
                            data = serviceType.name,
                            geomfilter = geomfilter,
                            size = size,
                            geometry = geometry,
                            crs = crs
                        )
                        val resultList: MutableList<SiGunGu_TBL> = mutableListOf()
                        apiResponse.response.result?.featureCollection?.features?.forEach {
                            resultList.add(
                                SiGunGu_TBL(
                                    full_nm = it.properties.full_nm ?: "",
                                    sig_cd = it.properties.sig_cd ?: "",
                                    sig_kor_nm = it.properties.sig_kor_nm ?: "",
                                    sig_eng_nm = it.properties.sig_eng_nm ?: ""
                                )
                            )
                        }
                        database.withTransaction {
                            database.sigunguDao.trancate()
                            database.sigunguDao.insert_List(resultList)
                            updateCollectTime(serviceType.name)
                        }
                    }
                }
            }catch (e:Exception){
                e.localizedMessage?.let { LOGGER.error(it) }
            }


        }

    }



    suspend fun recvGoCampingDataImageList( contentId:String) {


        try {
            val apiResponse = api.getImage(
                serviceKey = BuildConfig.GOCAMPING_API_KEY,
                numOfRows = numOfRows,
                pageNo = pageNo,
                MobileOS = MobileOS,
                MobileApp = MobileApp,
                _type = datatype,
                contentId = contentId
            )

            val resultList: MutableList<SiteImage_TBL> = mutableListOf()
            apiResponse.response?.body?.items?.item?.forEach {
                resultList.add(
                    SiteImage_TBL(
                        contentId = it.contentId,
                        createdtime = it.createdtime,
                        imageUrl = it.imageUrl,
                        modifiedtime = it.modifiedtime,
                        serialnum = it.serialnum
                    )
                )
            }
            _recvSiteImageListState.value = SearchScreenViewModel.RecvSiteImageListState.Success(resultList)


        } catch (e : Exception){

            val itemsZeroPasingErrMsg = "Expected BEGIN_OBJECT but was STRING at line 1 column 82 path"

            if(   e.localizedMessage?.contains(itemsZeroPasingErrMsg) ?: false  )  {
                _recvSiteImageListState.value = SearchScreenViewModel.RecvSiteImageListState.Success(emptyList())
            } else {
                _recvSiteImageListState.value = SearchScreenViewModel.RecvSiteImageListState.Error(GoCampingResponseStatus.SERVICETIMEOUT_ERROR)
            }
        }

    }



    suspend fun recvGoCampingData(
        serviceType: GoCampingService,
        mapX:String? = null,
        mapY:String?= null,
        keyword:String? = null,
        contentId:String? = null
    ) {

        if( CollectTypeList.find {
                it.name == serviceType.name}?.let { chkCollectTime(it) } == true  ) {

            val serviceKey = BuildConfig.GOCAMPING_API_KEY

            try {

                val apiResponse: Any = when (serviceType) {
                    GoCampingService.CAMPSITE -> {
                        api.getDefault(
                            serviceKey = serviceKey,
                            numOfRows = numOfRows,
                            pageNo = pageNo,
                            MobileOS = MobileOS,
                            MobileApp = MobileApp,
                            _type = datatype
                        )
                    }
                    GoCampingService.NEARCAMPSITE -> {
                        if ( !mapX.isNullOrEmpty() && !mapY.isNullOrEmpty()) {
                            api.getLocation(
                                serviceKey = serviceKey,
                                numOfRows = numOfRows,
                                pageNo = pageNo,
                                MobileOS = MobileOS,
                                MobileApp = MobileApp,
                                _type = datatype,
                                mapX = mapX,
                                mapY = mapY,
                                radius = nearMaxRadius
                            )
                        }else {}

                    }

                    GoCampingService.SITEIMAGE -> {
                        if (!contentId.isNullOrEmpty()) {
                            api.getImage(
                                serviceKey = serviceKey,
                                numOfRows = numOfRows,
                                pageNo = pageNo,
                                MobileOS = MobileOS,
                                MobileApp = MobileApp,
                                _type = datatype,
                                contentId = contentId
                            )
                        } else { }

                    }
                    GoCampingService.SEARCH -> {
                        if(!keyword.isNullOrEmpty()){
                            api.getSearch(
                                serviceKey = BuildConfig.GOCAMPING_API_KEY,
                                numOfRows = numOfRows ,
                                pageNo = pageNo,
                                MobileOS = MobileOS,
                                MobileApp = MobileApp,
                                _type = datatype,
                                keyword = keyword
                            )
                        } else {

                        }
                    }
                }

                val resultStatus = GoCampingResponseStatusList.first {
                    it.getDesc().first == when(serviceType){
                        GoCampingService.CAMPSITE,
                        GoCampingService.NEARCAMPSITE,
                        GoCampingService.SEARCH -> {
                            (apiResponse as GoCampingResponse).response?.header?.resultCode
                        }
                        GoCampingService.SITEIMAGE -> {
                            (apiResponse as GoCampingResponseImage).response?.header?.resultCode
                        }
                    }
                }





                when (resultStatus) {

                    GoCampingResponseStatus.OK -> {

                        when (serviceType) {

                            GoCampingService.CAMPSITE -> {
                                val resultList: MutableList<CampSite_TBL> = mutableListOf()
                                (apiResponse as GoCampingResponse).response?.body?.items?.item?.forEach {
                                    resultList.add(
                                        GoCampingRecvItem.toCampSite_TBL(it)
                                    )
                                }

                                database.withTransaction {
                                    database.campSiteDao.trancate()
                                    database.campSiteDao.insert_List(resultList)
                                    updateCollectTime(serviceType.name)
                                }
                            }
                            GoCampingService.NEARCAMPSITE -> {
                                val resultList: MutableList<NearCampSite_TBL> = mutableListOf()
                                (apiResponse as GoCampingResponse).response?.body?.items?.item?.forEach {
                                    resultList.add(
                                        GoCampingRecvItem.toNearCampSite_TBL(it)
                                    )
                                }

                                database.withTransaction {
                                    database.nearCampSiteDao.trancate()
                                    database.nearCampSiteDao.insert_List(resultList)
                                    updateCollectTime(serviceType.name)
                                }


                            }

                            GoCampingService.SITEIMAGE -> {
                                val resultList: MutableList<SiteImage_TBL> = mutableListOf()
                                (apiResponse as GoCampingResponseImage).response?.body?.items?.item?.forEach {
                                    resultList.add(
                                        SiteImage_TBL(
                                            contentId = it.contentId,
                                            createdtime = it.createdtime,
                                            imageUrl = it.imageUrl,
                                            modifiedtime = it.modifiedtime,
                                            serialnum = it.serialnum
                                        )
                                    )
                                }

                                siteImageListStateFlow.emit(resultList)

                            }
                            GoCampingService.SEARCH -> {}

                        }
                    }

                    else -> {}
                }


            } catch (e: Exception){
                e.localizedMessage?.let {
                    LOGGER.error(it)
                }
            } // catch


        }

    }



}

