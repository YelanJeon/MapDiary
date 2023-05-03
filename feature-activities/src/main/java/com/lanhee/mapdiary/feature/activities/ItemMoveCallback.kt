package com.lanhee.mapdiary.feature.activities

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class ItemMoveCallback(val adapter: ItemTouchHelperContract) : ItemTouchHelper.Callback(){

    override fun isLongPressDragEnabled(): Boolean {
        return false
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return false
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        return makeMovementFlags(dragFlags, 0)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder ): Boolean {
        adapter.onRowMoved(viewHolder.layoutPosition, target.layoutPosition)
        return true
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if(actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if(viewHolder is com.lanhee.mapdiary.feature.activities.ActivitiesHolder) {
                adapter.onRowSelected(viewHolder)
            }
        }
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        if(viewHolder is com.lanhee.mapdiary.feature.activities.ActivitiesHolder) {
            adapter.onRowClear(viewHolder)
        }
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

    }

    interface ItemTouchHelperContract {
        fun onRowMoved(fromPosition: Int, toPosition: Int)
        fun onRowSelected(viewHolder: com.lanhee.mapdiary.feature.activities.ActivitiesHolder)
        fun onRowClear(viewHolder: com.lanhee.mapdiary.feature.activities.ActivitiesHolder)
    }

    interface StartDragListener {
        fun requestDrag(viewHolder: com.lanhee.mapdiary.feature.activities.ActivitiesHolder)
    }
}