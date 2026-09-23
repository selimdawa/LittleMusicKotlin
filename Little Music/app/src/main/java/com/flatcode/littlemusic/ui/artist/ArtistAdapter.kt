package com.flatcode.littlemusic.ui.artist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemusic.databinding.ItemArtistBinding
import com.flatcode.littlemusic.model.Artist
import com.flatcode.littlemusic.utils.DATA
import com.flatcode.littlemusic.utils.isInterested
import com.flatcode.littlemusic.utils.loadImage
import java.util.Locale

class ArtistAdapter(
    private val onItemClick: (Artist) -> Unit, private val onInterestedClick: (Artist, View) -> Unit
) : ListAdapter<Artist, ArtistAdapter.ViewHolder>(DiffCallback), Filterable {

    var fullList: List<Artist> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemArtistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
                submitList(results?.values as? List<Artist> ?: emptyList())
            }
        }
    }

    fun setList(list: List<Artist>) {
        fullList = list
        submitList(list)
    }

    inner class ViewHolder(private val binding: ItemArtistBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Artist) {
            val id = item.id
            val name = item.name ?: ""
            val image = item.image ?: ""
            val albumCount = item.albumsCount
            val songsCount = item.songsCount

            binding.image.loadImage(image, true)

            if (name.isEmpty()) {
                binding.name.visibility = View.GONE
            } else {
                binding.name.visibility = View.VISIBLE
                binding.name.text = name
            }

            binding.numberAlbums.text = albumCount.toString()
            binding.numberSongs.text = songsCount.toString()

            binding.add.isInterested(id, DATA.ARTISTS)
            binding.add.setOnClickListener { onInterestedClick(item, it) }

            binding.item.setOnClickListener { onItemClick(item) }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Artist>() {
            override fun areItemsTheSame(oldItem: Artist, newItem: Artist): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Artist, newItem: Artist): Boolean {
                return oldItem == newItem
            }
        }
    }
}