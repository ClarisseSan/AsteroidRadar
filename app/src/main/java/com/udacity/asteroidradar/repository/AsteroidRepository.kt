package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants.API_KEY
import com.udacity.asteroidradar.api.getEndOfWeek
import com.udacity.asteroidradar.api.getToday
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.network.AsteroidApiFilter
import com.udacity.asteroidradar.network.NasaApi
import com.udacity.asteroidradar.network.NetworkAsteroidContainer
import com.udacity.asteroidradar.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

/*
* A Repository is just a regular class that has one (or more) methods that load data without
* specifying the data source as part of the main API. Because it's just a regular class,
* there's no need for an annotation to define a repository.
* The repository hides the complexity of managing the interactions between the database and the networking code.
* */

class AsteroidRepository(private val database: AsteroidDatabase) {

    /*
    You want a LiveData of with a list of Asteroids.
    Asteroid is the domain object.
    Room can return a LiveData of database objects called DatabaseAsteroid using the getAsteroid() method you wrote in the AsteroidDao.
    So you'll need to convert the list of DatabaseAsteroid to a list of Asteroid.
    You have written a Kotlin extension function for this already in an earlier step, called asDomainModel().

    Transformations.map is perfect for mapping the output of one LiveData to another type.
    Use Transformation.map to convert your LiveData list of DatabaseVideo objects to domain Asteroid objects.
    */
    val asteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAsteroids()) {
            it.asDomainModel()
        }

    val asteroidsToday : LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAsteroidsToday(getToday())){
            it.asDomainModel()
        }

    val asteroidsWeek : LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAsteroidsWeek(getToday(), getEndOfWeek())){
            it.asDomainModel()
        }


    //function to refresh the offline cache. Make it a suspend function since it will be called from a coroutine.
    suspend fun refreshAsteroid() {

        withContext(Dispatchers.IO) {
            //Get the data from the network and then put it in the database

            val asteroidList = NasaApi.retrofitService.getAsteroids(
                API_KEY
            )
            val parsedResult =
                NetworkAsteroidContainer(parseAsteroidsJsonResult(JSONObject(asteroidList)))

            database.asteroidDao.insertAsteroids(*parsedResult.asDatabaseModel())

        }

    }
}