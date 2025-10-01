package com.example.lab1.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.lab1.db.UserContract as U

data class User(
    val id: Long = 0L,
    val name: String,
    val isAdmin: Boolean = false
)

class UserRepository(context: Context) {
    private val helper = CarDbHelper(context)

    fun createUser(name: String, isAdmin: Boolean = false): Long {
        val db = helper.writableDatabase
        val v = ContentValues().apply {
            put(U.Col.NAME, name)
            put(U.Col.IS_ADMIN, if (isAdmin) 1 else 0)
        }
        return db.insert(U.TABLE, null, v)
    }

    fun getByName(name: String): User? {
        val db = helper.readableDatabase
        val c = db.query(U.TABLE, null, "${U.Col.NAME}=?", arrayOf(name), null, null, null)
        return c.use { if (it.moveToFirst()) it.readUser() else null }
    }

    fun setAdmin(userId: Long, admin: Boolean): Int {
        val db = helper.writableDatabase
        val v = ContentValues().apply { put(U.Col.IS_ADMIN, if (admin) 1 else 0) }
        return db.update(U.TABLE, v, "${U.Col.ID}=?", arrayOf(userId.toString()))
    }

    private fun Cursor.readUser(): User {
        fun idx(n: String) = getColumnIndexOrThrow(n)
        return User(
            id = getLong(idx(U.Col.ID)),
            name = getString(idx(U.Col.NAME)),
            isAdmin = getInt(idx(U.Col.IS_ADMIN)) == 1
        )
    }
}
