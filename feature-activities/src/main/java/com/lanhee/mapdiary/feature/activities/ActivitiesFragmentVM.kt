package com.lanhee.mapdiary.feature.activities

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lanhee.mapdiary.feature.base.MyColors
import com.lanhee.mapdiary.data.ActivitiesData
import com.lanhee.mapdiary.data.database.AppDatabase
import com.lanhee.mapdiary.data.database.DatabaseRepository
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

    private val _polyLine: PolylineOverlay = PolylineOverlay().apply {
                                                color = Color.parseColor("#FF6633")
                                                width = 10
                                                setPattern(30, 15)
                                                capType = PolylineOverlay.LineCap.Round
                                                joinType = PolylineOverlay.LineJoin.Round
                                            }

    fun setDate(date: Date) {
        _date.value = date
        loadActivities()
    }

    fun setMap(map: NaverMap) {
        _map.value = map
        _markers.value?.forEach {
            it.map = map
        }
        if((_markers.value?.size ?: 0) > 1) {
            _polyLine.map = map
        }
    }

    private fun loadActivities() {
        clearMap()
        viewModelScope.launch {
            val list = dbRepository.load(_date.value!!)
            setItems(list)
            list.forEach {
                addMarker(it.locationLat, it.locationLng)
            }
        }
    }

    fun clearMap() {
        _polyLine.map = null
        _markers.value?.forEach{
            it.map = null
        }
        _markers.value = listOf()
    }

    private fun setItems(items: List<ActivitiesData>) {
        _items.value = items
    }

    fun addItem(item: ActivitiesData) {
        viewModelScope.launch {
            item.order = _items.value!!.size
            dbRepository.save(item)
            val temp = if(_items.value == null) {
                mutableListOf()
            }else{
                MutableList(_items.value!!.size) { _items.value!![it] }
            }
            temp.add(
                ActivitiesData(
                    idx = temp.size,
                    item.locationName,
                    item.locationAddress,
                    item.locationLat,
                    item.locationLng,
                    temp.size,
                    _date.value!!
                )
            )
            _items.value = temp
        }
    }

    fun updateOrders(list: List<ActivitiesData>) {
        viewModelScope.launch {
            list.forEachIndexed { index, data ->
                data.order = index
                dbRepository.update(data)
            }
            _items.value = list

            clearMap()
            _items.value!!.forEach {
                addMarker(it.locationLat, it.locationLng)
            }
            drawAllPolyLine()
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
            dbRepository.delete(item.idx)
            val temp = MutableList(_items.value!!.size) { _items.value!![it] }
            temp.remove(item)

            updateOrders(temp)
        }
    }

    fun setModifyMode(isModifyMode: Boolean) {
        _isModifyMode.value = isModifyMode
    }

    fun addMarker(latitude: Double, longitude: Double) {
        val marker = Marker().apply {
            icon = OverlayImage.fromResource(com.lanhee.mapdiary.feature.base.R.drawable.ic_marker)
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
        val cameraUpdate = CameraUpdate.scrollAndZoomTo(LatLng(latitude, longitude), 16.0)
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

            val northEast = LatLng(maxLat, maxLng)
            val southWest = LatLng(minLat, minLng)

            map.value?.let {
                val cameraUpdate = CameraUpdate.fitBounds(LatLngBounds(southWest, northEast), 100, 300, 100, 50)
                    .animate(CameraAnimation.Linear)
                it.moveCamera(cameraUpdate)
            }

            _markers.value?.forEach {
                it.iconTintColor = Color.parseColor(MyColors.PRIMARY)
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

    private fun reDrawAllMarker() {
        _markers.value!!.forEach {
            it.map = null
        }
        _markers.value = listOf()
        _items.value!!.forEach {
            addMarker(it.locationLat, it.locationLng)
        }
    }

    fun drawAllPolyLine() {
        val myCoords = List<LatLng>(_markers.value!!.size) { index ->
            LatLng(_markers.value!![index].position.latitude, _markers.value!![index].position.longitude)
        }

        if(myCoords.size > 1) {
            _polyLine.coords = myCoords
            _polyLine.map = map.value
        }else{
            _polyLine.map = null
        }
    }

    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ActivitiesFragmentVM() as T
        }
    }
}