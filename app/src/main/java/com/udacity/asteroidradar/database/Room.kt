package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*

@Dao
interface AsteroidDao {
    //returns LiveData to observe for changes
    @Query("Select * from DatabaseAsteroid order by closeApproachDate ASC")
    fun getAsteroids(): LiveData<List<DatabaseAsteroid>>

    @Query("Select * from DatabaseAsteroid where closeApproachDate = :startDate order by closeApproachDate ASC")
    fun getAsteroidsToday(startDate : String) : LiveData<List<DatabaseAsteroid>>

    @Query("Select * from DatabaseAsteroid where closeApproachDate Between :startDate and :endDate order by closeApproachDate ASC")
    fun getAsteroidsWeek(startDate : String, endDate : String) : LiveData<List<DatabaseAsteroid>>

    //upsert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAsteroids(vararg videos: DatabaseAsteroid)
}

// Create the VideosDatabase class:
@Database(entities = [DatabaseAsteroid::class], version = 1)
abstract class AsteroidDatabase : RoomDatabase() {
    abstract val asteroidDao: AsteroidDao
}

//create singleton for the AsteroidDatabase
private lateinit var INSTANCE: AsteroidDatabase

fun getDatabase(context: Context): AsteroidDatabase {
    //make the initialization thread safe
    synchronized(AsteroidDatabase::class.java) {
        //check if already initialized
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                AsteroidDatabase::class.java,
                "asteroids"
            ).build()
        }
    }

    return INSTANCE
}
