package com.lanhee.mapdiary

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.lanhee.mapdiary.data.ActivitiesData
import com.lanhee.mapdiary.databinding.FragmentActivitiesBinding
import com.lanhee.mapdiary.dialog.AddLocationDialog
import com.lanhee.mapdiary.dialog.DatePickerDialog
import com.lanhee.mapdiary.dialog.InputDialog
import com.lanhee.mapdiary.utils.ActivitiesFragmentVM
import com.lanhee.mapdiary.utils.Utils
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import java.util.Date
import java.util.Calendar

class ActivitiesFragment: Fragment(), OnMapReadyCallback {

    private var binding: FragmentActivitiesBinding? = null
    private val viewModel by lazy { ViewModelProvider(this, ActivitiesFragmentVM.Factory())[ActivitiesFragmentVM::class.java] }

    private val recyclerView by lazy { binding!!.recycler }
    private val mAdapter by lazy { ActiviesAdapter() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentActivitiesBinding.inflate(inflater)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.date.observe(viewLifecycleOwner) {
            binding!!.tvDate.text = Utils.formatDateForText(viewModel.date.value!!)
            viewModel.loadActivities()
        }

        viewModel.items.observe(viewLifecycleOwner) {
            //리스트 변경 시
            mAdapter.currentList.forEach { data ->
                Log.i("TEST", "BEFORE LIST : ${data.idx}")
            }
            mAdapter.submitList(it)
            mAdapter.currentList.forEach { data ->
                Log.i("TEST", "AFTER LIST : ${data.idx}")
            }
            binding!!.tvStamp.text = "스탬프 ${it.size}개"
            recyclerView.scrollTo(0, 0)
        }

        viewModel.isModifyMode.observe(viewLifecycleOwner) {
            //편집하기/완료 전환 시
            binding!!.btnEdit.text = if(it) "완료" else "편집하기"
            mAdapter.isModifyMode = it
            (requireActivity() as IncludeFABActivity).setFABVisibility(if(it) View.GONE else View.VISIBLE)
        }

        viewModel.markers.observe(viewLifecycleOwner) {
            //마커 리스트 변경 시
            viewModel.drawAllPolyLine()
        }

        viewModel.map.observe(viewLifecycleOwner) {
            setMapUISetting(it)
        }



        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
            addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))
            addOnScrollListener(object: OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val manager = recyclerView.layoutManager as LinearLayoutManager
                    if(manager.findFirstCompletelyVisibleItemPosition() == 0
                        && manager.findLastCompletelyVisibleItemPosition() != mAdapter.itemCount
                    ) {
                        //최상단 스크롤한 경우
                        Log.i("TEST", "최상단임")
                        viewModel.showAllMarkers()
                    }
                }
            })
        }

        mAdapter.apply {
            onItemClick = { position ->
                viewModel.focusToMarker(viewModel.markers.value!![position])
            }
            onNameViewClickListener = { data, position ->
                InputDialog().apply {
                    title = "스탬프 내용 수정"
                    hint = data.locationAddress
                    isEnableEmpty = true
                    okClickEvent = { newName ->
                        viewModel.updateItemLocationName(newName, position)
                    }
                }.show(parentFragmentManager, "input")
            }
            onDeleteViewClickListener = { item, position ->
                viewModel.removeItem(item)
                viewModel.removeMarker(position)
            }
        }

        binding!!.apply {
            btnEdit.setOnClickListener {
                if(viewModel.isModifyMode.value == null) {
                    viewModel.setModifyMode(true)
                }else{
                    viewModel.setModifyMode(viewModel.isModifyMode.value!!.not())
                }

            }

            btnDate.setOnClickListener {
                DatePickerDialog().apply {
                    onDatePick = { year, month, day ->
                        val calendar = Calendar.getInstance()
                        calendar.set(Calendar.YEAR, year)
                        calendar.set(Calendar.MONTH, month)
                        calendar.set(Calendar.DAY_OF_MONTH, day)
                        viewModel.setDate(calendar.time)
                    }
                }.show(parentFragmentManager, "datePicker")
            }
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as MapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setMapUISetting(map: NaverMap) {
        map.uiSettings.apply {
            isCompassEnabled = false
            isScaleBarEnabled = false
            isZoomControlEnabled = false
            isIndoorLevelPickerEnabled = false
            setAllGesturesEnabled(false)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    fun onFABClick() {
        AddLocationDialog().apply {
            onAddLocation = { newData ->
                viewModel.addItem(newData)
                viewModel.addMarker(newData.locationLat, newData.locationLng)
                viewModel.focusToMarker(viewModel.markers.value!![viewModel.markers.value!!.size-1])
            }
        }.show(parentFragmentManager, "addLocation")

    }

    override fun onMapReady(map: NaverMap) {
        viewModel.setMap(map)

        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        viewModel.setDate(Date(System.currentTimeMillis()))
    }
}