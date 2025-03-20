package com.unchil.searchcampcompose.model
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Service (
    @SerialName("name") var name:String,
    @SerialName("version") var version:String,
    @SerialName("operation") var operation:String,
    @SerialName("time") var time:String
)

@Serializable
data class Record (
    @SerialName("total") var total:String,
    @SerialName("current") var current:String
)


@Serializable
data class Page (
    @SerialName("total") var total:String,
    @SerialName("current") var current:String,
    @SerialName("size") var size:String
)

@Serializable
data class Properties_LT_C_ADSIDO_INFO (
    @SerialName("ctp_eng_nm") var ctp_eng_nm:String?,
    @SerialName("ctprvn_cd") var ctprvn_cd:String?,
    @SerialName("ctp_kor_nm") var ctp_kor_nm:String?,

)

@Serializable
data class Properties_LT_C_ADSIGG_INFO (
    @SerialName("full_nm") var full_nm:String?,
    @SerialName("sig_cd") var sig_cd:String?,
    @SerialName("sig_kor_nm") var sig_kor_nm:String?,
    @SerialName("sig_eng_nm") var sig_eng_nm:String?,
)


@Serializable
data class Feature_LT_C_ADSIDO_INFO(
    @SerialName("type") var type:String,
    @SerialName("properties") var properties:Properties_LT_C_ADSIDO_INFO,
    @SerialName("id") var id:String
)

@Serializable
data class Feature_LT_C_ADSIGG_INFO(
    @SerialName("type") var type:String,
    @SerialName("properties") var properties:Properties_LT_C_ADSIGG_INFO,
    @SerialName("id") var id:String
)


@Serializable
data class  FeatureCollection_LT_C_ADSIDO_INFO (
    @SerialName("type") var type:String,
    @SerialName("bbox") var bbox:Array<Float>,
    @SerialName("features") var features:Array<Feature_LT_C_ADSIDO_INFO>
)

@Serializable
data class  FeatureCollection_LT_C_ADSIGG_INFO (
    @SerialName("type") var type:String,
    @SerialName("bbox") var bbox:Array<Float>,
    @SerialName("features") var features:Array<Feature_LT_C_ADSIGG_INFO>
)


@Serializable
data class Result_LT_C_ADSIDO_INFO (
    @SerialName("featureCollection") var featureCollection:FeatureCollection_LT_C_ADSIDO_INFO
)

@Serializable
data class Result_LT_C_ADSIGG_INFO (
    @SerialName("featureCollection") var featureCollection:FeatureCollection_LT_C_ADSIGG_INFO
)


@Serializable
data class Error(
    @SerialName("level") var level:String,
    @SerialName("code") var code:String,
    @SerialName("text") var text:String
)

@Serializable
data class VWorldRecvData_LT_C_ADSIDO_INFO (
    @SerialName("service") var service:Service,
    @SerialName("status") var status:String,
    @SerialName("record") var record:Record,
    @SerialName("page") var page:Page,
    @SerialName("result") var result:Result_LT_C_ADSIDO_INFO?,
 //   @SerialName("error") var error:Error?
)

@Serializable
data class VWorldRecvData_LT_C_ADSIGG_INFO (
    @SerialName("service") var service:Service,
    @SerialName("status") var status:String,
    @SerialName("record") var record:Record,
    @SerialName("page") var page:Page,
    @SerialName("result") var result:Result_LT_C_ADSIGG_INFO?,
    //   @SerialName("error") var error:Error?
)


@Serializable
data class VWorldResponse_LT_C_ADSIDO_INFO(
    @SerialName("response") var response:VWorldRecvData_LT_C_ADSIDO_INFO
)

@Serializable
data class VWorldResponse_LT_C_ADSIGG_INFO(
    @SerialName("response") var response:VWorldRecvData_LT_C_ADSIGG_INFO
)




enum class AdministrativeDistrictSiDo {
    SIDO_11,
    SIDO_26, SIDO_27, SIDO_28, SIDO_29, SIDO_30, SIDO_31, SIDO_36,
    SIDO_41, SIDO_43, SIDO_44, SIDO_45, SIDO_46, SIDO_47, SIDO_48,
    SIDO_50, SIDO_51, SIDO_52
}

fun AdministrativeDistrictSiDo.getCodeString():String {
    return when(this){
        AdministrativeDistrictSiDo.SIDO_11 -> "11"
        AdministrativeDistrictSiDo.SIDO_26 -> "26"
        AdministrativeDistrictSiDo.SIDO_27 -> "27"
        AdministrativeDistrictSiDo.SIDO_28 -> "28"
        AdministrativeDistrictSiDo.SIDO_29 -> "29"
        AdministrativeDistrictSiDo.SIDO_30 -> "30"
        AdministrativeDistrictSiDo.SIDO_31 -> "31"
        AdministrativeDistrictSiDo.SIDO_36 -> "36"
        AdministrativeDistrictSiDo.SIDO_41 -> "41"
        AdministrativeDistrictSiDo.SIDO_43 -> "43"
        AdministrativeDistrictSiDo.SIDO_44 -> "44"
        AdministrativeDistrictSiDo.SIDO_45 -> "45"
        AdministrativeDistrictSiDo.SIDO_46 -> "46"
        AdministrativeDistrictSiDo.SIDO_47 -> "47"
        AdministrativeDistrictSiDo.SIDO_48 -> "48"
        AdministrativeDistrictSiDo.SIDO_50 -> "50"
        AdministrativeDistrictSiDo.SIDO_51 -> "51"
        AdministrativeDistrictSiDo.SIDO_52 -> "52"
    }
}
fun AdministrativeDistrictSiDo.getExtentionNameList():Array<String>{
    return when(this){
        AdministrativeDistrictSiDo.SIDO_11 -> arrayOf("서울특별시", "서울시", "서울")
        AdministrativeDistrictSiDo.SIDO_26 -> arrayOf("부산광역시", "부산시", "부산")
        AdministrativeDistrictSiDo.SIDO_27 ->  arrayOf("대구광역시", "대구시", "대구")
        AdministrativeDistrictSiDo.SIDO_28 -> arrayOf("인천광역시", "인천시", "인천")
        AdministrativeDistrictSiDo.SIDO_29 -> arrayOf("광주광역시", "광주시", "광주")
        AdministrativeDistrictSiDo.SIDO_30 -> arrayOf("대전광역시", "대전시", "대전")
        AdministrativeDistrictSiDo.SIDO_31 -> arrayOf("울산광역시", "울산시", "울산")
        AdministrativeDistrictSiDo.SIDO_36 ->  arrayOf("세종특별자치시", "세종시", "세종")
        AdministrativeDistrictSiDo.SIDO_41 -> arrayOf("경기도", "경기")
        AdministrativeDistrictSiDo.SIDO_43 ->   arrayOf("충청북도", "충북")
        AdministrativeDistrictSiDo.SIDO_44 ->  arrayOf("충청남도", "충남")
        AdministrativeDistrictSiDo.SIDO_45 -> arrayOf("전라북도", "전북")
        AdministrativeDistrictSiDo.SIDO_46 -> arrayOf("전라남도", "전남")
        AdministrativeDistrictSiDo.SIDO_47 -> arrayOf("경상북도", "경북")
        AdministrativeDistrictSiDo.SIDO_48 -> arrayOf("경상남도", "경남")
        AdministrativeDistrictSiDo.SIDO_50 -> arrayOf("제주특별자치도", "제주도", "제주시", "제주")
        AdministrativeDistrictSiDo.SIDO_51 ->  arrayOf("강원특별자치도", "강원도", "강원")
        AdministrativeDistrictSiDo.SIDO_52 -> arrayOf("전북특별자치도","전라북도", "전북")
    }
}

val AdministrativeDistrictSiDoList = listOf(
    AdministrativeDistrictSiDo.SIDO_11,
    AdministrativeDistrictSiDo.SIDO_26,
    AdministrativeDistrictSiDo.SIDO_27,
    AdministrativeDistrictSiDo.SIDO_28,
    AdministrativeDistrictSiDo.SIDO_29,
    AdministrativeDistrictSiDo.SIDO_30,
    AdministrativeDistrictSiDo.SIDO_31,
    AdministrativeDistrictSiDo.SIDO_36,
    AdministrativeDistrictSiDo.SIDO_41,
    AdministrativeDistrictSiDo.SIDO_43,
    AdministrativeDistrictSiDo.SIDO_44,
    AdministrativeDistrictSiDo.SIDO_45,
    AdministrativeDistrictSiDo.SIDO_46,
    AdministrativeDistrictSiDo.SIDO_47,
    AdministrativeDistrictSiDo.SIDO_48,
    AdministrativeDistrictSiDo.SIDO_50,
    AdministrativeDistrictSiDo.SIDO_51,
    AdministrativeDistrictSiDo.SIDO_52
)


enum class VWorldService {
    LT_C_ADSIDO_INFO, LT_C_ADSIGG_INFO
}

enum class GoCampingService {
    CAMPSITE, NEARCAMPSITE,SEARCH,SITEIMAGE
}


enum class CollectType {
    LT_C_ADSIDO_INFO, LT_C_ADSIGG_INFO,  CAMPSITE, NEARCAMPSITE, SITEIMAGE, SYNC, WEATHER
}

val CollectTypeList = listOf(
    CollectType.LT_C_ADSIDO_INFO,
    CollectType.LT_C_ADSIGG_INFO,
    CollectType.CAMPSITE,
    CollectType.NEARCAMPSITE,
    CollectType.SITEIMAGE,
    CollectType.SYNC,
    CollectType.WEATHER
)

fun GoCampingService.getPoint():String {
    return when(this){
        GoCampingService.CAMPSITE -> "/basedList"
        GoCampingService.NEARCAMPSITE -> "/locationBasedList"
        GoCampingService.SEARCH -> "/searchList"
        GoCampingService.SITEIMAGE -> "/imageList"
    }
}
