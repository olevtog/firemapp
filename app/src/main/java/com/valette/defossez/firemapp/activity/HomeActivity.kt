package com.valette.defossez.firemapp.activity

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.location.Location
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
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.valette.defossez.firemapp.FiremappApp
import com.valette.defossez.firemapp.R
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

    val TIME_MOVE_CAMERA_MAX = 1500
    val TIME_MOVE_CAMERA_MIN = 500
    val ZOOM_CAMERA = 12.5f
    val REMOVE_LATITUDE = 0.02
    val DISTANCE_MOVE = 10000 //distance a choisir si move long ou court de la camera
    val DELAY_FIREWORK = 60 * 60 * 1000
    val EMAIL_ADRESS = "defossez.valette@gmail.com"

    lateinit var mMap: GoogleMap
    private val controller = FireworksController()
    private lateinit var currentMarker: Marker
    private var markers: HashMap<String, Marker> = HashMap()
    private var listFireworks : HashMap<String, Firework> = HashMap()
    private var favoriteState: Boolean = false
    private lateinit var locationService: LocationService
    private val ctx = this
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
    }


    override fun onResume() {
        super.onResume()
        try {
            mMap.clear()
            markers.clear()
            controller.getAllAfterDate(Date(),this)
        } catch (e: Exception) {
        }
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
                moveToUserLocation(latitude, longitude, time = 1)
            }
        }
    }


    fun closeKeyboard() {
        val inputManager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.SHOW_FORCED) //close keyboard
    }


    // -------------- MENU

    // on surcharge le bouton retour pour le menu, afin qu'il se ferme
    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else if (sliding_layout.panelState == SlidingUpPanelLayout.PanelState.EXPANDED) {
            sliding_layout.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
        }
        else {
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


    // -------------- MAP

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

        controller.getAllAfterDate(Date(), this)

        mMap.setOnMarkerClickListener { marker ->
            controller.getByIdMap(marker.tag.toString(), this)
            var currentFirework = listFireworks [currentMarker.tag.toString()]
            if(currentFirework!!.date < Date()){
                currentMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
            }else{
                currentMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            }
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
            currentMarker = marker
            true
        }

        mMap.setOnCameraMoveListener {
            closeKeyboard()
        }
        mMap.setOnMapClickListener {
            if (sliding_layout.panelState != SlidingUpPanelLayout.PanelState.HIDDEN) {
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
            if(f.date < Date()){
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
            }else{
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            }
            currentMarker = mMap.addMarker(options)
            currentMarker.tag = f.id
            markers.put(f.id, currentMarker)
            listFireworks.put(f.id, f)
        }
    }

    fun openDetail(firework: Firework) {
        closeKeyboard()

        favoriteState = FiremappApp.database.favoriteController().getByFirework(firework.id)
        val format = SimpleDateFormat("dd/MM/yyy HH:mm")
        textViewDate.text = format.format(firework.date)
        textViewAddress.text = firework.address
        textViewTitle.text = firework.title
        textViewDescription.text = firework.description

        sliding_layout.panelState = SlidingUpPanelLayout.PanelState.ANCHORED
        moveToUserLocation(firework.latitude, firework.longitude, isAnchored = true)
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
        localisation.setOnClickListener {
            val gmmIntentUri = Uri.parse("geo:" + firework.latitude + "," + firework.longitude + "?q=" + firework.address)
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }
        // when you want to signal event
        signalButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO) // it's not ACTION_SEND
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_SUBJECT, "[Firemapp] Retour sur l'application")
            intent.putExtra(Intent.EXTRA_TEXT, "Merci pour votre retour sur le feu d'artifice numéro ${firework.id} (veuillez ne pas supprimer ce numéro)." +
                    "\nMotif de votre retour : ")
            intent.data = Uri.parse("mailto:"+EMAIL_ADRESS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        // to add event in calendar
        calendarButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_INSERT)
            intent.type = "vnd.android.cursor.item/event"
            val startTime = firework.date.time
            val endTime = firework.date.time + DELAY_FIREWORK
            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime)
            intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime)
            intent.putExtra(CalendarContract.Events.TITLE, "FireMapp " + firework.title)
            intent.putExtra(CalendarContract.Events.DESCRIPTION, firework.description)
            intent.putExtra(CalendarContract.Events.EVENT_LOCATION, firework.address)
            startActivity(intent)
        }
    }

    private fun moveToUserLocation(latitude: Double = locationService.getLatitude(), longitude: Double = locationService.getLongitude(), time: Int = TIME_MOVE_CAMERA_MAX, isAnchored:Boolean = false) {
        if(latitude == 0.0 || longitude == 0.0){
            Toast.makeText(this, "Position GPS non trouvée", Toast.LENGTH_LONG).show()
            return
        }
        val actualLat = mMap.cameraPosition.target.latitude
        val actualLong = mMap.cameraPosition.target.longitude
        val loc1 = Location("")
        loc1.latitude = actualLat
        loc1.longitude = actualLong
        val loc2 = Location("")
        loc2.latitude = latitude
        loc2.longitude = longitude
        val distanceInMeters = loc1.distanceTo(loc2)
        var newTime = time
        if (newTime == TIME_MOVE_CAMERA_MAX) {
            if(distanceInMeters < DISTANCE_MOVE){
                newTime = TIME_MOVE_CAMERA_MIN
            }
        }
        if (isAnchored || sliding_layout.panelState == SlidingUpPanelLayout.PanelState.ANCHORED) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude - REMOVE_LATITUDE, longitude), ZOOM_CAMERA), newTime, null)
        } else {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), ZOOM_CAMERA), newTime, null)
        }
    }

    // -------------- DIALOG

    val cal = Calendar.getInstance()

    private fun displayDialogFilter() {
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_filter, null)
        val mBuilder = AlertDialog.Builder(this).setView(mDialogView)

        initDateTimePickers(mDialogView)

        val mAlertDialog = mBuilder.show()

        mDialogView.submit.setOnClickListener {
            if(mDialogView.inputStart.text.isNullOrEmpty() && mDialogView.inputEnd.text.isNullOrEmpty()){
                controller.getAll(this)
                mAlertDialog.dismiss()
            }else if(mDialogView.inputStart.text.isNullOrEmpty()){
                controller.getAllBeforeDate(SimpleDateFormat("dd/MM/yy").parse(mDialogView.inputEnd.text.toString()), this)
                mAlertDialog.dismiss()
            }else if(mDialogView.inputEnd.text.isNullOrEmpty()){
                controller.getAllAfterDate(SimpleDateFormat("dd/MM/yy").parse(mDialogView.inputStart.text.toString()), this)
                mAlertDialog.dismiss()
            }else{
                controller.getAllBetweenDates(SimpleDateFormat("dd/MM/yy").parse(mDialogView.inputStart.text.toString()), SimpleDateFormat("dd/MM/yy").parse(mDialogView.inputEnd.text.toString()), this)
                mAlertDialog.dismiss()
            }
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
}
