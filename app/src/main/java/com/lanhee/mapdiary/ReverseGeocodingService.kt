package com.lanhee.mapdiary

import com.lanhee.mapdiary.data.ReverseGeocodingData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ReverseGeocodingService {
    @GET("/map-reversegeocode/v2/gc")
    fun reverseGeocoding(
        @Query("coords") coords: String,
        @Query("orders") orders: String = "roadaddr",
        @Query("output") output: String = "json"
    ) : Call<ReverseGeocodingData>


}
