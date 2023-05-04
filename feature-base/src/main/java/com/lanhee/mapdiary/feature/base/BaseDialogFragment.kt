package com.lanhee.mapdiary.feature.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding

abstract class BaseDialogFragment<T: ViewBinding> : DialogFragment() {

    private var binding: T? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflateBinding(inflater)
        return binding!!.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.let {
            it.setLayout(getWindowWidth(), getWindowHeight())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    abstract fun inflateBinding(inflater: LayoutInflater): T

    open fun getWindowWidth() = (getScreenWidth(requireContext()) * 0.8f).toInt()
    open fun getWindowHeight() = WindowManager.LayoutParams.WRAP_CONTENT

    fun requireBinding(): T {
        return binding!!
    }




    fun getScreenWidth(context: Context) : Int{
        return context.resources.displayMetrics.widthPixels
    }

    fun getScreenHeight(context: Context) : Int{
        return context.resources.displayMetrics.heightPixels
    }

}
