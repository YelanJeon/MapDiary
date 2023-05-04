package com.lanhee.mapdiary.feature.inputdialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.widget.addTextChangedListener
import com.lanhee.mapdiary.feature.base.BaseDialogFragment
import com.lanhee.mapdiary.feature.inputdialog.databinding.DlgInputBinding

class InputDialog : BaseDialogFragment<DlgInputBinding>() {
    var title = "수정하기"
    var hint = ""
    var isEnableEmpty = true
    var okClickEvent: ((String) -> Unit)? = null

    override fun inflateBinding(inflater: LayoutInflater): DlgInputBinding {
        return DlgInputBinding.inflate(inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireBinding().tvDlgTitle.text = title
        requireBinding().etInput.hint = hint
        requireBinding().btnApply.setOnClickListener {
            okClickEvent?.invoke(requireBinding().etInput.text.toString())
            dismiss()
        }
        requireBinding().etInput.addTextChangedListener {
            requireBinding().btnApply.isEnabled = isEnableEmpty || it!!.isNotEmpty()
        }
    }

}
