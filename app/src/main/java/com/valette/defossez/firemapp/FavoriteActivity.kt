package com.valette.defossez.firemapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
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
        val users = ArrayList<Long>()
        var allFavFireworks = FiremappApp.database.favoriteController().getAll()
        var adapter = FavoriteAdapter(allFavFireworks)
        rv.adapter = adapter
    }
}
