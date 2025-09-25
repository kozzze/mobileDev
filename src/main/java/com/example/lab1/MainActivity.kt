package com.example.lab1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lab1.adapter.CarAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private lateinit var db: AppDatabase
    private lateinit var carDao: CarDao
    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate called")


        db = AppDatabase.getDatabase(this)
        carDao = db.carDao()


        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        if (recyclerView == null) {
            Log.e(TAG, "RecyclerView not found!")
            return
        }
        recyclerView.layoutManager = LinearLayoutManager(this)

        scope.launch {
            loadDataFromCsv()
            val cars = withContext(Dispatchers.IO) { carDao.getAllCars() }
            if (cars.isEmpty()) {
                Log.w(TAG, "No cars loaded from DB, using test data")
                val testCars = listOf(
                    Car(brand = "Toyota Camry", model = "Sedan", price = "$25,000", year = 2023),
                    Car(brand = "Honda Civic", model = "Coupe", price = "$22,000", year = 2022)
                )
                recyclerView.adapter = CarAdapter(testCars) { car ->
                    val intent = Intent(this@MainActivity, CarDetailActivity::class.java).apply {
                        putExtra("carName", "${car.brand} ${car.model}")
                        putExtra("carPrice", car.price)
                    }
                    startActivity(intent)
                }
            } else {
                recyclerView.adapter = CarAdapter(cars) { car ->
                    val intent = Intent(this@MainActivity, CarDetailActivity::class.java).apply {
                        putExtra("carName", "${car.brand} ${car.model}")
                        putExtra("carPrice", car.price)
                    }
                    startActivity(intent)
                }
            }
        }
    }

    private suspend fun loadDataFromCsv() {
        withContext(Dispatchers.IO) {
            try {
                val inputStream = assets.open("cars_data.csv")
                val reader = BufferedReader(InputStreamReader(inputStream))
                reader.readLine()
                reader.forEachLine { line ->
                    val parts = line.split(",").map { it.trim() }
                    if (parts.size >= 6) {
                        val brand = parts[1]
                        val model = parts[2]
                        val price = parts[3]
                        val year = parts[5].toIntOrNull() ?: 0
                        if (brand.isNotEmpty() && model.isNotEmpty() && price.isNotEmpty() && year > 0) {
                            val car = Car(brand = brand, model = model, price = price, year = year)
                            carDao.insert(car)
                        }
                    }
                }
                reader.close()
                Log.d(TAG, "Data loaded from cars_data.csv")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading CSV: ${e.message}")
            }
        }
    }
}