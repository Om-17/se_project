package com.example.tadm

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.Context
import android.os.Environment
import android.text.Editable
import android.text.InputFilter
import android.widget.ImageButton
import androidx.core.content.FileProvider

import androidx.fragment.app.Fragment
import com.example.tadm.Interface.BeforeNextClickListener
import com.example.tadm.Interface.FragmentDataListener
import com.example.tadm.model.NewEntryFormData
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar
import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Random

class BasicDetailsFragment : Fragment(), BeforeNextClickListener {
    private val PICK_IMAGE_REQUEST_CODE = 100 // Define your request code
    private val CAMERA_PERMISSION_REQUEST_CODE = 102
    private val CAMERA_REQUEST_CODE = 101 // Request code for camera capture
    private var cameraImageUri: Uri? = null // Variable to store the URI of the captured image
    private lateinit var pvlyTxt: TextInputEditText
    private lateinit var idTxt: TextInputEditText
    private lateinit var nameTxt: TextInputEditText
    private lateinit var ageTxt: TextInputEditText
    private lateinit var fnameTxt: TextInputEditText
    private lateinit var addressTxt: AutoCompleteTextView
    private lateinit var religionTxt: TextInputEditText
    private lateinit var msTxt: AutoCompleteTextView
    private lateinit var mobileTxt: TextInputEditText
    private var flag:Boolean=false
    private lateinit var detnationTxt: TextInputEditText
    private lateinit var durationTxt: TextInputEditText
    private lateinit var routeTxt: TextInputEditText
    private lateinit var listener: FragmentDataListener
    private var path_string: String? =""

    // Extension function to convert String to Editable
    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentDataListener) {
            listener = context
        } else {
            throw ClassCastException("$context must implement FragmentDataListener")
        }
    }

    private var beforeNextClickListener: BeforeNextClickListener? = null

    fun setBeforeNextClickListener(listener: BeforeNextClickListener) {
        beforeNextClickListener = listener
    }

    fun generateShortId(length: Int): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        val random = Random()
        return (1..length)
            .map { chars[random.nextInt(chars.length)] }
            .joinToString("")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_basic_details, container, false)
        val maritalStatusOptions = arrayOf("Single", "Married", "Divorced", "Widowed")
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, maritalStatusOptions)
        val autoCompleteTextView: AutoCompleteTextView = view.findViewById(R.id.msTxt)
        autoCompleteTextView.setAdapter(adapter)
        autoCompleteTextView.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                autoCompleteTextView.showDropDown()
            }
        }
        nameTxt = view.findViewById(R.id.nameTxt)
        idTxt = view.findViewById(R.id.idTxt)
        val maxLength = 3
        val inputFilter = InputFilter.LengthFilter(maxLength)
        idTxt.filters = arrayOf(inputFilter)
//        var randomId = UUID.randomUUID().toString()
//        val randomId = generateShortId(7)
//

        // Convert the random ID String to Editable
//        val editableId = randomId.toEditable()

        // Set the Editable random ID as the text of idTxt
//        idTxt.text = editableId

        fnameTxt = view.findViewById(R.id.fnameTxt)
        ageTxt=view.findViewById(R.id.ageTxt)
        addressTxt = view.findViewById(R.id.addressTxt)
        religionTxt = view.findViewById(R.id.religionTxt)
        msTxt = view.findViewById(R.id.msTxt)
        mobileTxt = view.findViewById(R.id.mobileTxt)
        val inputMobFilter = InputFilter.LengthFilter(10)
        mobileTxt.filters = arrayOf(inputMobFilter)
        detnationTxt = view.findViewById(R.id.detnationTxt)
        durationTxt = view.findViewById(R.id.durationTxt)
        routeTxt = view.findViewById(R.id.routeTxt)
        pvlyTxt = view.findViewById(R.id.pvlyTxt)

        pvlyTxt.setOnClickListener {
            showDatePickerDialog()
        }


        // Add image picker functionality to your button
        val addPhotoBtn: ImageButton = view.findViewById(R.id.addPhotoBtn)
        val cameraBtn: ImageButton = view.findViewById(R.id.cameraBtn)
        cameraBtn.setOnClickListener {
            openCamera()
        }
        addPhotoBtn.setOnClickListener {
            openImagePicker()
        }

//        img picker

        return view
    }

    @SuppressLint("IntentReset", "QueryPermissionsNeeded")
    private fun openImagePicker() {
        flag=false

        // Check if the app has camera permission
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request camera permission if not granted
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
            return
        }

        // Create an intent to pick an image from the gallery
        val pickIntent = Intent(Intent.ACTION_PICK, Images.Media.EXTERNAL_CONTENT_URI)
        pickIntent.type = "image/*"

        // Check if there is an activity available to handle the intents
        val pickActivities = requireActivity().packageManager.queryIntentActivities(
            pickIntent,
            PackageManager.MATCH_DEFAULT_ONLY
        )

        // Check if there are activities available to handle the intent
        if (!pickActivities.isEmpty()) {
            val chooserIntent = Intent.createChooser(pickIntent, "Select Image")

            startActivityForResult(chooserIntent, PICK_IMAGE_REQUEST_CODE)

        } else {
            // Handle the case where no activity is available to handle the image picker intent
            Toast.makeText(
                requireContext(),
                "No app available to pick an image.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == PICK_IMAGE_REQUEST_CODE && data != null) {
                // Get the URI of the selected image
                cameraImageUri = data.data
                cameraImageUri?.let {
                    val compressedUri =  compressImage(it)

                    // Display the selected image in the ImageView
                    val imageView: ImageView = requireView().findViewById(R.id.img)
                    imageView.setImageURI(compressedUri)
                }
            } else if (requestCode == CAMERA_REQUEST_CODE ) {
                // Display the captured image from the camera
                val imageView: ImageView = requireView().findViewById(R.id.img)
                val compressedUri = cameraImageUri?.let { compressImage(it) }
                compressedUri?.let {
                imageView.setImageURI(it)}
            }
        }
    }

    private fun openCamera() {
        flag=true
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request camera permission if not granted
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
            return
        }

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile: File? = createImageFile()
        photoFile?.let {
            cameraImageUri =
                FileProvider.getUriForFile(requireContext(), "com.tadm.fileprovider", it)
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
        }
    }
    private fun createImageFile(): File? {
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? =
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
        Log.i("ImageFile", "Image file path: ${imageFile.absolutePath}")
        path_string=imageFile.absolutePath
        return imageFile // Return the original file if renaming fails

    }
    private fun compressImage(uri: Uri): Uri? {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val options = BitmapFactory.Options()
        options.inSampleSize = 2 // Initial sample size

        val bitmap = BitmapFactory.decodeStream(inputStream, null, options)
        inputStream?.close()

        // Log the size of the original image
        val originalSize = bitmap?.byteCount ?: 0
        Log.i("ImageCompression", "Original image size: $originalSize bytes")

        val targetSize = 100628 // Target size in bytes
        var quality = 80 // Initial quality

        val tempFile = File(requireContext().cacheDir, "compressed_image_${System.currentTimeMillis()}.jpg")

        var outputStream: FileOutputStream? = null
        try {
            outputStream = FileOutputStream(tempFile)
            bitmap?.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            outputStream.flush()
            outputStream.close()

            val compressedSize = tempFile.length()
            Log.i("ImageCompression", "Compressed image size: $compressedSize bytes")

            // Adjust quality iteratively until target size is reached
            while (compressedSize > targetSize && quality > 0) {
                outputStream = FileOutputStream(tempFile)
                quality -= 10 // Decrease quality by 10 units
                bitmap?.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                outputStream.flush()
                outputStream.close()

                val newCompressedSize = tempFile.length()
                Log.i("ImageCompression", "New compressed image size: $newCompressedSize bytes")
            }

            return Uri.fromFile(tempFile)
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        } finally {
            outputStream?.close()
        }
    }
    private fun getImagePathFromUri(uri: Uri, context: Context): String? {
        var path: String? = null
        val projection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA)
        val contentResolver = context.contentResolver

        contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                path = cursor.getString(columnIndex)
            }
        }

        return path
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission granted, open the image picker
                openImagePicker()
            } else {
                // Camera permission denied, show a message or handle accordingly
                Toast.makeText(requireContext(), "Camera permission denied.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }


    override fun onBeforeNextClicked(): Boolean {
        println("next")
        var img: Uri? = null
        if (cameraImageUri != null) {
            img = cameraImageUri
        }
        println(cameraImageUri)
        println(img)

        if (img != null) {
            if (!flag) {
                try {

                path_string = getImagePathFromUri(img, requireContext())
                println(path_string)
                }
                catch(e: Exception) {

                }
            }
        }
        val formData = NewEntryFormData(
            idTxt.text.toString(),
            nameTxt.text.toString(),
            fnameTxt.text.toString(),
            addressTxt.text.toString(),
            religionTxt.text.toString(),
            msTxt.text.toString(),
            mobileTxt.text.toString(),
            detnationTxt.text.toString(),
            durationTxt.text.toString(),
            routeTxt.text.toString(),
            pvlyTxt.text.toString(),
            img,
            null,
            null,path_string,
            flag,
            ageTxt.text.toString()
        )
        listener.onBasicDataReceived(formData)
        // Handle the event before changing to the next fragment
        // Return true if you want to allow the navigation to the next fragment, false otherwise
        return true
    }

//    @SuppressLint("DiscouragedApi")
//    private fun showYearPickerDialog() {
//        val calendar = Calendar.getInstance()
//        val year = calendar.get(Calendar.YEAR)
//
//        val datePickerDialog = DatePickerDialog(
//            requireContext(),
//            { _: DatePicker, selectedYear: Int, _, _ ->
//                // Handle the selected year here
//                pvlyTxt.setText(selectedYear.toString())
//            },
//            year,  // Initial year
//            0,     // Month (0-based index)
//            1      // Day
//        )
//
//        // Set the maximum year to the current year
//        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
//
//        // Set the date picker mode to show only the year picker
//        datePickerDialog.datePicker.findViewById<View>(resources.getIdentifier("year", "id", "android"))?.visibility = View.VISIBLE
//        datePickerDialog.datePicker.findViewById<View>(resources.getIdentifier("month", "id", "android"))?.visibility = View.GONE
//        datePickerDialog.datePicker.findViewById<View>(resources.getIdentifier("day", "id", "android"))?.visibility = View.GONE
//
//        // Show the dialog
//        datePickerDialog.show()
//    }

    @SuppressLint("DiscouragedApi")
    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
                // Handle the selected date here
                val selectedDate = "$selectedDayOfMonth-${selectedMonth + 1}-$selectedYear"
                pvlyTxt.setText(selectedDate)
            },
            year,  // Initial year
            month, // Initial month
            dayOfMonth // Initial day of month
        )

        // Set the maximum date to the current date
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()

        // Show the dialog
        datePickerDialog.show()
    }


//private fun showYearPickerDialog() {
//    val calendar = Calendar.getInstance()
//    val year = calendar.get(Calendar.YEAR).toLong()
//
//    val datePickerDialog = MaterialDatePicker.Builder.datePicker()
//        .setTitleText("Select Year")
//        .setSelection(year) // Set default selection to current year
//        .build()
//
//    datePickerDialog.addOnPositiveButtonClickListener { selectedDate ->
//        val selectedCalendar = Calendar.getInstance()
//        selectedCalendar.timeInMillis = selectedDate
//        val selectedYear = selectedCalendar.get(Calendar.YEAR)
//        pvlyTxt.setText(selectedYear.toString())
//    }
//
//    datePickerDialog.show(requireActivity().supportFragmentManager, "YearPicker")
//}

}