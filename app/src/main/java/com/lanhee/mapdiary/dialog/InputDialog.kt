package com.lanhee.mapdiary.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.lanhee.mapdiary.databinding.DlgInputBinding

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

        requireBinding().run {
            tvDlgTitle.text = title
            etInput.hint = hint
            btnApply.setOnClickListener { okClickEvent?.invoke(etInput.text.toString()) }
            etInput.addTextChangedListener {
                btnApply.isEnabled = isEnableEmpty || it!!.isNotEmpty()
            }
        }
    }

}
