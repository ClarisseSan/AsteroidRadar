package com.udacity.asteroidradar.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Constants.BASE_URL
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET


/**Retrofit needs to have at least two things available to build API
1. BASE_URL
2. Converter factory that allows retrofit to return the server response in a useful format **/

//Use the Moshi builder to create the Moshi object
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

//Use Retrofit Builder with Base URl and scalar converter factor
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()


//implement the NasaApiService interface with @GetAsteroids returning PictureOfDay object
interface NasaApiService {

    @GET("planetary/apod?api_key=9MsA3RstB0RSZTAeY8VEeITJ7GfG0Mpj3eGdmPLQ")
    fun getPlanet(): Call<PictureOfDay>

}

//Create the NasaApi object using Retrofit to implement the NasaApiService
object NasaApi {
    val retrofitService: NasaApiService by lazy {
        retrofit.create(NasaApiService::class.java)
    }
}