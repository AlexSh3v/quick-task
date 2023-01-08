package com.alexsh3v.quicktask

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.alexsh3v.quicktask.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val arrayOfRows: ArrayList<Row> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        arrayOfRows.add(TaskRow("Task 1", "desc 1"))
        arrayOfRows.add(TaskRow("Task 2", "desc 2"))
        arrayOfRows.add(TaskRow("Task 3", "desc 3"))
        arrayOfRows.add(TaskRow("Task 4", "desc 4"))

        val mainAdapter = MainAdapter(this, arrayOfRows)
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
            arrayOfRows.add(TaskRow(taskName, taskDesc))
            binding.recyclerView.adapter?.notifyItemChanged(arrayOfRows.size)
        }
        dialog.show(supportFragmentManager, "DIALOG_ADD")
    }

    private fun editTaskAddDialogFragment(task: TaskRow, index: Int) {
        val dialog = TaskAdderFragment.createInstance(task.name, task.description) { taskName, taskDesc ->
            task.name = taskName
            task.description = taskDesc
            binding.recyclerView.adapter?.notifyItemChanged(index)
        }
        dialog.show(supportFragmentManager, "DIALOG_ADD")
    }

}