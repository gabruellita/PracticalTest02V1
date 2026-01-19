package ro.pub.cs.system.eim.practicaltest02v1

import android.util.Log
import java.net.InetAddress
import java.net.ServerSocket
import java.util.concurrent.ConcurrentHashMap
import kotlin.let

class ServerThread(
    private val serverPort: Int,
    private val word: String) : Thread() {
    private var serverSocket: ServerSocket? = null
    private var isRunning = false
//    private val weatherCache = ConcurrentHashMap<String, WeatherData>()

    override fun run() {
        try {
            serverSocket = ServerSocket(serverPort, 50, InetAddress.getByName(Constants.SERVER_HOST))
            isRunning = true
            Log.d(Constants.TAG, "Server started on port $serverPort")

            while (isRunning) {
                try {
                    val clientSocket = serverSocket?.accept()
                    clientSocket?.let {
                        Log.d(Constants.TAG, "Client connected: ${it.inetAddress}:${it.port}")
                        // Create a new CommunicationThread for each client
                        val communicationThread = CommunicationThread(it, word)
                        communicationThread.start()
                    }
                } catch (e: Exception) {
                    if (isRunning) {
                        Log.e(Constants.TAG, "Error accepting client: ${e.message}")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(Constants.TAG, "Server error: ${e.message}")
        } finally {
            stopServer()
        }
    }

    fun stopServer() {
        isRunning = false
        try {
            serverSocket?.close()
            Log.d(Constants.TAG, "Server stopped")
        } catch (e: Exception) {
            Log.e(Constants.TAG, "Error stopping server: ${e.message}")
        }
    }
}

