package com.flatcode.littlemusicadmin.ui.album

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.core.view.isVisible
import com.flatcode.littlemusicadmin.databinding.ItemAlbumBinding
import com.flatcode.littlemusicadmin.model.Album
import com.flatcode.littlemusicadmin.utils.loadImage

class AlbumAdapter(
    private val onItemClick: (Album) -> Unit,
    private val onMoreClick: (Album) -> Unit
) : ListAdapter<Album, AlbumAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAlbumBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position) ?: return
        holder.bind(item, onItemClick, onMoreClick)
    }

    class ViewHolder(private val binding: ItemAlbumBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Album, onItemClick: (Album) -> Unit, onMoreClick: (Album) -> Unit) {
            binding.image.loadImage(false, item.image)
            binding.name.isVisible = !item.name.isNullOrEmpty()
            binding.name.text = item.name

            binding.numberInterested.text = "${item.interestedCount}"
            binding.numberSongs.text = "${item.songsCount}"

            binding.more.setOnClickListener { onMoreClick(item) }
            binding.item.setOnClickListener { onItemClick(item) }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Album>() {
            override fun areItemsTheSame(oldItem: Album, newItem: Album): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Album, newItem: Album): Boolean =
                oldItem.name == newItem.name &&
                        oldItem.image == newItem.image &&
                        oldItem.artistId == newItem.artistId &&
                        oldItem.categoryId == newItem.categoryId &&
                        oldItem.interestedCount == newItem.interestedCount &&
                        oldItem.songsCount == newItem.songsCount
        }
    }
}
