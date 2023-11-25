package com.test.happyplaces.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "places-table")
data class PlaceModel(
    @PrimaryKey(autoGenerate = true) val id : Int,
    val title: String,
    val image: String,
    val description: String,
    val date: String,
    val location: String,
    val latitude: Double,
    val longitude: Double
)
