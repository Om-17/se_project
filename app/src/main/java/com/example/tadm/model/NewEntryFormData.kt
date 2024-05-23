package com.example.tadm.model

import android.net.Uri

data class NewEntryFormData(
    val d_id:String="",
    val d_name: String="",
    val d_fathername: String="",
    val d_address: String="",
    val d_religion: String="",
    val d_maritalstatus: String="",
    val d_mobno: String="",
    val d_destination: String="",
    val d_duration: String="",
    val d_routeuse: String="",
    val d_placevislastyear: String? =null,
    val imageData: Uri?=null,// Uri for the image data
    var d_familydeatils: String? =null,
    var d_deradetails: String? =null,
    var d_url: String? = "",
    var is_camera:Boolean= false,
    var d_age:String?=null,
    var id:Int=0,
    var is_sync:Boolean=false
)

data class PersonDetail(
    val id: Int,
    val d_id: String,
    val d_name: String,
    val d_picurl: String?="",
    val d_fathername: String,
    val d_address: String,
    val d_religion: String,
    val d_maritalstatus: String,
    val d_mobno: Long,
    val d_destination: String,
    val d_duration: String,
    val d_routeuse: String,
    val d_placevislastyear: String,
    val d_familydeatils: List<FamilyDetail>?= emptyList(),
    val d_deradetails: Map<String, String>? = null,
    var isExpanded: Boolean = false ,// default value is false
    var d_age:String="",
    var is_sync:Boolean=false
)

data class FamilyDetail(
    val age: String,
    val name: String,
    val gender: String,
    val relation: String,
    val aadhar_no: String,
    val mobile_no: String,
    val image_data:String,
    val image_base64:String
)
