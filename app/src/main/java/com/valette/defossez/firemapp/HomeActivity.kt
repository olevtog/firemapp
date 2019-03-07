package com.valette.defossez.firemapp

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.valette.defossez.firemapp.adapter.AddressAdapter
import com.valette.defossez.firemapp.controller.FireworksController
import com.valette.defossez.firemapp.entity.Favorite
import com.valette.defossez.firemapp.entity.Firework
import com.valette.defossez.firemapp.service.AddressService
import com.valette.defossez.firemapp.service.LocationService
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.dialog_filter.view.*
import kotlinx.android.synthetic.main.slide_up_layout_back.*
import kotlinx.android.synthetic.main.slide_up_layout_front.*
import java.text.SimpleDateFormat
import java.util.*


class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    val TIME_MOVE_CAMERA = 1000
    val ZOOM_CAMERA = 11.0f
    val REMOVE_LATITUDE = 0.07
    val EMAIL_ADRESS = "defossez.valette@gmail.com"

    private lateinit var mMap: GoogleMap
    private val controller = FireworksController()
    private lateinit var currentMarker: Marker
    private var markers: HashMap<String, Marker> = HashMap()
    private var favoriteState: Boolean = false
    private lateinit var locationService: LocationService
    private val ctx = this
    val addressService = AddressService(this)
    var addresses = ArrayList<String>()
    var timer = Timer()
    val DELAY: Long = 500

    override fun onCreate(savedInstanceState: Bundle?) {

        //prevent map lags
        window.setBackgroundDrawableResource(R.drawable.background_drawer)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        nav_view.setNavigationItemSelectedListener(this)

        // pour afficher ma map
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Mettre un point d'arret sur le sliding
        sliding_layout.anchorPoint = .4f

        // Pour ouvrir le menu
        buttonOpenMenu.setOnClickListener {
            closeKeyboard()
            drawer_layout.openDrawer(GravityCompat.START)
        }

        //init location service
        locationService = LocationService(this)

        //Aller a la position actuelle
        boutonLocalisation.setOnClickListener {
            closeKeyboard()
            moveToUserLocation()
        }

        //filter
        filterFloatingButton.setOnClickListener {
            displayDialogFilter()
        }

        initAutocomplete()
    }


    override fun onResume() {
        super.onResume()
        try {
            mMap.clear()
            markers.clear()
            controller.getAll(this)
        } catch (e: Exception) {
        }
    }


    fun closeKeyboard() {
        val inputManager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.SHOW_FORCED) //close keyboard
    }

/*
  _ __ ___   ___ _ __  _   _
 | '_ ` _ \ / _ \ '_ \| | | |
 | | | | | |  __/ | | | |_| |
 |_| |_| |_|\___|_| |_|\__,_|

*/

    // on surcharge le bouton retour pour le menu, afin qu'il se ferme
    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                val intent = Intent(this, FormAddFireworkActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_gallery -> {
                val intent = Intent(this, FavoriteActivity::class.java)
                startActivityForResult(intent, 1)
            }
            R.id.nav_info -> {
                val intent = Intent(this, AboutActivity::class.java)
                startActivity(intent)
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }


/*
  _ __ ___   __ _ _ __
 | '_ ` _ \ / _` | '_ \
 | | | | | | (_| | |_) |
 |_| |_| |_|\__,_| .__/
                 |_|
 */

    override fun onMarkerClick(marker: Marker?): Boolean {
        return true
    }

    /**
     * Manipulates the map_aubergine once available.
     * This callback is triggered when the map_aubergine is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_simple))

        // pour supprimer la barre en bas pour afficher la direction et un lien vers maps
        mMap.uiSettings.isMapToolbarEnabled = false
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = false
        // pour ajouter une marge afin de compass ne soit pas caché ainsi que le logo google
        mMap.setPadding(0, 120, 0, 0)

        // mettre la camera par defaut a cette emplacement
        mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(47.35, 2.2)))
        mMap.animateCamera(CameraUpdateFactory.zoomTo(6.0f))

        controller.getAll(this)

        mMap.setOnMarkerClickListener { marker ->
            controller.getByIdMap(marker.tag.toString(), this)
            currentMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
            currentMarker = marker
            true
        }

        mMap.setOnCameraMoveListener {
            closeKeyboard()
        }
        mMap.setOnMapClickListener {
            if(sliding_layout.panelState != SlidingUpPanelLayout.PanelState.HIDDEN) {
                sliding_layout.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
            }
        }
    }

    fun displayMarkers(fireworks: ArrayList<Firework>) {
        mMap.clear()
        for (f in fireworks) {
            var options = MarkerOptions()
            options.position(LatLng(f.latitude, f.longitude))
            options.title(f.title)
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            currentMarker = mMap.addMarker(options)
            currentMarker.tag = f.id
            markers.put(f.id, currentMarker)
        }
    }

    fun openDetail(firework: Firework) {
        closeKeyboard()
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(firework.latitude - REMOVE_LATITUDE, firework.longitude), ZOOM_CAMERA), TIME_MOVE_CAMERA, null)

        favoriteState = FiremappApp.database.favoriteController().getByFirework(firework.id)
        val format = SimpleDateFormat("dd/MM/yyy hh:mm")
        textViewDate.text = format.format(firework.date)
        textViewAddress.text = firework.address
        textViewTitle.text = firework.title
        textViewDescription.text = firework.description
        sliding_layout.panelState = SlidingUpPanelLayout.PanelState.ANCHORED
        if (favoriteState) {
            favorite.setBackgroundResource(R.drawable.ic_favorite)
        } else {
            favorite.setBackgroundResource(R.drawable.ic_favorite_border)
        }
        favorite.setOnClickListener {
            if (favoriteState) {
                FiremappApp.database.favoriteController().delete(firework.id)
                favorite.setBackgroundResource(R.drawable.ic_favorite_border)
            } else {
                FiremappApp.database.favoriteController().insert(Favorite(firework.id))
                favorite.setBackgroundResource(R.drawable.ic_favorite)
            }
            favoriteState = !favoriteState
        }
        route.setOnClickListener {
            val gmmIntentUri = Uri.parse("geo:" + firework.latitude + "," + firework.longitude + "?q=" + firework.address)
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }
        // when you want to signal event
        signalButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/html"
            intent.putExtra(Intent.EXTRA_EMAIL, EMAIL_ADRESS)
            intent.putExtra(Intent.EXTRA_SUBJECT, "[Firemapp] Anomalie" + firework.id)
            intent.putExtra(Intent.EXTRA_TEXT, "Motif de votre signalement : ")
            startActivity(Intent.createChooser(intent, "Signaler l'évenement pas email :"))
        }
        // to add event in calendar
        calendarButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_INSERT)
            intent.type = "vnd.android.cursor.item/event"
            val startTime = firework.date.time
            val endTime = firework.date.time + 60 * 60 * 1000
            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime)
            intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime)
            intent.putExtra(CalendarContract.Events.TITLE, "FireMapp " + firework.title)
            intent.putExtra(CalendarContract.Events.DESCRIPTION, firework.description)
            intent.putExtra(CalendarContract.Events.EVENT_LOCATION, firework.description)
            startActivity(intent)
        }
    }


    private fun moveToUserLocation() {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(locationService.getLatitude() - REMOVE_LATITUDE, locationService.getLongitude()), ZOOM_CAMERA), TIME_MOVE_CAMERA, null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                val id = data.extras.getString("id")
                val latitude = data.extras.getDouble("latitude")
                val longitude = data.extras.getDouble("longitude")
                controller.getByIdMap(markers[id]!!.tag.toString(), this)
                currentMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                markers[id]!!.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                currentMarker = markers[id]!!
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude - REMOVE_LATITUDE, longitude), ZOOM_CAMERA), 1, null)
            }
        }
    }

    // DIALOG

    val cal = Calendar.getInstance()

    private fun displayDialogFilter() {
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_filter, null)
        val mBuilder = AlertDialog.Builder(this).setView(mDialogView)

        initDateTimePickers(mDialogView)

        val mAlertDialog = mBuilder.show()

        mDialogView.search.setOnClickListener {
            controller.getAllBetweenDates(SimpleDateFormat("dd/MM/yy").parse(mDialogView.inputStart.text.toString()), SimpleDateFormat("dd/MM/yy").parse(mDialogView.inputEnd.text.toString()), this)
            mAlertDialog.dismiss()
            //get text from EditTexts of custom layout
        }

        mDialogView.cancel.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }

    private fun initDateTimePickers(mDialogView: View) {
        val startDate = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateStartDate(mDialogView)
        }

        val endDate = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateEndDate(mDialogView)
        }

        mDialogView.inputStart.setOnClickListener {
            DatePickerDialog(this, startDate, cal
                    .get(Calendar.YEAR), cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        mDialogView.inputEnd.setOnClickListener {
            DatePickerDialog(this, endDate, cal
                    .get(Calendar.YEAR), cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun updateStartDate(mDialogView: View) {
        val format = "dd/MM/yy"
        val sdf = SimpleDateFormat(format, Locale.FRANCE)
        mDialogView.inputStart.setText(sdf.format(cal.time))
    }

    private fun updateEndDate(mDialogView: View) {
        val format = "dd/MM/yy"
        val sdf = SimpleDateFormat(format, Locale.FRANCE)
        mDialogView.inputEnd.setText(sdf.format(cal.time))
    }

    val geocoder = Geocoder(this)

    private fun initAutocomplete() {
        var adapter = AddressAdapter(this, R.layout.dropdown, addresses)
        search.threshold = 1
        search.setAdapter<AddressAdapter>(adapter)
        search.imeOptions = EditorInfo.IME_ACTION_DONE

        search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(text : Editable?) {
                getAddressFromInput(text.toString(), adapter)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        search.setOnEditorActionListener { input, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_DONE){
                try {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(geocoder.getFromLocationName(input.text.toString(), 1)[0].latitude - REMOVE_LATITUDE, geocoder.getFromLocationName(input.text.toString(), 1)[0].longitude), ZOOM_CAMERA), TIME_MOVE_CAMERA, null)
                    false
                }catch (e : java.lang.Exception){
                    false
                }
            } else {
                false
            }
        }
    }

    private fun getAddressFromInput(text : String, adapter: ArrayAdapter<String>){
        progressBar.visibility = View.VISIBLE
        if(text.length > 3){
            timer.cancel()
            timer = Timer()
            timer.schedule(
                    object : TimerTask() {
                        override fun run() {
                            ctx.runOnUiThread {
                                if (text.isNotEmpty() && addressService.getAddresses(text, 3).isNotEmpty()) {
                                    var address = addressService.getAddresses(text, 5)[0].getAddressLine(0)
                                    adapter.clear()
                                    adapter.add(address)
                                }
                                progressBar.visibility = View.GONE
                            }

                        }
                    },
                    DELAY
            )
        }else{
            adapter.clear()
        }
    }
}
