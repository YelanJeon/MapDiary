package com.lanhee.mapdiary.data

data class ReverseGeocodingData(
    val results: List<Result>,
    val status: Status
) {
    fun getFormattedText(): CharSequence {
        return if(results.isEmpty()) {
            ""
        }else{
            val si = results[0].region.area1.name
            val gu = results[0].region.area2.name
            val roadName = results[0].land.name
            val roadNum = results[0].land.number1
            "$si $gu $roadName $roadNum"
        }
    }
}

data class Result(
    val code: Code,
    val name: String,
    val region: Region,
    val land: Land
)
data class Code(
    val id: String,
    val mappingId: String,
    val type: String
)
data class Region(
    val area0: Area0,
    val area1: Area0,
    val area2: Area0,
    val area3: Area0,
    val area4: Area0
)

data class Area0(
    val coords: Coords,
    val name: String
)

data class Land(
    val type: String,
    val name: String,
    val number1: String,
    val number2: String
)

data class Coords(
    val center: Center
)

data class Center(
    val crs: String,
    val x: Double,
    val y: Double
)

data class Status(
    val code: Int,
    val message: String,
    val name: String
)
