package com.example.tadm.util

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "lc_db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            DROP TABLE IF EXISTS syncData 
        """.trimIndent())
        db.execSQL(
            """ CREATE TABLE IF NOT EXISTS personDetail (Id INTEGER PRIMARY KEY AUTOINCREMENT,d_id Text Not Null,d_name TEXT Not Null,d_fathername TEXT Not Null,d_address LONGTEXT Not Null,d_religion TEXT Not Null,d_maritalstatus TEXT Not Null,d_mobno TEXT Not Null,d_destination TEXT Not Null,d_routeuse TEXT Not Null,d_placevislastyear TEXT Not Null,d_duration TEXT Not Null,d_familydeatils LONGTEXT ,d_picurl LONGTEXT  ) """
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
