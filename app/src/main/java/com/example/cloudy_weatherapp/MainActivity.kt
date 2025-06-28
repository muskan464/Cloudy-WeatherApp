package com.example.cloudy_weatherapp

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.cloudy_weatherapp.databinding.ActivityMainBinding
import com.google.android.gms.location.*
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var player: ExoPlayer
    private val apiKey = BuildConfig.WEATHER_API_KEY
    private val baseUrl = BuildConfig.WEATHER_BASE_URL


    private var isAutoLocationEnabled = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        player = ExoPlayer.Builder(this).build()
        binding.playerView.player = player

        setupLocationUpdates()
        getLocationAndFetchWeather()
        setupSearchListener()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupLocationUpdates() {
        locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            30000L
        ).build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                location?.let {
                    val lat = it.latitude
                    val lon = it.longitude
                    Log.d("MyApp", "Auto location lat=$lat, lon=$lon")
                    fetchWeatherDataByCoordinates(lat, lon)
                } ?: Log.d("MyApp", "Location is null")
            }
        }
    }

    private fun getLocationAndFetchWeather() {
        if (!isAutoLocationEnabled) {
            Log.d("MyApp", "Auto-tracking disabled")
            return
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            mainLooper
        )
    }

    private fun setupSearchListener() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    fetchWeatherDataByCity(it)


                    fusedLocationClient.removeLocationUpdates(locationCallback)
                    isAutoLocationEnabled = false
                    Log.d("MyApp", "Auto-tracking stopped after search")
                }
                return true
            }

            override fun onQueryTextChange(newText: String?) = true
        })
    }

    private fun fetchWeatherDataByCoordinates(lat: Double, lon: Double) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(baseUrl)
            .build()
            .create(ApiInterface::class.java)

        retrofit.getWeatherDataByCoordinates(lat, lon, apiKey, "metric")
            .enqueue(object : Callback<CloudyWeatherApp> {
                override fun onResponse(
                    call: Call<CloudyWeatherApp?>,
                    response: Response<CloudyWeatherApp?>
                ) {
                    val data = response.body()
                    if (response.isSuccessful && data != null) {
                        updateUI(data, data.name)
                    } else {
                        Log.d("MyApp", "API error: ${response.code()} ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<CloudyWeatherApp?>, t: Throwable) {
                    Log.e("MyApp", "API call failed: ${t.localizedMessage}")
                }
            })
    }

    private fun fetchWeatherDataByCity(cityName: String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(baseUrl)
            .build()
            .create(ApiInterface::class.java)

        retrofit.getWeatherData(cityName, apiKey, "metric")
            .enqueue(object : Callback<CloudyWeatherApp> {
                override fun onResponse(
                    call: Call<CloudyWeatherApp?>,
                    response: Response<CloudyWeatherApp?>
                ) {
                    val data = response.body()
                    if (response.isSuccessful && data != null) {
                        updateUI(data, cityName)
                    } else {
                        Log.d("MyApp", "API error: ${response.code()} ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<CloudyWeatherApp?>, t: Throwable) {
                    Log.e("MyApp", "API call failed: ${t.localizedMessage}")
                }
            })
    }

    private fun updateUI(weather: CloudyWeatherApp, cityName: String) {
        binding.temp.text = "${weather.main.temp} °C"
        binding.weather.text = weather.weather.firstOrNull()?.main ?: "Unknown"
        binding.max.text = "Max Temp: ${weather.main.temp_max} °C"
        binding.min.text = "Min Temp: ${weather.main.temp_min} °C"
        binding.humidity.text = "${weather.main.humidity} %"
        binding.windSpeed.text = "${weather.wind.speed} m/s"
        binding.sunrise.text = time(weather.sys.sunrise.toLong())
        binding.sunset.text = time(weather.sys.sunset.toLong())
        binding.sea.text = "${weather.main.pressure} hpa"
        binding.condition.text = weather.weather.firstOrNull()?.main ?: "Unknown"
        binding.day.text = dayName(System.currentTimeMillis())
        binding.date.text = date()
        binding.location.text = cityName

        changeImagesAccoundingToWeatherCondition(weather.weather.firstOrNull()?.main ?: "Clear")
    }

    private fun changeImagesAccoundingToWeatherCondition(condition: String) {
        val videoUri: Uri
        when (condition) {
            "Clear Sky", "Sunny", "Clear" -> {
                binding.lottieAnimationView.setAnimation(R.raw.sun)
                videoUri = Uri.parse("android.resource://${packageName}/raw/sunny_video2")
            }
            "Partly Clouds", "Clouds", "Overcast", "Mist", "Foggy" -> {
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
                videoUri = Uri.parse("android.resource://${packageName}/raw/cloudy_video")
            }
            "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain" , "Rain"-> {
                binding.lottieAnimationView.setAnimation(R.raw.rain)
                videoUri = Uri.parse("android.resource://${packageName}/raw/rainy_video2")
            }
            "Light Show", "Moderate Show", "Heavy Snow", "Blizzard" , "Snow" -> {
                binding.lottieAnimationView.setAnimation(R.raw.snow)
                videoUri = Uri.parse("android.resource://${packageName}/raw/snow_video")
            }
            "Storm", "Stormy", "Thunderstorm", "Lightning", "Thunder" -> {
                binding.lottieAnimationView.setAnimation(R.raw.stormy)
                videoUri = Uri.parse("android.resource://${packageName}/raw/stormy_video")
            }
            else -> {
                binding.lottieAnimationView.setAnimation(R.raw.sun)
                videoUri = Uri.parse("android.resource://${packageName}/raw/sunny_video2")
            }
        }

        val mediaItem = MediaItem.fromUri(videoUri)
        player.setMediaItem(mediaItem)
        player.repeatMode = ExoPlayer.REPEAT_MODE_ALL
        player.volume = 1f
        player.prepare()
        player.play()

        binding.lottieAnimationView.playAnimation()
    }

    private fun date(): String {
        return SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date())
    }

    private fun time(timestamp: Long): String {
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp * 1000))
    }

    private fun dayName(timestamp: Long): String {
        return SimpleDateFormat("EEEE", Locale.getDefault()).format(Date())
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            getLocationAndFetchWeather()
        } else {
            Log.e("MyApp", "Permission denied")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}
