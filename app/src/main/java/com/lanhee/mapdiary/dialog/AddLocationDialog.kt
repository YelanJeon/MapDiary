package com.lanhee.mapdiary.dialog

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.lanhee.mapdiary.R
import com.lanhee.mapdiary.RetrofitModule
import com.lanhee.mapdiary.ReverseGeocodingService
import com.lanhee.mapdiary.data.ActivitiesData
import com.lanhee.mapdiary.data.ReverseGeocodingData
import com.lanhee.mapdiary.databinding.DlgAddlocationBinding
import com.lanhee.mapdiary.utils.MyColors
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddLocationDialog: BaseDialogFragment<DlgAddlocationBinding>(), OnMapReadyCallback {

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    var onAddLocation : ((ActivitiesData) -> Unit)? = null

    private val mapFragment: MapFragment by lazy {
        childFragmentManager.findFragmentById(R.id.map) as MapFragment
    }

    override fun inflateBinding(inflater: LayoutInflater): DlgAddlocationBinding {
        return DlgAddlocationBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapFragment.getMapAsync(this)

        val onClick: ((View) -> Unit) = {
            if(it.id == requireBinding().btnApply.id) {
                val name = if(requireBinding().etName.text.isEmpty()) { requireBinding().etName.hint.toString() } else { requireBinding().etName.text.toString() }
                val address = requireBinding().tvAddress.text.toString()
                val newData = ActivitiesData(locationName = name, locationAddress = address, locationLat = latitude, locationLng = longitude)
                onAddLocation?.invoke(newData)
            }
            dismiss()
        }

        requireBinding().etName.addTextChangedListener {
            requireBinding().btnApply.isEnabled = it!!.isNotEmpty() || requireBinding().etName.hint.isNotEmpty()
        }

        requireBinding().btnApply.setOnClickListener(onClick)
        requireBinding().btnClose.setOnClickListener(onClick)

    }

    override fun onMapReady(map: NaverMap) {
        val marker = Marker()
        marker.position = LatLng(map.cameraPosition.target.latitude, map.cameraPosition.target.longitude)
        marker.icon = OverlayImage.fromResource(R.drawable.ic_marker)
        marker.iconTintColor = Color.parseColor(MyColors.PRIMARY)
        marker.map = map

        map.uiSettings.apply {
            isCompassEnabled = false
            isScaleBarEnabled = false
            isIndoorLevelPickerEnabled = false
        }

        map.addOnCameraChangeListener{ reason, animated ->
            marker.position = LatLng(map.cameraPosition.target.latitude, map.cameraPosition.target.longitude)
            requireBinding().tvAddress.text = "위치 이동 중"
            requireBinding().etName.hint = ""
            requireBinding().etName.setText("")
        }

        map.addOnCameraIdleListener {
            latitude = map.cameraPosition.target.latitude
            longitude = map.cameraPosition.target.longitude
            marker.position = LatLng(latitude, longitude)

//            Toast.makeText(requireContext(), "loc :: $longitude / $latitude", Toast.LENGTH_SHORT).show()

            RetrofitModule.createRetrofit().create(ReverseGeocodingService::class.java).reverseGeocoding("${longitude},${latitude}").enqueue(object: Callback<ReverseGeocodingData> {
                override fun onResponse(call: Call<ReverseGeocodingData>, response: Response<ReverseGeocodingData>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        if(response.body() == null) {
                            requireBinding().tvAddress.text = "위치 정보 조회 실패"
                            requireBinding().etName.setText("")
                        }else{
                            val data = response.body()!!
                            var text = data.getFormattedText()
                            if(text.isEmpty()) {
                                requireBinding().tvAddress.text = "위치 정보 조회 실패"
                                requireBinding().etName.setText("")
                            }else{
                                requireBinding().tvAddress.text = text
                                requireBinding().etName.hint = text
                                requireBinding().etName.setText("")
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ReverseGeocodingData>, t: Throwable) {
                    CoroutineScope(Dispatchers.Main).launch {
                        requireBinding().tvAddress.text = "주소를 찾을 수 없습니다"
                        requireBinding().etName.setText("")
                    }
                }
            })
        }
    }
}