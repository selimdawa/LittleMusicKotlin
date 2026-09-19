package com.flatcode.littlemusic.ui.song

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemusic.databinding.ItemSongBinding
import com.flatcode.littlemusic.filter.SongFilter
import com.flatcode.littlemusic.model.Song
import com.flatcode.littlemusic.ui.album.AlbumSongsActivity
import com.flatcode.littlemusic.ui.artist.ArtistSongsActivity
import com.flatcode.littlemusic.ui.category.CategorySongsActivity
import com.flatcode.littlemusic.utils.DATA
import com.flatcode.littlemusic.utils.checkFavorite
import com.flatcode.littlemusic.utils.checkLove
import com.flatcode.littlemusic.utils.convertDuration
import com.flatcode.littlemusic.utils.dataName
import com.flatcode.littlemusic.utils.incrementViewCount
import com.flatcode.littlemusic.utils.intentExtra2
import com.flatcode.littlemusic.utils.intentExtra3
import com.flatcode.littlemusic.utils.isFavorite
import com.flatcode.littlemusic.utils.isLoves
import com.flatcode.littlemusic.utils.nrLoves
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.scwang.wave.MultiWaveHeader

class SongAdapter(
    private val context: Context?, var list: ArrayList<Song?>,
    private var listener: (Song?, Int) -> Unit
) : RecyclerView.Adapter<SongAdapter.ViewHolder>(), Filterable {

    var selectedPosition = -1
    var filterList: ArrayList<Song?> = list
    private var filter: SongFilter? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSongBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val id = DATA.EMPTY + item!!.id
        val name = DATA.EMPTY + item.name
        val artistId = DATA.EMPTY + item.artistId
        val albumId = DATA.EMPTY + item.albumId
        val categoryId = DATA.EMPTY + item.categoryId
        val nrLoves = DATA.EMPTY + item.lovesCount

        val binding = holder.binding
        binding.name.text = name
        binding.artist.dataName(DATA.ARTISTS, artistId)
        binding.album.dataName(DATA.ALBUMS, albumId)
        binding.category.dataName(DATA.CATEGORIES, categoryId)
        val duration = item.duration!!.toLong().convertDuration()
        binding.duration.text = duration
        binding.nrLoves.text = nrLoves

        binding.favorite.isFavorite(id, DATA.FirebaseUserUid)
        binding.love.isLoves(id)
        binding.nrLoves.nrLoves(id)
        binding.favorite.setOnClickListener { binding.favorite.checkFavorite(id) }
        binding.love.setOnClickListener { binding.love.checkLove(id) }

        IntentData(DATA.ARTISTS, artistId, DATA.ARTIST, binding.artist)
        IntentData(DATA.ALBUMS, albumId, DATA.ALBUM, binding.album)
        IntentData(DATA.CATEGORIES, categoryId, DATA.CATEGORY, binding.category)

        holder.bind(item, listener, id)
        if (selectedPosition == position) open(binding.wave)
        else binding.wave.visibility = View.GONE
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getFilter(): Filter {
        if (filter == null) {
            filter = SongFilter(filterList, this)
        }
        return filter!!
    }

    inner class ViewHolder(val binding: ItemSongBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(getSongs: Song?, listener: (Song?, Int) -> Unit, id: String?) {
            binding.card.setOnClickListener {
                listener(getSongs, adapterPosition)
                id?.incrementViewCount()
            }
        }
    }

    private fun IntentData(database: String, dataId: String, type: String, text: TextView) {
        val reference = FirebaseDatabase.getInstance().getReference(database)
        reference.child(dataId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val Name = DATA.EMPTY + snapshot.child(DATA.NAME).value
                val Image = DATA.EMPTY + snapshot.child(DATA.IMAGE).value
                if (type == DATA.ARTIST) text.setOnClickListener {
                    context?.intentExtra2(
                        ArtistSongsActivity::class.java, DATA.ARTIST_ID, dataId, DATA.ARTIST_NAME, Name
                    )
                }
                if (type == DATA.ALBUM) text.setOnClickListener {
                    context?.intentExtra3(
                        AlbumSongsActivity::class.java, DATA.ALBUM_ID, dataId,
                        DATA.ALBUM_NAME, Name, DATA.ALBUM_IMAGE, Image
                    )
                }
                if (type == DATA.CATEGORY) text.setOnClickListener {
                    context?.intentExtra2(
                        CategorySongsActivity::class.java, DATA.CATEGORY_ID, dataId,
                        DATA.CATEGORY_NAME, Name
                    )
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
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
    }
}