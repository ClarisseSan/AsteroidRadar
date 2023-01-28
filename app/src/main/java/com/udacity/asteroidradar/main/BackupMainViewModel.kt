package com.udacity.asteroidradar.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.network.AsteroidApiFilter
import com.udacity.asteroidradar.network.NasaApi
import com.udacity.asteroidradar.network.NetworkAsteroid
import com.udacity.asteroidradar.network.PictureOfDay
import kotlinx.coroutines.launch
import org.json.JSONObject

class BackupMainViewModel : ViewModel() {

    enum class ApiStatus {
        LOADING,
        ERROR,
        DONE
    }

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
    private val _asteroids = MutableLiveData<List<NetworkAsteroid>>()
    val asteroids: LiveData<List<NetworkAsteroid>>
        get() = _asteroids

    //asteroid_status
    private val _statusAsteroids = MutableLiveData<ApiStatus>()
    val statusAsteroid: LiveData<ApiStatus>
        get() = _statusAsteroids

    //first asteroid
    private val _firstAsteroid = MutableLiveData<String>()
    val firstAsteroid: LiveData<String>
        get() = _firstAsteroid


    //selected Asteroid that can trigger navigation
    private val _navigateToSelectedAsteroid = MutableLiveData<NetworkAsteroid>()
    val navigateToSelectedAsteroid: LiveData<NetworkAsteroid>
        get() = _navigateToSelectedAsteroid


    init {
        getNasaImageData()
        getAsteroidData(AsteroidApiFilter.SHOW_ALL)
    }

    private fun getAsteroidData(filter: AsteroidApiFilter) {
        viewModelScope.launch {
            _statusAsteroids.value = ApiStatus.LOADING
            try {
                var result = NasaApi.retrofitService.getAsteroids(
                    Constants.API_KEY,
                    filter.start_date,
                    filter.end_date
                )
                val parsedResult = parseAsteroidsJsonResult(JSONObject(result))

                _asteroids.value = parsedResult
                _firstAsteroid.value = parsedResult.size.toString()

                _statusAsteroids.value = ApiStatus.DONE

            } catch (e: Exception) {
                _firstAsteroid.value = "Failure: " + e.message
                _statusAsteroids.value = ApiStatus.ERROR
                _asteroids.value = emptyList()
            }
        }


    }

    private fun getNasaImageData() {

        //call the object Singleton on NasaApiService on a background thread
        viewModelScope.launch {
            try {
                var result = NasaApi.retrofitService.getPlanet(Constants.API_KEY)
                _status.value = result?.title
                _planet.value = result
            } catch (e: java.lang.Exception) {
                _status.value = "Failure" + e.message
            }
        }


    }

    fun displayAsteroidDetails(asteroid: NetworkAsteroid) {
        _navigateToSelectedAsteroid.value = asteroid
    }

    fun displayAsteroidDetailsComplete() {
        _navigateToSelectedAsteroid.value = null
    }

    fun updateFilter(filter: AsteroidApiFilter) {
        getAsteroidData(filter)
    }
}