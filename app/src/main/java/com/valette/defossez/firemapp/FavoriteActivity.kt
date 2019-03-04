package com.valette.defossez.firemapp

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import com.valette.defossez.firemapp.adapter.FavoriteAdapter
import android.content.Intent



class FavoriteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)

        completeRecyclerView()
    }

    private fun completeRecyclerView() {
        val rv = findViewById<RecyclerView>(R.id.recyclerFavorite)
        rv.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        var allFavFireworks = FiremappApp.database.favoriteController().getAll()
        var adapter = FavoriteAdapter(allFavFireworks, this)
        rv.adapter = adapter
    }

    fun changeActivity(activity : Activity, latitude: Double, longitude: Double) {
        val intent = Intent()
        intent.putExtra("latitude", latitude)
        intent.putExtra("longitude", longitude)
        activity.setResult(RESULT_OK, intent)
        activity.finish()
    }

}
