package com.example.lab1.model

data class Car(
    val id: Long = 0,
    val brand: String,
    val model: String,
    val year: Int,
    val body: String?,
    val price: Double,
    val description: String? = null,
    val imageUrl: String? = null // URL вместо imageRes/BLOB
)
