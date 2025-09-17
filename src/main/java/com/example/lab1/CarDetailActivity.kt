package com.example.lab1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import com.example.lab1.R

class CarDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.car_detail)

        val carName = intent.getStringExtra("carName") ?: "Unknown"
        val carPrice = intent.getStringExtra("carPrice") ?: "Unknown"

        findViewById<TextView>(R.id.carName).text = carName
        findViewById<TextView>(R.id.carPrice).text = carPrice
    }
}