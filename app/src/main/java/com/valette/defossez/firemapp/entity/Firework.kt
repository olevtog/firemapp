package com.valette.defossez.firemapp.entity

import java.util.*

class Firework(var id: String, val title: String, val description: String, val latitude: Double, val longitude: Double, val address: String, val date: Date){

    constructor() : this(id="", title="", description="", latitude = 0.0, longitude = 0.0, address = "", date = Date())

    fun toJSON(): String{
        return "{title: $title, description: $description, latitude: $latitude, longitude: $longitude, address: $address, date: $date}"
    }
}