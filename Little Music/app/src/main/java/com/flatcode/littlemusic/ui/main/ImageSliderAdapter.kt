package com.flatcode.littlemusic.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import coil3.load
import com.flatcode.littlemusic.databinding.ItemSliderBinding
import com.flatcode.littlemusic.utils.DATA
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.selimdawa.autoimageslider.adapter.SliderViewAdapter

class ImageSliderAdapter(var context: Context?, var setTotalCount: Int) :
    SliderViewAdapter<ImageSliderAdapter.SliderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val binding = ItemSliderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SliderViewHolder(binding)
    }

    override fun onBind(viewHolder: SliderViewHolder, position: Int) {
        val dbChildKey = (position + 1).toString()

        FirebaseDatabase.getInstance().getReference(DATA.SLIDER_SHOW).child(dbChildKey)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val imageLink = snapshot.value?.toString()

                    if (!imageLink.isNullOrEmpty() && viewHolder.itemView.context != null) {
                        viewHolder.binding.imageView.load(imageLink)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    override fun getItemCount(): Int {
        return setTotalCount
    }

    class SliderViewHolder(val binding: ItemSliderBinding) : ViewHolder(binding.root)
}