package com.zeezaglobal.posresturant.Application

import android.app.Application
import androidx.room.Room
import com.zeezaglobal.posresturant.Database.POSDatabase

class POSApp : Application() {

    lateinit var database: POSDatabase

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            POSDatabase::class.java,
            "my-database"
        ).build()
}}