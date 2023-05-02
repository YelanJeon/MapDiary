package com.lanhee.mapdiary

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lanhee.mapdiary.utils.PrefModule

class SettingFragmentVM: ViewModel() {
    private val _isAutoDetectLocation = MutableLiveData<Boolean>()
    val isAutoDetectLocation = _isAutoDetectLocation as LiveData<Boolean>

    fun switchAutoDetectLocation() {
        _isAutoDetectLocation.value = _isAutoDetectLocation.value!!.not()
        PrefModule.setDetectLocationAuto(_isAutoDetectLocation.value!!)
    }

    fun loadSettings() {
        _isAutoDetectLocation.value = PrefModule.isDetectLocationAuto()
    }


    class Factory: ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingFragmentVM() as T
        }
    }
}