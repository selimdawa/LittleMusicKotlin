package com.flatcode.littlemusic.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemusic.databinding.ItemSongHomeBinding
import com.flatcode.littlemusic.model.Song
import com.flatcode.littlemusic.utils.DATA
import com.flatcode.littlemusic.utils.convertDuration
import com.flatcode.littlemusic.utils.dataName
import com.flatcode.littlemusic.utils.incrementViewCount
import com.flatcode.littlemusic.utils.isFavorite
import com.flatcode.littlemusic.utils.isLoves
import com.flatcode.littlemusic.utils.nrLoves
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Locale

class SongMainAdapter(
    private val onPlayClick: (Song, Int) -> Unit,
    private val onPauseClick: (Song, Int) -> Unit,
    private val onFavoriteClick: (Song, View) -> Unit,
    private val onLoveClick: (Song, View) -> Unit,
    private val onArtistClick: (String, String) -> Unit,
    private val onAlbumClick: (String, String, String) -> Unit,
    private val onCategoryClick: (String, String) -> Unit
) : ListAdapter<Song, SongMainAdapter.ViewHolder>(DiffCallback), Filterable {

    var selectedPosition = -1
        set(value) {
            val oldPosition = field
            field = value
            notifyItemChanged(oldPosition)
            notifyItemChanged(field)
        }

    var fullList: List<Song> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemSongHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    inner class ViewHolder(private val binding: ItemSongHomeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Song) {
            val id = item.id
            val name = item.name ?: ""
            val artistId = item.artistId ?: ""
            val albumId = item.albumId ?: ""
            val categoryId = item.categoryId ?: ""
            val nrLovesCount = item.lovesCount

            binding.name.text = name
            binding.artist.dataName(DATA.ARTISTS, artistId)
            binding.album.dataName(DATA.ALBUMS, albumId)
            binding.category.dataName(DATA.CATEGORIES, categoryId)
            binding.duration.text = item.duration?.toLongOrNull()?.convertDuration() ?: "00:00"
            binding.nrLoves.text = nrLovesCount.toString()

            binding.favorite.isFavorite(id, DATA.FirebaseUserUid)
            binding.love.isLoves(id)
            binding.nrLoves.nrLoves(id)

            binding.favorite.setOnClickListener { onFavoriteClick(item, it) }
            binding.love.setOnClickListener { onLoveClick(item, it) }

            setupIntentData(DATA.ARTISTS, artistId, DATA.ARTIST)
            setupIntentData(DATA.ALBUMS, albumId, DATA.ALBUM)
            setupIntentData(DATA.CATEGORIES, categoryId, DATA.CATEGORY)

            binding.play.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    onPlayClick(item, bindingAdapterPosition)
                    id.incrementViewCount()
                }
            }
            binding.pause.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    onPauseClick(item, bindingAdapterPosition)
                }
            }

            if (bindingAdapterPosition == selectedPosition) {
                binding.play.visibility = View.GONE
                binding.pause.visibility = View.VISIBLE
            } else {
                binding.play.visibility = View.VISIBLE
                binding.pause.visibility = View.GONE
            }
        }

        private fun setupIntentData(database: String, dataId: String, type: String) {
            val reference = FirebaseDatabase.getInstance().getReference(database)
            reference.child(dataId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val name = snapshot.child(DATA.NAME).value?.toString() ?: ""
                    val image = snapshot.child(DATA.IMAGE).value?.toString() ?: ""

                    val textView = when (type) {
                        DATA.ARTIST -> binding.artist
                        DATA.ALBUM -> binding.album
                        DATA.CATEGORY -> binding.category
                        else -> null
                    }

                    textView?.setOnClickListener {
                        when (type) {
                            DATA.ARTIST -> onArtistClick(dataId, name)
                            DATA.ALBUM -> onAlbumClick(dataId, name, image)
                            DATA.CATEGORY -> onCategoryClick(dataId, name)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
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
