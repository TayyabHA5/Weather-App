package com.example.weatherapp


import com.example.weatherapp.model.ModelWeather
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("data/2.5/weather")
    fun getWeather(
        @Query("q") city : String,
        @Query("appid") apiKey : String) : Call<ModelWeather>

}