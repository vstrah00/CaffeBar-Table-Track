package com.example.tablecount

import android.os.Build
import android.view.View

object HelperFunctions {
    // Function to hide navigation and status bars
    fun hideSystemUI(view: View) {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            view.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                    // Set the content to appear under the system bars so that the
                    // content doesn't resize when the system bars hide and show.
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    // Hide the nav bar and status bar
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
        }
    }

    fun sumUntilIndexExclusive(arr: MutableList<Number>, index: Int): Number {
        // Check if the index is within the bounds of the array
        if (index < 0 || index >= arr.size) {
            throw IndexOutOfBoundsException("Index is out of bounds")
        }

        var sum = 0.0 // Initialize sum as a double to handle potential floating-point numbers
        for (i in 0 until index) { // Iterate up to index - 1
            sum += arr[i].toDouble() // Convert Number to Double before summing
        }
        return sum // Return the sum as a Number
    }



}
