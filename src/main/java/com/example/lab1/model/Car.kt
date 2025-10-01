package com.example.lab1.model

data class Car(
    val id: Long = 0L,
    val brand: String,
    val model: String,
    val year: Int,
    val body: String? = null,
    val price: Double,
    val description: String? = null,
    val imageResId: Int = android.R.drawable.ic_menu_camera
)