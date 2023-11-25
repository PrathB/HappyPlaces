package com.test.happyplaces.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.test.happyplaces.models.PlaceModel

@Database(entities = [PlaceModel::class], version = 1 )
abstract class PlacesDatabase : RoomDatabase(){
    abstract fun placesDao() : PlacesDao
}