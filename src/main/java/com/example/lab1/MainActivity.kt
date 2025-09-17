package com.example.lab1

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lab1.adapter.CarAdapter
import com.example.lab1.model.Car
import com.example.lab1.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Пример данных

        val cars = listOf(
            Car("Toyota Camry", "$25,000", android.R.drawable.ic_menu_camera),
            Car("Honda Civic", "$22,000", android.R.drawable.ic_menu_camera),
            Car("Ford Mustang", "$40,000", android.R.drawable.ic_menu_camera)
        )

        recyclerView.adapter = CarAdapter(cars) { car ->
            val intent = Intent(this, CarDetailActivity::class.java)
            intent.putExtra("carName", car.name)
            intent.putExtra("carPrice", car.price)
            startActivity(intent)
        }
    }
}