package com.valette.defossez.firemapp.controller

import com.google.firebase.database.FirebaseDatabase
import com.valette.defossez.firemapp.entity.Firework

class FireworksController {

    private val database = FirebaseDatabase.getInstance()
    private val ref = database.getReference("fireworks")

    fun create(firework: Firework){
        val id = ref.push().key
        firework.id = id
        this.ref.push().setValue(firework)
    }

    fun getById(id: String){

    }
}