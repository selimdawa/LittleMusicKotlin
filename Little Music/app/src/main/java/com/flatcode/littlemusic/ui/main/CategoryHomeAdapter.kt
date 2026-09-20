package com.flatcode.littlemusic.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemusic.databinding.ItemCategoryHomeBinding
import com.flatcode.littlemusic.model.Category
import com.flatcode.littlemusic.ui.category.CategorySongsActivity
import com.flatcode.littlemusic.utils.DATA
import com.flatcode.littlemusic.utils.glideImage
import com.flatcode.littlemusic.utils.openActivity

class CategoryHomeAdapter(
    private val onItemClick: (Category) -> Unit
) : ListAdapter<Category, CategoryHomeAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemCategoryHomeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Category) {
            binding.image.glideImage(item.image, false)
            binding.image.setOnClickListener { onItemClick(item) }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Category>() {
            override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
                return oldItem == newItem
            }
        }
    }
}