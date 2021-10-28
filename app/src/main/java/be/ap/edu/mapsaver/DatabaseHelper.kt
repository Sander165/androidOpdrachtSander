package be.ap.edu.mapsaver

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.ArrayList

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_PUBS)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(DELETE_TABLE_PUBS)
        onCreate(db)
    }

    // loop through all rows and adding to Students list
    fun allPubs(): ArrayList<String> {
        val pubsArrayList = ArrayList<String>()
        var name: String
        val db = this.readableDatabase

        val projection = arrayOf(KEY_ID, DISPLAY_NAME, LONG, LAT)
        val sortOrder = "${DISPLAY_NAME} ASC"

        val cursor = db.query(
            TABLE_PUBS,
            projection,
            null,
            null,
            null,
            null,
            sortOrder
        )

        with(cursor) {
            while (moveToNext()) {
                val displayName = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME))
                val long = cursor.getString(cursor.getColumnIndex(LONG))
                val lat = cursor.getString(cursor.getColumnIndex(LAT))

                pubsArrayList.add(displayName + " " + long + " " + lat)
            }
        }
        cursor.close()

        return pubsArrayList
    }

    fun addPub(displayName: String, long: String, lat: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(DISPLAY_NAME, displayName)
        values.put(LONG, long)
        values.put(LAT, lat)


        return db.insert(TABLE_PUBS, null, values)
    }

    companion object {

        var DATABASE_NAME = "pubs_database"
        private val DATABASE_VERSION = 8
        private val TABLE_PUBS = "pubs"
        private val KEY_ID = "id"
        private val DISPLAY_NAME = "name"
        private val LONG = "long"
        private val LAT = "lat"


        private val CREATE_TABLE_PUBS = ("CREATE TABLE "
                + TABLE_PUBS + "(" + KEY_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT," + DISPLAY_NAME + " TEXT, " + LONG + " TEXT, " + LAT + " TEXT );")

        private val DELETE_TABLE_PUBS = "DROP TABLE IF EXISTS $TABLE_PUBS"

    }
}