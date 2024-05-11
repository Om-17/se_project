package com.example.tadm

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.tadm.Interface.BeforeNextClickListener
import com.example.tadm.Interface.FragmentDataListener
import com.example.tadm.api.ApiHelper
import com.example.tadm.model.NewEntryFormData
import com.example.tadm.util.ConnectivityUtil
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import android.Manifest
import androidx.core.app.ActivityCompat
import com.example.tadm.util.Loader

class NewEntryForm : AppCompatActivity(), FragmentDataListener,BeforeNextClickListener  {
    private var currentStep = 0
    private val totalSteps = 3 // Number of steps in the stepper
    private lateinit var adapter:StepperPagerAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var loader: Loader

    private var formdata = NewEntryFormData()

    override fun onBasicDataReceived(data: NewEntryFormData){
        println("basic detail for $data")
        formdata=data
    }
        override fun onFamilyDataReceived(data:String){
            println("family details $data")
            formdata.d_familydeatils = data
        }
        override fun onBeforeNextClicked(): Boolean {
            println("inti")
            val currentFragment = supportFragmentManager.findFragmentByTag("f$currentStep") as? BeforeNextClickListener

            // Check if the current fragment implements BeforeNextClickListener
            return currentFragment?.onBeforeNextClicked() ?: true

        }

        override fun onDeraDataReceived(data: String) {
            formdata.d_deradetails=data
            println("dera details $data")
        }
    private val REQUEST_CODE_STORAGE_PERMISSION = 1001
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_entry_form)
        if (!ConnectivityUtil.isInternetAvailable(this)) {
            showConnectivityAlert()
        } else {


        viewPager = findViewById(R.id.viewPager)
        val tabs: TabLayout = findViewById(R.id.tabs)
        val btnBack: Button = findViewById(R.id.btnBack)
        val btnNext: Button = findViewById(R.id.btnNext)
        val btnSave: Button = findViewById(R.id.btnSave)

            loader = Loader(this,"Please Wait Submiting Data.")


        // Create an adapter for managing fragments in the ViewPager
        adapter = StepperPagerAdapter(this, supportFragmentManager, lifecycle) // Use 'this' as BeforeNextClickListener

        viewPager.adapter = adapter

        // Connect TabLayout with ViewPager
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = getTabTitle(position)
        }.attach()
        viewPager.isUserInputEnabled = false
        // Initially, hide the Back button for the first step
        btnBack.visibility = if (currentStep == 0) View.INVISIBLE else View.VISIBLE

        // Show the Save button only on the last step
        btnSave.visibility = if (currentStep == totalSteps - 1) View.VISIBLE else View.GONE

        // Hide the Next button on the last step
        btnNext.visibility = if (currentStep == totalSteps - 1) View.GONE else View.VISIBLE

        btnNext.setOnClickListener {
            if (onBeforeNextClicked()) {
            if (currentStep < totalSteps - 1) { // Check if not on the last step
                currentStep++
                viewPager.setCurrentItem(currentStep, true) // Move to the next step
                btnBack.visibility = View.VISIBLE // Show Back button after moving to next step
                btnSave.visibility = if (currentStep == totalSteps - 1) View.VISIBLE else View.GONE // Show Save button on last step
                btnNext.visibility = if (currentStep == totalSteps - 1) View.GONE else View.VISIBLE // Hide Next button on last step
            }
            }
        }

        btnBack.setOnClickListener {
            if (currentStep > 0) { // Check if not on the first step
                currentStep--
                viewPager.setCurrentItem(currentStep, true) // Move to the previous step
                btnBack.visibility = if (currentStep == 0) View.INVISIBLE else View.VISIBLE // Hide Back button if on the first step
                btnSave.visibility = if (currentStep == totalSteps - 1) View.VISIBLE else View.GONE // Show Save button on last step
                btnNext.visibility = if (currentStep == totalSteps - 1) View.GONE else View.VISIBLE // Hide Next button on last step
            }
        }
            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ){
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_CODE_STORAGE_PERMISSION
                )
            }
        btnSave.setOnClickListener {
            onBeforeNextClicked()
            println(formdata)
            btnSave.visibility=View.INVISIBLE
            showLoader()
            ApiHelper.uploadFormData(
                formdata,

                onResponse = { response ->
                    // Handle API response

                    hideLoader()

                    btnSave.visibility=View.VISIBLE

                    Toast.makeText(this, "Sucessfully created.", Toast.LENGTH_SHORT).show()
                    println("Response: $response")
                    val intent = Intent(this, MainActivity::class.java)

                    startActivity(intent)
                    finish()
                },
                onFailure = { throwable ->
                    Toast.makeText(this, throwable.message, Toast.LENGTH_SHORT).show()
                    btnSave.visibility=View.VISIBLE
                    hideLoader()

                    // Handle API call failure
                    println("API call failed: ${throwable.message}")
                }
            )
        }
        }
    }

        private fun getTabTitle(position: Int): String {
        return when (position) {
            0 -> "Basic Details"
            1 -> "Family Details"
            2 -> "Additional Details"
            else -> "Step ${position + 1}"
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
                finish() // Close the app if the user chooses to exit
            }
            setCancelable(false)
            create().show()
        }
    }

}


class StepperPagerAdapter(private val beforeNextClickListener: BeforeNextClickListener, fm: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fm, lifecycle) {

    override fun getItemCount(): Int = 3 // Number of steps

    override fun createFragment(position: Int): Fragment {
        Log.d("StepperPagerAdapter", "Creating fragment for position: $position")

        return when (position) {
            0 -> BasicDetailsFragment().apply {
                setBeforeNextClickListener(beforeNextClickListener)

            }
            1 -> FamilyDetailsFragment().apply {
                setBeforeNextClickListener(beforeNextClickListener)
            }
            2 -> DeraDetailsFragment().apply {
                setBeforeNextClickListener(beforeNextClickListener)
            }
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }

}


//    override fun createFragment(position: Int): Fragment {
//        return when (position) {
//            0 -> BasicDetailsFragment()
//            1 -> FamilyDetailsFragment()
//            2 -> DeraDetailsFragment()
//            else -> throw IllegalArgumentException("Invalid position: $position")
//        }
//    }
//}