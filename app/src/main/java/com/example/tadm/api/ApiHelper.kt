package com.example.tadm.api

import android.util.Log
import com.example.tadm.model.NewEntryFormData
import retrofit2.Call
import retrofit2.Callback
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.File
object ApiHelper {
    private val service = RetrofitClient.service

    fun uploadFormData(
        formData: NewEntryFormData,
        // Add other parameters as needed
        onResponse: (ResponseBody?) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val d_id = RequestBody.create(MediaType.parse("text/plain"), formData.d_id)

        val name = RequestBody.create(MediaType.parse("text/plain"), formData.d_name)
        val fnameBody = RequestBody.create(MediaType.parse("text/plain"), formData.d_fathername)
        val addressBody = RequestBody.create(MediaType.parse("text/plain"), formData.d_address)
        // Create RequestBody objects for other parameters as well
        val d_religion = RequestBody.create(MediaType.parse("text/plain"), formData.d_religion)
        val d_familydeatils = RequestBody.create(MediaType.parse("text/plain"), formData.d_familydeatils)
        val d_mobno = RequestBody.create(MediaType.parse("text/plain"), formData.d_mobno)
        val d_duration = RequestBody.create(MediaType.parse("text/plain"), formData.d_duration)
        val d_destination = RequestBody.create(MediaType.parse("text/plain"), formData.d_destination)
        val d_placevislastyear = RequestBody.create(MediaType.parse("text/plain"), formData.d_placevislastyear)
        val d_maritalstatus = RequestBody.create(MediaType.parse("text/plain"), formData.d_maritalstatus)
        val d_routeuse = RequestBody.create(MediaType.parse("text/plain"), formData.d_routeuse)
        val d_deradetails = RequestBody.create(MediaType.parse("text/plain"), formData.d_deradetails)

        val imageUri = formData.imageData
        val imageFile = File(imageUri?.path ?: "")


            val imageRequestBody = RequestBody.create(MediaType.parse("image/*"), imageFile)
            val imagePart = MultipartBody.Part.createFormData("d_picurl", imageFile.name, imageRequestBody)

            val call = service.uploadFormData(d_id,name,fnameBody,addressBody,d_religion, d_maritalstatus, d_mobno, d_destination, d_routeuse, d_placevislastyear, d_duration, d_familydeatils, d_deradetails)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        onResponse(response.body())
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Unknown error"
                        println(errorBody)
                        onFailure(Throwable(errorBody))

                        if(response.code()!=400){
                            onFailure(Throwable("API call failed with status code: ${response.code()}"))
                        }}
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    onFailure(t)
                }
            })
            Log.e("FileError", "Image file does not exist at the specified path.")

//        val call = service.uploadFormData(d_id,name,fnameBody,addressBody,d_religion, d_maritalstatus, d_mobno, d_destination, d_routeuse, d_placevislastyear, d_duration, d_familydeatils, d_deradetails,imagePart)

    }
}
