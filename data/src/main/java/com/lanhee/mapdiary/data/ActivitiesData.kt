package com.lanhee.mapdiary.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "activities")
data class ActivitiesData(
    @PrimaryKey(autoGenerate = true) val idx: Int = 0,                //색인번호
    @ColumnInfo("location_name") var locationName: String,       //사용자가 설정한 이름
    @ColumnInfo("locationAddress") val locationAddress: String,    //실제 주소
    @ColumnInfo("locationLat") val locationLat: Double,        //위도
    @ColumnInfo("locationLng") val locationLng: Double,        //경도
    @ColumnInfo("my_order") var order: Int = 0,                 //리스트 상 위치
    @ColumnInfo("active_date") val active_date: Date = Date(System.currentTimeMillis())   //활동 소속 날짜
) {
    constructor(data: ActivitiesData) : this(data.idx, data.locationName, data.locationAddress, data.locationLat, data.locationLng, data.order, data.active_date)
}