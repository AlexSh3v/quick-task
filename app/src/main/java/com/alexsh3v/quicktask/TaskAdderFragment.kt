package com.alexsh3v.quicktask

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.fragment.app.DialogFragment

class TaskAdderFragment: DialogFragment() {

    private lateinit var onPressOk: (String, String) -> Unit
    private var presetStrings: () -> Unit = {}

    private lateinit var taskNameEditText: EditText
    private lateinit var taskDescEditText: EditText

    companion object {
        fun createInstance(presetTaskName: String, presetTaskDesc: String,
                           onPressOk: (String, String) -> Unit): TaskAdderFragment {
            val taskAdderFragment = TaskAdderFragment()
            taskAdderFragment.onPressOk = onPressOk
            taskAdderFragment.presetStrings = {
                taskAdderFragment.taskNameEditText.setText(presetTaskName)
                taskAdderFragment.taskDescEditText.setText(presetTaskDesc)
            }
            return taskAdderFragment
        }
        fun createInstance(onPressOk: (String, String) -> Unit): TaskAdderFragment {
            val taskAdderFragment = TaskAdderFragment()
            taskAdderFragment.onPressOk = onPressOk
            return taskAdderFragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_task_adder, null)

        taskNameEditText = view.findViewById<EditText>(R.id.taskNameEditText)
        taskDescEditText = view.findViewById<EditText>(R.id.taskDescEditText)
        presetStrings()

        return AlertDialog.Builder(activity)
            .setView(view)
            .setTitle("Create Task")
            .setPositiveButton(android.R.string.ok, DialogInterface.OnClickListener { _, _ ->
                val taskName = taskNameEditText.text.toString()
                val taskDescription = taskDescEditText.text.toString()
                onPressOk(taskName, taskDescription)
            })
            .setNegativeButton(android.R.string.cancel) { _, _ -> }
            .show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}