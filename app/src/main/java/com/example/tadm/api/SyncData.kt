import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import android.widget.Toast
import com.example.tadm.api.ApiGetNewEntryService
import com.example.tadm.api.Config
import com.example.tadm.model.FamilyDetail
import com.example.tadm.model.PersonDetail
import com.example.tadm.util.DatabaseHelper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Calendar

class SyncData {
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(Config.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService: ApiGetNewEntryService =
        retrofit.create(ApiGetNewEntryService::class.java)

    fun createDataSync(context: Context){

    }
    suspend fun syncData(context: Context): Boolean {
        val dbHelper = DatabaseHelper(context)
        val db = dbHelper.writableDatabase

         return withContext(Dispatchers.IO) { try {

                val response = apiService.getPersonDetails().execute()

            if (response.isSuccessful) {
                val personDetails = response.body()
                println("sync res $personDetails")
                personDetails?.let {
                    db.beginTransaction()
                    try {
                        db.delete("personDetail", null, null) // Clear existing data
                        personDetails.forEach { personDetail ->
                            val contentValues = ContentValues().apply {
                                put("d_id", personDetail.d_id)
                                    put("d_name", personDetail.d_name)
                                    put("d_fathername", personDetail.d_fathername)
                                    put("d_address", personDetail.d_address)
                                    put("d_religion", personDetail.d_religion)
                                    put("d_maritalstatus", personDetail.d_maritalstatus)
                                    put("d_mobno", personDetail.d_mobno.toString())
                                    put("d_destination", personDetail.d_destination)
                                    put("d_duration", personDetail.d_duration)
                                    put("d_routeuse", personDetail.d_routeuse)
                                    put("d_picurl",personDetail.d_picurl)
                                    put("d_placevislastyear", personDetail.d_placevislastyear)
                                    put("d_age", personDetail.d_age)
                                    put("is_create",true)
                                    put("is_sync",true)
                                    put(
                                        "d_familydeatils",
                                        Gson().toJson(personDetail.d_familydeatils)
                                    )
                                    put("d_deradetails", Gson().toJson(personDetail.d_deradetails))
                            }
                            db.insert("personDetail", null, contentValues)
                        }

                        val currentDateTime = Calendar.getInstance().timeInMillis.toString()
                        val syncContentValues = ContentValues().apply {
                            put("datetime", currentDateTime)
                        }
                        db.insert("syncData", null, syncContentValues)

                        db.setTransactionSuccessful()
                        return@withContext true  // Synchronization successful
                    } finally {
                        db.endTransaction()
                    }

                } ?:  return@withContext false    // Body is null, synchronization failed
            } else {
                println("API call unsuccessful, synchronization failed")
                return@withContext false // API call unsuccessful, synchronization failed
            }

        } catch (e: Exception) {
             withContext(Dispatchers.Main) {
                 Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
             }
            println(e)
            println(" Exception occurred, synchronization failed")
             return@withContext false // Exception occurred, synchronization failed
        } finally {
            db.close()
                printSQLiteData(context)

            // Print SQLite data here
        }
    }
    }

//    fun syncData(context: Context) {
//        val dbHelper = DatabaseHelper(context)
//        val db = dbHelper.writableDatabase
//
//        CoroutineScope(Dispatchers.IO).launch {
//
//            try {
//                val response = apiService.getPersonDetails().execute()
//
//                if (response.isSuccessful) {
//                    val personDetails = response.body()
//                    println(personDetails)
//                    personDetails?.let {
//                        db.beginTransaction()
//                        try {
//                            db.delete("personDetail", null, null) // Clear existing data
//                            personDetails.forEach { personDetail ->
//                                val contentValues = ContentValues().apply {
//                                    put("d_id", personDetail.d_id)
//                                    put("d_name", personDetail.d_name)
//                                    put("d_fathername", personDetail.d_fathername)
//                                    put("d_address", personDetail.d_address)
//                                    put("d_religion", personDetail.d_religion)
//                                    put("d_maritalstatus", personDetail.d_maritalstatus)
//                                    put("d_mobno", personDetail.d_mobno.toString())
//                                    put("d_destination", personDetail.d_destination)
//                                    put("d_duration", personDetail.d_duration)
//                                    put("d_routeuse", personDetail.d_routeuse)
//                                    put("d_placevislastyear", personDetail.d_placevislastyear)
//                                    put(
//                                        "d_familydeatils",
//                                        Gson().toJson(personDetail.d_familydeatils)
//                                    )
//                                    put("d_deradetails", Gson().toJson(personDetail.d_deradetails))
//                                }
//                                db.insert("personDetail", null, contentValues)
//                            }
//
//                            val currentDateTime = Calendar.getInstance().timeInMillis.toString()
//                            val syncContentValues = ContentValues().apply {
//                                put("datetime", currentDateTime)
//                            }
//                            db.insert("syncData", null, syncContentValues)
//
//
//
//                            db.setTransactionSuccessful()
//                        } finally {
//                            db.endTransaction()
//                        }
//                    }
//                } else {
//                    // Handle unsuccessful API call
//                }
//
//            } catch (e: Exception) {
//                // Handle API call or JSON parsing errors
//            } finally {
//                db.close()
//                printSQLiteData(context)
//            }
//        }
//    }

    fun printSQLiteData(context: Context) {
        try{
        val dbHelper = DatabaseHelper(context)
        val db: SQLiteDatabase = dbHelper.readableDatabase

        val projection = arrayOf(
            "d_id",
            "d_name",
            "d_fathername",
            "d_address",
            "d_religion",
            "d_maritalstatus",
            "d_mobno",
            "d_destination",
            "d_duration",
            "d_age",
            "d_routeuse",
            "d_placevislastyear",
            "d_familydeatils",
            "d_deradetails"
        )

        val cursor: Cursor? = db.query(
            "personDetail",
            projection,
            null,
            null,
            null,
            null,
            null
        )

        cursor?.use {
            while (it.moveToNext()) {
                val d_familydetailsJson = it.getString(it.getColumnIndexOrThrow("d_familydeatils"))

                var d_familydetailsList: List<FamilyDetail> = emptyList()
                if (!d_familydetailsJson.isNullOrEmpty()) {
                    d_familydetailsList = Gson().fromJson(
                        d_familydetailsJson,
                        object : TypeToken<List<FamilyDetail>>() {}.type
                    )
                }

                val d_deradetailsJson = it.getString(it.getColumnIndexOrThrow("d_deradetails"))
                var d_deradetailsMap: Map<String, String> = emptyMap()
                if (!d_deradetailsJson.isNullOrEmpty()) {
                    d_deradetailsMap = Gson().fromJson(
                        d_deradetailsJson,
                        object : TypeToken<Map<String, String>>() {}.type
                    )
                }

                val personDetail = PersonDetail(
                    id = -1, // The actual id value is not retrieved from the database in this example
                    d_id = it.getString(it.getColumnIndexOrThrow("d_id")),
                    d_name = it.getString(it.getColumnIndexOrThrow("d_name")),
                    d_fathername = it.getString(it.getColumnIndexOrThrow("d_fathername")),
                    d_address = it.getString(it.getColumnIndexOrThrow("d_address")),
                    d_religion = it.getString(it.getColumnIndexOrThrow("d_religion")),
                    d_maritalstatus = it.getString(it.getColumnIndexOrThrow("d_maritalstatus")),
                    d_mobno = it.getLong(it.getColumnIndexOrThrow("d_mobno")),
                    d_destination = it.getString(it.getColumnIndexOrThrow("d_destination")),
                    d_duration = it.getString(it.getColumnIndexOrThrow("d_duration")),
                    d_routeuse = it.getString(it.getColumnIndexOrThrow("d_routeuse")),
                    d_placevislastyear = it.getString(it.getColumnIndexOrThrow("d_placevislastyear")),
                    d_deradetails = d_deradetailsMap,
                    d_familydeatils = d_familydetailsList,
                    d_age = it.getInt(it.getColumnIndexOrThrow("d_age")).toString(),

                )

                Log.d("SQLiteData", "Person Detail: $personDetail")
            }
        }}
        catch (e:Exception){
            e.printStackTrace()
        }
    }



}


//        val currentDateTime = Calendar.getInstance().timeInMillis.toString()
//        val syncContentValues = ContentValues().apply {
//            put("datetime", currentDateTime)
//        }
//        db.insert("syncData", null, syncContentValues)
