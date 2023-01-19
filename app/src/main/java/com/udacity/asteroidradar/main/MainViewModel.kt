package com.udacity.asteroidradar.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.network.NasaApi
import com.udacity.asteroidradar.network.PictureOfDay
import kotlinx.coroutines.launch

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


    init {
        getNasaData()
    }

    private fun getNasaData() {

        //call the object Singleton on NasaApiService on a background thread
        viewModelScope.launch {
            try {
                var result = NasaApi.retrofitService.getPlanet()
                _status.value = result?.title
                _planet.value = result
            } catch (e: java.lang.Exception) {
                _status.value = "Failure" + e.message
            }
        }


    }
}