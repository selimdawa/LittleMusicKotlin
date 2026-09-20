package com.flatcode.littlemusicadmin.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.core.view.isVisible
import com.flatcode.littlemusicadmin.R
import com.flatcode.littlemusicadmin.databinding.ItemMainBinding
import com.flatcode.littlemusicadmin.model.Main

class MainAdapter(private val onItemClick: (Main) -> Unit) :
    ListAdapter<Main, MainAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = getItem(position)
        holder.bind(model, onItemClick)
    }

    class ViewHolder(private val binding: ItemMainBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(model: Main, onItemClick: (Main) -> Unit) {
            val image = model.image
            val number = model.number
            val name = model.title

            if (image != 0) {
                binding.image.setImageResource(image)
            } else {
                binding.image.setImageResource(R.drawable.ic_load)
            }

            binding.number.isVisible = number != 0
            binding.number.text = "$number"

            binding.name.text = name
            itemView.setOnClickListener {
                onItemClick(model)
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Main>() {
            override fun areItemsTheSame(oldItem: Main, newItem: Main): Boolean =
                oldItem.title == newItem.title

            override fun areContentsTheSame(oldItem: Main, newItem: Main): Boolean =
                oldItem.image == newItem.image &&
                        oldItem.number == newItem.number &&
                        oldItem.title == newItem.title
        }
    }
}