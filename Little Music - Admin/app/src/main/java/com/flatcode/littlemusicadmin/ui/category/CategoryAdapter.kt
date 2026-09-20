package com.flatcode.littlemusicadmin.ui.category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemusicadmin.databinding.ItemCategoryBinding
import com.flatcode.littlemusicadmin.model.Category
import com.flatcode.littlemusicadmin.utils.DATA

import com.flatcode.littlemusicadmin.utils.glide

class CategoryAdapter(
    private val onItemClick: (Category) -> Unit,
    private val onMoreClick: (Category) -> Unit
) : ListAdapter<Category, CategoryAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Category) {
            binding.name.text = item.name
            binding.numberSongs.text = item.songsCount.toString()
            binding.numberAlbums.text = item.albumsCount.toString()
            binding.numberInterested.text = item.interestedCount.toString()
            binding.image.glide(false, item.image)

            binding.root.setOnClickListener { onItemClick(item) }
            binding.more.setOnClickListener { onMoreClick(item) }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean =
            oldItem == newItem
    }
}