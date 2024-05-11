package com.example.tadm.util

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class Base64Convert {
    // Function to convert image URI to Base64 string
    fun convertImageToBase64(context: Context, imageUri: Uri): String? {
        var base64String: String? = null
        val contentResolver: ContentResolver = context.contentResolver

        try {
            val inputStream = contentResolver.openInputStream(imageUri)
            if (inputStream != null) {
                val byteArray = inputStream.readBytes()
                base64String = Base64.encodeToString(byteArray, Base64.DEFAULT)
                inputStream.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return base64String
    }


}
//// Usage example
//val imageUri = Uri.parse("content://media/external_primary/images/media/1000010628")
//val base64String = convertImageToBase64(context, imageUri)
//println("Base64 string: $base64String")
