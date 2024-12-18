package com.example.tablecount

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class TCPService : Service() {

    private val host = "DESKTOP-8HCLTFT" // Change to your server's IP or hostname
    private val port = 60881             // Change to your server's port
    private var isConnected = false      // Flag to indicate whether the client is connected

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startTCPClient()
        return START_STICKY
    }

    private fun startTCPClient() {
        GlobalScope.launch(Dispatchers.IO) {
            while (!isConnected) {
                try {
                    val socket = Socket(host, port)
                    val writer = PrintWriter(socket.getOutputStream(), true)
                    val reader = BufferedReader(InputStreamReader(socket.getInputStream(), "UTF-8"))

                    while (true) {
                        // Send data to the server
                        writer.println("update")
                        Log.d("MyTag", "Server response: Sending update")

                        val response = reader.readLine()

                        Log.d("MyTag", "Server response: $response")

                        // Broadcast received data to activities
                        val broadcastIntent = Intent("TCPIP_DATA_RECEIVED")
                        broadcastIntent.putExtra("data", response)
                        sendBroadcast(broadcastIntent)

                        // Add delay if needed
                        delay(1000) // Delay for 1 second
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    isConnected = false
                    delay(1000) // Retry after a delay
                }
            }
        }
    }
}
