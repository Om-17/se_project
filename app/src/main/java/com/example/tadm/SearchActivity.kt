package com.example.tadm

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tadm.model.FamilyDetail
import com.example.tadm.model.PersonDetail
import com.example.tadm.util.DatabaseHelper
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchActivity : AppCompatActivity() {
    private lateinit var searchCategory:MaterialAutoCompleteTextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PersonAdapter
    private  lateinit var searchTxt:TextInputEditText
    private lateinit var searchBtn:ImageButton

    private var personList: MutableList<PersonDetail> = mutableListOf()
    private lateinit var notdataTxt:TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchCategory = findViewById(R.id.scTxt)
        notdataTxt = findViewById(R.id.notdata) ?: return
        searchTxt = findViewById(R.id.searchTxt) ?: return
        searchBtn = findViewById(R.id.search_btn) ?: return

        val categ = arrayOf("Name", "Aadhar No", "Phone No")
        val cateAdapter = NoFilterArrayAdapter(this, R.layout.dropdown_item, categ)
        searchCategory.setAdapter(cateAdapter)

        searchCategory.setOnClickListener {
            searchCategory.showDropDown()
        }

        searchCategory.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                searchCategory.showDropDown()
            }
        }

        searchCategory.inputType = InputType.TYPE_NULL
        searchCategory.threshold = Int.MAX_VALUE
        recyclerView = findViewById(R.id.recyclerView) ?: return

        adapter = PersonAdapter(this, personList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        searchBtn.setOnClickListener(::searchData)
        fetchDataFromSQLite()

    }

    private fun searchData(view: View){
        try{
        val searchtxt = searchTxt.text.toString()
        val catTxt = searchCategory.text.toString()

        val dbHelper = DatabaseHelper(this)
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
            "d_picurl",
            "d_routeuse",
            "d_placevislastyear",
            "d_familydeatils",
            "d_deradetails"
        )

        val selection: String
        val selectionArgs: Array<String>

        // Set the selection based on the category text
        when (catTxt) {
            "Name" -> {
                selection = "d_name LIKE ?"
                selectionArgs = arrayOf("%$searchtxt%")
            }
            "Aadhar No" -> {
                selection = "d_duration LIKE ?"
                selectionArgs = arrayOf("%$searchtxt%")
            }
            "Phone No" -> {
                selection = "d_mobno LIKE ?"
                selectionArgs = arrayOf("%$searchtxt%")
            }
            // Add cases for other categories if needed
            else -> {
                // Default case if no category matches
                selection = ""
                selectionArgs = emptyArray()
            }
        }

        val cursor: Cursor? = db.query(
            "personDetail",
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        personList.clear()
            adapter.notifyDataSetChanged()


        cursor?.use {
            if (it.count == 0) {
                // Show a message or perform some action when data is not found
                notdataTxt.visibility= View.VISIBLE
            } else {
                notdataTxt.visibility= View.GONE

                while (it.moveToNext()) {
                val personDetail = PersonDetail(
                    id = -1, // The actual id value is not retrieved from the database in this example
                    d_id = it.getString(it.getColumnIndexOrThrow("d_id")),
                    d_name = it.getString(it.getColumnIndexOrThrow("d_name")),
                    d_fathername = it.getString(it.getColumnIndexOrThrow("d_fathername")),
                    d_age = it.getInt(it.getColumnIndexOrThrow("d_age")).toString(),

                    d_address = it.getString(it.getColumnIndexOrThrow("d_address")),
                    d_religion = it.getString(it.getColumnIndexOrThrow("d_religion")),
                    d_maritalstatus = it.getString(it.getColumnIndexOrThrow("d_maritalstatus")),
                    d_mobno = it.getLong(it.getColumnIndexOrThrow("d_mobno")),
                    d_destination = it.getString(it.getColumnIndexOrThrow("d_destination")),
                    d_duration = it.getString(it.getColumnIndexOrThrow("d_duration")),
                    d_routeuse = it.getString(it.getColumnIndexOrThrow("d_routeuse")),
                    d_placevislastyear = it.getString(it.getColumnIndexOrThrow("d_placevislastyear")),
                    d_picurl = it.getString(it.getColumnIndexOrThrow("d_picurl")),
                    d_deradetails = Gson().fromJson(
                        it.getString(it.getColumnIndexOrThrow("d_deradetails")),
                        object : TypeToken<Map<String, String>>() {}.type
                    ),
                    d_familydeatils = Gson().fromJson(
                        it.getString(it.getColumnIndexOrThrow("d_familydeatils")),
                        object : TypeToken<List<FamilyDetail>>() {}.type
                    )
                )
                personList.add(personDetail)
            }
            }
        }

        // Update the RecyclerView with the search results
        adapter.notifyDataSetChanged()
        }
        catch (e:Exception){
            e.printStackTrace()
        }

    }

    private fun fetchDataFromSQLite() {

        try {
            val dbHelper = DatabaseHelper(this)
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
                "d_picurl",
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
            personList.clear()
            adapter.notifyDataSetChanged()

            cursor?.use {

                if (it.count == 0) {
                    // Show a message or perform some action when data is not found
                    notdataTxt.visibility = View.VISIBLE
                } else {
                    notdataTxt.visibility = View.GONE

                    while (it.moveToNext()) {

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
                            d_picurl = it.getString(it.getColumnIndexOrThrow("d_picurl")),
                            d_age = it.getInt(it.getColumnIndexOrThrow("d_age")).toString(),


                            d_deradetails = Gson().fromJson(
                                it.getString(it.getColumnIndexOrThrow("d_deradetails")),
                                object : TypeToken<Map<String, String>>() {}.type
                            ),
                            d_familydeatils = Gson().fromJson(
                                it.getString(it.getColumnIndexOrThrow("d_familydeatils")),
                                object : TypeToken<List<FamilyDetail>>() {}.type
                            )
                        )
//                        println("list $personDetail")
                        personList.add(personDetail)
                    }
                }
            }

            adapter.notifyDataSetChanged()

        }
        catch (e:Exception){
            e.printStackTrace()
        }
    }

}
class NoFilterArrayAdapter(context: Context, resource: Int, objects: Array<String>) :
    ArrayAdapter<String>(context, resource, objects) {

    private val originalItems: Array<String> = objects.copyOf()

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                results.values = originalItems // Return the entire array as results
                results.count = originalItems.size
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                notifyDataSetChanged() // Notify adapter of changes (in this case, no changes)
            }
        }
    }
}

