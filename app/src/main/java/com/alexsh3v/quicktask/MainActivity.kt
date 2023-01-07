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

        binding.recyclerView.adapter = MainAdapter(this, arrayOfRows)
        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.item_add -> {
                Toast.makeText(this, "Add task", Toast.LENGTH_SHORT).show()
            }
        }

        return super.onOptionsItemSelected(item)
    }

}