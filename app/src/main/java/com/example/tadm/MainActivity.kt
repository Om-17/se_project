package com.example.tadm


import SyncData
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import com.example.tadm.util.ConnectivityUtil
import com.example.tadm.util.Loader
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var newEntryBtn:Button
    private lateinit var  searchBtn:Button
    private lateinit var loader: Loader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        newEntryBtn=findViewById(R.id.new_entry_btn)
        loader = Loader(this,"Please Wait Syncing Data.")

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

            showLoader()
            lifecycleScope.launch {
                val syncData = SyncData()
                val syncSuccessful = syncData.syncData(this@MainActivity)
                syncData.printSQLiteData(this@MainActivity)
                hideLoader()

//                if (syncSuccessful) {
//                    // Synchronization was successful
//                    // You can perform any further actions here
//                } else {
//                    // Synchronization failed
//                    // Handle the failure, e.g., show an error message
//                }
            }
        }
}
    private fun showLoader() {
        if (!isFinishing && !isDestroyed) { // Check if activity is running
            loader.show()
        }
    }


    private fun hideLoader() {
        if (loader.isShowing) {
            loader.dismiss()
        }
    }
}