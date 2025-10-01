package com.example.lab1.db

object CarContract {
    const val TABLE = "cars"
    object Col {
        const val ID = "_id"
        const val BRAND = "brand"
        const val MODEL = "model"
        const val YEAR = "year"
        const val BODY = "body"
        const val PRICE = "price"
        const val DESCRIPTION = "description"
        const val IMAGE_URL = "image_url" // TEXT
    }
}
