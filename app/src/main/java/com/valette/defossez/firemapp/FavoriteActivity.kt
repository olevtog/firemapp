package com.valette.defossez.firemapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.LinearLayout
import com.valette.defossez.firemapp.adapter.FavoriteAdapter


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

    fun finish(id: String, latitude: Double, longitude: Double) {
        val intent = Intent()
        intent.putExtra("id", id)
        intent.putExtra("latitude", latitude)
        intent.putExtra("longitude", longitude)
        setResult(RESULT_OK, intent)
        finish()
    }

}
