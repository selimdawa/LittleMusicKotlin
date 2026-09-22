package com.flatcode.littlemusic.ui.album

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemusic.databinding.ItemAlbumBinding
import com.flatcode.littlemusic.model.Album
import com.flatcode.littlemusic.utils.DATA
import com.flatcode.littlemusic.utils.loadImage
import com.flatcode.littlemusic.utils.isInterested
import java.util.*

class AlbumAdapter(
    private val onItemClick: (Album) -> Unit,
    private val onInterestedClick: (Album, View) -> Unit
) : ListAdapter<Album, AlbumAdapter.ViewHolder>(DiffCallback), Filterable {

    var fullList: List<Album> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAlbumBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
                    fullList.filter { it.name?.uppercase(Locale.getDefault())?.contains(query) == true }
                }
                results.count = filteredList.size
                results.values = filteredList
                return results
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                submitList(results?.values as? List<Album> ?: emptyList())
            }
        }
    }

    fun setList(list: List<Album>) {
        fullList = list
        submitList(list)
    }

    inner class ViewHolder(private val binding: ItemAlbumBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Album) {
            val id = item.id
            val name = item.name ?: ""
            val image = item.image ?: ""
            val interestedCount = item.interestedCount
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

            binding.add.isInterested(id, DATA.ALBUMS)
            binding.add.setOnClickListener { onInterestedClick(item, it) }
            
            binding.item.setOnClickListener { onItemClick(item) }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Album>() {
            override fun areItemsTheSame(oldItem: Album, newItem: Album): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Album, newItem: Album): Boolean {
                return oldItem == newItem
            }
        }
    }
}
