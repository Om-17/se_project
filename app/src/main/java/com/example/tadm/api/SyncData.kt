import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
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
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Calendar

class SyncData {
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(Config.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService: ApiGetNewEntryService = retrofit.create(ApiGetNewEntryService::class.java)

    fun syncData(context: Context) {
        val dbHelper = DatabaseHelper(context)
        val db = dbHelper.writableDatabase

        CoroutineScope(Dispatchers.IO).launch {

            try {
                val response = apiService.getPersonDetails().execute()

                if (response.isSuccessful) {
                    val personDetails = response.body()
                    println(personDetails)
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
                                    put("d_placevislastyear", personDetail.d_placevislastyear)
                                    put("d_familydeatils", Gson().toJson(personDetail.d_familydeatils))
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
                        } finally {
                            db.endTransaction()
                        }
                    }
                } else {
                    // Handle unsuccessful API call
                }

            } catch (e: Exception) {
                // Handle API call or JSON parsing errors
            } finally {
                db.close()
            }
    }
    }

    fun printSQLiteData(context: Context) {
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
            "d_routeuse",
            "d_placevislastyear",
            "d_familydetails",
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
                val d_familydetailsJson = it.getString(it.getColumnIndexOrThrow("d_familydetails"))
                val d_familydetailsList: List<FamilyDetail> = Gson().fromJson(
                    d_familydetailsJson,
                    object : TypeToken<List<FamilyDetail>>() {}.type
                )
                val d_deradetailsJson = it.getString(it.getColumnIndexOrThrow("d_deradetails"))
                val d_deradetailsMap: Map<String, String> = Gson().fromJson(
                    d_deradetailsJson,
                    object : TypeToken<Map<String, String>>() {}.type
                )

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
                 d_picurl=it.getString(it.getColumnIndexOrThrow("d_picurl")),
                    d_deradetails = d_deradetailsMap,
                    d_familydeatils =  d_familydetailsList,

                )
                Log.d("SQLiteData", "Person Detail: $personDetail")
            }
        }
    }
}

//        val currentDateTime = Calendar.getInstance().timeInMillis.toString()
//        val syncContentValues = ContentValues().apply {
//            put("datetime", currentDateTime)
//        }
//        db.insert("syncData", null, syncContentValues)
