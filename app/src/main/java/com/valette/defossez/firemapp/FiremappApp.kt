package com.valette.defossez.firemapp

import android.app.Application
import android.arch.persistence.room.Room

/**
 * Classe principale de l'application
 */
class FiremappApp : Application() {

    /**
     * Permet d'obternie la base de donnée sous forme de singleton
     */
    companion object {
        lateinit var database: FiremappDatabase
        var myLongitude = 0.0
        var myLatitude = 0.0
    }

    override fun onCreate() {
        super.onCreate()
        FiremappApp.database = Room.databaseBuilder(this, FiremappDatabase::class.java, "favorite.db").allowMainThreadQueries().build()
    }
}