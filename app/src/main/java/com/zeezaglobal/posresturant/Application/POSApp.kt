package com.zeezaglobal.posresturant.Application

import android.app.Application
import androidx.room.Room
import com.zeezaglobal.posresturant.Database.POSDatabase

class POSApp : Application() {

    companion object {
        lateinit var database: POSDatabase
            private set
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            POSDatabase::class.java,
            "pos_database"
        ).build()
    }
}