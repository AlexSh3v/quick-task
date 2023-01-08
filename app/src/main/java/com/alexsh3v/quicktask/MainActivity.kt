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
        const val APP_PREFERENCE = "myTasks"
        const val TASKS_PREF_STRING = "ArrayListOfRows"
    }

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var binding: ActivityMainBinding
    private var tasks: ArrayList<TaskRow> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE)
        loadTasks()
//
//        tasks.add(TaskRow("Task 1", "desc 1"))
//        tasks.add(TaskRow("Task 2", "desc 2"))
//        tasks.add(TaskRow("Task 3", "desc 3"))
//        tasks.add(TaskRow("Task 4", "desc 4"))
//
        dumpTasks()

        val mainAdapter = MainAdapter(this, tasks)
        mainAdapter.onTaskClickListener = { taskRow, index -> editTaskAddDialogFragment(taskRow, index) }
        binding.recyclerView.adapter = mainAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        // Attach Drag & Swipe helper
        val touchCallback = ItemTouchHelperCallback(mainAdapter)
        val touchHelper = ItemTouchHelper(touchCallback)
        touchHelper.attachToRecyclerView(binding.recyclerView)
        mainAdapter.onDragStarted = { viewHolder -> touchHelper.startDrag(viewHolder) }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.item_add -> {
                createTaskAddDialogFragment()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun createTaskAddDialogFragment() {
        val dialog = TaskAdderFragment.createInstance { taskName, taskDesc ->
            tasks.add(TaskRow(taskName, taskDesc))
            binding.recyclerView.adapter?.notifyItemChanged(tasks.size)
            dumpTasks()
        }
        dialog.show(supportFragmentManager, "DIALOG_ADD")
    }

    private fun editTaskAddDialogFragment(task: TaskRow, index: Int) {
        val dialog = TaskAdderFragment.createInstance(task.name, task.description) { taskName, taskDesc ->
            task.name = taskName
            task.description = taskDesc
            binding.recyclerView.adapter?.notifyItemChanged(index)
            dumpTasks()
        }
        dialog.show(supportFragmentManager, "DIALOG_ADD")
    }

    private fun dumpTasks() {
        val editor = sharedPreferences.edit()
        val gson = Gson()
//        val array = Array<Row>(arrayOfRows.size) {
//            return@Array arrayOfRows[it]
//        }
        val json = gson.toJson(DumpOfTasks(tasks))
        editor.putString(TASKS_PREF_STRING, json)
        editor.apply()
    }

    private fun clearTasks() {
        val editor = sharedPreferences.edit()
        editor.putString(TASKS_PREF_STRING, "")
        editor.apply()
    }

    private fun loadTasks() {
//        clearTasks()
        val gson = Gson()
        val json = sharedPreferences.getString(TASKS_PREF_STRING, "")
        if (json == "")
            return
        tasks = gson.fromJson(json, DumpOfTasks::class.java).tasks

        Log.d("LOAD_TASK", tasks.toString())
    }

}