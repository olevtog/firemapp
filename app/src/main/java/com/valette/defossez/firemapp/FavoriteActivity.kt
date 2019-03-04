package com.valette.defossez.firemapp

import android.content.Context
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
        val users = ArrayList<Long>()
        var allFavFireworks = FiremappApp.database.favoriteController().getAll()
        var adapter = FavoriteAdapter(allFavFireworks, this)
        rv.adapter = adapter
    }

    fun changeActivity() {
        Log.d("oky","oky")
        val returnIntent = Intent()
        returnIntent.putExtra("result", "valuuuue")
        setResult(RESULT_OK, returnIntent)
        finish()
        Log.d("oky2","oky2")
    }

}
