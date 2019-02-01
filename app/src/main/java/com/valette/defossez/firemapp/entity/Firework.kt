package com.valette.defossez.firemapp.entity

import java.util.*

class Firework(var id: String?, val title: String, val description: String, val latitude: Double, val longitude: Double, val address: String, val date: Date){
    fun toJSON(): String{
        return "{title: $title, description: $description, latitude: $latitude, longitude: $longitude, address: $address, date: $date}"
    }
}