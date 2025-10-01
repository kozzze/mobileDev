package com.example.lab1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lab1.adapter.CarAdapter
import com.example.lab1.db.CarRepository
import com.example.lab1.db.UserRepository
import com.example.lab1.model.Car
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var carRepo: CarRepository
    private lateinit var userRepo: UserRepository
    private val io = Executors.newSingleThreadExecutor()

    private lateinit var rv: RecyclerView
    private var adapter: CarAdapter? = null

    private lateinit var userNameText: TextView
    private lateinit var logoutButton: Button
    private lateinit var favoritesButton: Button
    private lateinit var addCarButton: Button

    private var showingFavorites = false
    private var currentUserId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        carRepo = CarRepository(this)
        userRepo = UserRepository(this)

        // проверка входа
        val name = AuthManager.getUserName(this)
        if (name == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        } else {
            Session.currentUserName = name
            Session.isAdmin = AuthManager.isAdmin(this)
        }

        val user = userRepo.getByName(Session.currentUserName!!)!!
        currentUserId = user.id

        // UI
        userNameText = findViewById(R.id.userNameText)
        logoutButton = findViewById(R.id.logoutButton)
        favoritesButton = findViewById(R.id.favoritesButton)
        addCarButton = findViewById(R.id.addCarButton)

        userNameText.text = "Пользователь: ${user.name}"

        if (Session.isAdmin) {
            addCarButton.visibility = android.view.View.VISIBLE
        }

        rv = findViewById(R.id.recyclerView)
        rv.layoutManager = LinearLayoutManager(this)

        loadAllCars()

        // кнопка выйти
        logoutButton.setOnClickListener {
            AuthManager.clearUser(this)
            Session.currentUserName = null
            Session.isAdmin = false
            Toast.makeText(this, "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // кнопка избранное
        favoritesButton.setOnClickListener {
            if (showingFavorites) {
                loadAllCars()
                favoritesButton.text = "Избранное"
                showingFavorites = false
            } else {
                loadFavorites()
                favoritesButton.text = "Все машины"
                showingFavorites = true
            }
        }

        // кнопка добавить авто (для админа)
        addCarButton.setOnClickListener {
            showAddCarDialog()
        }
    }

    private fun loadAllCars() {
        io.execute {
            val cars = carRepo.getAll()
            runOnUiThread {
                adapter = CarAdapter(cars) { car: Car ->
                    val i = Intent(this, CarDetailActivity::class.java)
                    i.putExtra("car_id", car.id)
                    startActivity(i)
                }
                rv.adapter = adapter
            }
        }
    }

    private fun loadFavorites() {
        io.execute {
            val favCars = carRepo.getFavorites(currentUserId)
            runOnUiThread {
                adapter = CarAdapter(favCars) { car: Car ->
                    val i = Intent(this, CarDetailActivity::class.java)
                    i.putExtra("car_id", car.id)
                    startActivity(i)
                }
                rv.adapter = adapter
                Toast.makeText(this, "Показаны избранные машины", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showAddCarDialog() {
        val brandEt = EditText(this).apply { hint = "Марка" }
        val modelEt = EditText(this).apply { hint = "Модель" }
        val yearEt  = EditText(this).apply { hint = "Год"; inputType = android.text.InputType.TYPE_CLASS_NUMBER }
        val bodyEt  = EditText(this).apply { hint = "Кузов" }
        val priceEt = EditText(this).apply { hint = "Цена"; inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 16, 32, 0)
            addView(brandEt)
            addView(modelEt)
            addView(yearEt)
            addView(bodyEt)
            addView(priceEt)
        }

        AlertDialog.Builder(this)
            .setTitle("Добавить машину")
            .setView(layout)
            .setPositiveButton("Сохранить") { _, _ ->
                val brand = brandEt.text.toString().trim()
                val model = modelEt.text.toString().trim()
                val year  = yearEt.text.toString().toIntOrNull()
                val body  = bodyEt.text.toString().trim().ifBlank { null }
                val price = priceEt.text.toString().toDoubleOrNull()

                if (brand.isEmpty() || model.isEmpty() || year == null || price == null) {
                    Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                io.execute {
                    carRepo.insert(Car(brand = brand, model = model, year = year, body = body, price = price))
                    val cars = carRepo.getAll()
                    runOnUiThread {
                        adapter = CarAdapter(cars) { car: Car ->
                            val i = Intent(this, CarDetailActivity::class.java)
                            i.putExtra("car_id", car.id)
                            startActivity(i)
                        }
                        rv.adapter = adapter
                        Toast.makeText(this, "Машина добавлена", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
}
