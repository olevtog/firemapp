package com.valette.defossez.firemapp.controller

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.valette.defossez.firemapp.entity.Favorite

/**
 * Permet d'effectuer des requetes à la base de donnée
 */
@Dao
interface FavoriteController {

    @Query("SELECT * from favorites")
    fun getAll(): List<Favorite>

    @Query("SELECT * FROM favorites WHERE firework = :firework ")
    fun getByFirework(firework:String): Boolean

    @Insert
    fun insert(favorite: Favorite)

    @Query("SELECT * FROM favorites WHERE firework = :firework ")
    fun delete(firework: String) : Boolean

}