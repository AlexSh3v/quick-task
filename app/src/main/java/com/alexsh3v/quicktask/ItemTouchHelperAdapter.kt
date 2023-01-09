package com.alexsh3v.quicktask

import androidx.recyclerview.widget.RecyclerView

interface ItemTouchHelperAdapter {
    fun onItemMoved(fromPosition: Int, toPosition: Int)
    fun onItemSwiped(position: Int)
}
