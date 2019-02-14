package com.valette.defossez.firemapp.controller

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.valette.defossez.firemapp.entity.Firework


class FireworksController {

    companion object {
        private val database = FirebaseDatabase.getInstance()
        val ref = database.reference.child("fireworks")
        var fireworks = ArrayList<Firework>()
    }

    init {
        ref.addListenerForSingleValueEvent( object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val children = snapshot.children
                children.forEach {
                    fireworks.add(it.getValue(Firework::class.java)!!)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                println("lol")
            }
        })
    }

    fun create(firework: Firework){
        val id = ref.push().key
        firework.id = id
        ref.push().setValue(firework)
    }

    fun getById(id: String){

    }

    fun getAll() : ArrayList<Firework>{
        return fireworks
    }
}