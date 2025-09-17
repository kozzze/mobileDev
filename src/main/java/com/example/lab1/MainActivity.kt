package com.example.lab1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lab1.adapter.CarAdapter
import com.example.lab1.model.Car

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate called")
        Toast.makeText(this, R.string.lifecycle_main_create, Toast.LENGTH_SHORT).show()

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val cars = listOf(
            Car("Toyota Camry", "$25,000", android.R.drawable.ic_menu_camera),
            Car("Honda Civic", "$22,000", android.R.drawable.ic_menu_camera),
            Car("Ford Mustang", "$40,000", android.R.drawable.ic_menu_camera)
        )

        recyclerView.adapter = CarAdapter(cars) { car ->
            Toast.makeText(this, getString(R.string.clicked_item, car.name), Toast.LENGTH_SHORT).show()
            val intent = Intent(this, CarDetailActivity::class.java).apply {
                putExtra("carName", car.name)
                putExtra("carPrice", car.price)
            }
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart called")
        Toast.makeText(this, R.string.lifecycle_main_start, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume called")
        Toast.makeText(this, R.string.lifecycle_main_resume, Toast.LENGTH_SHORT).show()
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause called")
        Toast.makeText(this, R.string.lifecycle_main_pause, Toast.LENGTH_SHORT).show()
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop called")
        Toast.makeText(this, R.string.lifecycle_main_stop, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy called")
        Toast.makeText(this, R.string.lifecycle_main_destroy, Toast.LENGTH_SHORT).show()
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "onRestart called")
        Toast.makeText(this, R.string.lifecycle_main_restart, Toast.LENGTH_SHORT).show()
    }
}