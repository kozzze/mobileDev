package com.example.lab1

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
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

    private var isAdmin: Boolean = false
    private val MENU_ADD_CAR = 1001
    private val MENU_LOGOUT = 1002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // защита: если не логин — в LoginActivity
        val name = AuthManager.getUserName(this)
        if (name == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        } else {
            Session.currentUserName = name
            Session.isAdmin = AuthManager.isAdmin(this)
        }
        isAdmin = Session.isAdmin

        carRepo = CarRepository(this)
        userRepo = UserRepository(this)

        rv = findViewById(R.id.recyclerView)
        rv.layoutManager = LinearLayoutManager(this)

        io.execute {
            if (carRepo.getAll().isEmpty()) {
                listOf(
                    Car(brand = "Toyota", model = "Camry", year = 2020, body = "Sedan", price = 25000.0),
                    Car(brand = "Honda",  model = "Civic", year = 2019, body = "Coupe", price = 22000.0),
                    Car(brand = "VW",     model = "Golf",  year = 2018, body = "Hatch", price = 9800.0)
                ).forEach { carRepo.insert(it) }
            }
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Создаём пункты меню программно
        if (isAdmin) {
            val add = menu.add(Menu.NONE, MENU_ADD_CAR, Menu.NONE, "Добавить авто")
            add.setIcon(android.R.drawable.ic_input_add)
            add.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        }
        val logout = menu.add(Menu.NONE, MENU_LOGOUT, Menu.NONE, "Выход")
        logout.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            MENU_ADD_CAR -> { showAddCarDialog(); true }
            MENU_LOGOUT -> { doLogout(); true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showAddCarDialog() {
        val brandEt = EditText(this).apply { hint = "Марка (brand)" }
        val modelEt = EditText(this).apply { hint = "Модель (model)" }
        val yearEt  = EditText(this).apply {
            hint = "Год (year)"; inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }
        val bodyEt  = EditText(this).apply { hint = "Кузов (body)" }
        val priceEt = EditText(this).apply {
            hint = "Цена (price)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 16, 32, 0)
            addView(brandEt); addView(modelEt); addView(yearEt); addView(bodyEt); addView(priceEt)
        }

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Добавить авто")
            .setView(layout)
            .setPositiveButton("Сохранить") { _, _ ->
                val brand = brandEt.text.toString().trim()
                val model = modelEt.text.toString().trim()
                val year  = yearEt.text.toString().toIntOrNull()
                val body  = bodyEt.text.toString().trim().ifBlank { null }
                val price = priceEt.text.toString().toDoubleOrNull()

                if (brand.isEmpty() || model.isEmpty() || year == null || price == null) {
                    Toast.makeText(this, "Заполните бренд, модель, год и цену", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                io.execute {
                    carRepo.insert(Car(brand = brand, model = model, year = year, body = body, price = price))
                    val cars = carRepo.getAll()
                    runOnUiThread {
                        Toast.makeText(this, "Авто добавлено", Toast.LENGTH_SHORT).show()
                        adapter = CarAdapter(cars) { car: Car ->
                            val i = Intent(this, CarDetailActivity::class.java)
                            i.putExtra("car_id", car.id)
                            startActivity(i)
                        }
                        rv.adapter = adapter
                    }
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun doLogout() {
        AuthManager.clearUser(this)
        Session.currentUserName = null
        Session.isAdmin = false
        Toast.makeText(this, "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
