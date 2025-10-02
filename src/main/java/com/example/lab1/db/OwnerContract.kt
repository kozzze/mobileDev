package com.example.lab1.db

object OwnerContract {
    const val TABLE = "owners"
    object Col {
        const val ID = "_id"
        const val CAR_ID = "car_id"
        const val FIRST_NAME = "first_name"
        const val LAST_NAME = "last_name"
        const val PHONE = "phone"
        const val TELEGRAM = "telegram"
        const val EMAIL = "email"
    }
}
