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
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class MainAdapter(private val context: Context,
                  private val arrayOfRows: ArrayList<TaskRow>
                  ): RecyclerView.Adapter<MainAdapter.TaskViewHolder>(), ItemTouchHelperAdapter {

    lateinit var touchHelper: ItemTouchHelper
    var onTaskClickListener: (TaskRow, Int) -> Unit = { _, _ -> }
    var onDragStarted: (TaskViewHolder) -> Unit = { _ -> }
    var onTasksDataChanged: () -> Unit = {}

    companion object {
        const val LOG_TAG = "MainAdapter"
        const val TypeTask = 1
    }

    class TaskViewHolder(private val context: Context, itemView: View): RecyclerView.ViewHolder(itemView) {
        private val taskNameTextView: TextView = itemView.findViewById(R.id.taskNameTextView)
        private val taskDescTextView: TextView = itemView.findViewById(R.id.taskDescTextView)
        private val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)
        private val dragImageView: ImageView = itemView.findViewById(R.id.dragImageView)
        private val editImageView: ImageView = itemView.findViewById(R.id.editImageView)

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

        private fun setupCheckBox(taskRow: TaskRow, state: Boolean) {
            taskRow.isChecked = state
            val taskNameTextColorResource: Int
            val taskDescTextColorResource: Int
            val taskNamePaintFlags: Int
            val taskDescPaintFlags: Int

            if (state) { // Task Completed
                taskNameTextColorResource = R.color.task_name_completed
                taskDescTextColorResource = R.color.task_description_completed
                taskNamePaintFlags = taskNameTextView.paintFlags.or(Paint.STRIKE_THRU_TEXT_FLAG)
                taskDescPaintFlags = taskDescTextView.paintFlags.or(Paint.STRIKE_THRU_TEXT_FLAG)
            } else {
                taskNameTextColorResource = R.color.task_name
                taskDescTextColorResource = R.color.task_description
                taskNamePaintFlags = taskNameTextView.paintFlags.xor(Paint.STRIKE_THRU_TEXT_FLAG)
                taskDescPaintFlags = taskDescTextView.paintFlags.xor(Paint.STRIKE_THRU_TEXT_FLAG)
            }

            taskNameTextView.setTextColor(context.getColor(taskNameTextColorResource))
            taskDescTextView.setTextColor(context.getColor(taskDescTextColorResource))
            taskNameTextView.paintFlags = taskNamePaintFlags
            taskDescTextView.paintFlags = taskDescPaintFlags


        }

        fun bind(row: Row) {
            val taskRow: TaskRow = row as TaskRow
            taskNameTextView.text = taskRow.name
            taskDescTextView.text = taskRow.description
            checkBox.isChecked = taskRow.isChecked
            checkBox.setOnCheckedChangeListener { _, state -> setupCheckBox(taskRow, state) }
        }

    }

    // On every new type update 2 methods below
    override fun getItemViewType(position: Int): Int = TypeTask
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        Log.d(LOG_TAG, "create view holder with type -> $viewType")
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.recycler_view_task_row, parent, false)
        return TaskViewHolder(context, view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        Log.d(LOG_TAG, "binding holder -> $holder at pos $position")
        val row = arrayOfRows[position]
        startAnimation(holder, position)
        holder.bind(row)
        // edit task when hold
        holder.onViewLongPressed {
            val i = holder.adapterPosition
            onTaskClickListener(arrayOfRows[i], i)
        }
        // drag task between others
        holder.onDragButtonTouchedDown { onDragStarted(holder) }
    }

    private fun startAnimation(holder: TaskViewHolder, position: Int) {
        val animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in)
        holder.itemView.startAnimation(animation)
    }

    override fun getItemCount(): Int = arrayOfRows.size

    override fun onItemMoved(fromPosition: Int, toPosition: Int) {
        arrayOfRows.add(toPosition, arrayOfRows.removeAt(fromPosition))
        notifyItemMoved(fromPosition, toPosition)
        onTasksDataChanged()
    }

    override fun onItemSwiped(position: Int) {
        arrayOfRows.removeAt(position)
        notifyItemRemoved(position)
        onTasksDataChanged()
    }

}