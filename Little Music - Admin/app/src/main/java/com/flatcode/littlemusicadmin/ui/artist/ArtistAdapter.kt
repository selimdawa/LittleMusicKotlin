package com.flatcode.littlemusicadmin.ui.artist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.core.view.isVisible
import com.flatcode.littlemusicadmin.databinding.ItemArtistBinding
import com.flatcode.littlemusicadmin.model.Artist
import com.flatcode.littlemusicadmin.utils.loadImage

class ArtistAdapter(
    private val onItemClick: (Artist) -> Unit,
    private val onMoreClick: (Artist) -> Unit
) : ListAdapter<Artist, ArtistAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemArtistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position) ?: return
        holder.bind(item, onItemClick, onMoreClick)
    }

    class ViewHolder(private val binding: ItemArtistBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Artist, onItemClick: (Artist) -> Unit, onMoreClick: (Artist) -> Unit) {
            binding.image.loadImage(true, item.image)
            binding.name.isVisible = !item.name.isNullOrEmpty()
            binding.name.text = item.name

            binding.numberAlbums.text = "${item.albumsCount}"
            binding.numberSongs.text = "${item.songsCount}"

            binding.more.setOnClickListener { onMoreClick(item) }
            binding.item.setOnClickListener { onItemClick(item) }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Artist>() {
            override fun areItemsTheSame(oldItem: Artist, newItem: Artist): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Artist, newItem: Artist): Boolean =
                oldItem.name == newItem.name &&
                        oldItem.image == newItem.image &&
                        oldItem.aboutTheArtist == newItem.aboutTheArtist &&
                        oldItem.albumsCount == newItem.albumsCount &&
                        oldItem.songsCount == newItem.songsCount
        }
    }
}
