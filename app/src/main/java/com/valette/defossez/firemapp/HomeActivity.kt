package com.valette.defossez.firemapp

import android.Manifest
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.widget.Toast
import com.google.android.gms.maps.model.*
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.valette.defossez.firemapp.controller.FireworksController
import com.valette.defossez.firemapp.entity.Firework
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.slide_up_layout_back.*
import kotlinx.android.synthetic.main.slide_up_layout_front.*
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList


class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {


    private lateinit var mMap: GoogleMap
    private val controller = FireworksController()

    private var addressTrouvee = true
    private var myLatitude: Double = 0.0
    private var myLongitude: Double = 0.0
    private var locationManager: LocationManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        nav_view.setNavigationItemSelectedListener(this)

        // pour afficher ma map_aubergine
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Mettre un point d'arret sur le sliding
        sliding_layout.anchorPoint = .3f

        // Pour ouvrir le menu
        buttonOpenMenu.setOnClickListener {
            drawer_layout.openDrawer(GravityCompat.START)
        }

        lancerLaRechercheDeLaLocalisation()

        //Pour mettre la map_aubergine quand on clique sur le bouton
        boutonLocalisation.setOnClickListener {
            allerALaPositionActuelle()
        }
    }

    override fun onResume() {
        super.onResume()
        try{
            mMap.clear()
            controller.getAll(this)
        } catch (e : Exception){}
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

            }
            R.id.nav_share -> {

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
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_silver))

        // pour supprimer la barre en bas pour afficher la direction et un lien vers maps
        mMap.uiSettings.isMapToolbarEnabled = false
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = false
        // pour ajouter une marge afin de compass ne soit pas caché ainsi que le logo google
        mMap.setPadding(0, 120, 0, 100)

        // mettre la camera par defaut a cette emplacement
        mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(47.35, 2.2)))
        mMap.animateCamera(CameraUpdateFactory.zoomTo(6.0f))

        controller.getAll(this)

        mMap.setOnMarkerClickListener { marker ->
            controller.getById(marker.tag.toString(), this)
            true
        }
    }

    fun displayMarkers(fireworks: ArrayList<Firework>) {
        for (f in fireworks) {
            mMap.addMarker(MarkerOptions()
                    .position(LatLng(f.latitude, f.longitude))
                    .title(f.title)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
                    .tag = f.id
        }
    }

    fun openDetail(firework: Firework) {
        val format = SimpleDateFormat("dd/MM/yyy hh:mm")
        textViewDate.text = format.format(firework.date)
        textViewAddress.text = firework.address
        textViewTitle.text = firework.title
        textViewDescription.text = firework.description
        sliding_layout.panelState = SlidingUpPanelLayout.PanelState.ANCHORED
    }

    private fun allerALaPositionActuelle() {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(myLatitude, myLongitude), 12.0f), 1500, null)
    }

    /*
       _____ _____   _____   _                 _ _           _   _
      / ____|  __ \ / ____| | |               | (_)         | | (_)
     | |  __| |__) | (___   | | ___   ___ __ _| |_ ___  __ _| |_ _  ___  _ __
     | | |_ |  ___/ \___ \  | |/ _ \ / __/ _` | | / __|/ _` | __| |/ _ \| '_ \
     | |__| | |     ____) | | | (_) | (_| (_| | | \__ \ (_| | |_| | (_) | | | |
      \_____|_|    |_____/  |_|\___/ \___\__,_|_|_|___/\__,_|\__|_|\___/|_| |_|

     */
    private fun lancerLaRechercheDeLaLocalisation() {
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
        Log.d("TAG", "" + myLatitude + " azerty" + myLongitude)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        } else {
            val myLocation = locationManager?.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
            if (myLocation != null) {
                myLatitude = myLocation!!.latitude
                myLongitude = myLocation!!.longitude
            }
            locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, locationListener);
            locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener);
        }
    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(myLocation: Location) {
            myLatitude = myLocation.latitude
            myLongitude = myLocation.longitude
            if (addressTrouvee) {
                Log.d("TAG", "Position : " + myLatitude + " ; " + myLongitude)
                addressTrouvee = false
            }
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }


}
