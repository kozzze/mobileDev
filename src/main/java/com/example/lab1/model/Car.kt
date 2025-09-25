package com.example.lab1

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "car")
data class Car(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val brand: String,
    val model: String,
    val price: String,
    val year: Int,
    val imageResId: Int = android.R.drawable.ic_menu_camera
)