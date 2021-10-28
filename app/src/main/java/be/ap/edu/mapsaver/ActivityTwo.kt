package be.ap.edu.mapsaver

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import java.util.ArrayList


class ActivityTwo : AppCompatActivity() {
    private var pubNames: TextView? = null
    private var arrayList: ArrayList<String>? = null
    private var databaseHelper: DatabaseHelper? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_two)

        databaseHelper = DatabaseHelper(this)

        pubNames = findViewById(R.id.textView3) as TextView

        arrayList = databaseHelper!!.allPubs()
        if (arrayList!!.size > 0) {
            var txt: String = ""
            for (i in arrayList!!.indices) {
                txt += "\n" + arrayList!![i]
            }
            pubNames!!.text = txt
        }
    }
}