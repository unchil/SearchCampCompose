package com.unchil.searchcampcompose.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Service (
    @SerializedName("name") var name:String,
    @SerializedName("version") var version:String,
    @SerializedName("operation") var operation:String,
    @SerializedName("time") var time:String
)

@Serializable
data class Record (
    @SerializedName("total") var total:String,
    @SerializedName("current") var current:String
)


@Serializable
data class Page (
    @SerializedName("total") var total:String,
    @SerializedName("current") var current:String,
    @SerializedName("size") var size:String
)

@Serializable
data class Properties_LT_C_ADSIDO_INFO (
    @SerializedName("ctp_eng_nm") var ctp_eng_nm:String?,
    @SerializedName("ctprvn_cd") var ctprvn_cd:String?,
    @SerializedName("ctp_kor_nm") var ctp_kor_nm:String?,

)

@Serializable
data class Properties_LT_C_ADSIGG_INFO (
    @SerializedName("full_nm") var full_nm:String?,
    @SerializedName("sig_cd") var sig_cd:String?,
    @SerializedName("sig_kor_nm") var sig_kor_nm:String?,
    @SerializedName("sig_eng_nm") var sig_eng_nm:String?,
)


@Serializable
data class Feature_LT_C_ADSIDO_INFO(
    @SerializedName("type") var type:String,
    @SerializedName("properties") var properties:Properties_LT_C_ADSIDO_INFO,
    @SerializedName("id") var id:String
)

@Serializable
data class Feature_LT_C_ADSIGG_INFO(
    @SerializedName("type") var type:String,
    @SerializedName("properties") var properties:Properties_LT_C_ADSIGG_INFO,
    @SerializedName("id") var id:String
)


@Serializable
data class  FeatureCollection_LT_C_ADSIDO_INFO (
    @SerializedName("type") var type:String,
    @SerializedName("bbox") var bbox:Array<Float>,
    @SerializedName("features") var features:Array<Feature_LT_C_ADSIDO_INFO>
)

@Serializable
data class  FeatureCollection_LT_C_ADSIGG_INFO (
    @SerializedName("type") var type:String,
    @SerializedName("bbox") var bbox:Array<Float>,
    @SerializedName("features") var features:Array<Feature_LT_C_ADSIGG_INFO>
)


@Serializable
data class Result_LT_C_ADSIDO_INFO (
    @SerializedName("featureCollection") var featureCollection:FeatureCollection_LT_C_ADSIDO_INFO
)

@Serializable
data class Result_LT_C_ADSIGG_INFO (
    @SerializedName("featureCollection") var featureCollection:FeatureCollection_LT_C_ADSIGG_INFO
)


@Serializable
data class Error(
    @SerializedName("level") var level:String,
    @SerializedName("code") var code:String,
    @SerializedName("text") var text:String
)

@Serializable
data class VWorldRecvData_LT_C_ADSIDO_INFO (
    @SerializedName("service") var service:Service,
    @SerializedName("status") var status:String,
    @SerializedName("record") var record:Record,
    @SerializedName("page") var page:Page,
    @SerializedName("result") var result:Result_LT_C_ADSIDO_INFO?,
 //   @SerializedName("error") var error:Error?
)

@Serializable
data class VWorldRecvData_LT_C_ADSIGG_INFO (
    @SerializedName("service") var service:Service,
    @SerializedName("status") var status:String,
    @SerializedName("record") var record:Record,
    @SerializedName("page") var page:Page,
    @SerializedName("result") var result:Result_LT_C_ADSIGG_INFO?,
    //   @SerializedName("error") var error:Error?
)


@Serializable
data class VWorldResponse_LT_C_ADSIDO_INFO(
    @SerializedName("response") var response:VWorldRecvData_LT_C_ADSIDO_INFO
)

@Serializable
data class VWorldResponse_LT_C_ADSIGG_INFO(
    @SerializedName("response") var response:VWorldRecvData_LT_C_ADSIGG_INFO
)



