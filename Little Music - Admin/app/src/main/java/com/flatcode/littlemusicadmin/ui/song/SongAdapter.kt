package com.flatcode.littlemusicadmin.ui.song

import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemusicadmin.model.Song
import com.flatcode.littlemusicadmin.utils.*
import com.flatcode.littlemusicadmin.databinding.ItemSongBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.scwang.wave.MultiWaveHeader

class SongAdapter(
    private val activity: Activity, var list: ArrayList<Song?>, listener: (Song?, Int) -> Unit,
) : RecyclerView.Adapter<SongAdapter.ViewHolder>(), Filterable {

    var selectedPosition = -1
    private val listener: (Song?, Int) -> Unit
    var filterList: ArrayList<Song?>
    private var filter: SongFilter? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSongBinding.inflate(LayoutInflater.from(activity), parent, false)
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

        holder.binding.name.text = name
        holder.binding.artist.dataName(DATA.ARTISTS, artistId)
        holder.binding.album.dataName(DATA.ALBUMS, albumId)
        holder.binding.category.dataName(DATA.CATEGORIES, categoryId)
        val duration = item.duration!!.toLong().convertDuration()
        holder.binding.duration.text = duration
        holder.binding.nrLoves.text = nrLoves

        holder.binding.favorite.isFavorite(id, DATA.FirebaseUserUid)
        holder.binding.love.isLoves(id)
        holder.binding.nrLoves.nrLoves(id)

        holder.binding.favorite.setOnClickListener { holder.binding.favorite.checkFavorite(id) }
        holder.binding.love.setOnClickListener { holder.binding.love.checkLove(id) }
        IntentData(DATA.ARTISTS, artistId, DATA.ARTIST, holder.binding.artist)
        IntentData(DATA.ALBUMS, albumId, DATA.ALBUM, holder.binding.album)
        IntentData(DATA.CATEGORIES, categoryId, DATA.CATEGORY, holder.binding.category)

        holder.binding.more.setOnClickListener {
            item.moreDelete(
                activity, DATA.ARTISTS, artistId, DATA.SONGS_COUNT, DATA.CATEGORIES,
                categoryId, DATA.SONGS_COUNT, DATA.ALBUMS, albumId, DATA.SONGS_COUNT
            )
        }
        holder.bind(item, listener, id)
        if (selectedPosition == position) open(holder.binding.wave) else holder.binding.wave.visibility = View.GONE
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
                id!!.incrementViewCount()
                binding.wave.waveHeight = 40
            }
        }
    }

    private fun IntentData(database: String?, dataId: String, type: String?, text: TextView) {
        val reference = FirebaseDatabase.getInstance().getReference(database!!)
        reference.child(dataId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val Name = DATA.EMPTY + snapshot.child(DATA.NAME).value
                val Image = DATA.EMPTY + snapshot.child(DATA.IMAGE).value
                if (type == DATA.ARTIST) text.setOnClickListener {
                    activity.openActivity(
                        ArtistSongsActivity::class.java, DATA.ARTIST_ID, dataId, DATA.ARTIST_NAME, Name
                    )
                }
                if (type == DATA.ALBUM) text.setOnClickListener {
                    activity.openActivity(
                        AlbumSongsActivity::class.java, DATA.ALBUM_ID, dataId,
                        DATA.ALBUM_NAME, Name, DATA.ALBUM_IMAGE, Image
                    )
                }
                if (type == DATA.CATEGORY) text.setOnClickListener {
                    activity.openActivity(
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

    init {
        filterList = list
        this.listener = listener
    }
}