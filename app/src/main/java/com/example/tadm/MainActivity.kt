package com.example.tadm


import SyncData
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Shader
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.example.tadm.Dialogs.ConfirmDialog
import com.example.tadm.util.ConnectivityUtil
import com.example.tadm.util.Loader
import com.example.tadm.util.SnackbarUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
class MainActivity : AppCompatActivity() {
    private lateinit var newEntryBtn:Button
    private lateinit var  searchBtn:Button
    private lateinit var loader: Loader
    private lateinit var  imgView:ImageView

    private lateinit var syncBtn:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        newEntryBtn=findViewById(R.id.new_entry_btn)
        imgView=findViewById(R.id.imageView)
        val transformation: Transformation = object : Transformation {
            override fun transform(source: Bitmap): Bitmap {
                val size = Math.min(source.width, source.height)
                val x = (source.width - size) / 2
                val y = (source.height - size) / 2
                val squaredBitmap = Bitmap.createBitmap(source, x, y, size, size)
                if (squaredBitmap != source) {
                    source.recycle()
                }
                val bitmap = Bitmap.createBitmap(size, size, source.config)
                val canvas = Canvas(bitmap)
                val paint = Paint()
                val shader = BitmapShader(
                    squaredBitmap,
                    Shader.TileMode.CLAMP,
                    Shader.TileMode.CLAMP
                )
                paint.shader = shader
                paint.isAntiAlias = true
                val radius = size / 2f
                canvas.drawCircle(radius, radius, radius, paint)
                squaredBitmap.recycle()
                return bitmap
            }

            override fun key(): String {
                return "circle"
            }
        }

// Load image with circular transformation
        Picasso.get()
            .load(R.drawable.armylogo)
            .transform(transformation)
            .into(imgView)
        syncBtn=findViewById(R.id.syncBtn)
        loader = Loader(this,"Please Wait Syncing Data.")
        syncBtn.setOnClickListener{
            if (ConnectivityUtil.isInternetAvailable(this)) {

                showLoader()
                lifecycleScope.launch {
                    withContext(Dispatchers.Main) {
                        showLoader()
                    }

                    val syncData = SyncData()
                    val syncSuccessful = syncData.syncData(this@MainActivity)
                    val message = "Successfully data sync."
                    if (syncSuccessful) {
                        val snackbarUtils = SnackbarUtils(this@MainActivity)
                        val rootView = findViewById<View>(android.R.id.content)
                        snackbarUtils.showSnackbar(rootView, message)
                    }
                    withContext(Dispatchers.Main) {
                        hideLoader()
                    }
                }
            }
            else{
                showConnectivityAlert()
            }
        }
        newEntryBtn.setOnClickListener{
            val intent = Intent(this, NewEntryForm::class.java)

            // Start the new activity
            startActivity(intent)
        }
        searchBtn=findViewById(R.id.search_btn)
        searchBtn.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            // Start the new activity
            startActivity(intent)
        }

        if (ConnectivityUtil.isInternetAvailable(this)) {

//            showLoader()
            lifecycleScope.launch {
                withContext(Dispatchers.Main) {
                    showLoader()
                }
//                val syncData = SyncData()
//                val syncSuccessful = syncData.syncData(this@MainActivity)



                    val syncData = SyncData()
                    val syncSuccessful = syncData.syncData(this@MainActivity)
                    println("statue==> $syncSuccessful")


                withContext(Dispatchers.Main) {
                    hideLoader()
                }
            }
        }
}
    private fun showLoader() {
        if (!isFinishing && !isDestroyed) { // Check if activity is running
            loader.show()
        }
    }
    private fun showExitConfirmationDialog() {

    val exitDialog = ConfirmDialog(
        this,
        {
            // User clicked Yes, exit the activity
            finishAffinity()
        },
        {
            // User clicked No, do nothing
        },
        "Exit",
        "Are you sure you want to exit?"
    )

    exitDialog.show()
}

    private fun showConnectivityAlert() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.apply {
            setTitle("No Internet Connection")
            setMessage("Please check your internet connection and try again.")
            setPositiveButton("Retry") { dialog, _ ->
                dialog.dismiss()
                recreate() // Recreate the activity to retry
            }
            setNegativeButton("Exit") { dialog, _ ->
                dialog.dismiss()

            }
            setCancelable(false)
            create().show()
        }
    }
@Deprecated("Deprecated in Java")
@SuppressLint("MissingSuperCall")
override fun onBackPressed() {
    showExitConfirmationDialog()
}


    private fun hideLoader() {
        if (loader.isShowing) {
            loader.dismiss()
        }
    }
}