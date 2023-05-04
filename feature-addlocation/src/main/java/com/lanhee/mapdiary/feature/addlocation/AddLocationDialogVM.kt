package com.lanhee.mapdiary.feature.addlocation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lanhee.mapdiary.data.ReverseGeocodingData
import com.lanhee.mapdiary.feature.base.RetrofitModule
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.NaverMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddLocationDialogVM: ViewModel() {
    private val _location = MutableStateFlow(LatLng(0.0, 0.0))
    val location = _location.asStateFlow()

    private val _address = MutableStateFlow("")
    val address = _address.asStateFlow()

    private val _addressName = MutableStateFlow("")
    val addressName = _addressName.asStateFlow()

    private val _map = MutableLiveData<NaverMap?>()
    val map = _map as LiveData<NaverMap?>

    private var state = State.STATE_CAM_IDLE

    fun setLocation(lat: Double, lng: Double) {
        _location.value = LatLng(lat, lng)
    }

    fun setAddress(address: String) {
        _address.value = address
        if(state == State.STATE_CAM_MOVE || state == State.STATE_FAIL) {
            setAddressName("")
        }else{
            setAddressName(address)
        }
    }

    fun setAddressName(addressName: String) {
        _addressName.value = addressName
    }

    fun setMap(map: NaverMap) {
        _map.value = map
    }

    fun setState(state: State) {
        this.state = state
        when(state) {
            State.STATE_CAM_MOVE -> {
                setAddress("위치 이동 중")
                setAddressName("")
            }
            State.STATE_CAM_IDLE -> {
                startReverseGeocoding()
            }
            State.STATE_FAIL -> {
                setAddress("위치 정보 조회 실패")
                setAddressName("")
            }
            State.STATE_SUCCESS -> {
                //nothing
            }
        }
    }

    fun startReverseGeocoding() {
        val latitude = location.value!!.latitude
        val longitude = location.value!!.longitude
        RetrofitModule.createRetrofit().create(ReverseGeocodingService::class.java).reverseGeocoding("${longitude},${latitude}").enqueue(object:
            Callback<ReverseGeocodingData> {
            override fun onResponse(call: Call<ReverseGeocodingData>, response: Response<ReverseGeocodingData>) {
                if(response.body() == null || response.body()!!.getFormattedText().isEmpty()) {
                    setState(State.STATE_FAIL)
                }else{
                    setState(State.STATE_SUCCESS)
                    setAddress(response.body()!!.getFormattedText().toString())
                    setAddressName(response.body()!!.getFormattedText().toString())
                }
            }

            override fun onFailure(call: Call<ReverseGeocodingData>, t: Throwable) {
                setState(State.STATE_FAIL)
            }
        })
    }

    class Factory: ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AddLocationDialogVM() as T
        }
    }
}