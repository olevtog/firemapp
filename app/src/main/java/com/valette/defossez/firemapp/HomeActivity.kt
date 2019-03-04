package com.valette.defossez.firemapp

import android.app.Activity
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
import android.net.Uri
import android.widget.Toast
import com.google.android.gms.maps.model.*
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.valette.defossez.firemapp.controller.FireworksController
import com.valette.defossez.firemapp.entity.Favorite
import com.valette.defossez.firemapp.entity.Firework
import com.valette.defossez.firemapp.service.LocationService
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.slide_up_layout_back.*
import kotlinx.android.synthetic.main.slide_up_layout_front.*
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList


class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    val TIME_MOVE_CAMERA = 1500
    val ZOOM_CAMERA = 11.0f
    val REMOVE_LATITUDE = 0.07

    private lateinit var mMap: GoogleMap
    private lateinit var locationService: LocationService
    private val controller = FireworksController()
    private lateinit var currentMarker: Marker
    private var markers: HashMap<String, Marker> = HashMap()
    private var favoriteState: Boolean = false



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
            drawer_layout.openDrawer(GravityCompat.START)
        }

        //Lancer la recherche de la position
        locationService = LocationService()
        locationService.lancerLaRechercheDeLaLocalisation(this)

        //Aller a la position actuelle
        boutonLocalisation.setOnClickListener {
            allerANotrePosition()
        }
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
    }

    fun displayMarkers(fireworks: ArrayList<Firework>) {
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
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(firework.latitude - REMOVE_LATITUDE, firework.longitude), ZOOM_CAMERA), TIME_MOVE_CAMERA, null)

        favoriteState = FiremappApp.database.favoriteController().getByFirework(firework.id!!)
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
                FiremappApp.database.favoriteController().delete(firework.id!!)
                favorite.setBackgroundResource(R.drawable.ic_favorite_border)
            } else {
                FiremappApp.database.favoriteController().insert(Favorite(firework.id!!))
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
    }


    private fun allerANotrePosition() {
        if (locationService.addressTrouvee) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(locationService.myLatitude - REMOVE_LATITUDE, locationService.myLongitude), ZOOM_CAMERA), TIME_MOVE_CAMERA, null)
        } else {
            Toast.makeText(this, "Attente de votre position...", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
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

}
