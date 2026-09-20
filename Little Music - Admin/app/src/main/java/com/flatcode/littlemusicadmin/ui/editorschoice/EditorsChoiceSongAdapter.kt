package com.flatcode.littlemusicadmin.ui.editorschoice

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.core.view.isVisible
import com.flatcode.littlemusicadmin.databinding.ItemEditorsChoiceBinding
import com.flatcode.littlemusicadmin.model.Song
import com.flatcode.littlemusicadmin.utils.DATA
import com.flatcode.littlemusicadmin.utils.dataName

class EditorsChoiceSongAdapter(private val onAddClick: (Song) -> Unit) :
    ListAdapter<Song, EditorsChoiceSongAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEditorsChoiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position) ?: return
        holder.bind(item, onAddClick)
    }

    class ViewHolder(private val binding: ItemEditorsChoiceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Song, onAddClick: (Song) -> Unit) {
            binding.name.isVisible = !item.name.isNullOrEmpty()
            binding.name.text = item.name

            binding.nrViews.text = item.viewsCount.toString()
            binding.nrLoves.text = item.lovesCount.toString()

            binding.artist.dataName(DATA.ARTISTS, item.artistId)
            binding.album.dataName(DATA.ALBUMS, item.albumId)
            binding.category.dataName(DATA.CATEGORIES, item.categoryId)

            binding.add.setOnClickListener { onAddClick(item) }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Song>() {
            override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean =
                oldItem.name == newItem.name &&
                        oldItem.viewsCount == newItem.viewsCount &&
                        oldItem.lovesCount == newItem.lovesCount &&
                        oldItem.artistId == newItem.artistId &&
                        oldItem.albumId == newItem.albumId &&
                        oldItem.categoryId == newItem.categoryId
        }
    }
}
