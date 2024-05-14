package com.example.tadm

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.InputFilter
import android.text.InputType
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.marginStart
import com.example.tadm.Interface.BeforeNextClickListener
import com.example.tadm.Interface.FragmentDataListener
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.util.Base64
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class FamilyDetailsFragment : Fragment(),BeforeNextClickListener {
    private val inputLayoutList = mutableListOf<TextInputLayout>()
    private lateinit var listener: FragmentDataListener
    private val inputLayoutMap = mutableMapOf<TextInputLayout, Int>()
    private lateinit var containerLayout: LinearLayout
    private val PICK_IMAGE_REQUEST_CODE = 100 // Define your request code
    private val CAMERA_PERMISSION_REQUEST_CODE = 102
    private val CAMERA_REQUEST_CODE = 101 // Request code for camera capture
    private var cameraImageUri: Uri? = null
    private var flag: Boolean = false
    private var path_string: String? = ""
    private  var imgViewID:Int=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentDataListener) {
            listener = context
        } else {
            throw ClassCastException("$context must implement FragmentDataListener")
        }
    }

    private lateinit var listLayout: LinearLayout
    private lateinit var scrollView: ScrollView
    private lateinit var addBtn: Button

    private var beforeNextClickListener: BeforeNextClickListener? = null

    fun setBeforeNextClickListener(listener: BeforeNextClickListener) {
        beforeNextClickListener = listener
    }

    override fun onBeforeNextClicked(): Boolean {
        val jsonArray = collectInputValues()
        val jsonString = jsonArray.toString()
        listener.onFamilyDataReceived(jsonString)
        return true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_family_details, container, false)
        listLayout = view.findViewById(R.id.input_box_layout)
        scrollView = view.findViewById(R.id.list_item)
        addBtn = view.findViewById(R.id.addBtn)
        containerLayout =
            scrollView.findViewById(R.id.input_box_layout) // Initialize containerLayout

        addNewForm(true)

        addBtn.setOnClickListener {
            addNewForm(false)
        }


        return view
    }
    private val MAX_IMAGE_SIZE_BYTES = 3 * 1024 * 1024 // 3MB in bytes


    private fun openImagePicker(imageView: ImageView) {
        flag = false

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
        val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickIntent.type = "image/*"
//        pickIntent.putExtra(
//            "imageViewId",
//            imageView.id
//        ) // Add imageViewId as an extra to pickIntent
        imgViewID=imageView.id
//        startActivityForResult(pickIntent, PICK_IMAGE_REQUEST_CODE)
        // Check if there is an activity available to handle the intents
        val pickActivities = requireActivity().packageManager.queryIntentActivities(
            pickIntent,
            PackageManager.MATCH_DEFAULT_ONLY
        )

//         Check if there are activities available to handle the intent
        if (!pickActivities.isEmpty()) {
            startActivityForResult(
                pickIntent,
                PICK_IMAGE_REQUEST_CODE
            ) // Start activity for result with pickIntent

        } else {
            // Handle the case where no activity is available to handle the image picker intent
            Toast.makeText(
                requireContext(),
                "No app available to pick an image.",
                Toast.LENGTH_SHORT
            ).show()
        }



}
    private fun convertImageToBase64(uri: Uri?, context: Context): String? {
        uri ?: return null
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val buffer = ByteArrayOutputStream()
        val bufferLength = 1024
        val data = ByteArray(bufferLength)
        var length: Int
        while (inputStream.read(data, 0, bufferLength).also { length = it } != -1) {
            buffer.write(data, 0, length)
        }
        val base64String = Base64.encodeToString(buffer.toByteArray(), Base64.DEFAULT)
        inputStream.close()
        return base64String
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == PICK_IMAGE_REQUEST_CODE && data != null) {
                // Get the URI of the selected image
                cameraImageUri = data.data
                cameraImageUri?.let { uri ->
                    // Get the imageViewId from the intent extras
//                    val imageViewId = data.getIntExtra("imageViewId", 0)
                    Log.d("ImageViewId", "Received imageViewId: $imgViewID")
                    val compressedUri = compressImage(uri)
                    compressedUri?.let { compressed ->
                        if (imgViewID != 0) {
                            // Find the ImageView using the imageViewId and set the image URI
                            val imageView: ImageView? = requireView().findViewById(imgViewID)
                            imgViewID = 0
                            Log.d("ImageViewId", "Found ImageView: $imageView")
                            imageView?.setImageURI(compressed)
                            imageView?.tag = compressed
                        } else {
                            Log.e("ImageViewId", "Invalid imageViewId received: $imgViewID")
                        }

                    }
                }
            } else if (requestCode == CAMERA_REQUEST_CODE) {
                // Get the URI of the captured image from the camera
                println("camera ==> $cameraImageUri")
                val uri = cameraImageUri

                uri?.let {
                    val compressedUri = compressImage(uri)
                    compressedUri?.let {
                        val imageView: ImageView? = requireView().findViewById(imgViewID)
                        imageView?.setImageURI(it)
                        imageView?.tag = it
                    }
//                    // Get the imageViewId from the intent extras
////                    val imageViewId = data.getIntExtra("imageViewId", 0)
//                    Log.d("ImageViewId", "Received imageViewId from camera: $imgViewID")
//                    // Find the ImageView using the imageViewId and set the image URI
//                    val imageView: ImageView? = requireView().findViewById(imgViewID)
//                    imgViewID=0
//                    Log.d("ImageViewId", "Found ImageView from camera: $imageView")
//                    imageView?.setImageURI(uri)
//                    imageView?.tag = uri

                }
                cameraImageUri=null
            }
        }
    }



    private fun openCamera(imageView: ImageView) {
        flag = true
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
            imgViewID=imageView.id
            // Pass the imageView ID as an extra to the cameraIntent
//            cameraIntent.putExtra("imageViewId", imageView.id)
//            println(cameraImageUri)

            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
        }
    }

    private fun createImageFile(): File? {
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? =
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
//        Log.i("ImageFile", "Image file path: ${imageFile.absolutePath}")


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

//    private fun compressImage(uri: Uri): Uri? {
//        val inputStream = requireContext().contentResolver.openInputStream(uri)
//        val options = BitmapFactory.Options()
//        options.inSampleSize = 2 // Adjust the sample size as needed for compression
//
//        val bitmap = BitmapFactory.decodeStream(inputStream, null, options)
//        inputStream?.close()
//
//        // Log the size of the original image
//        val originalSize = bitmap?.byteCount ?: 0
//        Log.i("ImageCompression", "Original image size: $originalSize bytes")
//
//        val compressedBitmap = bitmap?.let {
//            val outputStream = ByteArrayOutputStream()
//            it.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
//            BitmapFactory.decodeStream(ByteArrayInputStream(outputStream.toByteArray()))
//        }
//        val timeStamp: String =SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
//
//        val tempFile = File(requireContext().cacheDir, "compressed_image${timeStamp}_.jpg")
//        val outputStream = FileOutputStream(tempFile)
//        compressedBitmap?.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
//        outputStream.flush()
//        outputStream.close()
//
//        val compressedSize = tempFile.length()
//        Log.i("ImageCompression", "Compressed image size: $compressedSize bytes")
//
//        return if (tempFile.exists()) Uri.fromFile(tempFile) else null
//    }


    // Update the onActivityResult method to use compressImage and set the compressed image to the ImageView



    private fun addNewForm(isFirstRecord:Boolean) {
        // Inflate the input box layout for a new form
        val inputLayout = LayoutInflater.from(requireContext())
            .inflate(R.layout.input_box_layout, scrollView, false) as LinearLayout

        // Customize the new input layout here if needed

        // Create a new TextInputLayout and add the inflated LinearLayout as its child
        val textInputLayout = TextInputLayout(requireContext())
        textInputLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val mobno:TextInputEditText = inputLayout.findViewById(R.id.mobileTxt)
        val inputMobFilter = InputFilter.LengthFilter(10)
        mobno.filters = arrayOf(inputMobFilter)

        val imageView: ImageView = inputLayout.findViewById(R.id.img)
        imageView.id = View.generateViewId()
        inputLayoutMap[textInputLayout] =  imageView.id
        val cameraBtn: ImageButton = inputLayout.findViewById(R.id.cameraBtn)
        val addPhotoBtn: ImageButton = inputLayout.findViewById(R.id.addPhotoBtn2)

        cameraBtn.setOnClickListener {
            openCamera(imageView)
        }

        addPhotoBtn.setOnClickListener {
            openImagePicker(imageView)
        }

        textInputLayout.addView(inputLayout)


        // Add the delete button to the TextInputLayout


        if (!isFirstRecord) {
            val deleteBtn = MaterialButton(requireContext())
            deleteBtn.text = "Delete"
            deleteBtn.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {

                    gravity = Gravity.CENTER
                    marginStart = resources.getDimensionPixelSize(R.dimen.margin_start)
                    marginEnd= resources.getDimensionPixelSize(R.dimen.margin_start)

                    gravity = Gravity.END

            }

            deleteBtn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red))
            deleteBtn.setOnClickListener {
                // Remove the corresponding input layout and view when the delete button is clicked
                inputLayoutList.remove(textInputLayout)
                containerLayout.removeView(textInputLayout)
            }


            textInputLayout.addView(deleteBtn)
        }

        // Add the new TextInputLayout to the container layout inside the ScrollView
        val containerLayout = scrollView.findViewById<LinearLayout>(R.id.input_box_layout)
        containerLayout.addView(textInputLayout)
        inputLayoutList.add(textInputLayout)
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

    private fun collectInputValues(): JSONArray {
        val jsonArray = JSONArray()
        println(inputLayoutList)
        for (inputLayout in inputLayoutList) {
            val jsonObject = JSONObject()

            val nameTxt = inputLayout.findViewById<TextInputEditText>(R.id.nameTxt)
            val ageTxt = inputLayout.findViewById<TextInputEditText>(R.id.agetxt)
            val genderTxt = inputLayout.findViewById<TextInputEditText>(R.id.genderTxt)
            val relationTxt = inputLayout.findViewById<TextInputEditText>(R.id.RelationTxt)
            val aadharTxt = inputLayout.findViewById<TextInputEditText>(R.id.aadharTxt)
            val mobileTxt = inputLayout.findViewById<TextInputEditText>(R.id.mobileTxt)

            jsonObject.put("name", nameTxt?.text.toString())
            jsonObject.put("age", ageTxt?.text.toString())
            jsonObject.put("gender", genderTxt?.text.toString())
            jsonObject.put("relation", relationTxt?.text.toString())
            jsonObject.put("aadhar_no", aadharTxt?.text.toString())
            jsonObject.put("mobile_no", mobileTxt?.text.toString())
            val imageViewId = inputLayoutMap[inputLayout]
            println("img id $imageViewId")
            val imageView= imageViewId?.let { inputLayout.findViewById<ImageView>(it) }
            println(imageView)
            var imageUri = imageView?.tag as? Uri // Assuming you set the URI as a tag for the ImageView
            println("img $imageUri")
            if (!flag) {
                try {

                    path_string = imageUri?.let { getImagePathFromUri(it, requireContext()) }

                }
                catch(e: Exception) {
                    e.printStackTrace()
                }
            }

            if (imageUri != null) {

                // Convert the image to base64 format
                val base64Image = convertImageToBase64(imageUri, requireContext())

                var  finalDataUri:String=""
                if (!flag) {
                     finalDataUri = "data:${path_string?.substringAfterLast('/')};base64:$base64Image"

                println(path_string?.substringAfterLast('/'))
                }else{
                     finalDataUri = "data:${imageUri.encodedPath?.substringAfterLast('/')};base64:$base64Image"

                }

                jsonObject.put("image_data", finalDataUri)

                jsonObject.put("image_base64",finalDataUri)

            }

            jsonArray.put(jsonObject)
        }
        println(jsonArray)
        return jsonArray
    }


}