package com.test.happyplaces.adapters

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.test.happyplaces.activities.AddHappyPlaceActivity
import com.test.happyplaces.activities.MainActivity
import com.test.happyplaces.databinding.ItemHappyPlaceBinding
import com.test.happyplaces.database.PlaceModel

class HappyPlaceAdapter(private val happyPlaces: List<PlaceModel>) :
    RecyclerView.Adapter<HappyPlaceAdapter.ViewHolder>() {
    private var onClickListener: OnClickListener? = null
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

        holder.itemView.setOnClickListener{
            if(onClickListener!=null){
                onClickListener!!.onCLick(position, model = happyPlace)
            }
        }
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    fun notifyEditItem(activity: Activity,position : Int,requestCode : Int){
        val intent = Intent(activity,AddHappyPlaceActivity::class.java)
        intent.putExtra(MainActivity.Extra_Place_Details,happyPlaces[position])
        activity.startActivityForResult(intent,requestCode)
        notifyItemChanged(position)
    }

    interface OnClickListener{
        fun onCLick(position: Int,model: PlaceModel)
    }
}