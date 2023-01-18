package com.udacity.asteroidradar.network

import com.udacity.asteroidradar.Constants.BASE_URL
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET


/**Retrofit needs to have at least two things available to build API
1. BASE_URL
2. Converter factory that allows retofit to returnthe server response in a useful format **/


//Use Retrofit Builder with Base URl and scalar converter factor
private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

//implement the NasaApiService interface with @GetAsteroids returning String
interface NasaApiService {

    @GET("planetary/apod?api_key=9MsA3RstB0RSZTAeY8VEeITJ7GfG0Mpj3eGdmPLQ")
    fun getPlanet(): Call<String>

}

//Create the NasaApi object using Retrofit to implement the NasaApiService
object NasaApi {
    val retrofitService: NasaApiService by lazy {
        retrofit.create(NasaApiService::class.java)
    }
}