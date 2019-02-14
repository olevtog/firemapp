package com.valette.defossez.firemapp.controller

import com.google.firebase.database.FirebaseDatabase
import com.valette.defossez.firemapp.entity.Firework
import com.google.firebase.FirebaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener



class FireworksController {

    private val database = FirebaseDatabase.getInstance()
    private val ref = database.getReference("fireworks")
    private val fireworks = ArrayList<Firework>()

    init {
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: FirebaseError?) {
                //test
            }

            override fun onDataChange(snapshot: DataSnapshot?) {
                val children = snapshot!!.children
                children.forEach {
                    fireworks.add(it.getValue(Firework::class.java)!!)
                }
            }
        })
    }

    fun create(firework: Firework){
        val id = ref.push().key
        firework.id = id
        this.ref.push().setValue(firework)
    }

    fun getById(id: String){

    }

    fun getAll() : ArrayList<Firework>{
        return fireworks
    }
}