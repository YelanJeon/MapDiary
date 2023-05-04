package com.lanhee.mapdiary.feature.setting

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lanhee.mapdiary.feature.setting.databinding.FragmentSettingBinding


class SettingFragment: Fragment() {

    var binding: FragmentSettingBinding? = null
    private val viewModel by lazy { ViewModelProvider(this,
        SettingFragmentVM.Factory()
    )[SettingFragmentVM::class.java] }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(inflater)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding!!.btnSettingAuto.requestFocus()
        binding!!.btnSettingAuto.setOnClickListener {
            viewModel.switchAutoDetectLocation()
        }

        viewModel.isAutoDetectLocation.observe(viewLifecycleOwner) {
            binding!!.scSettingAuto.isChecked = it
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.loadSettings()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}