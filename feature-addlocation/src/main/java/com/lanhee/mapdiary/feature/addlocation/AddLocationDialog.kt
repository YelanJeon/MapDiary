package com.lanhee.mapdiary.feature.addlocation

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.lanhee.mapdiary.feature.base.BaseDialogFragment
import com.lanhee.mapdiary.feature.base.MyColors
import com.lanhee.mapdiary.feature.addlocation.databinding.DlgAddlocationBinding
import com.lanhee.mapdiary.data.ActivitiesData
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch

class AddLocationDialog: BaseDialogFragment<DlgAddlocationBinding>(), OnMapReadyCallback {
    private val viewModel by lazy { ViewModelProvider(this, AddLocationDialogVM.Factory())[AddLocationDialogVM::class.java]}

    var onAddLocation : ((ActivitiesData) -> Unit)? = null

    private val marker by lazy {
        Marker().apply {
            icon = OverlayImage.fromResource(com.lanhee.mapdiary.feature.base.R.drawable.ic_marker)
            iconTintColor = Color.parseColor(MyColors.PRIMARY)
        }
    }

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
                val newData = ActivitiesData(
                    locationName = viewModel.addressName.value!!,
                    locationAddress = viewModel.address.value!!,
                    locationLat = viewModel.location.value!!.latitude,
                    locationLng = viewModel.location.value!!.longitude
                )
                onAddLocation?.invoke(newData)
            }
            dismiss()
        }

        requireBinding().etName.addTextChangedListener {
            if(it.toString() != viewModel.addressName.value) {
                viewModel.setAddressName(it.toString())
            }
        }
        requireBinding().tvAddress.addTextChangedListener {
            if(it.toString() != viewModel.address.value) {
                viewModel.setAddress(it.toString())
            }
        }

        requireBinding().btnApply.setOnClickListener(onClick)
        requireBinding().btnClose.setOnClickListener(onClick)


        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.address.collectLatest {
                        Log.i("TEST", "address changed :: $it")
                        requireBinding().tvAddress.text = it
                    }
                }

                launch {
                    viewModel.addressName.collectLatest {
                        Log.i("TEST", "addressName changed :: $it")
                        requireBinding().etName.hint = it
                        requireBinding().btnApply.isEnabled = it!!.isNotEmpty() || requireBinding().etName.hint.isNullOrEmpty().not()
                    }
                }

                launch {
                    viewModel.location.collectLatest { marker.position = it }
                }
            }
        }

        viewModel.map.observe(this) {
            it?.let {
                viewModel.setLocation(it.cameraPosition.target.latitude, it.cameraPosition.target.longitude)
                marker.map = it
            }
        }

    }



    override fun onMapReady(map: NaverMap) {
        viewModel.setMap(map)

        map.apply {
            uiSettings.apply {
                isCompassEnabled = false
                isScaleBarEnabled = false
                isIndoorLevelPickerEnabled = false
            }

            addOnCameraChangeListener{ reason, animated ->
                viewModel.setLocation(map.cameraPosition.target.latitude, map.cameraPosition.target.longitude)
                viewModel.setState(State.STATE_CAM_MOVE)
            }

            addOnCameraIdleListener {
                viewModel.setLocation(map.cameraPosition.target.latitude, map.cameraPosition.target.longitude)
                viewModel.setState(State.STATE_CAM_IDLE)
            }
        }
    }
}