package com.udacity.asteroidradar.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants.API_KEY
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.network.NasaApi
import com.udacity.asteroidradar.network.PictureOfDay
import kotlinx.coroutines.launch
import org.json.JSONObject

class MainViewModel : ViewModel() {

    //internal mutable data for that stores the status of the most recent response
    private val _status = MutableLiveData<String>()

    //external livedata for the recent response
    val status: LiveData<String>
        get() = _status


    //planet LiveData
    private val _planet = MutableLiveData<PictureOfDay>()
    val planet: LiveData<PictureOfDay>
        get() = _planet


    //asteroid LiveData
    private val _asteroids = MutableLiveData<ArrayList<Asteroid>>()
    val asteroids: LiveData<ArrayList<Asteroid>>
        get() = _asteroids

    //asteroid_status
    private val _statusAsteroids = MutableLiveData<String>()
    val statusAsteroid: LiveData<String>
        get() = _statusAsteroids

    //first asteroid
    private val _firstAsteroid = MutableLiveData<String>()
    val firstAsteroid: LiveData<String>
        get() = _firstAsteroid


    init {
        getNasaImageData()
        getAsteroidData()
    }

    private fun getAsteroidData() {
        viewModelScope.launch {
            try {
                var result = NasaApi.retrofitService.getAsteroids(API_KEY)
                val parsedResult = parseAsteroidsJsonResult(JSONObject(result))

                _asteroids.value = parsedResult
                _firstAsteroid.value = parsedResult.size.toString()

            } catch (e: Exception) {
                _firstAsteroid.value = "Failure: " + e.message
            }
        }


    }

    private fun getNasaImageData() {

        //call the object Singleton on NasaApiService on a background thread
        viewModelScope.launch {
            try {
                var result = NasaApi.retrofitService.getPlanet(API_KEY)
                _status.value = result?.title
                _planet.value = result
            } catch (e: java.lang.Exception) {
                _status.value = "Failure" + e.message
            }
        }


    }
}