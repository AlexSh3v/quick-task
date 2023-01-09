package com.alexsh3v.quicktask

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.alexsh3v.quicktask.databinding.ActivityMainBinding
import com.google.gson.Gson
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    companion object {
        const val LOG_TAG = "MainActivity"
        const val APP_PREFERENCE = "myTasks"
        const val TASKS_PREF_STRING = "ArrayListOfRows"
        const val DIALOG_ADD_TASK = "DIALOG_ADD"
    }

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var binding: ActivityMainBinding
    private var tasks: ArrayList<TaskRow> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load tasks
        sharedPreferences = getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE)
        loadTasksFromSharedPreference()

        // Create adapter
        val mainAdapter = MainAdapter(this, tasks)
        mainAdapter.onTaskClickListener = { taskRow, index -> editTaskAddDialogFragment(taskRow, index) }
        mainAdapter.onTasksDataChanged = { saveTasksToSharedPreference() }

        // View layout
        binding.recyclerView.adapter = mainAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        // Attach Drag & Swipe helper
        val touchCallback = ItemTouchHelperCallback(mainAdapter)
        val touchHelper = ItemTouchHelper(touchCallback)
        mainAdapter.touchHelper = touchHelper
        touchHelper.attachToRecyclerView(binding.recyclerView)
        mainAdapter.onDragStarted = { viewHolder -> touchHelper.startDrag(viewHolder) }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle option buttons

        when (item.itemId) {
            // Add task button
            R.id.item_add -> {
                createTaskAddDialogFragment()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun createTaskAddDialogFragment() {
        Log.d(LOG_TAG, "CREATE TASK")
        val dialog = TaskAdderFragment.createInstance { taskName, taskDesc ->
            tasks.add(TaskRow(taskName, taskDesc))
            binding.recyclerView.adapter?.notifyItemChanged(tasks.size)
            saveTasksToSharedPreference()
        }
        dialog.show(supportFragmentManager, DIALOG_ADD_TASK)
    }

    private fun editTaskAddDialogFragment(task: TaskRow, index: Int) {
        // Edit task with existing task name and description
        Log.d(LOG_TAG, "EDIT TASK: Task(name=${task.name}, desc=${task.description}) at index=$index")
        val dialog = TaskAdderFragment.createInstance(task.name, task.description) { taskName, taskDesc ->
            task.name = taskName
            task.description = taskDesc
            binding.recyclerView.adapter?.notifyItemChanged(index)
            saveTasksToSharedPreference()
        }
        dialog.show(supportFragmentManager, DIALOG_ADD_TASK)
    }

    private fun saveTasksToSharedPreference() {
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(DumpOfTasks(tasks))
        editor.putString(TASKS_PREF_STRING, json)
        editor.apply()
    }

    private fun clearTasks() {
        val editor = sharedPreferences.edit()
        editor.putString(TASKS_PREF_STRING, "")
        editor.apply()
    }

    private fun loadTasksFromSharedPreference() {
        val gson = Gson()
        val json = sharedPreferences.getString(TASKS_PREF_STRING, "")

        if (json == "") // skip if string isn't found
            return
        tasks = gson.fromJson(json, DumpOfTasks::class.java).tasks

        Log.d(LOG_TAG, "Loaded tasks array: ${tasks.toString()}")
    }

}