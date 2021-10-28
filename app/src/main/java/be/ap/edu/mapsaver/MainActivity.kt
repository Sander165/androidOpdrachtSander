package be.ap.edu.mapsaver

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Button
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.ItemizedOverlay
import org.osmdroid.views.overlay.OverlayItem
import java.io.File
import java.net.URL
import java.util.*

class MainActivity : Activity() {

    private var mMapView: MapView? = null
    private var mMyLocationOverlay: ItemizedOverlay<OverlayItem>? = null
    private var items = ArrayList<OverlayItem>()
    private val urlNominatim = "https://nominatim.openstreetmap.org/search?q=pubs+mortsel&format=json"
    private var databaseHelper: DatabaseHelper? = null
    private var listButton: Button? = null



    override fun onDestroy() {
        databaseHelper!!.close()
        super.onDestroy()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        databaseHelper = DatabaseHelper(this)

        val osmConfig = Configuration.getInstance()
        osmConfig.userAgentValue = packageName
        val basePath = File(cacheDir.absolutePath, "osmdroid")
        osmConfig.osmdroidBasePath = basePath
        val tileCache = File(osmConfig.osmdroidBasePath, "tile")
        osmConfig.osmdroidTileCache = tileCache

        setContentView(R.layout.activity_main)
        mMapView = findViewById(R.id.mapview)
        listButton = findViewById(R.id.button3)


        loadSavedPreferences()

        listButton.setOnClickListener {
            intent = Intent(this, ActivityTwo::class.java)
            startActivity(intent)
        }

    }

    private fun loadSavedPreferences() {
        val sharedPreferences = PreferenceManager
            .getDefaultSharedPreferences(this)
        val isFirstStart = sharedPreferences.getBoolean("firstStart", true)
        if (isFirstStart) {
            initMap()
        } else {
            initMapDB()
        }
    }

    private fun initMapDB() {
        val arrayList: List<String> = databaseHelper!!.allPubs()
        if (arrayList!!.size > 0) {
            val splittedList: List<String> = arrayList.split(" ").toTypedArray()
            for (i in arrayList!!.indices) {
                addMarker(GeoPoint(splittedList[i+3].toDouble(), splittedList[i+2].toDouble()), splittedList[i+1])
            }
        }
    }

    private fun initMap() {
        mMapView?.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)

        mMapView?.controller?.setZoom(17.0)


        val task = MyAsyncTask()
        val array = task.execute(URL(urlNominatim))

        for(pub in array) {
            databaseHelper!!.addPub(pub.displayName, pub.long, pub.lat)
            addMarker(GeoPoint(pub.lat, pub.long), pub.displayName)
        }

        val sharedPreferences = PreferenceManager
            .getDefaultSharedPreferences(this)
        val editor = sharedPreferences.edit()
        editor.putBoolean("firstStart", false)
        editor.commit()

    }

    private fun addMarker(geoPoint: GeoPoint, name: String) {
        items.add(OverlayItem(name, name, geoPoint))
        mMyLocationOverlay = ItemizedIconOverlay(items, null, applicationContext)
        mMapView?.overlays?.add(mMyLocationOverlay)
    }

    private fun setCenter(geoPoint: GeoPoint, name: String) {
        mMapView?.controller?.setCenter(geoPoint)
        addMarker(geoPoint, name)
    }

    override fun onPause() {
        super.onPause()
        mMapView?.onPause()
    }

    override fun onResume() {
        super.onResume()
        mMapView?.onResume()
    }

    @SuppressLint("StaticFieldLeak")
    inner class MyAsyncTask : AsyncTask<URL, Int, String>() {

        override fun doInBackground(vararg params: URL?): String {
            val client = OkHttpClient()
            val response: Response
            val request = Request.Builder()
                .url(params[0]!!)
                .build()
            response = client.newCall(request).execute()

            return response.body!!.string()
        }


        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
        }
    }
}
