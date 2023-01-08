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
import androidx.recyclerview.widget.RecyclerView

class MainAdapter(private val context: Context,
                  private val arrayOfRows: ArrayList<Row>
                  ): RecyclerView.Adapter<MainAdapter.MyHolder>(), ItemTouchHelperAdapter {

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

        fun onViewLongPressed(callback: () -> Unit) {
            itemView.setOnLongClickListener {
                callback()
                return@setOnLongClickListener true
            }
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
            is TextRow -> TypeText
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
        holder.bind(row)
        when (holder) {
            is TaskHolder -> bind(holder as TaskHolder, row as TaskRow, position)
            is TextHolder -> bind(holder as TextHolder, row as TextRow, position)
        }
    }

    private fun startAnimation(holder: MyHolder, position: Int) {
        val animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in)
        holder.itemView.startAnimation(animation)
    }

    private fun bind(holder: TaskHolder, row: TaskRow, position: Int) {
        // edit task when hold
        holder.onViewLongPressed { onTaskClickListener(row, position) }
        // drag task between others
        holder.onDragButtonTouchedDown { onDragStarted(holder) }
    }

    private fun bind(textHolder: TextHolder, row: TextRow, position: Int) {

    }

    override fun getItemCount(): Int = arrayOfRows.size

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        val previousRow = arrayOfRows.removeAt(fromPosition)
        arrayOfRows.add(if (toPosition > fromPosition) toPosition - 1 else toPosition, previousRow)
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onItemMoveFromNowToLater(fromPosition: Int, toPosition: Int) {
        val firstRow = arrayOfRows[fromPosition]
        arrayOfRows[fromPosition] = arrayOfRows[toPosition]
        arrayOfRows[toPosition] = firstRow
        notifyItemChanged(toPosition)
        notifyItemChanged(fromPosition)
    }

    override fun onItemDismiss(position: Int) {
        arrayOfRows.removeAt(position)
        notifyItemRemoved(position)
    }

}