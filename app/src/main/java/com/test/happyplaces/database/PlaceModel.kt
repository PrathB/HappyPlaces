package com.test.happyplaces.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

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
) : Serializable
