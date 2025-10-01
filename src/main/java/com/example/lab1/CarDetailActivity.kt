package com.example.lab1

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.lab1.db.CarRepository
import com.example.lab1.db.UserRepository
import java.text.NumberFormat
import java.util.Locale
import android.view.View
import java.util.concurrent.Executors

class CarDetailActivity : AppCompatActivity() {

    private lateinit var carRepo: CarRepository
    private lateinit var userRepo: UserRepository
    private val io = Executors.newSingleThreadExecutor()

    private var carId: Long = -1
    private var userId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.car_detail)

        // Включаем «стрелку назад» в тулбаре
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Детали авто"

        carRepo = CarRepository(this)
        userRepo = UserRepository(this)

        val currentName = AuthManager.getUserName(this)
        if (currentName == null) {
            Toast.makeText(this, "Авторизуйтесь заново", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        userRepo.getByName(currentName)?.let { userId = it.id }

        carId = intent.getLongExtra("car_id", -1)

        val nameText  = findViewById<TextView>(R.id.carName)
        val priceText = findViewById<TextView>(R.id.carPrice)
        val descText  = findViewById<TextView>(R.id.carDescription)
        val yearText  = findViewById<TextView>(R.id.carYear)
        val bodyText  = findViewById<TextView>(R.id.carBodyType)

        val editBtn   = findViewById<Button>(R.id.editDescButton)
        val favBtn    = findViewById<Button>(R.id.favButton)
        val addBtn    = findViewById<Button>(R.id.addDeskButton)

        if (!Session.isAdmin) {
            editBtn.visibility = View.GONE
            addBtn.visibility = View.GONE
        }

        val backToMain = findViewById<Button>(R.id.backToMainButton)
        backToMain.setOnClickListener {
            finish()
        }

        // загрузка данных и статуса избранного
        io.execute {
            val car = carRepo.getById(carId)
            val fav = carRepo.isFavorite(userId, carId)
            runOnUiThread {
                car?.let {
                    nameText.text  = "${it.brand} ${it.model}"
                    priceText.text = NumberFormat.getCurrencyInstance(Locale.US).format(it.price)
                    descText.text  = it.description ?: "Описание машины"
                    yearText.text  = "Год выпуска: ${it.year}"
                    bodyText.text  = "Тип кузова: ${it.body ?: "—"}"
                }
                favBtn.text = if (fav) "Убрать из избранного" else "В избранное"
            }
        }

        editBtn.setOnClickListener {
            val input = EditText(this)
            input.setText(descText.text)
            AlertDialog.Builder(this)
                .setTitle("Изменить описание")
                .setView(input)
                .setPositiveButton("Сохранить") { _, _ ->
                    val newText = input.text.toString()
                    descText.text = newText
                    io.execute { carRepo.updateDescription(carId, newText) }
                    Toast.makeText(this, "Описание сохранено", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Отмена", null)
                .show()
        }

        favBtn.setOnClickListener {
            io.execute {
                val nowFav = carRepo.isFavorite(userId, carId)
                if (nowFav) carRepo.removeFavorite(userId, carId) else carRepo.addFavorite(userId, carId)
                runOnUiThread {
                    favBtn.text = if (nowFav) "В избранное" else "Убрать из избранного"
                    Toast.makeText(this, if (nowFav) "Убрано из избранного" else "Добавлено в избранное", Toast.LENGTH_SHORT).show()
                }
            }
        }

        addBtn.setOnClickListener {
            val input = EditText(this)
            AlertDialog.Builder(this)
                .setTitle("Добавить описание")
                .setView(input)
                .setPositiveButton("Сохранить") { _, _ ->
                    val newText = input.text.toString()
                    descText.text = newText
                    io.execute { carRepo.updateDescription(carId, newText) }
                    Toast.makeText(this, "Описание добавлено", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Отмена", null)
                .show()
        }


    }

    // обработка клика по «стрелке назад» в тулбаре
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                Toast.makeText(this, "Назад", Toast.LENGTH_SHORT).show()
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
