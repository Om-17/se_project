package com.example.tadm.util

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "lc_db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
//        db.execSQL("""
//            DROP TABLE IF EXISTS syncData
//        """.trimIndent())
        db.execSQL(
            """ CREATE TABLE IF NOT EXISTS personDetail (Id INTEGER PRIMARY KEY AUTOINCREMENT,d_id Text ,d_name TEXT ,d_fathername TEXT ,d_address LONGTEXT ,d_religion TEXT ,d_maritalstatus TEXT ,d_mobno TEXT ,d_destination TEXT ,d_routeuse TEXT ,d_placevislastyear TEXT ,d_duration TEXT ,d_familydeatils LONGTEXT,d_deradetails LONGTEXT ,d_picurl LONGTEXT,d_age INTEGER   ) """
        )
        db.execSQL(
            """CREATE TABLE IF NOT EXISTS syncData (
                Id INTEGER PRIMARY KEY AUTOINCREMENT,
                datetime TEXT NOT NULL
            )"""
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle database upgrade if needed
    }
}
