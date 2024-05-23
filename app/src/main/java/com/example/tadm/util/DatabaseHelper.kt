package com.example.tadm.util

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.tadm.model.FamilyDetail
import com.example.tadm.model.NewEntryFormData
import com.example.tadm.model.PersonDetail
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "lc_db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
//        db.execSQL("""
//            DROP TABLE IF EXISTS syncData
//        """.trimIndent())
        db.execSQL(
         """
             CREATE TABLE IF NOT EXISTS personDetail (
                 Id INTEGER PRIMARY KEY AUTOINCREMENT,
                 d_id TEXT,
                 d_name TEXT,
                 d_fathername TEXT,
                 d_address TEXT,
                 d_religion TEXT,
                 d_maritalstatus TEXT,
                 d_mobno TEXT,
                 d_destination TEXT,
                 d_routeuse TEXT,
                 d_placevislastyear TEXT,
                 d_duration TEXT,
                 d_familydeatils TEXT,
                 d_deradetails TEXT,
                 d_picurl LONGTEXT,
                 d_age INTEGER,
                 is_sync BOOLEAN DEFAULT 'false',
                 is_create BOOLEAN DEFAULT 'false'
             )

         """.trimIndent()
          )
        db.execSQL(
            """CREATE TABLE IF NOT EXISTS syncData (
                Id INTEGER PRIMARY KEY AUTOINCREMENT,
                datetime TEXT NOT NULL
            )"""
        )
    }
    fun doesPersonDetailExist(d_id: String): Boolean {
        val db = readableDatabase
        val query = "SELECT COUNT(*) FROM personDetail WHERE d_id = ?"
        val cursor = db.rawQuery(query, arrayOf(d_id))

        var exists = false
        if (cursor.moveToFirst()) {
            exists = cursor.getInt(0) > 0
        }
        cursor.close()
        return exists
    }

    fun getUnsyncedPersonDetails(): List<NewEntryFormData> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM personDetail WHERE is_sync = 0", null)
        val personDetails = mutableListOf<NewEntryFormData>()

        if (cursor.moveToFirst()) {
            do {
//                val d_familydetailsJson = cursor.getString(cursor.getColumnIndexOrThrow("d_familydeatils"))
//
//                var d_familydetailsList: List<FamilyDetail> = emptyList()
//                if (!d_familydetailsJson.isNullOrEmpty()) {
//                    d_familydetailsList = Gson().fromJson(
//                        d_familydetailsJson,
//                        object : TypeToken<List<FamilyDetail>>() {}.type
//                    )
//                }
//
//                val d_deradetailsJson = cursor.getString(cursor.getColumnIndexOrThrow("d_deradetails"))
//                var d_deradetailsMap: Map<String, String> = emptyMap()
//                if (!d_deradetailsJson.isNullOrEmpty()) {
//                    d_deradetailsMap = Gson().fromJson(
//                        d_deradetailsJson,
//                        object : TypeToken<Map<String, String>>() {}.type
//                    )
//                }

                val personDetail = NewEntryFormData(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("Id")),
                    d_id = cursor.getString(cursor.getColumnIndexOrThrow("d_id")),
                    d_name = cursor.getString(cursor.getColumnIndexOrThrow("d_name")),
                    d_fathername = cursor.getString(cursor.getColumnIndexOrThrow("d_fathername")),
                    d_address = cursor.getString(cursor.getColumnIndexOrThrow("d_address")),
                    d_religion = cursor.getString(cursor.getColumnIndexOrThrow("d_religion")),
                    d_maritalstatus = cursor.getString(cursor.getColumnIndexOrThrow("d_maritalstatus")),
                    d_mobno = cursor.getString(cursor.getColumnIndexOrThrow("d_mobno")),
                    d_destination = cursor.getString(cursor.getColumnIndexOrThrow("d_destination")),
                    d_routeuse = cursor.getString(cursor.getColumnIndexOrThrow("d_routeuse")),
                    d_placevislastyear = cursor.getString(cursor.getColumnIndexOrThrow("d_placevislastyear")),
                    d_duration = cursor.getString(cursor.getColumnIndexOrThrow("d_duration")),
                    d_familydeatils = cursor.getString(cursor.getColumnIndexOrThrow("d_familydeatils")),
                    d_deradetails = cursor.getString(cursor.getColumnIndexOrThrow("d_deradetails")),
                    d_url = cursor.getString(cursor.getColumnIndexOrThrow("d_picurl")),
                    d_age = cursor.getInt(cursor.getColumnIndexOrThrow("d_age")).toString(),
                    is_sync = cursor.getInt(cursor.getColumnIndexOrThrow("is_sync")) > 0,

                )
                personDetails.add(personDetail)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return personDetails
    }
    fun insertPersonDetail(personDetail: NewEntryFormData): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("d_id", personDetail.d_id)
            put("d_name", personDetail.d_name)
            put("d_fathername", personDetail.d_fathername)
            put("d_address", personDetail.d_address)
            put("d_religion", personDetail.d_religion)
            put("d_maritalstatus", personDetail.d_maritalstatus)
            put("d_mobno", personDetail.d_mobno)
            put("d_destination", personDetail.d_destination)
            put("d_routeuse", personDetail.d_routeuse)
            put("d_placevislastyear", personDetail.d_placevislastyear)
            put("d_duration", personDetail.d_duration)
            put("d_familydeatils", personDetail.d_familydeatils)
            put("d_deradetails", personDetail.d_deradetails)
            put("d_picurl", personDetail.d_url)
            put("d_age", personDetail.d_age)
            put("is_sync", false)

        }
        return db.insert("personDetail", null, values)
    }
    fun updatePersonDetailSyncStatus(id: String, is_sync: Boolean) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("is_sync", is_sync)
        }
        db.update("personDetail", values, "id=?", arrayOf(id))
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle database upgrade if needed
    }
}
