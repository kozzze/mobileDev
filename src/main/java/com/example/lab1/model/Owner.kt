package com.example.lab1.model

data class Owner(
    val id: Long = 0,
    val carId: Long,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val telegram: String? = null,
    val email: String? = null
)
