package com.lanhee.mapdiary.utils

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lanhee.mapdiary.R
import com.lanhee.mapdiary.data.ActivitiesData
import com.lanhee.mapdiary.database.AppDatabase
import com.lanhee.mapdiary.database.DatabaseRepository
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.overlay.PolylineOverlay
import kotlinx.coroutines.launch
import java.util.Date
import kotlin.math.max
import kotlin.math.min

class ActivitiesFragmentVM: ViewModel() {
    val dbRepository by lazy { DatabaseRepository(AppDatabase.getInstance()) }

    private val _date = MutableLiveData<Date>()
    val date = _date as LiveData<Date>

    private val _map = MutableLiveData<NaverMap>()
    val map = _map as LiveData<NaverMap>

    private val _items = MutableLiveData<List<ActivitiesData>>()
    val items = _items as LiveData<List<ActivitiesData>>

    private val _isModifyMode = MutableLiveData<Boolean>()
    val isModifyMode = _isModifyMode as LiveData<Boolean>

    private val _markers = MutableLiveData<List<Marker>>()
    val markers = _markers as LiveData<List<Marker>>

    private val _polyLine = MutableLiveData<PolylineOverlay>()

    fun setDate(date: Date) {
        _date.value = date
    }

    fun setMap(map: NaverMap) {
        _map.value = map
    }

    fun loadActivities() {
        Log.i("TEST", "loadActivities")
        clearMap()
        Log.i("TEST", "clearMap")
        viewModelScope.launch {
            val list = dbRepository.load(_date.value!!)
            Log.i("TEST", "load success")
            Log.i("TEST", "before set items > ${items.value!!.size}")
            setItems(list)
            Log.i("TEST", "after set items > ${items.value!!.size}")
            list.forEach {
                addMarker(it.locationLat, it.locationLng)
                Log.i("TEST", "addmarker success")
            }
        }
    }

    private fun clearMap() {
        _polyLine.value?.map = null
        _markers.value?.forEach{
            it.map = null
        }
        _items.value = listOf()
        _markers.value = listOf()
    }

    private fun setItems(items: List<ActivitiesData>) {
        _items.value = items
    }

    fun addItem(item: ActivitiesData) {
        viewModelScope.launch {
            dbRepository.save(item)
            val temp = if(_items.value == null) {
                mutableListOf()
            }else{
                MutableList(_items.value!!.size) { _items.value!![it] }
            }
            temp.add(ActivitiesData(idx=temp.size, item.locationName, item.locationAddress, item.locationLat, item.locationLng, temp.size, _date.value!!))
            _items.value = temp
        }
    }

    fun updateItemLocationName(name: String, position: Int) {
        viewModelScope.launch {
            val temp = MutableList(_items.value!!.size) { _items.value!![it].copy() }
            temp[position].locationName = name

            dbRepository.update(temp[position])

            _items.value = temp
        }

    }

    fun removeItem(item: ActivitiesData) {
        viewModelScope.launch {
            dbRepository.delete(item)
            val temp = MutableList(_items.value!!.size) { _items.value!![it] }
            temp.remove(item)
            _items.value = temp
        }
    }

    fun setModifyMode(isModifyMode: Boolean) {
        _isModifyMode.value = isModifyMode
    }

    fun addMarker(latitude: Double, longitude: Double) {
        val marker = Marker().apply {
            icon = OverlayImage.fromResource(R.drawable.ic_marker)
            iconTintColor = Color.parseColor(MyColors.PRIMARY)
            position = LatLng(latitude, longitude)
            map = this@ActivitiesFragmentVM.map.value
        }

        val temp = if(_markers.value == null) {
                        mutableListOf()
                    }else{
                        MutableList(_markers.value!!.size) { _markers.value!![it] }
                    }
        temp.add(marker)
        _markers.value = temp
    }

    fun removeMarker(position: Int) {
        _markers.value!![position].map = null

        val temp = MutableList(_markers.value!!.size) { _markers.value!![it] }
        temp.removeAt(position)
        _markers.value = temp

    }

    fun moveCamera(marker: Marker) {
        moveCamera(marker.position.latitude, marker.position.longitude)
    }

    private fun moveCamera(latitude: Double, longitude: Double) {
        val cameraUpdate = CameraUpdate.scrollAndZoomTo(LatLng(latitude, longitude), 14.0)
            .animate(CameraAnimation.Linear)
        map.value!!.moveCamera(cameraUpdate)
    }

    fun showAllMarkers() {
        if(_markers.value == null) {
            return
        }

        if(_markers.value != null && _markers.value!!.size == 1) {
            moveCamera(_markers.value!![0])
        }else{
            var maxLat = 0.0
            var minLat = 0.0
            var maxLng = 0.0
            var minLng = 0.0
            _markers.value?.forEach { marker ->
                val lat = marker.position.latitude
                val lng = marker.position.longitude

                if(maxLat == 0.0) maxLat = lat
                if(minLat == 0.0) minLat = lat
                if(maxLng == 0.0) maxLng = lng
                if(minLng == 0.0) minLng = lng

                maxLat = max(maxLat, lat)
                minLat = min(minLat, lat)
                maxLng = max(maxLng, lng)
                minLng = min(minLng, lng)
            }
            val padding = 0.001
            val northEast = LatLng(maxLat+(padding*2), maxLng+padding)
            val southWest = LatLng(minLat-(padding/2), minLng-padding)

            map.value?.let {
                val cameraUpdate = CameraUpdate.fitBounds(LatLngBounds(southWest, northEast))
                    .animate(CameraAnimation.Linear)
                it.moveCamera(cameraUpdate)
            }
        }
    }

    fun focusToMarker(marker: Marker) {
        moveCamera(marker)
        _markers.value!!.forEach {
            it.iconTintColor =  if(it == marker) {
                Color.parseColor(MyColors.ACCENT)
            }else {
                Color.parseColor(MyColors.PRIMARY)
            }
        }
    }

    fun drawAllPolyLine() {
        if(_markers.value!!.size <= 1) {
            return
        }

        if(_polyLine.value != null) {
            //기존 라인 지우기
            _polyLine.value!!.map = null
        }

        val myCoords = List<LatLng>(_markers.value!!.size) { index ->
            LatLng(_markers.value!![index].position.latitude, _markers.value!![index].position.longitude)
        }

        val polyLine = PolylineOverlay().apply {
            coords = myCoords
            color = Color.parseColor("#FF6633")
            width = 10
            setPattern(30, 15)
            capType = PolylineOverlay.LineCap.Round
            joinType = PolylineOverlay.LineJoin.Round

            map = this@ActivitiesFragmentVM._map.value
        }
        _polyLine.value = polyLine
    }


    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ActivitiesFragmentVM() as T
        }
    }
}