package com.alexsh3v.quicktask

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alexsh3v.quicktask.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val arrayOfRows: ArrayList<Row> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        arrayOfRows.add(TextRow("Do Now", R.drawable.ic_clock, R.color.doNow))
        arrayOfRows.add(TaskRow("Do something", "Ha-ha"))
        arrayOfRows.add(TextRow("Do Later", R.drawable.ic_tasks, R.color.doLater).showExtraGap())
        arrayOfRows.add(TaskRow("Nothing", "lol"))

        binding.recyclerView.adapter = MainAdapter(this, arrayOfRows).also {
            it.onTaskClickListener = { taskRow, index -> editTaskAddDialogFragment(taskRow, index) }
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

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