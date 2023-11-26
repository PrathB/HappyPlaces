package com.test.happyplaces.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.test.happyplaces.databinding.ItemHappyPlaceBinding
import com.test.happyplaces.database.PlaceModel

class HappyPlaceAdapter(private val happyPlaces: List<PlaceModel>) :
    RecyclerView.Adapter<HappyPlaceAdapter.ViewHolder>() {
    class ViewHolder(binding: ItemHappyPlaceBinding):
        RecyclerView.ViewHolder(binding.root){
            val placeImg = binding.civHappyPlace
            val name = binding.tvHappyPlaceName
            val description = binding.tvHappyPlaceDescription
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return(ViewHolder(ItemHappyPlaceBinding.inflate(LayoutInflater.from(parent.context),parent,false)))
    }

    override fun getItemCount(): Int {
        return happyPlaces.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val happyPlace = happyPlaces[position]

        holder.placeImg.setImageURI(Uri.parse(happyPlace.image))
        holder.name.text = happyPlace.title
        holder.description.text = happyPlace.description
    }

}