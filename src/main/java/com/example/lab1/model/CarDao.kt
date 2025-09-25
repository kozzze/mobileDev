package com.example.lab1

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface CarDao {
    @Query("SELECT * FROM car")
    fun getAllCars(): List<Car>

    @Insert
    fun insert(car: Car)

    @Update
    fun update(car: Car)
}