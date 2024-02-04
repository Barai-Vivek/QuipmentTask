package com.vivek.quipmenttask.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vivek.quipmenttask.data.converter.Converters
import com.vivek.quipmenttask.data.model.Trip


@Database(entities = [Trip::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class TripDatabase : RoomDatabase(){
    abstract fun getTripDao(): TripDao
}