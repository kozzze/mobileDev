package com.example.lab1.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.lab1.db.UserContract as U

data class User(
    val id: Long = 0L,
    val name: String,
    val password: String,
    val isAdmin: Boolean = false
)

class UserRepository(context: Context) {
    private val helper = CarDbHelper(context)

    fun registerUser(name: String, password: String, isAdmin: Boolean = false): Long {
        val db = helper.writableDatabase
        val v = ContentValues().apply {
            put(U.Col.NAME, name)
            put(U.Col.PASSWORD, password)
            put(U.Col.IS_ADMIN, if (isAdmin) 1 else 0)
        }
        return db.insert(U.TABLE, null, v)
    }

    fun checkLogin(name: String, password: String): User? {
        val db = helper.readableDatabase
        val c = db.query(
            U.TABLE,
            null,
            "${U.Col.NAME}=? AND ${U.Col.PASSWORD}=?",
            arrayOf(name, password),
            null, null, null
        )
        return c.use { if (it.moveToFirst()) it.readUser() else null }
    }

    fun getByName(name: String): User? {
        val db = helper.readableDatabase
        val c = db.query(U.TABLE, null, "${U.Col.NAME}=?", arrayOf(name), null, null, null)
        return c.use { if (it.moveToFirst()) it.readUser() else null }
    }

    private fun Cursor.readUser(): User {
        fun idx(n: String) = getColumnIndexOrThrow(n)
        return User(
            id = getLong(idx(U.Col.ID)),
            name = getString(idx(U.Col.NAME)),
            password = getString(idx(U.Col.PASSWORD)),
            isAdmin = getInt(idx(U.Col.IS_ADMIN)) == 1
        )
    }
}
