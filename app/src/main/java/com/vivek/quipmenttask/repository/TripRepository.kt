package com.vivek.quipmenttask.repository

import androidx.lifecycle.LiveData
import com.vivek.quipmenttask.data.db.TripDao
import com.vivek.quipmenttask.data.model.Trip
import javax.inject.Inject

class TripRepository @Inject constructor(private val tripDao: TripDao) {

    suspend fun insertTrip(trip: Trip) = tripDao.insertTrip(trip)

    suspend fun removeAllTrips() = tripDao.removeAll()

    fun getAllTrips(): LiveData<List<Trip>> = tripDao.getAllTrips()

    fun getTripsCount(): LiveData<Int> = tripDao.getTripsCount()

}