package com.example.tadm.api
import com.example.tadm.model.PersonDetail
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Part
interface NewEntryApiServiceInterface {
    @Multipart
    @POST("fillDeatils")
    fun uploadFormData(
        @Part("d_id") d_id: RequestBody,
        @Part("d_name") name: RequestBody,
        @Part("d_fathername") fname: RequestBody,
        @Part("d_address") address: RequestBody,
        @Part("d_religion") d_religion: RequestBody,
        @Part("d_maritalstatus") d_maritalstatus: RequestBody,
        @Part("d_mobno") d_mobno: RequestBody,
        @Part("d_destination") d_destination: RequestBody,
        @Part("d_routeuse") d_routeuse: RequestBody,
        @Part("d_placevislastyear") d_placevislastyear: RequestBody,
        @Part("d_duration") d_duration: RequestBody,
        @Part("d_familydeatils") d_familydeatils:RequestBody,
        @Part("d_deradetails") d_deradetails:RequestBody,
        @Part("d_age") d_age:RequestBody,
        @Part d_picurl: MultipartBody.Part? = null
    ): Call<ResponseBody>
}


interface ApiGetNewEntryService {
    @GET("fillDeatils") // Endpoint path relative to BASE_URL
    fun getPersonDetails(): Call<List<PersonDetail>> // Assuming PersonDetail is your data class
}
