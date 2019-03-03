package com.valette.defossez.firemapp.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "favorites")
data class Favorite(var firework: String) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}