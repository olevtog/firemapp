package com.valette.defossez.firemapp.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.valette.defossez.firemapp.activity.FavoriteActivity
import com.valette.defossez.firemapp.FiremappApp
import com.valette.defossez.firemapp.R
import com.valette.defossez.firemapp.controller.FireworksController
import com.valette.defossez.firemapp.entity.Favorite
import com.valette.defossez.firemapp.entity.Firework
import kotlinx.android.synthetic.main.favorite_firework.view.*

class FavoriteAdapter(val favList: List<Favorite>, val activity: FavoriteActivity) : RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {

    private val controller = FireworksController()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var idFirework = favList[position].firework

        //display favorite
        controller.getByIdFavorite(idFirework, this, holder)

        var favoriteState = true;
        holder.listView.favorite.setOnClickListener {
            if (favoriteState) {
                FiremappApp.database.favoriteController().delete(idFirework)
                holder.listView.favorite.setBackgroundResource(R.drawable.ic_favorite_border_red)
            } else {
                FiremappApp.database.favoriteController().insert(Favorite(idFirework))
                holder.listView.favorite.setBackgroundResource(R.drawable.ic_favorite_red)
            }
            favoriteState = !favoriteState
        }
    }


    fun openDetail(firework: Firework, holder: ViewHolder) {
        holder.listView.textName.text = firework.title
        holder.listView.textLocalisation.text = firework.address
        holder.listView.setOnClickListener {
            activity.finish(firework.id, firework.latitude, firework.longitude)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.favorite_firework, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return favList.size
    }

    class ViewHolder(val listView: View) : RecyclerView.ViewHolder(listView)


}