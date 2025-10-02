package com.example.lab1.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.lab1.db.CarContract as C
import com.example.lab1.db.UserContract as U
import com.example.lab1.db.FavoriteContract as F
import com.example.lab1.db.CarContract
import com.example.lab1.db.OwnerContract


private const val DB_NAME = "cars.db"
private const val DB_VERSION = 9

class CarDbHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        // users
        db.execSQL("""
            CREATE TABLE ${U.TABLE} (
                ${U.Col.ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${U.Col.NAME} TEXT NOT NULL UNIQUE,
                ${U.Col.PASSWORD} TEXT NOT NULL,
                ${U.Col.IS_ADMIN} INTEGER NOT NULL DEFAULT 0
            );
        """.trimIndent())

        // cars
        db.execSQL("""
            CREATE TABLE ${C.TABLE} (
                ${C.Col.ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${C.Col.BRAND} TEXT NOT NULL,
                ${C.Col.MODEL} TEXT NOT NULL,
                ${C.Col.YEAR} INTEGER NOT NULL,
                ${C.Col.BODY} TEXT,
                ${C.Col.PRICE} REAL NOT NULL,
                ${C.Col.DESCRIPTION} TEXT,
                ${C.Col.IMAGE_URL} TEXT,
                UNIQUE(${C.Col.BRAND}, ${C.Col.MODEL}, ${C.Col.YEAR}) ON CONFLICT IGNORE
            );
        """.trimIndent())

        db.execSQL("CREATE INDEX IF NOT EXISTS idx_car_brand ON ${C.TABLE}(${C.Col.BRAND});")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_car_year  ON ${C.TABLE}(${C.Col.YEAR});")

        // favorites
        db.execSQL("""
            CREATE TABLE ${F.TABLE} (
                ${F.Col.USER_ID} INTEGER NOT NULL,
                ${F.Col.CAR_ID}  INTEGER NOT NULL,
                PRIMARY KEY (${F.Col.USER_ID}, ${F.Col.CAR_ID}),
                FOREIGN KEY (${F.Col.USER_ID}) REFERENCES ${U.TABLE}(${U.Col.ID}) ON DELETE CASCADE,
                FOREIGN KEY (${F.Col.CAR_ID})  REFERENCES ${C.TABLE}(${C.Col.ID}) ON DELETE CASCADE
            );
        """.trimIndent())

        // --- owners ---
        db.execSQL("""
    CREATE TABLE ${OwnerContract.TABLE} (
        ${OwnerContract.Col.ID} INTEGER PRIMARY KEY AUTOINCREMENT,
        ${OwnerContract.Col.CAR_ID} INTEGER NOT NULL,
        ${OwnerContract.Col.FIRST_NAME} TEXT NOT NULL,
        ${OwnerContract.Col.LAST_NAME} TEXT NOT NULL,
        ${OwnerContract.Col.PHONE} TEXT NOT NULL,
        ${OwnerContract.Col.TELEGRAM} TEXT,
        ${OwnerContract.Col.EMAIL} TEXT,
        FOREIGN KEY (${OwnerContract.Col.CAR_ID}) REFERENCES ${CarContract.TABLE}(${CarContract.Col.ID}) ON DELETE CASCADE
    );
""".trimIndent())


    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${F.TABLE}")
        db.execSQL("DROP TABLE IF EXISTS ${C.TABLE}")
        db.execSQL("DROP TABLE IF EXISTS ${U.TABLE}")
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }
}
