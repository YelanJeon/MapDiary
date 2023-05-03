package com.lanhee.mapdiary.feature.datepicker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.lanhee.mapdiary.feature.base.BaseDialogFragment
import com.lanhee.mapdiary.feature.datepicker.databinding.DlgDatepickerBinding

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