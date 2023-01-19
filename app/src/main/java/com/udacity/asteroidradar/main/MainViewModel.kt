package com.udacity.asteroidradar.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.network.NasaApi
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    //internal mutable data for that stores the status of the most recent response
    private val _response = MutableLiveData<String>()

    //external livedata for the recent status
    val response: LiveData<String>
        get() = _response


    init {
        getNasaData()
    }

    private fun getNasaData() {

        //call the object Singleton on NasaApiService on a background thread
        viewModelScope.launch {
            try {
                var result = NasaApi.retrofitService.getPlanet()
                _response.value = result?.title
            } catch (e: java.lang.Exception) {
                _response.value = "Failure" + e.message
            }
        }


    }
}