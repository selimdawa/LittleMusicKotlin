package com.flatcode.littlemusic.ui.category

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemusic.databinding.ItemCategoryBinding
import com.flatcode.littlemusic.filter.CategoryFilter
import com.flatcode.littlemusic.model.Category
import com.flatcode.littlemusic.utils.DATA
import com.flatcode.littlemusic.utils.checkInterested
import com.flatcode.littlemusic.utils.loadImage
import com.flatcode.littlemusic.utils.openActivity
import com.flatcode.littlemusic.utils.isInterested
import java.text.MessageFormat

class CategoryAdapter(
    private val onItemClick: (Category) -> Unit,
    private val onInterestedClick: (Category, View) -> Unit
) : ListAdapter<Category, CategoryAdapter.ViewHolder>(DiffCallback), Filterable {

    var fullList: List<Category> = emptyList()
    private var filter: CategoryFilter? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getFilter(): Filter {
        return filter ?: CategoryFilter(fullList, this).also { filter = it }
    }

    fun setList(list: List<Category>) {
        fullList = list
        submitList(list)
    }

    inner class ViewHolder(private val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Category) {
            val id = item.id
            val name = item.name ?: ""
            val image = item.image ?: ""
            val interestedCount = item.interestedCount
            val albumCount = item.albumsCount
            val songsCount = item.songsCount

            binding.image.loadImage(image, false)

            if (name.isEmpty()) {
                binding.name.visibility = View.GONE
            } else {
                binding.name.visibility = View.VISIBLE
                binding.name.text = name
            }

            binding.numberInterested.text = interestedCount.toString()
            binding.numberSongs.text = songsCount.toString()
            binding.numberAlbums.text = albumCount.toString()

            binding.add.isInterested(id, DATA.CATEGORIES)
            binding.add.setOnClickListener { onInterestedClick(item, it) }

            binding.item.setOnClickListener { onItemClick(item) }
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
