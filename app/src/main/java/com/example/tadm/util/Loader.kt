package com.example.tadm.util


import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle

import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import com.example.tadm.R
import kotlinx.coroutines.*

class Loader(context: Context, private val message:String="") : Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
    private lateinit var progressBar: ProgressBar
    private lateinit var messtxt : TextView
    init {

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        setCancelable(false)
        setContentView(R.layout.loader)

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        messtxt = findViewById(R.id.messTxt)
        messtxt.text=message
        progressBar = findViewById(R.id.progressBar)

        progressBar.max = 100 // Set maximum value if tracking progress
        progressBar.progress = 0

    }

    override fun show() {
        super.show()
        startLoadingProcess()

    }

    //    private fun startLoadingProcess() {
//        // Example of a loading process
//        // Update the progress bar as the process progresses
//        progressBar.progress = 0
//        val handler = Handler(Looper.getMainLooper())
//        handler.postDelayed({
//            // Update progress here
//            progressBar.progress = 50 // Example progress update
//        }, 2000) // Delayed for demonstration
//    }
    private fun startLoadingProcess() {
        progressBar.progress = 0 // Initialize progress

        // Launch a coroutine in the Main scope for UI updates
        CoroutineScope(Dispatchers.Main).launch {
            // Use 'withContext' to switch to a background thread
            withContext(Dispatchers.IO) {
                for (i in 1..100) {
                    delay(100) // Simulating a time-consuming task

                    // Update the UI on the main thread
                    launch(Dispatchers.Main) {
                        progressBar.progress = i
                    }
                }
            }
        }

    }
}
