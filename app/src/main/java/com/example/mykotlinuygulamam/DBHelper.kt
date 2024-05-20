package com.example.mykotlinuygulamam

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 2
        private const val DATABASE_NAME = "ProductDatabase"
        private const val TABLE_PRODUCTS = "products"
        private const val KEY_ID = "id"
        private const val KEY_NAME = "name"
        private const val KEY_PRICE = "price"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_IMAGE_URL = "imageUrl"
        private const val KEY_CATEGORY = "category"
        private const val KEY_IS_FAVORITE = "isFavorite"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_PRODUCTS_TABLE = ("CREATE TABLE $TABLE_PRODUCTS (" +
                "$KEY_ID TEXT PRIMARY KEY," +
                "$KEY_NAME TEXT," +
                "$KEY_PRICE REAL," +
                "$KEY_DESCRIPTION TEXT," +
                "$KEY_IMAGE_URL TEXT," +
                "$KEY_CATEGORY TEXT," +
                "$KEY_IS_FAVORITE INTEGER DEFAULT 0)")
        db.execSQL(CREATE_PRODUCTS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE $TABLE_PRODUCTS ADD COLUMN $KEY_IS_FAVORITE INTEGER DEFAULT 0")
        }
    }

    fun addProduct(id: String, name: String, price: Double, description: String, imageUrl: String, category: String, isFavorite: Int = 0) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_ID, id)
        values.put(KEY_NAME, name)
        values.put(KEY_PRICE, price)
        values.put(KEY_DESCRIPTION, description)
        values.put(KEY_IMAGE_URL, imageUrl)
        values.put(KEY_CATEGORY, category)
        values.put(KEY_IS_FAVORITE, isFavorite) // Favori bilgisi ekleniyor

        db.insert(TABLE_PRODUCTS, null, values)
        db.close()
    }

    @SuppressLint("Range")
    fun getAllProducts(): List<FavoriUrun> {
        val productList = mutableListOf<FavoriUrun>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_PRODUCTS", null)

        if (cursor.moveToFirst()) {
            do {
                val product = FavoriUrun(
                    id =  cursor.getString(cursor.getColumnIndex(KEY_ID)),
                    name = cursor.getString(cursor.getColumnIndex(KEY_NAME)),
                    price = cursor.getDouble(cursor.getColumnIndex(KEY_PRICE)),
                    description = cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)),
                    imageUrl = cursor.getString(cursor.getColumnIndex(KEY_IMAGE_URL)),
                )
                productList.add(product)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return productList
    }

    @SuppressLint("Range")
    fun getAllFavoriteProducts(): List<FavoriUrun> {
        val productList = mutableListOf<FavoriUrun>()
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_PRODUCTS WHERE $KEY_IS_FAVORITE= 1"
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val product = FavoriUrun(
                    id =  cursor.getString(cursor.getColumnIndex(KEY_ID)),
                    name = cursor.getString(cursor.getColumnIndex(KEY_NAME)),
                    price = cursor.getDouble(cursor.getColumnIndex(KEY_PRICE)),
                    description = cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)),
                    imageUrl = cursor.getString(cursor.getColumnIndex(KEY_IMAGE_URL)),
                )
                productList.add(product)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return productList
    }

}