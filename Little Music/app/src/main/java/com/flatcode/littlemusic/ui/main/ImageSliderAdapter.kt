package com.flatcode.littlemusic.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import com.flatcode.littlemusic.databinding.ItemSliderBinding
import com.flatcode.littlemusic.utils.loadImage
import io.selimdawa.autoimageslider.adapter.SliderViewAdapter

class ImageSliderAdapter(private val imageList: List<String>) :
    SliderViewAdapter<ImageSliderAdapter.SliderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val binding = ItemSliderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SliderViewHolder(binding)
    }

    override fun onBind(viewHolder: SliderViewHolder, position: Int) {
        val imageLink = imageList[position]
        viewHolder.binding.imageView.loadImage(imageLink)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    class SliderViewHolder(val binding: ItemSliderBinding) : ViewHolder(binding.root)
}
