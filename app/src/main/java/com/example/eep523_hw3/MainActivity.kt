package com.example.eep523_hw3

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    // Declare views and default text
    private lateinit var editText: EditText
    private lateinit var submitButton: Button
    private lateinit var errorTextView: TextView
    private lateinit var mainContainer: RelativeLayout
    private val defaultText = "Seattle,US"

    // Variables for city and API key
    private var CITY: String = defaultText
    private val API: String = "e52aba36fa999284a53cfc3f91919c3f"
    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        editText = findViewById(R.id.inputCity)
        submitButton = findViewById(R.id.submitButton)
        errorTextView = findViewById(R.id.errorText)
        mainContainer = findViewById(R.id.container)

        // Set the default text in the EditText
//        editText.setText(defaultText)

        // Set a click listener for the submit button
        submitButton.setOnClickListener {
            // Get the text from the EditText
            val inputText = editText.text.toString()

            // If the text is blank, use the default text; otherwise, use the input text
            CITY = if (inputText.isBlank()) defaultText else inputText

            // Execute the weather task to update weather information
            weatherTask().execute()
        }

        // Get location on initial load
        getLocation()
    }

    // Method to request location updates
    private fun getLocation() {
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        // Check if location permissions are granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permissions if not granted
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCode)
        } else {
            // Request location updates if permissions are granted
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    // Get latitude and longitude from location
                    val latitude = location.latitude
                    val longitude = location.longitude
                    CITY = "$latitude,$longitude"
                    // Execute the weather task to update weather information
                    weatherTask().execute()
                    // Remove location updates after getting the location
                    locationManager.removeUpdates(this)
                }

                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

                override fun onProviderEnabled(provider: String) {}

                override fun onProviderDisabled(provider: String) {}
            })
        }
    }

    // Handle the result of location permission request
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation()
            }
        }
    }

    // Inner class to handle the asynchronous task of fetching weather data
    inner class weatherTask() : AsyncTask<String, Void, String>() {
        // Before executing the task, show the loader and hide the main container and error text
        override fun onPreExecute() {
            super.onPreExecute()
            findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
            errorTextView.visibility = View.GONE
        }

        // Perform the background operation of fetching data from the API
        override fun doInBackground(vararg params: String?): String? {
            var response: String?
            try {
                // Construct the API URL with the city or coordinates
                val url = if (CITY.contains(",")) {
                    // Coordinates are being used
                    "https://api.openweathermap.org/data/2.5/weather?lat=${CITY.split(",")[0]}&lon=${CITY.split(",")[1]}&units=metric&appid=$API"
                } else {
                    // City name is being used
                    "https://api.openweathermap.org/data/2.5/weather?q=$CITY&units=metric&appid=$API"
                }
                response = URL(url).readText(Charsets.UTF_8)
            } catch (e: Exception) {
                response = null
            }
            return response
        }

        // After the background operation, update the UI with the fetched data
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                // Check if the result is null or empty
                if (result == null || result.isEmpty()) {
                    throw Exception("No response from server")
                }

                // Parse the JSON response
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

                // Extract data from the JSON response
                val updatedAt: Long = jsonObj.getLong("dt")
                val updatedAtText = "Updated at: " + SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(Date(updatedAt * 1000))
                val temp = main.getString("temp") + "°C"
                val tempMin = "Min Temp: " + main.getString("temp_min") + "°C"
                val tempMax = "Max Temp: " + main.getString("temp_max") + "°C"
                val pressure = main.getString("pressure")
                val humidity = main.getString("humidity")

                val sunrise: Long = sys.getLong("sunrise")
                val sunset: Long = sys.getLong("sunset")
                val windSpeed = wind.getString("speed")
                val weatherDescription = weather.getString("description")

                val address = jsonObj.getString("name") + ", " + sys.getString("country")

                // Populate the views with the extracted data
                findViewById<TextView>(R.id.address).text = address
                findViewById<TextView>(R.id.updated_at).text = updatedAtText
                findViewById<TextView>(R.id.status).text = weatherDescription.capitalize()
                findViewById<TextView>(R.id.temp).text = temp
                findViewById<TextView>(R.id.temp_min).text = tempMin
                findViewById<TextView>(R.id.temp_max).text = tempMax
                findViewById<TextView>(R.id.sunrise).text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunrise * 1000))
                findViewById<TextView>(R.id.sunset).text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunset * 1000))
                findViewById<TextView>(R.id.wind).text = windSpeed
                findViewById<TextView>(R.id.pressure).text = pressure
                findViewById<TextView>(R.id.humidity).text = humidity

                // Change the background based on weather description
                Log.d("WeatherApp", "Weather Description: $weatherDescription")
                changeBackground(weatherDescription)

                // Hide the loader and show the main container
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE

            } catch (e: Exception) {
                // In case of an error, hide the loader and show the error text below the input field
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                errorTextView.text = "Error: ${e.message}. Please check the city name or try again later."
                errorTextView.visibility = View.VISIBLE
            }
        }

        // Method to change the background based on weather description
        private fun changeBackground(description: String) {
            when {
                description.contains("clear", true) -> {
                    mainContainer.setBackgroundResource(R.drawable.clear_sky)
                    Log.d("WeatherApp", "Background changed to clear_sky")
                }
                description.contains("clouds", true) -> {
                    mainContainer.setBackgroundResource(R.drawable.cloudy_sky)
                    Log.d("WeatherApp", "Background changed to few_clouds")
                }
                description.contains("rain", true) -> {
                    mainContainer.setBackgroundResource(R.drawable.rainy_sky)
                    Log.d("WeatherApp", "Background changed to rain")
                }
                description.contains("thunderstorm", true) -> {
                    mainContainer.setBackgroundResource(R.drawable.thunder_sky)
                    Log.d("WeatherApp", "Background changed to thunderstorm")
                }
                description.contains("snow", true) -> {
                    mainContainer.setBackgroundResource(R.drawable.snow_sky)
                    Log.d("WeatherApp", "Background changed to snow")
                }
                else -> {
                    mainContainer.setBackgroundResource(R.drawable.gradient_bg)
                    Log.d("WeatherApp", "Background changed to default_bg")
                }
            }
        }
    }

}