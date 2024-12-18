package com.unchil.searchcampcompose.model

import com.unchil.searchcampcompose.db.entity.CampSite_TBL
import com.unchil.searchcampcompose.db.entity.NearCampSite_TBL
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


enum class GoCampingSyncStatus {
    U,A,D,N
}

enum class GoCampingResponseStatus {
    INVALID_REQUEST_PARAMETER_ERROR,
    NO_MANDATORY_REQUEST_PARAMETERS_ERROR,
    TEMPORARILY_DISABLE_THE_SERVICEKEY_ERROR,
    UNSIGNED_CALL_ERROR,
    SUCCESS,
    NORMAL_CODE,
    DB_ERROR,
    NODATA_ERROR,
    SERVICETIMEOUT_ERROR,
    OK
}

fun GoCampingResponseStatus.getDesc():Pair<String,String> {
    return when(this){
        GoCampingResponseStatus.INVALID_REQUEST_PARAMETER_ERROR -> Pair("10", "잘못된 요청 파라메터 에러")
        GoCampingResponseStatus.NO_MANDATORY_REQUEST_PARAMETERS_ERROR ->  Pair("11", "필수 요청 파라메터가 없음")
        GoCampingResponseStatus.TEMPORARILY_DISABLE_THE_SERVICEKEY_ERROR ->  Pair("21", "일시적으로 사용할수 없는 서비스키")
        GoCampingResponseStatus.UNSIGNED_CALL_ERROR ->Pair("33", "서명되지않은 호출")
        GoCampingResponseStatus.SUCCESS -> Pair("200", "성공")
        GoCampingResponseStatus.NORMAL_CODE -> Pair("00", "정상")
        GoCampingResponseStatus.DB_ERROR -> Pair("02", "데이터베이스 에러")
        GoCampingResponseStatus.NODATA_ERROR -> Pair("03", "데이터 없음 에러")
        GoCampingResponseStatus.SERVICETIMEOUT_ERROR -> Pair("05", "서비스 연결 실패 에러")
        GoCampingResponseStatus.OK -> Pair("0000", "요청 서비스 정상 리턴")
    }
}


 val GoCampingResponseStatusList:List<GoCampingResponseStatus> = listOf(
     GoCampingResponseStatus.OK,
     GoCampingResponseStatus.SUCCESS,
     GoCampingResponseStatus.NORMAL_CODE,
     GoCampingResponseStatus.DB_ERROR,
     GoCampingResponseStatus.INVALID_REQUEST_PARAMETER_ERROR,
     GoCampingResponseStatus.NODATA_ERROR,
     GoCampingResponseStatus.NO_MANDATORY_REQUEST_PARAMETERS_ERROR,
     GoCampingResponseStatus.SERVICETIMEOUT_ERROR,
     GoCampingResponseStatus.TEMPORARILY_DISABLE_THE_SERVICEKEY_ERROR,
     GoCampingResponseStatus.UNSIGNED_CALL_ERROR
 )


data class SiteDefaultData (
    var contentId:String, //콘텐츠 ID
    var facltNm:String, //야영장명
    var lineIntro:String, //한줄소개
    var intro:String, //소개
    var lctCl:String, //입지구분
    var animalCmgCl:String, //애완동물출입
    var glampInnerFclty:String, //글램핑 - 내부시설
    var sbrsCl:String, //부대시설
    var eqpmnLendCl:String, //캠핑장비대여
    var resveCl:String, //예약 구분
    var resveUrl:String, //예약 페이지
    var tel:String, //전화
    var facltDivNm:String, //사업주체.구분
    var induty:String, //업종
    var doNm:String, //도
    var sigunguNm:String, //시군구
    var addr1:String, //주소
    var firstImageUrl:String, //대표이미지
    var homepage:String, //홈페이지
    var latitude:Double,  // 위도
    var longitude:Double, // 경도

    var isImageListLoaded:Boolean? = null
) {
    companion object{
        fun setInitValue():SiteDefaultData {
            return SiteDefaultData(contentId = "2963", facltNm= "초막골생태공원 느티나무야영장 ", lineIntro=  "일반캠핑과 글램핑이 함께하는 공간", intro=  "초막골생태공원 느티나무 야영장은 경기도 군포시에 자리 잡고 있다. 군포시청을 기점으로 오금초등학교를 지나 도장터널과 능내터널 거치면 닿는다. 도착까지 걸리는 시간은 약 10분이다. 이곳은 일반캠핑과 글램핑이 함께하는 공간이다. 일반캠핑은 47면을 마련했으며, 전기와 화로 사용이 가능하다. 단, 화로 사용 시 장작은 이용할 수 없으며, 숯만 허용한다. 이는 글램핑도 마찬가지다. 글램핑은 고급형 11면, 일반형 5면을 갖췄다. 고급형은 내부에 침대, 냉난방기, 개수대, 냉장고, TV, 취사도구, 식기류, 화장실, 샤워실 등을 비치했다. 일반형은 냉난방기, 화장실, 샤워실, 개수대를 구비하지 않았다. 매점을 운영하지 않으므로 자동차로 10여 분 거리에 있는 마트를 이용해야 한다.",
            lctCl=  "숲,도심", animalCmgCl=  "불가능", glampInnerFclty=  "침대,TV,에어컨,냉장고,난방기구,취사도구,내부화장실", sbrsCl=  "부대시설",
            eqpmnLendCl=  "대여장비", resveCl=  "온라인실시간예약", resveUrl=  "http://www.gunpo.go.kr/cms/content/view/2325.do",
            tel=  "031-390-7666", facltDivNm=  "지자체", induty=  "자동차야영장,글램핑", doNm=  "경기도", sigunguNm=  "군포시",
            addr1=  "경기도 군포시 산본동  919 ", firstImageUrl=  "https://gocamping.or.kr/upload/camp/2963/thumb/thumb_720_3546MBYdxSjqEUebEYHSYZBj.jpg",
            homepage=  "http://www.gunpo.go.kr/cms/content/view/2325.do", latitude=  37.386092, longitude=  126.934793)
        }
    }
}


@Serializable
data class GoCampingResponse(
    @SerialName("response") var response:GoCampingRecvData?
)

@Serializable
data class GoCampingRecvItem (
    @SerialName("addr1") var addr1:String, //주소
    @SerialName("addr2") var addr2:String, //주소상세
    @SerialName("allar") var allar:String, //전체면적
    @SerialName("animalCmgCl") var animalCmgCl:String, //애완동물출입
    @SerialName("autoSiteCo") var autoSiteCo:String, //주요시설 자동차야영장
    @SerialName("bizrno") var bizrno:String, //사업자번호
    @SerialName("brazierCl") var brazierCl:String, //화로대
    @SerialName("caravAcmpnyAt") var caravAcmpnyAt:String, //개인 카라반 동반 여부(Y:사용, N:미사용)
    @SerialName("caravInnerFclty") var caravInnerFclty:String, //카라반 - 내부시설
    @SerialName("caravSiteCo") var caravSiteCo:String, //주요시설 카라반
    @SerialName("clturEvent") var clturEvent:String, //자체문화행사명
    @SerialName("clturEventAt") var clturEventAt:String, //자체문화행사 여부(Y:사용, N:미사용)
    @SerialName("contentId") var contentId:String, //콘텐츠 ID
    @SerialName("createdtime") var createdtime:String, //등록일
    @SerialName("direction") var direction:String, //오시는 길 컨텐츠
    @SerialName("doNm") var doNm:String, //도
    @SerialName("eqpmnLendCl") var eqpmnLendCl:String, //캠핑장비대여
    @SerialName("exprnProgrm") var exprnProgrm:String, //체험프로그램명
    @SerialName("exprnProgrmAt") var exprnProgrmAt:String, //체험프로그램 여부(Y:사용, N:미사용)
    @SerialName("extshrCo") var extshrCo:String, //소화기 개수
    @SerialName("facltDivNm") var facltDivNm:String, //사업주체.구분
    @SerialName("facltNm") var facltNm:String, //야영장명
    @SerialName("featureNm") var featureNm:String, //특징
    @SerialName("fireSensorCo") var fireSensorCo:String, //화재감지기 개수
    @SerialName("firstImageUrl") var firstImageUrl:String, //대표이미지
    @SerialName("frprvtSandCo") var frprvtSandCo:String, //방화사 개수
    @SerialName("frprvtWrppCo") var frprvtWrppCo:String, //방화수 개수
    @SerialName("glampInnerFclty") var glampInnerFclty:String, //글램핑 - 내부시설
    @SerialName("glampSiteCo") var glampSiteCo:String, //주요시설 글램핑
    @SerialName("gnrlSiteCo") var gnrlSiteCo:String, //주요시설 일반야영장
    @SerialName("homepage") var homepage:String, //홈페이지
    @SerialName("hvofBgnde") var hvofBgnde:String, //휴장기간.휴무기간 시작일
    @SerialName("hvofEnddle") var hvofEnddle:String, //휴장기간.휴무기간 종료일
    @SerialName("induty") var induty:String, //업종
    @SerialName("indvdlCaravSiteCo") var indvdlCaravSiteCo:String, //주요시설 개인 카라반
    @SerialName("insrncAt") var insrncAt:String, //영업배상책임보험 가입여부(Y:사용, N:미사용)
    @SerialName("intro") var intro:String, //소개
    @SerialName("lctCl") var lctCl:String, //입지구분
    @SerialName("lineIntro") var lineIntro:String, //한줄소개
    @SerialName("manageNmpr") var manageNmpr:String, //상주관리인원
    @SerialName("manageSttus") var manageSttus:String, //운영상태.관리상태
    @SerialName("mangeDivNm") var mangeDivNm:String, //운영주체.관리주체 (직영, 위탁)
    @SerialName("mapX") var mapX:String, //경도(X)
    @SerialName("mapY") var mapY:String, //위도(Y)
    @SerialName("mgcDiv") var mgcDiv:String, //운영기관.관리기관
    @SerialName("modifiedtime") var modifiedtime:String, //수정일
    @SerialName("operDeCl") var operDeCl:String, //운영일
    @SerialName("operPdCl") var operPdCl:String, //운영기간
    @SerialName("posblFcltyCl") var posblFcltyCl:String, //주변이용가능시설
    @SerialName("posblFcltyEtc") var posblFcltyEtc:String, //주변이용가능시설 기타
    @SerialName("prmisnDe") var prmisnDe:String, //인허가일자
    @SerialName("resveCl") var resveCl:String, //예약 구분
    @SerialName("resveUrl") var resveUrl:String, //예약 페이지
    @SerialName("sbrsCl") var sbrsCl:String, //부대시설
    @SerialName("sbrsEtc") var sbrsEtc:String, //부대시설 기타
    @SerialName("sigunguNm") var sigunguNm:String, //시군구
    @SerialName("siteBottomCl1") var siteBottomCl1:String, //잔디
    @SerialName("siteBottomCl2") var siteBottomCl2:String, //파쇄석
    @SerialName("siteBottomCl3") var siteBottomCl3:String, //테크
    @SerialName("siteBottomCl4") var siteBottomCl4:String, //자갈
    @SerialName("siteBottomCl5") var siteBottomCl5:String, //맨흙
    @SerialName("sitedStnc") var sitedStnc:String, //사이트간 거리
    @SerialName("siteMg1Co") var siteMg1Co:String, //사이트 크기1 수량
    @SerialName("siteMg1Vrticl") var siteMg1Vrticl:String, //사이트 크기1 세로
    @SerialName("siteMg1Width") var siteMg1Width:String, //사이트 크기1 가로
    @SerialName("siteMg2Co") var siteMg2Co:String, //사이트 크기2 수량
    @SerialName("siteMg2Vrticl") var siteMg2Vrticl:String, //사이트 크기2 세로
    @SerialName("siteMg2Width") var siteMg2Width:String, //사이트 크기2 가로
    @SerialName("siteMg3Co") var siteMg3Co:String, //사이트 크기3 수량
    @SerialName("siteMg3Vrticl") var siteMg3Vrticl:String, //사이트 크기3 세로
    @SerialName("siteMg3Width") var siteMg3Width:String, //사이트 크기3 가로
    @SerialName("swrmCo") var swrmCo:String, //샤워실 개수
    @SerialName("tel") var tel:String, //전화
    @SerialName("themaEnvrnCl") var themaEnvrnCl:String, //테마환경
    @SerialName("toiletCo") var toiletCo:String, //화장실 개수
    @SerialName("tooltip") var tooltip:String, //툴팁
    @SerialName("tourEraCl") var tourEraCl:String, //여행시기
    @SerialName("trlerAcmpnyAt") var trlerAcmpnyAt:String, //개인 트레일러 동반 여부(Y:사용, N:미사용)
    @SerialName("trsagntNo") var trsagntNo:String, //관광사업자번호
    @SerialName("wtrplCo") var wtrplCo:String, //개수대 개수
    @SerialName("zipcode") var zipcode:String, //우편번호

    @SerialName("syncStatus") var syncStatus:String? //콘덴츠 상태


){
    companion object{
        fun toCampSite_TBL(it:GoCampingRecvItem):CampSite_TBL{
            val result =  CampSite_TBL(
                contentId = it.contentId,
                addr1 = it.addr1,
                addr2 = it.addr2,
                allar = it.allar,
                animalCmgCl = it.animalCmgCl,
                autoSiteCo = it.autoSiteCo,
                bizrno = it.bizrno,
                brazierCl = it.brazierCl,
                caravAcmpnyAt = it.caravAcmpnyAt,
                caravInnerFclty = it.caravInnerFclty,
                caravSiteCo = it.caravSiteCo,
                clturEvent = it.clturEvent,
                clturEventAt = it.clturEventAt,
                createdtime = it.createdtime,
                direction = it.direction,
                doNm = it.doNm,
                eqpmnLendCl = it.eqpmnLendCl,
                exprnProgrm = it.exprnProgrm,
                exprnProgrmAt = it.exprnProgrmAt,
                extshrCo = it.extshrCo,
                facltDivNm = it.facltDivNm,
                facltNm = it.facltNm,
                featureNm = it.featureNm,
                fireSensorCo = it.fireSensorCo,
                firstImageUrl = it.firstImageUrl,
                frprvtSandCo = it.frprvtSandCo,
                frprvtWrppCo = it.frprvtWrppCo,
                glampInnerFclty = it.glampInnerFclty,
                glampSiteCo = it.glampSiteCo,
                gnrlSiteCo = it.gnrlSiteCo,
                homepage = it.homepage,
                hvofBgnde = it.hvofBgnde,
                hvofEnddle = it.hvofEnddle,
                induty = it.induty,
                indvdlCaravSiteCo = it.indvdlCaravSiteCo,
                insrncAt = it.insrncAt,
                intro = it.intro,
                lctCl = it.lctCl,
                lineIntro = it.lineIntro,
                manageNmpr = it.manageNmpr,
                manageSttus = it.manageSttus,
                mangeDivNm = it.mangeDivNm,
                mapX = it.mapX,
                mapY = it.mapY,
                mgcDiv = it.mgcDiv,
                modifiedtime = it.modifiedtime,
                operDeCl = it.operDeCl,
                operPdCl = it.operPdCl,
                posblFcltyCl = it.posblFcltyCl,
                posblFcltyEtc = it.posblFcltyEtc,
                prmisnDe = it.prmisnDe,
                resveCl = it.resveCl,
                resveUrl = it.resveUrl,
                sbrsCl = it.sbrsCl,
                sbrsEtc = it.sbrsEtc,
                sigunguNm = it.sigunguNm,
                siteBottomCl1 = it.siteBottomCl1,
                siteBottomCl2 = it.siteBottomCl2,
                siteBottomCl3 = it.siteBottomCl3,
                siteBottomCl4 = it.siteBottomCl4,
                siteBottomCl5 = it.siteBottomCl5,
                sitedStnc = it.sitedStnc,
                siteMg1Co = it.siteMg1Co,
                siteMg1Vrticl = it.siteMg1Vrticl,
                siteMg1Width = it.siteMg1Width,
                siteMg2Co = it.siteMg2Co,
                siteMg2Vrticl = it.siteMg2Vrticl,
                siteMg2Width = it.siteMg2Width,
                siteMg3Co = it.siteMg3Co,
                siteMg3Vrticl = it.siteMg3Vrticl,
                siteMg3Width = it.siteMg3Width,
                swrmCo = it.swrmCo,
                tel = it.tel,
                themaEnvrnCl = it.themaEnvrnCl,
                toiletCo = it.toiletCo,
                tooltip = it.tooltip,
                tourEraCl = it.tourEraCl,
                trlerAcmpnyAt = it.trlerAcmpnyAt,
                trsagntNo = it.trsagntNo,
                wtrplCo = it.wtrplCo,
                zipcode = it.zipcode
            )

            return result
        }

        fun toNearCampSite_TBL(it:GoCampingRecvItem):NearCampSite_TBL{
            val result =  NearCampSite_TBL(
                contentId = it.contentId,
                addr1 = it.addr1,
                addr2 = it.addr2,
                allar = it.allar,
                animalCmgCl = it.animalCmgCl,
                autoSiteCo = it.autoSiteCo,
                bizrno = it.bizrno,
                brazierCl = it.brazierCl,
                caravAcmpnyAt = it.caravAcmpnyAt,
                caravInnerFclty = it.caravInnerFclty,
                caravSiteCo = it.caravSiteCo,
                clturEvent = it.clturEvent,
                clturEventAt = it.clturEventAt,
                createdtime = it.createdtime,
                direction = it.direction,
                doNm = it.doNm,
                eqpmnLendCl = it.eqpmnLendCl,
                exprnProgrm = it.exprnProgrm,
                exprnProgrmAt = it.exprnProgrmAt,
                extshrCo = it.extshrCo,
                facltDivNm = it.facltDivNm,
                facltNm = it.facltNm,
                featureNm = it.featureNm,
                fireSensorCo = it.fireSensorCo,
                firstImageUrl = it.firstImageUrl,
                frprvtSandCo = it.frprvtSandCo,
                frprvtWrppCo = it.frprvtWrppCo,
                glampInnerFclty = it.glampInnerFclty,
                glampSiteCo = it.glampSiteCo,
                gnrlSiteCo = it.gnrlSiteCo,
                homepage = it.homepage,
                hvofBgnde = it.hvofBgnde,
                hvofEnddle = it.hvofEnddle,
                induty = it.induty,
                indvdlCaravSiteCo = it.indvdlCaravSiteCo,
                insrncAt = it.insrncAt,
                intro = it.intro,
                lctCl = it.lctCl,
                lineIntro = it.lineIntro,
                manageNmpr = it.manageNmpr,
                manageSttus = it.manageSttus,
                mangeDivNm = it.mangeDivNm,
                mapX = it.mapX,
                mapY = it.mapY,
                mgcDiv = it.mgcDiv,
                modifiedtime = it.modifiedtime,
                operDeCl = it.operDeCl,
                operPdCl = it.operPdCl,
                posblFcltyCl = it.posblFcltyCl,
                posblFcltyEtc = it.posblFcltyEtc,
                prmisnDe = it.prmisnDe,
                resveCl = it.resveCl,
                resveUrl = it.resveUrl,
                sbrsCl = it.sbrsCl,
                sbrsEtc = it.sbrsEtc,
                sigunguNm = it.sigunguNm,
                siteBottomCl1 = it.siteBottomCl1,
                siteBottomCl2 = it.siteBottomCl2,
                siteBottomCl3 = it.siteBottomCl3,
                siteBottomCl4 = it.siteBottomCl4,
                siteBottomCl5 = it.siteBottomCl5,
                sitedStnc = it.sitedStnc,
                siteMg1Co = it.siteMg1Co,
                siteMg1Vrticl = it.siteMg1Vrticl,
                siteMg1Width = it.siteMg1Width,
                siteMg2Co = it.siteMg2Co,
                siteMg2Vrticl = it.siteMg2Vrticl,
                siteMg2Width = it.siteMg2Width,
                siteMg3Co = it.siteMg3Co,
                siteMg3Vrticl = it.siteMg3Vrticl,
                siteMg3Width = it.siteMg3Width,
                swrmCo = it.swrmCo,
                tel = it.tel,
                themaEnvrnCl = it.themaEnvrnCl,
                toiletCo = it.toiletCo,
                tooltip = it.tooltip,
                tourEraCl = it.tourEraCl,
                trlerAcmpnyAt = it.trlerAcmpnyAt,
                trsagntNo = it.trsagntNo,
                wtrplCo = it.wtrplCo,
                zipcode = it.zipcode
            )

            return result
        }
    }

}



@Serializable
data class GoCampingRecvItems(
    @SerialName("item")var item:Array<GoCampingRecvItem>
)


@Serializable
data class GoCampingRecvBody(
    @SerialName("items") var items:GoCampingRecvItems?,
    @SerialName("numOfRows") var numOfRows:Int, // 한 페이지의 결과 수
    @SerialName("pageNo") var pageNo:Int, // 현재 조회된 데이터의 페이지 번호
    @SerialName("totalCount")  var totalCount:Int // 전체 데이터의 총 수
)


@Serializable
data class GoCampingRecvData (
    @SerialName("header") var header:GoCampingRecvHeader,
    @SerialName("body") var body:GoCampingRecvBody
)



@Serializable
data class GoCampingRecvHeader (
    @SerialName("resultCode") var resultCode:String,  // API 호출 결과의 상태 코드
    @SerialName("resultMsg") var resultMsg:String // API 호출 결과의 상태
)


@Serializable
data class GoCampingRecvItemImage(
    @SerialName("contentId") var contentId:String, // 콘텐츠 ID
    @SerialName("createdtime") var createdtime:String, //등록일
    @SerialName("imageUrl") var imageUrl:String, //이미지 URL
    @SerialName("modifiedtime") var modifiedtime:String, // 수정일
    @SerialName("serialnum") var serialnum:String //이미지 일련번호
)

@Serializable
data class GoCampingRecvItemImages(
    @SerialName("item") var item: Array<GoCampingRecvItemImage>
)

@Serializable
data class GoCampingRecvBodyImage(
    @SerialName("items") var items:GoCampingRecvItemImages?,
    @SerialName("numOfRows") var numOfRows:Int, // 한 페이지의 결과 수
    @SerialName("pageNo") var pageNo:Int, // 현재 조회된 데이터의 페이지 번호
    @SerialName("totalCount") var totalCount:Int // 전체 데이터의 총 수
)

@Serializable
data class GoCampingRecvBodyImageEmpty(
    @SerialName("items") var items:String,
    @SerialName("numOfRows") var numOfRows:Int, // 한 페이지의 결과 수
    @SerialName("pageNo") var pageNo:Int, // 현재 조회된 데이터의 페이지 번호
    @SerialName("totalCount") var totalCount:Int // 전체 데이터의 총 수
)

@Serializable
data class GoCampingRecvDataImage(
    @SerialName("header") var header:GoCampingRecvHeader,
    @SerialName("body") var body:GoCampingRecvBodyImage
)

@Serializable
data class  GoCampingRecvDataImageEmpty(
    @SerialName("header") var header:GoCampingRecvHeader,
    @SerialName("body") var body:GoCampingRecvBodyImageEmpty
)

@Serializable
data class   GoCampingResponseImage(
    @SerialName("response") var response:GoCampingRecvDataImage?
)

@Serializable
data class    GoCampingResponseImageEmpty(
    @SerialName("response") var response:GoCampingRecvDataImageEmpty
)



