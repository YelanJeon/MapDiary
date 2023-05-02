package com.lanhee.mapdiary

import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lanhee.mapdiary.data.ActivitiesData
import com.lanhee.mapdiary.databinding.ItemActivitiesBinding
import com.lanhee.mapdiary.utils.MyColors
import com.lanhee.mapdiary.utils.Utils

class ActivitiesAdapter: ListAdapter<ActivitiesData, ActivitiesHolder>(object: DiffUtil.ItemCallback<ActivitiesData>() {
    override fun areItemsTheSame(oldItem: ActivitiesData, newItem: ActivitiesData): Boolean {
        Log.i("TEST", "areItemSame :: ${oldItem.idx} == ${newItem.idx}")
        return oldItem.idx == newItem.idx
    }

    override fun areContentsTheSame(oldItem: ActivitiesData, newItem: ActivitiesData): Boolean {
        Log.i("TEST", "areContentsSame :: ${oldItem.locationName} == ${newItem.locationName}")
        return oldItem == newItem
    }
}) {
    var isModifyMode = false
        set(value) {
          field = value
          notifyItemRangeChanged(0, itemCount)
        }

    var selectPosition = -1
        set(value) {
            val oldPosition = field;
            field = value
            if(oldPosition != -1) {
                notifyItemChanged(oldPosition)
            }
            if(field != -1) {
                notifyItemChanged(field)
            }
        }

    fun isSelectPosition(position: Int) = selectPosition == position

    var onItemClick: ((Int) -> Unit)? = null
    var onNameViewClickListener: ((ActivitiesData, Int) -> Unit)? = null
    var onDeleteViewClickListener: ((ActivitiesData, Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivitiesHolder {
        Log.i("TEST", "onCreateViewHolder")
        val holder = ActivitiesHolder(ItemActivitiesBinding.inflate(LayoutInflater.from(parent.context)))
        holder.itemView.layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, Utils.dimensionToPixel(holder.itemView.context, 60f).toInt())
        holder.itemView.setOnClickListener {
            selectPosition = if(isSelectPosition(holder.layoutPosition)) -1 else holder.layoutPosition
            onItemClick?.invoke(holder.layoutPosition)
        }
        holder.binding.tvContent.setOnClickListener {
            onNameViewClickListener?.invoke(getItem(holder.layoutPosition), holder.layoutPosition)
        }
        holder.binding.btnDelete.setOnClickListener {
            onDeleteViewClickListener?.invoke(getItem(holder.layoutPosition), holder.layoutPosition)
        }
        return holder
    }

    override fun onBindViewHolder(holder: ActivitiesHolder, position: Int) {
        Log.i("TEST", "onBindViewHolder")
        holder.bind(getItem(position), isModifyMode, isSelectPosition(position))
    }
}
class ActivitiesHolder(val binding: ItemActivitiesBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(data: ActivitiesData, isModifyMode: Boolean, isSelected: Boolean) {
        if(isModifyMode) {
            binding.root.isClickable = false
            binding.btnDelete.visibility = View.VISIBLE
            binding.btnReorder.visibility = View.VISIBLE
            binding.tvContent.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_edit, 0)
            binding.tvContent.isClickable = true
            binding.tvNumber.backgroundTintList = ColorStateList.valueOf(Color.parseColor(MyColors.PRIMARY))
        }else{
            binding.root.isClickable = true
            binding.btnDelete.visibility = View.GONE
            binding.btnReorder.visibility = View.GONE
            binding.tvContent.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            binding.tvContent.isClickable = false
            if(isSelected) {
                binding.tvNumber.backgroundTintList = ColorStateList.valueOf(Color.parseColor(MyColors.ACCENT))
            }else{
                binding.tvNumber.backgroundTintList = ColorStateList.valueOf(Color.parseColor(MyColors.PRIMARY))
            }
        }
        binding.tvNumber.text = (layoutPosition+1).toString()
        binding.tvContent.text = data.locationName
    }

}