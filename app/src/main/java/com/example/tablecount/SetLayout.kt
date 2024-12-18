package com.example.tablecount

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import android.text.InputType
import android.util.TypedValue
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SetLayout : AppCompatActivity() {
    private lateinit var parentLayout: LinearLayout
    private lateinit var tvTCP: TextView
    private var numOfBastas: Int = 0
    private var receivedTCPData: String? = null
    private val enteredValues = mutableListOf<Number>()
    private val textViewIds = mutableListOf<Int>()


    private val dataReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                if (it.action == "TCPIP_DATA_RECEIVED") {
                    val receivedTCPData = it.getStringExtra("data")
                    tvTCP.text = "Received value: $receivedTCPData!"
                    //Log.d("MyTag", "Received data: $receivedTCPData")
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_set_layout)
        //tvTCP = findViewById(R.id.tvTCPRecieved)

        HelperFunctions.hideSystemUI(window.decorView)
        showPopup()

        val btnSave = findViewById<Button>(R.id.btnSetLaySave)

        // Set OnClickListener for the button
        btnSave.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }

    private fun showPopup() {
        window.decorView.post {
            val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as? LayoutInflater
            val popupView = inflater?.inflate(R.layout.popup_layout, null)
            if (popupView != null) {
                val width = LinearLayout.LayoutParams.WRAP_CONTENT
                val height = LinearLayout.LayoutParams.WRAP_CONTENT
                val focusable = true
                val popupWindow = PopupWindow(popupView, width, height, focusable)

                val editTextName = popupView.findViewById<EditText>(R.id.etPopup1)
                val buttonSubmit = popupView.findViewById<Button>(R.id.btnPopup)

                popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)

                buttonSubmit?.setOnClickListener {
                    val temp = editTextName.text.toString().toInt()
                    if(temp>0){
                        numOfBastas = temp

                        // Clear focus from EditText
                        popupWindow.dismiss()
                    }else{
                        Toast.makeText(applicationContext, "Invalid input", Toast.LENGTH_SHORT).show()
                    }

                }

                popupWindow.setOnDismissListener {
                    showDynamicPopup() // Show dynamic popup after regular popup is dismissed
                }

            }
        }
    }

    private fun showDynamicPopup() {
        Log.d("ShowDynamicPopup", "Showing dynamic popup")

        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dynamicPopupView = inflater.inflate(R.layout.popup_dynamic, null) as ConstraintLayout

        // Get the parent layout inside the dynamic popup
        val dynamicPopupLayout = dynamicPopupView.findViewById<ConstraintLayout>(R.id.dynamicPopupLayout)

        // Check if the dynamicPopupLayout is null
        if (dynamicPopupLayout == null) {
            Log.e("ShowDynamicPopup", "Dynamic popup layout is null")
            return
        }


        // Arrays to store IDs of TextViews and EditTexts
        val editTextIds = mutableListOf<Int>()

        // Add TextViews and EditTexts dynamically
        for (i in 0 until numOfBastas) {
            // Add TextView
            val textView = TextView(this)
            textView.id = View.generateViewId()
            textView.text = "Bašta ${i + 1}"
            textView.setTextAppearance(R.style.TextViewPopup) // Apply custom style
            dynamicPopupLayout.addView(textView)
            textViewIds.add(textView.id) // Store TextView ID

            // Set layout parameters for TextView
            val textParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            textParams.startToStart = dynamicPopupLayout.id // Start from the start of the layout
            textParams.endToEnd = dynamicPopupLayout.id // End at the end of the layout
            textParams.topToBottom = if (i == 0) ConstraintSet.UNSET else editTextIds[i - 1] // Place below previous TextView if not the first one
            textParams.topMargin = resources.getDimensionPixelSize(R.dimen.textview_margin)
            textParams.bottomMargin = resources.getDimensionPixelSize(R.dimen.textview_margin)
            textView.layoutParams = textParams

            // Add EditText
            val editText = EditText(this)
            editText.id = View.generateViewId()
            editText.hint = "Broj stolova"
            editText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL // Set input type
            editText.setTextAppearance(R.style.EditTextPopup) // Apply custom style
            dynamicPopupLayout.addView(editText)
            editTextIds.add(editText.id) // Store EditText ID

            // Set layout parameters for EditText
            val editParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            editParams.startToStart = dynamicPopupLayout.id // Start from the start of the layout
            editParams.endToEnd = dynamicPopupLayout.id // End at the end of the layout
            editParams.topToBottom = textViewIds[i] // Place EditText below corresponding TextView
            editParams.topMargin = resources.getDimensionPixelSize(R.dimen.edittext_margin)
            editParams.bottomMargin = resources.getDimensionPixelSize(R.dimen.edittext_margin)
            editText.layoutParams = editParams
        }

        val button = Button(this)
        button.id = View.generateViewId()
        button.text = "Potvrdi"
        button.setBackgroundResource(R.drawable.btn_important) // Set background resource
        button.setTextColor(ContextCompat.getColor(this, R.color.modernBlack)) // Set text color
        button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f) // Set text size
        button.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        dynamicPopupLayout.addView(button)

        // Set layout parameters for the button
        val buttonParams = button.layoutParams as ConstraintLayout.LayoutParams
        buttonParams.startToStart = dynamicPopupLayout.id
        buttonParams.endToEnd = dynamicPopupLayout.id
        buttonParams.topToBottom = editTextIds.last() // Place button below the last EditText
        buttonParams.topMargin = resources.getDimensionPixelSize(R.dimen.btnMargin)
        button.layoutParams = buttonParams

        val popupWindow = PopupWindow(
            dynamicPopupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        popupWindow.showAtLocation(dynamicPopupView, Gravity.CENTER, 0, 0)

        // Add click listener to the button
        button.setOnClickListener {
            var dismissFlag:Int = 1
            // Iterate through the EditText IDs and retrieve the corresponding values
            for (editTextId in editTextIds) {
                val editTemp = dynamicPopupLayout.findViewById<EditText>(editTextId).text.toString().toInt()
                if(editTemp>0){
                    enteredValues.add(editTemp)
                }else{
                    enteredValues.clear()
                    dismissFlag=0
                    break
                }
            }
            if(dismissFlag==1){
                popupWindow.dismiss()
                createTextViewsDynamic()
            }else{
                Toast.makeText(applicationContext, "Invalid input", Toast.LENGTH_SHORT).show()
            }
        }

    }


    @SuppressLint("ClickableViewAccessibility")
    private fun createTextViewsDynamic() {
        val mainLayout = findViewById<ConstraintLayout>(R.id.main)

        val constraintSet = ConstraintSet()

        // List to store child layout IDs
        val childLayoutIds = mutableListOf<Int>()

        for (i in 0 until this.numOfBastas) {
            // Create a ConstraintLayout to hold dynamically created TextViews
            val childLayout = ConstraintLayout(this)
            childLayout.id = View.generateViewId()
            mainLayout.addView(childLayout)
            childLayoutIds.add(childLayout.id) // Add child layout ID to the list
            Log.d("datamsg", "Created child layout $i: $childLayout")

            // Add border to the child layout
            childLayout.setBackgroundResource(R.drawable.basta_look)
            childLayout.translationZ = -1f // or any negative value as per requirement

            // Set constraints for the child layout
            constraintSet.connect(childLayout.id, ConstraintSet.START, mainLayout.id, ConstraintSet.START, 0)
            constraintSet.connect(childLayout.id, ConstraintSet.END, mainLayout.id, ConstraintSet.END, 0)

            if (i == 0) {
                // If it's the first child layout, connect top to mainLayout's top
                constraintSet.connect(childLayout.id, ConstraintSet.TOP, mainLayout.id, ConstraintSet.TOP, 0)
            } else {
                // If it's not the first child layout, connect top to the bottom of the previous child layout
                constraintSet.connect(childLayout.id, ConstraintSet.TOP, childLayoutIds[i - 1], ConstraintSet.BOTTOM, 0)
            }

            if (i == numOfBastas - 1) {
                // If it's the last child layout, connect bottom to mainLayout's bottom
                constraintSet.connect(childLayout.id, ConstraintSet.BOTTOM, mainLayout.id, ConstraintSet.BOTTOM, 0)
            }

            constraintSet.constrainPercentHeight(childLayout.id, 1f / numOfBastas)
            constraintSet.applyTo(mainLayout)

            // Add dynamically created TextViews to the child layout
            for (j in 0 until enteredValues[i].toInt()) {
                val textView = DraggableView(this)

                val tableLocalID = HelperFunctions.sumUntilIndexExclusive(enteredValues, i).toInt() + j
                Log.d("datamsg", "Creating table num: ${tableLocalID}")

                textView.text = "Stol ${tableLocalID + 1}"

                textView.textSize = 16f
                textView.setPadding(10,5,10,5)
                textView.setBackgroundResource(R.drawable.circle_background)

                val layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
                )
                textView.id = View.generateViewId()
                childLayout.addView(textView, layoutParams)




                // Set constraints for the TextViews
                constraintSet.clone(childLayout)
                constraintSet.connect(textView.id, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 15 + j*55)
                constraintSet.connect(textView.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 30)
                constraintSet.applyTo(childLayout)

            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        registerReceiver(dataReceiver, IntentFilter("TCPIP_DATA_RECEIVED"), RECEIVER_NOT_EXPORTED)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(dataReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}

