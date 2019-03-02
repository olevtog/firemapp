package com.valette.defossez.firemapp.controller

import com.valette.defossez.firemapp.entity.Firework
import com.google.firebase.firestore.FirebaseFirestore
import com.valette.defossez.firemapp.HomeActivity


class FireworksController {

        private val db = FirebaseFirestore.getInstance()

    fun create(firework: Firework){
        val newFireworkRef = db.collection("fireworks").document()
        firework.id = newFireworkRef.id
        newFireworkRef.set(firework)
    }

    fun getAll(activity : HomeActivity) {
        var fireworks = ArrayList<Firework>()
        db.collection("fireworks").get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        fireworks.add(document.toObject(Firework::class.java))
                    }
                    activity.displayMarkers(fireworks)
                }
    }

    fun getById(id : String, activity : HomeActivity){
        db.collection("fireworks").document(id).get()
                .addOnSuccessListener { document ->
                if (document != null) {
                    activity.openDetail(document.toObject(Firework::class.java)!!)
                } else {
                    print("err")
            }
        }
    }
}