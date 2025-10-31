package com.longpt.projectll1.presentation.ui

import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.longpt.projectll1.databinding.ActivityChooseLocationBinding
import com.longpt.projectll1.utils.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class ChooseLocationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChooseLocationBinding
    private lateinit var map: MapView
    private var selectedMarker: Marker? = null
    private  var defaultLat: Double= 21.0285
    private  var defaultLng: Double = 105.8542
    private lateinit var mode: String
    private val geocoder by lazy { Geocoder(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mode = intent.getStringExtra("mode") ?: "add"
        defaultLat = intent.getDoubleExtra("lat", defaultLat)
        defaultLng = intent.getDoubleExtra("lng", defaultLng)

        // Cấu hình OSMdroid
        Configuration.getInstance().load(
            applicationContext, PreferenceManager.getDefaultSharedPreferences(applicationContext)
        )
        //setup map
        map = binding.mapView
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setBuiltInZoomControls(true)
        map.setMultiTouchControls(true)

        val mapController = map.controller
        mapController.setZoom(18.0)
        val startPoint = GeoPoint(defaultLat, defaultLng)
        mapController.setCenter(startPoint)

        placeMarker(startPoint)
        reverseGeocode(startPoint)

        //Search
        binding.edtSearch.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = v.text.toString().trim()
                if (query.isNotEmpty()) searchAddress(query)
                true
            } else false
        }

        map.overlays.add(object : Overlay() {
            override fun onSingleTapConfirmed(e: MotionEvent, mapView: MapView): Boolean {
                val proj = mapView.projection
                val geo = proj.fromPixels(e.x.toInt(), e.y.toInt()) as GeoPoint
                placeMarker(geo)
                reverseGeocode(geo)
                return true
            }
        })
        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                if (query.length > 2) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        try {
                            val url = "https://nominatim.openstreetmap.org/search?q=${
                                URLEncoder.encode(query, "UTF-8")
                            }&format=json&addressdetails=1&limit=5&countrycodes=vn"
                            val connection = URL(url).openConnection() as HttpURLConnection
                            connection.setRequestProperty(
                                "User-Agent",
                                "MyApp/1.0 (your_email@example.com)"
                            )
                            connection.connectTimeout = 5000
                            connection.readTimeout = 5000

                            val result =
                                connection.inputStream.bufferedReader().use { it.readText() }
                            connection.disconnect()
                            val jsonArray = JSONArray(result)
                            val suggestions = mutableListOf<String>()
                            for (i in 0 until jsonArray.length()) {
                                val name = jsonArray.getJSONObject(i).getString("display_name")
                                suggestions.add(name)
                            }
                            withContext(Dispatchers.Main) {
                                val adapter = ArrayAdapter(
                                    this@ChooseLocationActivity,
                                    android.R.layout.simple_dropdown_item_1line,
                                    suggestions
                                )
                                binding.edtSearch.setAdapter(adapter)
                                binding.edtSearch.showDropDown()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.edtSearch.setOnItemClickListener { parent, _, position, _ ->
            val selected = parent.getItemAtPosition(position) as String
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val url = "https://nominatim.openstreetmap.org/search?q=${
                        URLEncoder.encode(selected, "UTF-8")
                    }&format=json&addressdetails=1&limit=5&countrycodes=vn"
                    val connection = URL(url).openConnection() as HttpURLConnection
                    connection.setRequestProperty(
                        "User-Agent",
                        "MyApp/1.0 (your_email@example.com)"
                    )
                    connection.connectTimeout = 5000
                    connection.readTimeout = 5000

                    val result = connection.inputStream.bufferedReader().use { it.readText() }
                    connection.disconnect()
                    val json = JSONArray(result).getJSONObject(0)
                    val lat = json.getDouble("lat")
                    val lon = json.getDouble("lon")
                    val geo = GeoPoint(lat, lon)
                    withContext(Dispatchers.Main) {
                        map.controller.animateTo(geo)
                        map.controller.setZoom(18.0)
                        placeMarker(geo)

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        binding.btnSaveLocation.setOnClickListener {
            selectedMarker?.let { marker ->
                val geo = marker.position
                val addressText =
                    geocoder.getFromLocation(geo.latitude, geo.longitude, 1)?.firstOrNull()
                        ?.getAddressLine(0) ?: "${geo.latitude}, ${geo.longitude}"
                "Địa chỉ đã lưu: ${geo.latitude}, ${geo.longitude}".showToast(this)
                val resultIntent = Intent().apply {
                    putExtra("lat", geo.latitude)
                    putExtra("lng", geo.longitude)
                    putExtra("fullAddress", addressText)
                }
                setResult(RESULT_OK, resultIntent)
                finish()
            }
        }
        binding.iBtnBack.setOnClickListener{
            AlertDialog.Builder(this)
                .setTitle("Cảnh báo")
                .setMessage("Vị trí chưa được lưu! Bạn có muốn thoát không?")
                .setPositiveButton("Có") { _, _ ->
                    finish()
                }
                .setNegativeButton("Không", null)
                .show()
        }
    }

    private fun searchAddress(query: String) {
        Thread {
            try {
                val results = geocoder.getFromLocationName(query, 1)
                if (results?.isNotEmpty() == true) {
                    val address = results[0]
                    val geo = GeoPoint(address.latitude, address.longitude)
                    runOnUiThread {
                        map.controller.animateTo(geo)
                        map.controller.setZoom(18.0)
                        placeMarker(geo)
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "Không tìm thấy địa chỉ", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    private fun placeMarker(geo: GeoPoint) {
        selectedMarker?.let { map.overlays.remove(it) }
        val marker = Marker(map).apply {
            position = geo
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            icon = ContextCompat.getDrawable(
                this@ChooseLocationActivity, android.R.drawable.ic_menu_mylocation
            )
        }
        map.overlays.add(marker)
        selectedMarker = marker
        map.invalidate()
    }

    private fun reverseGeocode(geo: GeoPoint) {
        Thread {
            try {
                val addresses = geocoder.getFromLocation(geo.latitude, geo.longitude, 1)
                if (addresses?.isNotEmpty() == true) {
                    addresses[0].getAddressLine(0) ?: "${geo.latitude}, ${geo.longitude}"

                } else {
                    runOnUiThread {
                        "Không tìm thấy địa chỉ phù hợp!".showToast(this)
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    "Lỗi: ${e.message}".showToast(this)
                }
            }
        }.start()
    }


    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }
}