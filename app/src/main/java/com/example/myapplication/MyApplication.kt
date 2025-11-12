package com.example.myapplication

import android.app.Application
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.ProductRepository

class MyApplication : Application() {

    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { ProductRepository(database.productDao()) }
}