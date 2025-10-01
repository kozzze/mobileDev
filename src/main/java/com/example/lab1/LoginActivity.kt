package com.example.lab1

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.lab1.db.UserRepository
import java.util.concurrent.Executors

class LoginActivity : AppCompatActivity() {

    private val io = Executors.newSingleThreadExecutor()
    private var isRegisterMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val nameEt = findViewById<EditText>(R.id.inputName)
        val passEt = findViewById<EditText>(R.id.inputPassword)
        val adminCb = findViewById<CheckBox>(R.id.inputIsAdmin)
        val switchMode = findViewById<TextView>(R.id.switchMode)
        val loginBtn = findViewById<Button>(R.id.loginButton)

        val userRepo = UserRepository(this)

        // переключатель вход/регистрация
        switchMode.setOnClickListener {
            isRegisterMode = !isRegisterMode
            if (isRegisterMode) {
                loginBtn.text = "Зарегистрироваться"
                adminCb.visibility = android.view.View.VISIBLE
                switchMode.text = "Уже есть аккаунт? Войти"
            } else {
                loginBtn.text = "Войти"
                adminCb.visibility = android.view.View.GONE
                switchMode.text = "Нет аккаунта? Регистрация"
            }
        }

        loginBtn.setOnClickListener {
            val name = nameEt.text.toString().trim()
            val pass = passEt.text.toString().trim()
            val isAdmin = adminCb.isChecked

            if (name.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Введите логин и пароль", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isRegisterMode) {
                // регистрация
                io.execute {
                    if (userRepo.getByName(name) != null) {
                        runOnUiThread {
                            Toast.makeText(this, "Такой пользователь уже есть", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        userRepo.registerUser(name, pass, isAdmin)
                        AuthManager.saveUser(this, name, isAdmin)
                        Session.currentUserName = name
                        Session.isAdmin = isAdmin
                        runOnUiThread {
                            Toast.makeText(this, "Регистрация успешна", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
                    }
                }
            } else {
                // вход
                io.execute {
                    val user = userRepo.checkLogin(name, pass)
                    if (user == null) {
                        runOnUiThread {
                            Toast.makeText(this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        AuthManager.saveUser(this, user.name, user.isAdmin)
                        Session.currentUserName = user.name
                        Session.isAdmin = user.isAdmin
                        runOnUiThread {
                            Toast.makeText(this, "Добро пожаловать, ${user.name}", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
                    }
                }
            }
        }
    }
}
