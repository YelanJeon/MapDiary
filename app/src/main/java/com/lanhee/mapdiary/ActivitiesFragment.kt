package com.lanhee.mapdiary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lanhee.mapdiary.data.ActivitiesData
import com.lanhee.mapdiary.databinding.FragmentActivitiesBinding
import com.lanhee.mapdiary.dialog.AddLocationDialog
import com.lanhee.mapdiary.dialog.InputDialog

class ActivitiesFragment: Fragment() {

    var binding: FragmentActivitiesBinding? = null

    val recyclerView by lazy { binding!!.recycler }
    val mAdapter by lazy { ActiviesAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentActivitiesBinding.inflate(inflater)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
            addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))
        }

        mAdapter.apply {
            onNameViewClickListener = object: OnNameViewClicked {
                override fun onClick(data: ActivitiesData, view: View, position: Int) {
                    InputDialog().apply {
                        title = "스탬프 내용 수정"
                        hint = data.locationAddress
                        okClickEvent = { newName ->
                            data.locationName = newName
                            notifyItemChanged(position)
                        }
                    }.show(parentFragmentManager, "input")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    fun onFABClick() {
        AddLocationDialog().apply {
            onAddLocation = { newData ->
                val list = MutableList(mAdapter.itemCount) { mAdapter.currentList[it] }
                list.add(newData)
                mAdapter.submitList(list)
            }
        }.show(parentFragmentManager, "addLocation")

    }
}