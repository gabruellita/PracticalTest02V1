package ro.pub.cs.system.eim.practicaltest02v1

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Log.e
import android.widget.TextView
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.io.IOException
import java.net.Socket
import kotlin.also
import kotlin.text.trim
//import org.json.JSONArray
import org.json.JSONObject

class ClientThread(
    private val serverAddress: String,
    private val serverPort: Int,
    private val word: String,
    private val responseTextView: TextView
) : Thread() {

//    val url = "https://www.google.com/complete/search?client=firefox&q=$word"
//    val request = Request.Builder().url(url).build()
    private val client = OkHttpClient()
//    val response = client.newCall(request).execute()
    private val mainHandler: Handler = Handler(Looper.getMainLooper())

    override fun run() {
        var socket: Socket? = null
        try {
            val startTime = System.currentTimeMillis()

            socket = Socket(serverAddress, serverPort)
            Log.d(Constants.TAG, "Connected to server at $serverAddress:$serverPort")

            val writer = Utilities.getWriter(socket)
            val reader = Utilities.getReader(socket)

            // Send city and parameter
            writer.println(word)

            // Read response
            val response = kotlin.text.StringBuilder()
//            val rep = reader.readLine()
//            var line: String?
//            while (reader.readLine().also { line = it } != null) {
//                response.append(line).append("\n")
//            }

//            val endTime = System.currentTimeMillis()
//            val responseTime = endTime - startTime

            val finalResponse = response.toString()

            val url = "https://www.google.com/complete/search?client=firefox&q=$word"
            val request = Request.Builder().url(url).build()
//            private val client = OkHttpClient()
            val response2 = client.newCall(request).execute()

            if (response2.isSuccessful) {
                val jsonResponse = JSONArray(response2.body?.string())
                val suggestions = jsonResponse.getJSONArray(1)
                val list2 = List(suggestions.length()) { index -> suggestions.getString(index) }
                mainHandler.post {
//                responseTextView.text = "$finalResponse\n\nResponse time: ${responseTime}ms"
                    responseTextView.text = list2.joinToString("\n")
                }
            } else {
                mainHandler.post {
//                responseTextView.text = "$finalResponse\n\nResponse time: ${responseTime}ms"
                    responseTextView.text = "vvv"
                }
            }




//            Log.d(Constants.TAG, "Response received in ${responseTime}ms")

        } catch (e: IOException) {
            Log.e(Constants.TAG, "Client error: ${e.message}")
            mainHandler.post {
                responseTextView.text = "Error: ${e.message}\n\nMake sure the server is running."
            }
        } finally {
            try {
                socket?.close()
                Log.d(Constants.TAG, "Client connection closed")
            } catch (e: IOException) {
                Log.e(Constants.TAG, "Error closing socket: ${e.message}")
            }
        }
    }
}

