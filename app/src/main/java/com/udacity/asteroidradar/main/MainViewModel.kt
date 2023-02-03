package com.udacity.asteroidradar.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants.API_KEY
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.network.AsteroidApiFilter
import com.udacity.asteroidradar.network.NasaApi
import com.udacity.asteroidradar.network.PictureOfDay
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    enum class ApiStatus {
        LOADING,
        ERROR,
        DONE
    }


    //planet LiveData
    private val _planet = MutableLiveData<PictureOfDay>()
    val planet: LiveData<PictureOfDay>
        get() = _planet


    //asteroid_status
    private val _statusAsteroids = MutableLiveData<ApiStatus>()
    val statusAsteroid: LiveData<ApiStatus>
        get() = _statusAsteroids

    //first asteroid
    private val _firstAsteroid = MutableLiveData<String>()
    val firstAsteroid: LiveData<String>
        get() = _firstAsteroid


    //selected Asteroid that can trigger navigation
    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid>()
    val navigateToSelectedAsteroid: LiveData<Asteroid>
        get() = _navigateToSelectedAsteroid


    //This is the main scope for all coroutines launched by the MainViewModel
    //Since we passed ViewModelJob, you can cancel all coroutines launched by uiScope by calling viewModelJob.cancel()
    private val viewModelJob = SupervisorJob()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)


    //create the database singleton
    private val database = getDatabase(application)

    //create repository
    private val repository = AsteroidRepository(database)


    init {
        getNasaImageData()
        getAsteroidData()
    }

    private var _filter = MutableLiveData(AsteroidApiFilter.SHOW_ALL)

    //asteroid LiveData
    private val _asteroids = Transformations.switchMap(_filter) {
        when (it!!) {
            AsteroidApiFilter.SHOW_TODAY -> repository.asteroidsToday
            AsteroidApiFilter.SHOW_WEEK -> repository.asteroidsWeek
            else -> repository.asteroids
        }
    }

    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids


    private fun getAsteroidData() {
        viewModelScope.launch {

            _statusAsteroids.value = ApiStatus.LOADING

            try {

                //refresh asteroids using repository
                repository.refreshAsteroid()
                Log.e("Database size >>", _asteroids.value?.size.toString())

                _firstAsteroid.value = _asteroids.value?.size.toString()

                _statusAsteroids.value = ApiStatus.DONE

            } catch (e: Exception) {
                _firstAsteroid.value = "Failure: " + e.message
                _statusAsteroids.value = ApiStatus.ERROR
            }
        }


    }

    private fun getNasaImageData() {

        //call the object Singleton on NasaApiService on a background thread
        viewModelScope.launch {
            try {
                var result = NasaApi.retrofitService.getPlanet(API_KEY)
                _planet.value = result
            } catch (e: java.lang.Exception) {
                Log.e("Failure", e.message.toString())
            }
        }


    }

    fun displayAsteroidDetails(asteroid: Asteroid) {
        _navigateToSelectedAsteroid.value = asteroid
    }

    fun displayAsteroidDetailsComplete() {
        _navigateToSelectedAsteroid.value = null
    }

    fun updateFilter(filter: AsteroidApiFilter) {
        _filter.postValue(filter)
    }

    /**
     * Factory for constructing DevByteViewModel with parameter
     */
    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}