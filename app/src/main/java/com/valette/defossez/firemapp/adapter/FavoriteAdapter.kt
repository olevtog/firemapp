package com.valette.defossez.firemapp.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.valette.defossez.firemapp.R
import com.valette.defossez.firemapp.controller.FireworksController
import com.valette.defossez.firemapp.entity.Favorite
import com.valette.defossez.firemapp.entity.Firework

class FavoriteAdapter(val userList: List<Favorite>) : RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {

    private val controller = FireworksController()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var idFirework = userList[position].firework
        controller.getByIdFavorite(idFirework, this, holder, position)
    }


    fun openDetail(firework: Firework, holder: ViewHolder, position: Int) {
        holder?.txtName?.text = firework.title
        holder?.txtLocalisation?.text = firework.address
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.favorite_firework, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtName = itemView.findViewById<TextView>(R.id.textName)
        val txtLocalisation = itemView.findViewById<TextView>(R.id.textLocalisation)

    }

}