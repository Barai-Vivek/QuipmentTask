package com.vivek.quipmenttask.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vivek.quipmenttask.data.model.Trip

@Dao
interface TripDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) //if some data is same/conflict, it'll be replace with new data.
    suspend fun insertTrip(note: Trip)

    @Query("DELETE FROM trip")
    suspend fun removeAll()

    @Query("SELECT * FROM trip ORDER BY pickTime")
    fun getAllTrips(): LiveData<List<Trip>>

    @Query("SELECT COUNT(*) FROM trip")
    fun getTripsCount(): LiveData<Int>
}