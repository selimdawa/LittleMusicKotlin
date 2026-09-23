package com.flatcode.littlemusic.ui.category

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemusic.databinding.ItemCategoryMainBinding
import com.flatcode.littlemusic.model.Category
import com.flatcode.littlemusic.utils.loadBlurImage
import com.flatcode.littlemusic.utils.loadImage

class CategoryMainAdapter(
    private val onItemClick: (Category) -> Unit
) : ListAdapter<Category, CategoryMainAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemCategoryMainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemCategoryMainBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Category) {
            val name = item.name ?: ""
            val image = item.image ?: ""

            binding.image.loadImage(image, false)
            binding.imageBlur.loadBlurImage(image, 50, false)

            if (name.isEmpty()) {
                binding.name.visibility = View.GONE
            } else {
                binding.name.visibility = View.VISIBLE
                binding.name.text = name
            }

            binding.card.setOnClickListener { onItemClick(item) }
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