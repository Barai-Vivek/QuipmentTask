package com.vivek.quipmenttask.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vivek.quipmenttask.data.model.Trip
import com.vivek.quipmenttask.repository.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TripViewModel @Inject constructor(private val tripRepository: TripRepository) : ViewModel() {
    suspend fun insertTrip(trip: Trip) = tripRepository.insertTrip(trip)
    suspend fun removeAllTrips() = tripRepository.removeAllTrips()
    fun getAllTrips() = tripRepository.getAllTrips()

    val tripCount = tripRepository.getTripsCount()

}