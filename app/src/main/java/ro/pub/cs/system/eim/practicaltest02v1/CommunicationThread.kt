package ro.pub.cs.system.eim.practicaltest02v1

import android.util.Log
import android.util.Log.e
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.Socket
import java.net.URL
import java.util.concurrent.ConcurrentHashMap
import kotlin.also
import kotlin.collections.set
import kotlin.text.isEmpty
import kotlin.text.lowercase
import okhttp3.OkHttpClient // Client HTTP pentru cereri de rețea
//import org.json.JSONArray // Pentru a parsa răspunsurile JSON
import okhttp3.Request

class CommunicationThread(
    private val clientSocket: Socket,
    private val word: String
) : Thread() {

    private val client = OkHttpClient()

    override fun run() {
        try {
            val reader = Utilities.getReader(clientSocket)
            val writer = Utilities.getWriter(clientSocket)

            // Read city name
            val city = reader.readLine()
            if (city == null || city.isEmpty()) {
                writer.println("Error: City name required")
                return
            }

            // Read parameter
            val parameter = reader.readLine()
            if (parameter == null || parameter.isEmpty()) {
                writer.println("Error: Parameter required")
                return
            }

            Log.d(Constants.TAG, "Request: city=$city, parameter=$parameter")

            // Check cache first
//            var weatherData = weatherCache[city.lowercase()]
//
//            if (weatherData == null) {
//                Log.d(Constants.TAG, "Fetching weather data from API for $city")
//                weatherData = fetchWeatherFromAPI(city)
//                if (weatherData != null) {
//                    weatherCache[city.lowercase()] set weatherData
//                    Log.d(Constants.TAG, "Cached weather data for $city")
//                }
//            } else {
//                Log.d(Constants.TAG, "Using cached weather data for $city")
//            }

            var data = fetchFromAPI(city)

            // Send response
            if (data != null) {
                val response = data
                writer.println(response)
            } else {
                writer.println("Error: Could not fetch weather data for $city")
            }

        } catch (e: Exception) {
            Log.e(Constants.TAG, "Error handling client: ${e.message}")
        } finally {
            try {
                clientSocket.close()
                Log.d(Constants.TAG, "Connection closed with ${clientSocket.inetAddress}")
            } catch (e: Exception) {
                Log.e(Constants.TAG, "Error closing client socket: ${e.message}")
            }
        }
    }

    private fun fetchFromAPI(word: String): List<String>? {
        return try {
            val urlString = "https://www.google.com/complete/search?client=firefox&q=$word"
            val url = URL(urlString)
            val request = Request.Builder().url(url).build()
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 10000
            connection.readTimeout = 10000

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = kotlin.text.StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()

                List(response.length) { index -> response.toString() }
////                parseData(word, response.toString())
//
////                val jsonResponse = JSONArray(response.body?.string())
////                val suggestions = jsonResponse.getJSONArray(1)
////                List(suggestions.length()) { index -> suggestions.getString(index) }
//                response.toString()
//            val response = client.newCall(request).execute()
//            val response = client.newCall(request).execute()
//
//            // Verifică dacă cererea a avut succes (cod 200)
//            if (response.isSuccessful) {
//                // Convertește răspunsul în String și apoi în JSONArray
//                // Răspunsul Google este un array JSON cu 2 elemente:
//                // [0] = query-ul original, [1] = array-ul de sugestii
//                val jsonResponse = JSONArray(response.body?.string())
//
//                // Obține array-ul de sugestii (indexul 1)
//                val suggestions = jsonResponse.getJSONArray(1)
//
//                // Convertește JSONArray în List<String>
//                // List() creează o listă cu un număr specific de elemente
//                List(suggestions.length()) { index -> suggestions.getString(index) }
            } else {
                Log.e(Constants.TAG, "HTTP error: $responseCode")
                null
            }
        } catch (e: Exception) {
            Log.e(Constants.TAG, "Error fetching weather: ${e.message}")
            null
        }
    }

//    private fun parseData(city: String, jsonResponse: String): String? {
//        return try {
////            val json = JSONObject(jsonResponse)
////            val main = json.getJSONObject("main")
////            val wind = json.getJSONObject("wind")
////            val weatherArray = json.getJSONArray("weather")
////            val weather = weatherArray.getJSONObject(0)
//
//            val jsonResponse = JSONArray(jsonResponse.body?.string())
//
//            WeatherData(
//                city = city,
//                temperature = "${main.getDouble("temp")}°C",
//                windSpeed = "${wind.getDouble("speed")} m/s",
//                weatherCondition = weather.getString("description"),
//                pressure = "${main.getInt("pressure")} hPa",
//                humidity = "${main.getInt("humidity")}%"
//            )
//        } catch (e: Exception) {
//            Log.e(Constants.TAG, "Error parsing weather data: ${e.message}")
//            null
//        }
//    }
}

