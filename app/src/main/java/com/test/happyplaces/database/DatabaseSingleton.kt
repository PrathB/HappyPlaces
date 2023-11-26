package com.test.happyplaces.database

import android.content.Context
import androidx.room.Room

object DatabaseSingleton {
    private var INSTANCE: PlacesDatabase? = null

    fun getInstance(context: Context): PlacesDatabase {
        if (INSTANCE == null) {
            synchronized(PlacesDatabase::class.java) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        PlacesDatabase::class.java,
                        "places-database"
                    ).build()
                }
            }
        }
        return INSTANCE!!
    }
}