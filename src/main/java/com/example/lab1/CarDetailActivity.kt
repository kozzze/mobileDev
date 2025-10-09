package com.example.lab1

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
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
        val descText = findViewById<TextView>(R.id.carDescriptionText)
        val yearText = findViewById<TextView>(R.id.carYear)
        val bodyText = findViewById<TextView>(R.id.carBodyType)
        val editBtn = findViewById<Button>(R.id.editDescButton)
        val favBtn = findViewById<Button>(R.id.favButton)
        val backBtn = findViewById<Button>(R.id.backButton)
        val contactBtn = findViewById<Button>(R.id.contactButton)

        val userName = Session.currentUserName
        val user = userRepo.getByName(userName ?: "")
        if (user == null) {
            Toast.makeText(this, getString(R.string.auth_error), Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        currentUserId = user.id

        io.execute {
            val car = carRepo.getById(carId)
            val fav = carRepo.isFavorite(currentUserId, carId)
            runOnUiThread {
                isFavNow = fav
                favBtn.text = if (isFavNow) getString(R.string.remove_from_favorites)
                else getString(R.string.add_to_favorites)

                car?.let {
                    nameText.text = "${it.brand} ${it.model}"
                    priceText.text = NumberFormat.getCurrencyInstance(Locale.US).format(it.price)
                    descText.text = it.description ?: getString(R.string.no_description)
                    yearText.text = getString(R.string.car_year, it.year)
                    bodyText.text = getString(R.string.car_body, it.body ?: "—")

                    if (!it.imageUrl.isNullOrBlank()) {
                        Glide.with(this)
                            .load(it.imageUrl)
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

        editBtn.setOnClickListener {
            if (!Session.isAdmin) {
                Toast.makeText(this, getString(R.string.edit_only_admin), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val currentDesc = descText.text.toString()
            val input = EditText(this).apply {
                setText(currentDesc)
                setSelection(currentDesc.length)
                hint = getString(R.string.desc_hint)
            }

            AlertDialog.Builder(this)
                .setTitle(getString(R.string.desc_edit_title))
                .setView(input)
                .setPositiveButton(getString(R.string.add_description)) { _, _ ->
                    val newText = input.text.toString().trim()
                    if (newText.isNotEmpty() && newText != currentDesc) {
                        io.execute {
                            carRepo.updateDescription(carId, newText)
                            runOnUiThread {
                                descText.text = newText
                                Toast.makeText(this, getString(R.string.desc_updated), Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this, getString(R.string.desc_not_changed), Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton(getString(R.string.back), null)
                .show()
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
                    favBtn.text = if (isFavNow)
                        getString(R.string.remove_from_favorites)
                    else
                        getString(R.string.add_to_favorites)
                    Toast.makeText(
                        this,
                        if (isFavNow)
                            getString(R.string.added_to_favorites)
                        else
                            getString(R.string.removed_from_favorites),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        contactBtn.setOnClickListener {
            val i = Intent(this, OwnerDetailActivity::class.java)
            i.putExtra("car_id", carId)
            startActivity(i)
        }

        backBtn.setOnClickListener { finish() }
    }
}
