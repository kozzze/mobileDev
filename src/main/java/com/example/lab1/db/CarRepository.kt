package com.example.lab1.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.lab1.model.Car
import com.example.lab1.db.CarContract as C
import com.example.lab1.db.FavoriteContract as F

class CarRepository(context: Context) {
    private val helper = CarDbHelper(context)

    // Cars
    fun insert(car: Car): Long {
        val db = helper.writableDatabase
        val v = ContentValues().apply {
            put(C.Col.BRAND, car.brand)
            put(C.Col.MODEL, car.model)
            put(C.Col.YEAR,  car.year)
            put(C.Col.BODY,  car.body)
            put(C.Col.PRICE, car.price)
            put(C.Col.DESCRIPTION, car.description)
            put(C.Col.IMAGE_RES, car.imageResId)
        }
        return db.insertWithOnConflict(C.TABLE, null, v, SQLiteDatabase.CONFLICT_IGNORE)
    }

    fun updateDescription(id: Long, description: String): Int {
        val db = helper.writableDatabase
        val v = ContentValues().apply { put(C.Col.DESCRIPTION, description) }
        return db.update(C.TABLE, v, "${C.Col.ID}=?", arrayOf(id.toString()))
    }

    fun getAll(): List<Car> {
        val db = helper.readableDatabase
        val cur = db.query(
            C.TABLE,
            arrayOf(C.Col.ID, C.Col.BRAND, C.Col.MODEL, C.Col.YEAR, C.Col.BODY, C.Col.PRICE, C.Col.DESCRIPTION, C.Col.IMAGE_RES),
            null, null, null, null,
            "${C.Col.BRAND} ASC, ${C.Col.MODEL} ASC"
        )
        return cur.use { it.toCars() }
    }

    fun getById(id: Long): Car? {
        val db = helper.readableDatabase
        val cur = db.query(C.TABLE, null, "${C.Col.ID}=?", arrayOf(id.toString()), null, null, null)
        return cur.use { if (it.moveToFirst()) it.readCar() else null }
    }

    // Favorites
    fun addFavorite(userId: Long, carId: Long): Long {
        val db = helper.writableDatabase
        val v = ContentValues().apply {
            put(F.Col.USER_ID, userId)
            put(F.Col.CAR_ID, carId)
        }
        return db.insertWithOnConflict(F.TABLE, null, v, SQLiteDatabase.CONFLICT_IGNORE)
    }

    fun removeFavorite(userId: Long, carId: Long): Int {
        val db = helper.writableDatabase
        return db.delete(F.TABLE, "${F.Col.USER_ID}=? AND ${F.Col.CAR_ID}=?", arrayOf(userId.toString(), carId.toString()))
    }

    fun isFavorite(userId: Long, carId: Long): Boolean {
        val db = helper.readableDatabase
        val c = db.query(
            F.TABLE,
            arrayOf(F.Col.USER_ID),
            "${F.Col.USER_ID}=? AND ${F.Col.CAR_ID}=?",
            arrayOf(userId.toString(), carId.toString()),
            null, null, null, "1"
        )
        c.use { return it.moveToFirst() }
    }

    fun getFavorites(userId: Long): List<Car> {
        val db = helper.readableDatabase
        val sql = """
            SELECT c.${C.Col.ID}, c.${C.Col.BRAND}, c.${C.Col.MODEL}, c.${C.Col.YEAR},
                   c.${C.Col.BODY}, c.${C.Col.PRICE}, c.${C.Col.DESCRIPTION}, c.${C.Col.IMAGE_RES}
            FROM ${C.TABLE} c
            INNER JOIN ${F.TABLE} f ON f.${F.Col.CAR_ID} = c.${C.Col.ID}
            WHERE f.${F.Col.USER_ID} = ?
            ORDER BY c.${C.Col.BRAND} ASC, c.${C.Col.MODEL} ASC
        """.trimIndent()
        val cur = db.rawQuery(sql, arrayOf(userId.toString()))
        return cur.use { it.toCars() }
    }

    // Cursor helpers
    private fun Cursor.toCars(): List<Car> {
        val list = ArrayList<Car>(count)
        while (moveToNext()) list.add(readCar())
        return list
    }

    private fun Cursor.readCar(): Car {
        fun idx(n: String) = getColumnIndexOrThrow(n)
        return Car(
            id = getLong(idx(C.Col.ID)),
            brand = getString(idx(C.Col.BRAND)),
            model = getString(idx(C.Col.MODEL)),
            year  = getInt(idx(C.Col.YEAR)),
            body  = getString(idx(C.Col.BODY)),
            price = getDouble(idx(C.Col.PRICE)),
            description = getString(idx(C.Col.DESCRIPTION)),
            imageResId  = getInt(idx(C.Col.IMAGE_RES))
        )
    }
}
