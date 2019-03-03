package com.valette.defossez.firemapp

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.valette.defossez.firemapp.controller.FavoriteController
import com.valette.defossez.firemapp.entity.Favorite

/**
 * Initialise la base de donnée Room
 */
@Database(entities = arrayOf(Favorite::class), version = 1,  exportSchema = false)
abstract class FiremappDatabase : RoomDatabase() {

    abstract fun favoriteController(): FavoriteController
}