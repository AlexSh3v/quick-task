package com.alexsh3v.quicktask

import androidx.recyclerview.widget.RecyclerView

interface ItemTouchHelperAdapter {
    fun onItemMove(viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder,
                   fromPosition: Int, toPosition: Int)
    fun onItemDismiss(position: Int)
}
