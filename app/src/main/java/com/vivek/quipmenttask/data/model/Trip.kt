package com.vivek.quipmenttask.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "trip")
data class Trip(
    @PrimaryKey(autoGenerate = true)
    val id:Int?,
    val customerName : String,
    val pickUpAddress : String,
    val dropOffAddress: String,
    val price: Double,
    val pickTime: Date,
)
