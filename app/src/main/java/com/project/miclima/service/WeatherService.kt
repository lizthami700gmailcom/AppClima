package com.project.miclima.service

import com.project.miclima.model.InfoWeather
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("weather")
    fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") appid: String,
        @Query("lang") lang: String,
        @Query("units") units: String
    ): Call<InfoWeather>
}