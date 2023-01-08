package com.alexsh3v.quicktask

import androidx.recyclerview.widget.RecyclerView

interface ItemTouchHelperAdapter {
    fun onItemMove(fromPosition: Int, toPosition: Int)
    fun onItemDismiss(position: Int)
    fun onItemMoveFromNowToLater(fromPosition: Int, toPosition: Int)
}
