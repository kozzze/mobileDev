package com.example.lab1.db

object UserContract {
    const val TABLE = "users"
    object Col {
        const val ID = "_id"
        const val NAME = "name"
        const val PASSWORD = "password"
        const val IS_ADMIN = "is_admin"
    }
}