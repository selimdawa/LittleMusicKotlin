package com.flatcode.littlemusic.ui.song

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemusic.databinding.ItemSongBinding
import com.flatcode.littlemusic.model.Song
import com.flatcode.littlemusic.utils.DATA
import com.flatcode.littlemusic.utils.convertDuration
import com.flatcode.littlemusic.utils.dataName
import com.flatcode.littlemusic.utils.incrementViewCount
import com.flatcode.littlemusic.utils.isFavorite
import com.flatcode.littlemusic.utils.isLoves
import com.flatcode.littlemusic.utils.nrLoves
import java.util.Locale

class SongAdapter(
    private val onItemClick: (Song, Int) -> Unit,
    private val onFavoriteClick: (Song, View) -> Unit,
    private val onLoveClick: (Song, View) -> Unit,
    private val onArtistClick: (String, String) -> Unit, // id, name
    private val onAlbumClick: (String, String, String) -> Unit, // id, name, image
    private val onCategoryClick: (String, String) -> Unit // id, name
) : ListAdapter<Song, SongAdapter.ViewHolder>(DiffCallback), Filterable {

    var selectedPosition = -1
        set(value) {
            val oldPosition = field
            field = value
            notifyItemChanged(oldPosition)
            notifyItemChanged(field)
        }

    var fullList: List<Song> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                val query = constraint?.toString()?.uppercase(Locale.getDefault()) ?: ""
                val filteredList = if (query.isEmpty()) {
                    fullList
                } else {
                    fullList.filter {
                        it.name?.uppercase(Locale.getDefault())?.contains(query) == true
                    }
                }
                results.count = filteredList.size
                results.values = filteredList
                return results
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                submitList(results?.values as? List<Song> ?: emptyList())
            }
        }
    }

    fun setList(list: List<Song>) {
        fullList = list
        submitList(list)
    }

    inner class ViewHolder(private val binding: ItemSongBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Song) {
            val id = item.id
            val name = item.name ?: ""
            val artistId = item.artistId ?: ""
            val albumId = item.albumId ?: ""
            val categoryId = item.categoryId ?: ""
            val nrLovesCount = item.lovesCount

            val categoryName = item.categoryName ?: ""
            val albumName = item.albumName ?: ""
            val artistName = item.artistName ?: ""

            val displayArtist = if (artistName.startsWith("-")) "" else artistName
            val displayAlbum = if (albumName.startsWith("-")) "" else albumName
            val displayCategory = if (categoryName.startsWith("-")) "" else categoryName

            binding.name.text = name

            binding.artist.text = displayArtist
            if (displayArtist.isEmpty() && artistId.isNotEmpty()) {
                binding.artist.dataName(DATA.ARTISTS, artistId)
            }

            binding.album.text = displayAlbum
            if (displayAlbum.isEmpty() && albumId.isNotEmpty()) {
                binding.album.dataName(DATA.ALBUMS, albumId)
            }

            binding.category.text = displayCategory
            if (displayCategory.isEmpty() && categoryId.isNotEmpty()) {
                binding.category.dataName(DATA.CATEGORIES, categoryId)
            }
            binding.duration.text = item.duration?.toLongOrNull()?.convertDuration() ?: "00:00"
            binding.nrLoves.text = nrLovesCount.toString()

            binding.favorite.isFavorite(id, DATA.FirebaseUserUid)
            binding.love.isLoves(id)
            binding.nrLoves.nrLoves(id)

            binding.favorite.setOnClickListener { onFavoriteClick(item, it) }
            binding.love.setOnClickListener { onLoveClick(item, it) }

            binding.artist.setOnClickListener {
                if (artistId.isNotEmpty()) {
                    onArtistClick(artistId, binding.artist.text.toString())
                }
            }

            binding.album.setOnClickListener {
                if (albumId.isNotEmpty()) {
                    onAlbumClick(albumId, binding.album.text.toString(), "")
                }
            }

            binding.category.setOnClickListener {
                if (categoryId.isNotEmpty()) {
                    onCategoryClick(categoryId, binding.category.text.toString())
                }
            }

            binding.card.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick(item, bindingAdapterPosition)
                    id.incrementViewCount()
                }
            }

            if (selectedPosition == bindingAdapterPosition) {
                binding.wave.visibility = View.VISIBLE
            } else {
                binding.wave.visibility = View.GONE
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Song>() {
            override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
                return oldItem == newItem
            }
        }
    }
}