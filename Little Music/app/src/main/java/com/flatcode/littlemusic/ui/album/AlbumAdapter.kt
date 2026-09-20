package com.flatcode.littlemusic.ui.album

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemusic.databinding.ItemAlbumBinding
import com.flatcode.littlemusic.filter.AlbumFilter
import com.flatcode.littlemusic.model.Album
import com.flatcode.littlemusic.utils.DATA
import com.flatcode.littlemusic.utils.checkInterested
import com.flatcode.littlemusic.utils.glideImage
import com.flatcode.littlemusic.utils.openActivity
import com.flatcode.littlemusic.utils.isInterested
import java.text.MessageFormat

class AlbumAdapter(
    private val onItemClick: (Album) -> Unit,
    private val onInterestedClick: (Album, View) -> Unit
) : ListAdapter<Album, AlbumAdapter.ViewHolder>(DiffCallback), Filterable {

    var fullList: List<Album> = emptyList()
    private var filter: AlbumFilter? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAlbumBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getFilter(): Filter {
        return filter ?: AlbumFilter(fullList, this).also { filter = it }
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

            binding.image.glideImage(image, false)

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
