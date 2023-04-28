package com.lanhee.mapdiary.dialog

import android.app.DatePickerDialog
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
import com.lanhee.mapdiary.databinding.DlgDatepickerBinding
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

class DatePickerDialog: BaseDialogFragment<DlgDatepickerBinding>() {

    var onDatePick: ((Int, Int, Int) -> Unit)? = null

    val datePicker by lazy { requireBinding().datePicker }

    override fun inflateBinding(inflater: LayoutInflater): DlgDatepickerBinding {
        return DlgDatepickerBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val onDismiss: ((View) -> Unit) = { view ->
            if(view.id == requireBinding().btnApply.id) {
                onDatePick?.invoke(datePicker.year, datePicker.month, datePicker.dayOfMonth)
            }
            dismiss()
        }
        requireBinding().btnApply.setOnClickListener(onDismiss)

        requireBinding().btnClose.setOnClickListener(onDismiss)

    }
}