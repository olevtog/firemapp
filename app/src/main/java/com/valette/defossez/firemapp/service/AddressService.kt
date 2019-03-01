package com.valette.defossez.firemapp.service

import android.content.Context
import android.location.Address
import android.location.Geocoder
import java.util.*

class AddressService(context : Context){

    private val context = context
    private val geocoder: Geocoder = Geocoder(this.context, Locale.getDefault())

    fun getAddresses(address : String, size : Int) : List<Address> {
        return geocoder.getFromLocationName(address,size)
    }

}