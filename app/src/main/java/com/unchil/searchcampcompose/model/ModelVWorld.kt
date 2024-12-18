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



