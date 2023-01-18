package com.udacity.asteroidradar.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.udacity.asteroidradar.network.NasaApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {

    //internal mutable data for that stores the status of the most recent response
    private val _response = MutableLiveData<String>()

    //external livedata for the recent status
    val response : LiveData<String>
    get() = _response


    init {
        getNasaData()
    }

    private fun getNasaData() {
        //call the object Singleton on NasaApiService
        //Enqueue means loading it on the background thread
        NasaApi.retrofitService.getPlanet().enqueue(object : Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                _response.value = response.body()
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                _response.value = "Failure" + t.message
            }

        })

    }
}