package com.lanhee.mapdiary

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lanhee.mapdiary.databinding.FragmentSettingBinding


class SettingFragment: Fragment() {

    var binding: FragmentSettingBinding? = null
    private val viewModel by lazy { ViewModelProvider(this, SettingFragmentVM.Factory())[SettingFragmentVM::class.java] }

    lateinit var serviceIntent: Intent

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
            if(viewModel.isAutoDetectLocation.value!!) {
                //startService
                startDetectLocationService()
            }else{
                //stopService
                stopDetectLocationService()
            }
        }

        viewModel.isAutoDetectLocation.observe(viewLifecycleOwner) {
            binding!!.scSettingAuto.isChecked = it
        }

    }

    private fun startDetectLocationService() {
        serviceIntent = Intent(requireContext(), DetectLocationService::class.java)
        requireActivity().startService(serviceIntent)

        //notification
        val manager = NotificationManagerCompat.from(requireContext())

        val channelName = "mapDiary"
        val channerlId = "mapDiary_0"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = channelName
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channerlId, name, importance)
            channel.description = "this is $channelName notification channel"
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            manager.createNotificationChannel(channel)
        }

        val noti = NotificationCompat.Builder(requireContext(), channerlId)
        noti.setContentTitle("서비스 실행 중")
        noti.setContentText("서비스가 실행 중입니다!")
        noti.setSmallIcon(R.drawable.ic_launcher_foreground)

        val deleteIntent = Intent(requireContext(), DetectLocationService::class.java)
        deleteIntent.putExtra("stop", true)
        val pendingIntent = PendingIntent.getService(
            requireContext(),
            0,
            deleteIntent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        noti.setDeleteIntent(pendingIntent)
        noti.setAutoCancel(false)

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        manager.notify(0, noti.build())
    }

    private fun stopDetectLocationService() {
        requireActivity().stopService(Intent(requireContext(), DetectLocationService::class.java))

        val manager = NotificationManagerCompat.from(requireContext())
        manager.cancel(0)
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