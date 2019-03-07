package com.valette.defossez.firemapp.controller

import android.app.Activity
import com.valette.defossez.firemapp.entity.Firework
import com.google.firebase.firestore.FirebaseFirestore
import com.valette.defossez.firemapp.FavoriteActivity
import com.valette.defossez.firemapp.HomeActivity
import com.valette.defossez.firemapp.adapter.FavoriteAdapter
import java.util.*


class FireworksController {

    private val db = FirebaseFirestore.getInstance()

    fun create(firework: Firework) {
        val newFireworkRef = db.collection("fireworks").document()
        firework.id = newFireworkRef.id
        newFireworkRef.set(firework)
    }

    fun getAll(activity: HomeActivity) {
        var fireworks = ArrayList<Firework>()
        db.collection("fireworks").get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        fireworks.add(document.toObject(Firework::class.java))
                    }
                    activity.displayMarkers(fireworks)
                }
    }

    fun getAllBetweenDates(start : Date, end : Date, activity: HomeActivity) {
        var fireworks = ArrayList<Firework>()
        db.collection("fireworks").whereGreaterThanOrEqualTo("date", start).whereLessThanOrEqualTo("date", end).get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        fireworks.add(document.toObject(Firework::class.java))
                    }
                    activity.displayMarkers(fireworks)
                }
    }

    fun getAllAfterDate(start : Date, activity: HomeActivity) {
        var fireworks = ArrayList<Firework>()
        db.collection("fireworks").whereGreaterThanOrEqualTo("date", start).get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        fireworks.add(document.toObject(Firework::class.java))
                    }
                    activity.displayMarkers(fireworks)
                }
    }

    fun getAllBeforeDate(end : Date, activity: HomeActivity) {
        var fireworks = ArrayList<Firework>()
        db.collection("fireworks").whereGreaterThanOrEqualTo("date", end).get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        fireworks.add(document.toObject(Firework::class.java))
                    }
                    activity.displayMarkers(fireworks)
                }
    }

    fun getByIdMap(id: String, activity: HomeActivity) {
        db.collection("fireworks").document(id).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        activity.openDetail(document.toObject(Firework::class.java)!!)
                    } else {
                        print("err")
                    }
                }
    }

    fun getByIdFavorite(id: String, activity: FavoriteAdapter, holder: FavoriteAdapter.ViewHolder) {
        db.collection("fireworks").document(id).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        activity.openDetail(document.toObject(Firework::class.java)!!, holder)
                    } else {
                        print("err")
                    }
                }
    }
}