package com.lanhee.mapdiary.feature.activities

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lanhee.mapdiary.feature.activities.databinding.ItemActivitiesBinding
import com.lanhee.mapdiary.feature.base.MyColors
import com.lanhee.mapdiary.data.ActivitiesData
import java.util.Collections

class ActivitiesAdapter: ItemMoveCallback.ItemTouchHelperContract, ListAdapter<ActivitiesData, ActivitiesHolder>(object: DiffUtil.ItemCallback<ActivitiesData>() {
    override fun areItemsTheSame(oldItem: ActivitiesData, newItem: ActivitiesData): Boolean {
        return (oldItem.idx == newItem.idx) && (oldItem.order == newItem.order)
    }

    override fun areContentsTheSame(oldItem: ActivitiesData, newItem: ActivitiesData): Boolean {
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
    var dragListener: ItemMoveCallback.StartDragListener? = null
    var moveListener: (() -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivitiesHolder {
        val holder = ActivitiesHolder(ItemActivitiesBinding.inflate(LayoutInflater.from(parent.context)))
        holder.itemView.layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, dimensionToPixel(holder.itemView.context, 60f).toInt())
        holder.itemView.setOnClickListener {
            selectPosition = if(isSelectPosition(holder.layoutPosition)) -1 else holder.layoutPosition
            onItemClick?.invoke(holder.layoutPosition)
        }
        holder.binding.tvContent.setOnClickListener {
            onNameViewClickListener?.invoke(getItem(holder.layoutPosition), holder.layoutPosition)
        }
        holder.binding.btnDelete.setOnClickListener {
            notifyItemRangeChanged(0, itemCount)
            onDeleteViewClickListener?.invoke(getItem(holder.layoutPosition), holder.layoutPosition)
        }
        holder.binding.btnReorder.setOnTouchListener { v, event ->
            if (event?.action == MotionEvent.ACTION_DOWN) {
                dragListener?.requestDrag(holder)
            }
            false
        }
        return holder
    }

    override fun onBindViewHolder(holder: ActivitiesHolder, position: Int) {
        holder.bind(getItem(position), isModifyMode, isSelectPosition(position))
    }

    override fun onRowMoved(fromPosition: Int, toPosition: Int) {
        val temp = List(currentList.size) { currentList[it] }

        if(fromPosition<toPosition){
            for(i in fromPosition until toPosition) {
                Collections.swap(temp, i, i+1)
            }
        }else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(temp, i, i - 1)
            }
        }
        submitList(temp)
    }

    override fun onRowSelected(viewHolder: ActivitiesHolder) {
        viewHolder.binding.root.alpha = 0.5f
    }

    override fun onRowClear(viewHolder: ActivitiesHolder) {
        viewHolder.binding.root.alpha = 1f
        notifyItemRangeChanged(0, itemCount)
        moveListener?.invoke()
    }

    fun dimensionToPixel(context: Context, dp: Float) : Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)
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
        binding.tvNumber.text = (data.order+1).toString()
        binding.tvContent.text = data.locationName
    }
}