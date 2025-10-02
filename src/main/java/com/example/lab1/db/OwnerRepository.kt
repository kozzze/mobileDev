package com.example.lab1.db

import android.content.ContentValues
import android.content.Context
import com.example.lab1.model.Owner
import com.example.lab1.db.OwnerContract as O

class OwnerRepository(context: Context) {
    private val helper = CarDbHelper(context)

    fun insert(owner: Owner): Long {
        val db = helper.writableDatabase
        val v = ContentValues().apply {
            put(O.Col.CAR_ID, owner.carId)
            put(O.Col.FIRST_NAME, owner.firstName)
            put(O.Col.LAST_NAME, owner.lastName)
            put(O.Col.PHONE, owner.phone)
            put(O.Col.TELEGRAM, owner.telegram)
            put(O.Col.EMAIL, owner.email)
        }
        return db.insert(O.TABLE, null, v)
    }

    fun getByCarId(carId: Long): Owner? {
        val db = helper.readableDatabase
        val c = db.query(O.TABLE, null, "${O.Col.CAR_ID}=?",
            arrayOf(carId.toString()), null, null, null)
        c.use {
            if (it.moveToFirst()) {
                return Owner(
                    id = it.getLong(it.getColumnIndexOrThrow(O.Col.ID)),
                    carId = carId,
                    firstName = it.getString(it.getColumnIndexOrThrow(O.Col.FIRST_NAME)),
                    lastName = it.getString(it.getColumnIndexOrThrow(O.Col.LAST_NAME)),
                    phone = it.getString(it.getColumnIndexOrThrow(O.Col.PHONE)),
                    telegram = it.getString(it.getColumnIndexOrThrow(O.Col.TELEGRAM)),
                    email = it.getString(it.getColumnIndexOrThrow(O.Col.EMAIL))
                )
            }
        }
        return null
    }
}
