package com.vivek.quipmenttask.di.module

import android.content.Context
import androidx.room.Room
import com.vivek.quipmenttask.data.db.TripDao
import com.vivek.quipmenttask.data.db.TripDatabase
import com.vivek.quipmenttask.repository.TripRepository
import com.vivek.quipmenttask.viewmodel.TripViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class DBModule {
    @Provides
    @Singleton
    fun providesTripDataBase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context.applicationContext, TripDatabase::class.java, "trips_database.db"
    ).build()

    @Provides
    @Singleton
    fun providesTripDao(tripDatabase: TripDatabase) = tripDatabase.getTripDao()

}