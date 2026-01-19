package ro.pub.cs.system.eim.practicaltest02v1

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PracticalTest02v1MainActivity : AppCompatActivity() {

    private lateinit var serverPortEditText: EditText
    private lateinit var startServerButton: Button
    private lateinit var stopServerButton: Button
    private lateinit var serverStatusTextView: TextView

    private lateinit var clientAddressEditText: EditText
    private lateinit var clientPortEditText: EditText
    private lateinit var cityEditText: EditText
    private lateinit var parameterEditText: EditText
    private lateinit var getButton: Button
    private lateinit var responseTextView: TextView

    private var serverThread: ServerThread? = null
    private val mainHandler: Handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_practical_test02v1_main)

        // Initialize views
        serverPortEditText = findViewById(R.id.server_port_edit_text)
        startServerButton = findViewById(R.id.start_server_button)
        stopServerButton = findViewById(R.id.stop_server_button)
        serverStatusTextView = findViewById(R.id.server_status_text_view)

        clientAddressEditText = findViewById(R.id.client_address_edit_text)
        clientPortEditText = findViewById(R.id.client_port_edit_text)
        cityEditText = findViewById(R.id.city_edit_text)
//        parameterEditText = findViewById(R.id.parameter_edit_text)
        getButton = findViewById(R.id.get_weather_button)
        responseTextView = findViewById(R.id.response_text_view)

        // Set up listeners
        startServerButton.setOnClickListener {
            startServer()
        }

        stopServerButton.setOnClickListener {
            stopServer()
        }

        getButton.setOnClickListener {
            get_comp()
        }

    }

    private fun startServer() {
        val portText = serverPortEditText.text.toString()
        val word = cityEditText.text.toString()
        if (portText.isEmpty()) {
            Toast.makeText(this, "Please enter server port", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val port = portText.toInt()
            serverThread = ServerThread(port, word)
            serverThread?.start()

            mainHandler.post {
                serverStatusTextView.text = "Server Status: Running on port $port"
                startServerButton.isEnabled = false
                stopServerButton.isEnabled = true
                serverPortEditText.isEnabled = false
                Toast.makeText(this, "Server started on port $port", Toast.LENGTH_SHORT).show()
            }

            Log.d(Constants.TAG, "Server started on port $port")
        } catch (_: NumberFormatException) {
            Toast.makeText(this, "Invalid port number", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error starting server: ${e.message}", Toast.LENGTH_SHORT).show()
            Log.e(Constants.TAG, "Error starting server", e)
        }
    }

    private fun stopServer() {
        serverThread?.stopServer()
        serverThread = null

        mainHandler.post {
            serverStatusTextView.text = "Server Status: Stopped"
            startServerButton.isEnabled = true
            stopServerButton.isEnabled = false
            serverPortEditText.isEnabled = true
            Toast.makeText(this, "Server stopped", Toast.LENGTH_SHORT).show()
        }

        Log.d(Constants.TAG, "Server stopped")
    }

    private fun get_comp() {

        val address = clientAddressEditText.text.toString()
        val portText = clientPortEditText.text.toString()
        val word = cityEditText.text.toString()


        if (address.isEmpty() || portText.isEmpty() || word.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val port = portText.toInt()

            // Clear previous response and disable button
            responseTextView.text = "Loading..."
            getButton.isEnabled = false

            // Create and start ClientThread
            val clientThread = ClientThread(address, port, word, responseTextView)
            clientThread.start()

            // Re-enable button after a delay
            mainHandler.postDelayed({
                getButton.isEnabled = true
            }, 1000)

        } catch (_: NumberFormatException) {
            Toast.makeText(this, "Invalid port number", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopServer()
    }
}