package com.test.happyplaces.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlacesDao {
    @Insert
    suspend fun insert(happyPlace : PlaceModel)

    @Delete
    suspend fun delete(happyPlace: PlaceModel)

    @Query("SELECT * FROM `places-table` ")
    fun fetchAllPlaces() : Flow<List<PlaceModel>>
}