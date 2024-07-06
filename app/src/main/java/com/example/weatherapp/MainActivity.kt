package com.example.weatherapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.model.ModelWeather
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var apiKey = "a39f82c810c8f0f57d9d399a35feaa3a"
    private var BASE_URL = "https://api.openweathermap.org/"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fetchWeatherData("Sargodha")
        searchCity()


    }

    private fun changeImageAccordingToWeatherCondition(condition: String) {
        when(condition){
            "Clear Sky","Sunny","Clear"->{
                binding.root.setBackgroundResource(R.drawable.mainbg)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "Partly Cluds","Clouds","Overcast","Mist","Foggy"->{
                binding.root.setBackgroundResource(R.drawable.cloud)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rain"->{
                binding.root.setBackgroundResource(R.drawable.rain)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Light Snow","Moderate Snow","Heavy Snow","Blizzard",->{
                binding.root.setBackgroundResource(R.drawable.snow)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
        }
        binding.lottieAnimationView.playAnimation()

    }

    private fun searchCity() {
        val searchCity = binding.searchView
        searchCity.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun fetchWeatherData(cityName : String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ApiInterface::class.java)

        val call = retrofit.getWeather(cityName,apiKey)
        call.enqueue(object : Callback<ModelWeather?> {
            override fun onResponse(call: Call<ModelWeather?>, response: Response<ModelWeather?>) {
                val weather = response.body()
                if (response.isSuccessful && weather !=null){
                    val cityName = weather.name
                    val temperature = kelvinToCelsius(weather.main.temp)
                    val maxTemp = weather.main.temp_max
                    val minTemp = weather.main.temp_min
                    val humidity = weather.main.humidity
                    val winSpeed = weather.wind.speed
                    val sunRise = weather.sys.sunrise
                    val sunSet = weather.sys.sunset
                    val seaLevel = weather.main.sea_level
                    val condition = weather.weather.firstOrNull()?.main?: "unknown"

                    binding.tvMaxTemp.text = "Min : $maxTemp °"
                    binding.tvMinTemp.text = "Min : $minTemp °"
                    binding.tvhumidity1.text = "$humidity %"
                    binding.winSpeed1.text = "$winSpeed m/s"
                    binding.tvSunRise1.text = sunRise.toString()
                    binding.tvSunSet1.text = sunSet.toString()
                    binding.tvTemperature.text = "$temperature °C"
                    binding.tvCityName.text = "$cityName"
                    binding.tvSea1.text = "$seaLevel hpa"
                    binding.tvCondition.text = "$condition"
                    binding.tvRain1.text = "$condition"
                    binding.tvCityName.text = "$cityName"
                    binding.tvDay.text = dayName(System.currentTimeMillis())
                    binding.tvDate.text = date()


                    changeImageAccordingToWeatherCondition(condition)


                }

            }

            override fun onFailure(p0: Call<ModelWeather?>, p1: Throwable) {
               Log.d("TAG","Failwed")
            }
        })

    }

    private fun kelvinToCelsius(kelvin: Double): String {
        return "%.2f".format(kelvin - 273.15f)
    }
    private fun dayName(timestamp: Long) : String{
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())
    }
    private fun date() : String{
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format(Date())
    }
}