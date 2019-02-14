package com.valette.defossez.firemapp

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_draver.*
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.slide_up_layout_back.*
import android.content.Intent
import com.valette.defossez.firemapp.entity.Firework
import java.time.Instant
import java.util.*


class DrawerActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener  {


    private lateinit var mMap: GoogleMap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_draver)

        nav_view.setNavigationItemSelectedListener(this)

        // pour afficher ma map
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Mettre un point d'arret sur le sliding
        sliding_layout.anchorPoint = .3f

        // Pour ouvrir le menu
        buttonOpenMenu.setOnClickListener {
            drawer_layout.openDrawer(GravityCompat.START)
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
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // pour supprimer la barre en bas pour afficher la direction et un lien vers maps
        mMap.uiSettings.isMapToolbarEnabled = false
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = false
        // pour ajouter une marge afin de compass ne soit pas caché ainsi que le logo google
        mMap.setPadding(0,120,0,100)

        // mettre la camera par defaut a cette emplacement
        mMap.animateCamera(CameraUpdateFactory.zoomBy(25.0F))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(48.8534, 2.3488)))


        val listFirework = ArrayList<Firework>()
        var f1 = Firework("5", "titre5", "description5", 48.64, 2.64, "adress", Date())
        var f2 = Firework("6", "titre6", "description6", 48.74, 2.74, "adress", Date())
        var f3 = Firework("7", "titre7", "description7", 47.64, 2.84, "adress", Date())
        var f4 = Firework("8", "titre8", "description8", 49.64, 2.34, "adress", Date())
        listFirework.add(f1)
        listFirework.add(f2)
        listFirework.add(f3)
        listFirework.add(f4)

        listFirework.forEach {
            f -> mMap.addMarker(MarkerOptions()
                .position(LatLng(f.latitude, f.longitude))
                .title(f.title)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
        }


    }

}
