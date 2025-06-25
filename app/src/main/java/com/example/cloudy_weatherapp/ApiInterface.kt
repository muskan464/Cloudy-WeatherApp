package com.example.cloudy_weatherapp

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("weather")
    fun getWeatherData(
        @Query("q") city: String,
        @Query("appid") appid: String,
        @Query("units") units: String
    ): Call<CloudyWeatherApp>

    @GET("weather")
    fun getWeatherDataByCoordinates(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") appid: String,
        @Query("units") units: String
    ): Call<CloudyWeatherApp>
}
