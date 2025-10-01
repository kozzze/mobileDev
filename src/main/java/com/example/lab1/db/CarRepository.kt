package com.example.lab1.db

import android.content.ContentValues
import android.content.Context
import com.example.lab1.model.Car
import com.example.lab1.db.CarContract as C

class CarRepository(context: Context) {
    private val helper = CarDbHelper(context)

    fun insert(car: Car): Long {
        val db = helper.writableDatabase
        val v = ContentValues().apply {
            put(C.Col.BRAND, car.brand)
            put(C.Col.MODEL, car.model)
            put(C.Col.YEAR, car.year)
            put(C.Col.BODY, car.body)
            put(C.Col.PRICE, car.price)
            put(C.Col.DESCRIPTION, car.description)
            put(C.Col.IMAGE_URL, car.imageUrl)
        }
        return db.insert(C.TABLE, null, v)
    }

    fun getAll(): List<Car> {
        val db = helper.readableDatabase
        val c = db.query(C.TABLE, null, null, null, null, null, null)
        val list = mutableListOf<Car>()
        c.use {
            while (c.moveToNext()) list.add(c.readCar())
        }
        return list
    }

    fun getById(id: Long): Car? {
        val db = helper.readableDatabase
        val c = db.query(C.TABLE, null, "${C.Col.ID}=?", arrayOf(id.toString()), null, null, null)
        c.use { if (it.moveToFirst()) return it.readCar() }
        return null
    }

    fun updateDescription(id: Long, desc: String) {
        val db = helper.writableDatabase
        val v = ContentValues().apply { put(C.Col.DESCRIPTION, desc) }
        db.update(C.TABLE, v, "${C.Col.ID}=?", arrayOf(id.toString()))
    }

    // ---------- Избранное ----------
    fun isFavorite(userId: Long, carId: Long): Boolean {
        val db = helper.readableDatabase
        val c = db.query(
            FavoriteContract.TABLE,
            arrayOf(FavoriteContract.Col.USER_ID),
            "${FavoriteContract.Col.USER_ID}=? AND ${FavoriteContract.Col.CAR_ID}=?",
            arrayOf(userId.toString(), carId.toString()),
            null, null, null
        )
        c.use { return it.moveToFirst() }
    }

    fun addFavorite(userId: Long, carId: Long) {
        val db = helper.writableDatabase
        val v = ContentValues().apply {
            put(FavoriteContract.Col.USER_ID, userId)
            put(FavoriteContract.Col.CAR_ID,  carId)
        }
        db.insert(FavoriteContract.TABLE, null, v)
    }

    fun removeFavorite(userId: Long, carId: Long) {
        val db = helper.writableDatabase
        db.delete(
            FavoriteContract.TABLE,
            "${FavoriteContract.Col.USER_ID}=? AND ${FavoriteContract.Col.CAR_ID}=?",
            arrayOf(userId.toString(), carId.toString())
        )
    }

    fun getFavorites(userId: Long): List<Car> {
        val db = helper.readableDatabase
        val sql = """
            SELECT c.* FROM ${C.TABLE} c
            INNER JOIN ${FavoriteContract.TABLE} f
              ON f.${FavoriteContract.Col.CAR_ID} = c.${C.Col.ID}
            WHERE f.${FavoriteContract.Col.USER_ID} = ?
            ORDER BY c.${C.Col.BRAND}, c.${C.Col.MODEL}, c.${C.Col.YEAR}
        """.trimIndent()
        val c = db.rawQuery(sql, arrayOf(userId.toString()))
        val list = mutableListOf<Car>()
        c.use { while (it.moveToNext()) list.add(it.readCar()) }
        return list
    }

    private fun android.database.Cursor.readCar(): Car {
        return Car(
            id = getLong(getColumnIndexOrThrow(C.Col.ID)),
            brand = getString(getColumnIndexOrThrow(C.Col.BRAND)),
            model = getString(getColumnIndexOrThrow(C.Col.MODEL)),
            year = getInt(getColumnIndexOrThrow(C.Col.YEAR)),
            body = getString(getColumnIndexOrThrow(C.Col.BODY)),
            price = getDouble(getColumnIndexOrThrow(C.Col.PRICE)),
            description = getString(getColumnIndexOrThrow(C.Col.DESCRIPTION)),
            imageUrl = getString(getColumnIndexOrThrow(C.Col.IMAGE_URL))
        )
    }
}
