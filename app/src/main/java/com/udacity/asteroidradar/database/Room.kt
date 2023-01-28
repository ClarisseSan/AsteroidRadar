package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AsteroidDao {
    //returns LiveData to observe for changes
    @Query("Select * from DatabaseAsteroid")
    fun getAsteroids(): LiveData<List<DatabaseAsteroid>>

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
