package com.alexsh3v.quicktask

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class MainAdapter(private val context: Context,
                  private val arrayOfRows: ArrayList<TaskRow>
                  ): RecyclerView.Adapter<MainAdapter.MyHolder>(), ItemTouchHelperAdapter {

    lateinit var touchHelper: ItemTouchHelper
    var onTaskClickListener: (TaskRow, Int) -> Unit = { _, _ -> }
    var onDragStarted: (MyHolder) -> Unit = {_ -> }

    companion object {
        const val LOG_TAG = "MainAdapter"
        const val TypeText = 0
        const val TypeTask = 1
    }

    abstract class MyHolder(context: Context, itemView: View): RecyclerView.ViewHolder(itemView) {
        abstract fun bind(row: Row)
    }

    class TaskHolder(private val context: Context, itemView: View): MyHolder(context, itemView) {
        private val taskNameTextView: TextView = itemView.findViewById(R.id.taskNameTextView)
        private val taskDescTextView: TextView = itemView.findViewById(R.id.taskDescTextView)
        private val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)
        private val dragImageView: ImageView = itemView.findViewById(R.id.dragImageView)
        private val editImageView: ImageView = itemView.findViewById(R.id.editImageView)

        val text: String
            get() {
                return "${taskNameTextView.text} -- ${taskDescTextView.text}"
            }

        fun onViewLongPressed(callback: () -> Unit) {
//            itemView.setOnLongClickListener {
//                callback()
//                return@setOnLongClickListener true
//            }
            editImageView.setOnClickListener { callback() }
        }

        @SuppressLint("ClickableViewAccessibility")
        fun onDragButtonTouchedDown(callback: () -> Unit) {
            dragImageView.setOnTouchListener { _, motionEvent ->
                if (MotionEvent.ACTION_DOWN == motionEvent.action) {
                    callback()
                }
                return@setOnTouchListener false
            }

        }

        override fun bind(row: Row) {
            val taskRow: TaskRow = row as TaskRow
            taskNameTextView.text = taskRow.name
            taskDescTextView.text = taskRow.description
            checkBox.isChecked = taskRow.isChecked
            checkBox.setOnCheckedChangeListener { _, state ->
                taskRow.isChecked = state

                if (state) { // Task Completed
                    taskNameTextView.setTextColor(context.getColor(R.color.task_name_completed))
                    taskDescTextView.setTextColor(context.getColor(R.color.task_description_completed))
                    taskNameTextView.paintFlags = taskNameTextView.paintFlags.or(Paint.STRIKE_THRU_TEXT_FLAG)
                    taskDescTextView.paintFlags = taskDescTextView.paintFlags.or(Paint.STRIKE_THRU_TEXT_FLAG)
                } else {
                    taskNameTextView.setTextColor(context.getColor(R.color.task_name))
                    taskDescTextView.setTextColor(context.getColor(R.color.task_description))
                    taskNameTextView.paintFlags = taskNameTextView.paintFlags.xor(Paint.STRIKE_THRU_TEXT_FLAG)
                    taskDescTextView.paintFlags = taskDescTextView.paintFlags.xor(Paint.STRIKE_THRU_TEXT_FLAG)
                }

            }
        }

    }

    class TextHolder(private val context: Context, private val itemView: View): MyHolder(context, itemView) {
        private val infoTextView: TextView = itemView.findViewById(R.id.infoTitleTextView)
        private val infoImageView: ImageView = itemView.findViewById(R.id.infoImageView)
        private val extraGapView: RelativeLayout = itemView.findViewById(R.id.extraGap)

        override fun bind(row: Row) {
            val textRow = row as TextRow
            infoTextView.text = textRow.s
            infoTextView.setTextColor(context.getColor(textRow.colorResource))
            infoImageView.setImageResource(textRow.drawableResource)
            extraGapView.visibility = when (textRow.extraGap) {
                true -> View.VISIBLE
                else -> View.GONE
            }
        }

    }

    // On every new type update 2 methods below
    override fun getItemViewType(position: Int): Int {
        return when (arrayOfRows[position]) {
            is TaskRow -> TypeTask
            else -> throw NoSuchFieldException("specify type above!")
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        Log.d(LOG_TAG, "create view holder with type -> $viewType")
        val inflater = LayoutInflater.from(context)
        return when (viewType) {
            TypeText -> TextHolder(context, inflater.inflate(R.layout.recycler_view_text_row,
                                                             parent, false))
            TypeTask -> TaskHolder(context, inflater.inflate(R.layout.recycler_view_task_row,
                                                             parent, false))
            else -> throw NoSuchFieldException("add layout here for type!")
        }
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        Log.d(LOG_TAG, "binding holder -> $holder at pos $position")
        val row = arrayOfRows[position]
        startAnimation(holder, position)
        bind(holder as TaskHolder, row, position)
    }

    private fun startAnimation(holder: MyHolder, position: Int) {
        val animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in)
        holder.itemView.startAnimation(animation)
    }

    private fun bind(holder: TaskHolder, row: TaskRow, position: Int) {
        holder.bind(row)
        // edit task when hold
        holder.onViewLongPressed {
            val i = holder.adapterPosition
            onTaskClickListener(arrayOfRows[i], i)
        }
        // drag task between others
        holder.onDragButtonTouchedDown { onDragStarted(holder) }
    }

    override fun getItemCount(): Int = arrayOfRows.size

    override fun onItemMove(
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder,
        fromPosition: Int,
        toPosition: Int
    ) {
        Log.d(LOG_TAG, "######### ${(viewHolder as TaskHolder).text}   --->   ${(target as TaskHolder).text}")
        Log.d(LOG_TAG, "######### $fromPosition $toPosition")
        val nextRow = arrayOfRows[toPosition]
        arrayOfRows.add(toPosition, arrayOfRows.removeAt(fromPosition))
//        val targetPosition = viewHolder.adapterPosition

//        Log.d(LOG_TAG, "$newPosition $fromPosition")
//        Log.d(LOG_TAG, "######### $newPosition $targetPosition")
        notifyItemMoved(fromPosition, toPosition)
        // edit task when hold
//        bind(viewHolder as TaskHolder, previousRow, toPosition)
//        bind(target as TaskHolder, nextRow, fromPosition)
//        (viewHolder as TaskHolder).onViewLongPressed { onTaskClickListener(previousRow, newPosition) }
//        (target as TaskHolder).onViewLongPressed { onTaskClickListener(nextRow, targetPosition) }

    }

    override fun onItemDismiss(position: Int) {
        arrayOfRows.removeAt(position)
        notifyItemRemoved(position)
    }

}