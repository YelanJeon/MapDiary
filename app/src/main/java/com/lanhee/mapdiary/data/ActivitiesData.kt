package com.lanhee.mapdiary.data

class ActivitiesData(
    val idx: Int = 0,                //색인번호
    var locationName: String,       //사용자가 설정한 이름
    val locationAddress: String,    //실제 주소
    val locationLat: Double,        //위도
    val locationLng: Double,        //경도
    var order: Int = 0,                 //리스트 상 위치
)
