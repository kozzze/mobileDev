package com.example.lab1

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CarDetailActivity : AppCompatActivity() {
    private val TAG = "CarDetailActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.car_detail)
        Log.d(TAG, "onCreate called")

        val carName = intent.getStringExtra("carName") ?: "Unknown"
        val carPrice = intent.getStringExtra("carPrice") ?: "Unknown"

        findViewById<TextView>(R.id.carName).text = carName
        findViewById<TextView>(R.id.carPrice).text = carPrice

        findViewById<Button>(R.id.addDeskButton).setOnClickListener {
            Toast.makeText(this, getString(R.string.add_description_clicked, carName), Toast.LENGTH_SHORT).show()
            // логика для "Добавить описание"
        }

        findViewById<Button>(R.id.backButton).setOnClickListener {
            Toast.makeText(this, R.string.return_to_main, Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy called")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "onRestart called")
    }
}