package com.alexsh3v.quicktask

import android.content.Context
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MainAdapter(private val context: Context,
                  private val arrayOfRows: ArrayList<Row>
                  ): RecyclerView.Adapter<MainAdapter.MyHolder>() {

    var onTaskClickListener: (TaskRow, Int) -> Unit = { _, _ -> }

    companion object {
        const val LOG_TAG = "MainAdapter"
        const val TypeText = 0
        const val TypeTask = 1
    }

    abstract class MyHolder(context: Context, itemView: View): RecyclerView.ViewHolder(itemView) {
        abstract fun bind(row: Row)
    }

    class TaskHolder(private val context: Context, private val itemView: View): MyHolder(context, itemView) {
        private val taskNameTextView: TextView = itemView.findViewById(R.id.taskNameTextView)
        private val taskDescTextView: TextView = itemView.findViewById(R.id.taskDescTextView)
        private val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)

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
        holder.bind(row)
        if (holder is TaskHolder) // edit task when pressed
            holder.itemView.setOnLongClickListener {
                onTaskClickListener(row as TaskRow, position)
                return@setOnLongClickListener true
            }
    }

    override fun getItemCount(): Int = arrayOfRows.size

}