package com.example.lab1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lab1.db.UserRepository
import java.util.concurrent.Executors

class LoginActivity : AppCompatActivity() {

    private val io = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val nameEt = findViewById<EditText>(R.id.inputName)
        val adminCb = findViewById<CheckBox>(R.id.inputIsAdmin)
        val loginBtn = findViewById<Button>(R.id.loginButton)

        // если уже логин был — сразу в Main
        AuthManager.getUserName(this)?.let {
            Session.currentUserName = it
            Session.isAdmin = AuthManager.isAdmin(this)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        loginBtn.setOnClickListener {
            val name = nameEt.text.toString().trim()
            val isAdmin = adminCb.isChecked

            if (name.isEmpty()) {
                Toast.makeText(this, "Введите имя", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // сохраняем в БД пользователей и в префсы
            io.execute {
                val userRepo = com.example.lab1.db.UserRepository(this)
                val existing = userRepo.getByName(name)
                if (existing == null) {
                    userRepo.createUser(name, isAdmin)
                } else if (existing.isAdmin != isAdmin) {
                    userRepo.setAdmin(existing.id, isAdmin)
                }

                AuthManager.saveUser(this, name, isAdmin)
                Session.currentUserName = name
                Session.isAdmin = isAdmin

                runOnUiThread {
                    Toast.makeText(this, "Здравствуйте, $name", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
        }
    }
}
