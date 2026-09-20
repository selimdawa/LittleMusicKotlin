package com.flatcode.littlemusicadmin.ui.song

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageView
import androidx.core.view.isVisible
import com.flatcode.littlemusicadmin.databinding.ItemSongBinding
import com.flatcode.littlemusicadmin.model.Song
import com.flatcode.littlemusicadmin.utils.DATA
import com.flatcode.littlemusicadmin.utils.convertDuration
import com.flatcode.littlemusicadmin.utils.dataName
import com.flatcode.littlemusicadmin.utils.isFavorite
import com.flatcode.littlemusicadmin.utils.isLoves
import com.flatcode.littlemusicadmin.utils.nrLoves
import com.scwang.wave.MultiWaveHeader

class SongAdapter(
    private val onItemClick: (Song, Int) -> Unit,
    private val onFavoriteClick: (Song, ImageView) -> Unit,
    private val onLoveClick: (Song, ImageView) -> Unit,
    private val onMoreClick: (Song) -> Unit,
    private val onArtistClick: (String) -> Unit,
    private val onAlbumClick: (String) -> Unit,
    private val onCategoryClick: (String) -> Unit
) : ListAdapter<Song, SongAdapter.ViewHolder>(DIFF_CALLBACK) {

    var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position) ?: return
        holder.bind(
            item, position, selectedPosition,
            onItemClick, onFavoriteClick, onLoveClick, onMoreClick,
            onArtistClick, onAlbumClick, onCategoryClick
        )
    }

    class ViewHolder(private val binding: ItemSongBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: Song, position: Int, selectedPosition: Int,
            onItemClick: (Song, Int) -> Unit,
            onFavoriteClick: (Song, ImageView) -> Unit,
            onLoveClick: (Song, ImageView) -> Unit,
            onMoreClick: (Song) -> Unit,
            onArtistClick: (String) -> Unit,
            onAlbumClick: (String) -> Unit,
            onCategoryClick: (String) -> Unit
        ) {
            val id = item.id
            val artistId = item.artistId
            val albumId = item.albumId
            val categoryId = item.categoryId

            binding.name.text = item.name
            binding.artist.dataName(DATA.ARTISTS, artistId)
            binding.album.dataName(DATA.ALBUMS, albumId)
            binding.category.dataName(DATA.CATEGORIES, categoryId)

            binding.duration.text = item.duration?.toLongOrNull()?.convertDuration() ?: ""
            binding.nrLoves.text = item.lovesCount.toString()

            binding.favorite.isFavorite(id, DATA.FirebaseUserUid)
            binding.love.isLoves(id)
            binding.nrLoves.nrLoves(id)

            binding.favorite.setOnClickListener { onFavoriteClick(item, binding.favorite) }
            binding.love.setOnClickListener { onLoveClick(item, binding.love) }

            binding.artist.setOnClickListener { artistId?.let { onArtistClick(it) } }
            binding.album.setOnClickListener { albumId?.let { onAlbumClick(it) } }
            binding.category.setOnClickListener { categoryId?.let { onCategoryClick(it) } }

            binding.more.setOnClickListener { onMoreClick(item) }

            binding.card.setOnClickListener {
                onItemClick(item, position)
                binding.wave.waveHeight = 40
            }

            binding.wave.isVisible = selectedPosition == position
            if (binding.wave.isVisible) {
                open(binding.wave)
            }
        }
    }

    companion object {
        fun open(wave: MultiWaveHeader) {
            wave.velocity = 1f
            wave.progress = 1f
            wave.isRunning
            wave.gradientAngle = 45
            wave.waveHeight = 40
            wave.startColor = Color.WHITE
            wave.closeColor = Color.DKGRAY
            wave.visibility = View.VISIBLE
        }

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Song>() {
            override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean =
                oldItem.name == newItem.name &&
                        oldItem.publisher == newItem.publisher &&
                        oldItem.categoryId == newItem.categoryId &&
                        oldItem.artistId == newItem.artistId &&
                        oldItem.albumId == newItem.albumId &&
                        oldItem.duration == newItem.duration &&
                        oldItem.songLink == newItem.songLink &&
                        oldItem.viewsCount == newItem.viewsCount &&
                        oldItem.lovesCount == newItem.lovesCount
        }
    }
}