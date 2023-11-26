package com.test.happyplaces.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PlaceModel::class], version = 1 )
abstract class PlacesDatabase : RoomDatabase(){
    abstract fun placesDao() : PlacesDao
}