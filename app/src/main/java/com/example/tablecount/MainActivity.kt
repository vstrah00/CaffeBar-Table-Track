package com.example.tablecount

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import com.example.tablecount.R

class MainActivity : AppCompatActivity() {

    private val dataReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                if (it.action == "TCP_IP_DATA_RECEIVED") {
                    val data = it.getStringExtra("data")
                    // Handle the received data here
                    Log.d("MyTag", "Received data: $data")
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        HelperFunctions.hideSystemUI(window.decorView)

        startTCPService()

        val btn0: Button = findViewById<Button>(R.id.btn0)

        btn0.setOnClickListener {
            val intent = Intent(this, SetLayout::class.java)
            startActivity(intent)
        }
    }

    private fun startTCPService() {
        val serviceIntent = Intent(this, TCPService::class.java)
        startService(serviceIntent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        registerReceiver(dataReceiver, IntentFilter("TCP_IP_DATA_RECEIVED"), RECEIVER_NOT_EXPORTED)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(dataReceiver)
    }
}

