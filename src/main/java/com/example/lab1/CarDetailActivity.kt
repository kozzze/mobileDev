package com.example.lab1
import android.content.Intent

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.lab1.db.CarRepository
import com.example.lab1.db.UserRepository
import java.text.NumberFormat
import java.util.Locale
import java.util.concurrent.Executors

class CarDetailActivity : AppCompatActivity() {

    private lateinit var carRepo: CarRepository
    private lateinit var userRepo: UserRepository
    private val io = Executors.newSingleThreadExecutor()

    private var carId: Long = -1
    private var currentUserId: Long = -1
    private var isFavNow: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.car_detail)

        carRepo = CarRepository(this)
        userRepo = UserRepository(this)

        carId = intent.getLongExtra("car_id", -1)

        val carImage = findViewById<ImageView>(R.id.carImage)
        val nameText = findViewById<TextView>(R.id.carName)
        val priceText = findViewById<TextView>(R.id.carPrice)
        val descText = findViewById<EditText>(R.id.carDescription)
        val yearText = findViewById<TextView>(R.id.carYear)
        val bodyText = findViewById<TextView>(R.id.carBodyType)

        val editBtn = findViewById<Button>(R.id.editDescButton)
        val favBtn  = findViewById<Button>(R.id.favButton)
        val backBtn = findViewById<Button>(R.id.backButton)

        // доступ к редактированию описания
        if (!Session.isAdmin) {
            editBtn.visibility = View.GONE
            descText.isEnabled = false
        } else {
            descText.isEnabled = true
        }

        // получаем id юзера из БД по имени из сессии
        val userName = Session.currentUserName
        if (userName.isNullOrBlank()) {
            Toast.makeText(this, "Сессия не найдена. Войдите заново.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        val user = userRepo.getByName(userName)
        if (user == null) {
            Toast.makeText(this, "Пользователь не найден в БД", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        currentUserId = user.id

        io.execute {
            val car = carRepo.getById(carId)
            val fav = carRepo.isFavorite(currentUserId, carId)
            runOnUiThread {
                isFavNow = fav
                favBtn.text = if (isFavNow) "Из избранного" else "В избранное"

                car?.let {
                    nameText.text = "${it.brand} ${it.model}"
                    priceText.text = NumberFormat.getCurrencyInstance(Locale.US).format(it.price)
                    descText.setText(it.description ?: "описание машины")
                    yearText.text = "Год выпуска: ${it.year}"
                    bodyText.text = "Тип кузова: ${it.body ?: "—"}"

                    val url = it.imageUrl
                    if (!url.isNullOrBlank()) {
                        Glide.with(this)
                            .load(url)
                            .placeholder(android.R.drawable.ic_menu_camera)
                            .error(android.R.drawable.ic_menu_report_image)
                            .centerCrop()
                            .into(carImage)
                    } else {
                        carImage.setImageResource(android.R.drawable.ic_menu_camera)
                    }
                }
            }
        }

        // редактирование описания (только админ)
        editBtn.setOnClickListener {
            if (!Session.isAdmin) {
                Toast.makeText(this, "Только админ может менять описание", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val newText = descText.text.toString()
            io.execute {
                carRepo.updateDescription(carId, newText)
                runOnUiThread { Toast.makeText(this, "Описание обновлено", Toast.LENGTH_SHORT).show() }
            }
        }

        favBtn.setOnClickListener {
            io.execute {
                if (carRepo.isFavorite(currentUserId, carId)) {
                    carRepo.removeFavorite(currentUserId, carId)
                    isFavNow = false
                } else {
                    carRepo.addFavorite(currentUserId, carId)
                    isFavNow = true
                }
                runOnUiThread {
                    favBtn.text = if (isFavNow) "Из избранного" else "В избранное"
                    Toast.makeText(
                        this,
                        if (isFavNow) "Добавлено в избранное" else "Удалено из избранного",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        val contactBtn = findViewById<Button>(R.id.contactButton)

        contactBtn.setOnClickListener {
            val i = Intent(this, OwnerDetailActivity::class.java)
            i.putExtra("car_id", carId)
            startActivity(i)
        }

        backBtn.setOnClickListener { finish() }
    }
}
