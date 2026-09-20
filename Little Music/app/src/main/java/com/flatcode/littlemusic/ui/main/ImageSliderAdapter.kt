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

class ImageSliderAdapter(private val imageList: List<String>) :
    SliderViewAdapter<ImageSliderAdapter.SliderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val binding = ItemSliderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SliderViewHolder(binding)
    }

    override fun onBind(viewHolder: SliderViewHolder, position: Int) {
        val imageLink = imageList[position]
        if (imageLink.isNotEmpty()) {
            viewHolder.binding.imageView.load(imageLink)
        }
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    class SliderViewHolder(val binding: ItemSliderBinding) : ViewHolder(binding.root)
}
