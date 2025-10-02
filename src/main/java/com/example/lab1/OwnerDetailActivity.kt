package com.example.lab1

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lab1.db.OwnerRepository

class OwnerDetailActivity : AppCompatActivity() {
    private lateinit var repo: OwnerRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.owner_detail)

        repo = OwnerRepository(this)
        val carId = intent.getLongExtra("car_id", -1)

        val nameView = findViewById<TextView>(R.id.ownerName)
        val phoneView = findViewById<TextView>(R.id.ownerPhone)
        val tgView = findViewById<TextView>(R.id.ownerTelegram)
        val emailView = findViewById<TextView>(R.id.ownerEmail)
        val backBtn = findViewById<Button>(R.id.backButton)

        val owner = repo.getByCarId(carId)
        if (owner != null) {
            nameView.text = "${owner.firstName} ${owner.lastName}"
            phoneView.text = "Телефон: ${owner.phone}"
            tgView.text = "Telegram: ${owner.telegram ?: "-"}"
            emailView.text = "Email: ${owner.email ?: "-"}"
        } else {
            nameView.text = "Владелец не найден"
            phoneView.text = "-"
            tgView.text = "-"
            emailView.text = "-"
            Toast.makeText(this, "Владелец для этой машины отсутствует", Toast.LENGTH_SHORT).show()
        }

        backBtn.setOnClickListener {
            finish()
        }
    }
}
