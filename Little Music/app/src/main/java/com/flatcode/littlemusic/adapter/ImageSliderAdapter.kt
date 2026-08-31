package com.flatcode.littlemusic.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.flatcode.littlemusic.R
import com.flatcode.littlemusic.utils.DATA
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.selimdawa.autoimageslider.adapter.SliderViewAdapter

class ImageSliderAdapter(var context: Context?, var setTotalCount: Int) :
    SliderViewAdapter<ImageSliderAdapter.SliderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_slider, parent, false)
        return SliderViewHolder(view)
    }

    override fun onBind(viewHolder: SliderViewHolder, position: Int) {
        val dbChildKey = (position + 1).toString()

        FirebaseDatabase.getInstance().getReference(DATA.SLIDER_SHOW).child(dbChildKey)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val imageLink = snapshot.value?.toString()

                    if (!imageLink.isNullOrEmpty() && viewHolder.itemView.context != null) {
                        Glide.with(viewHolder.itemView.context).load(imageLink)
                            .into(viewHolder.imageSlider)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    override fun getItemCount(): Int {
        return setTotalCount
    }

    class SliderViewHolder(itemView: View) : SliderViewAdapter.ViewHolder(itemView) {
        val imageSlider: ImageView = itemView.findViewById(R.id.imageView)
    }
}