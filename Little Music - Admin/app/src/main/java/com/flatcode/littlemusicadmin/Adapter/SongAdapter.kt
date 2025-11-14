package com.flatcode.littlemusicadmin.Adapter

import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemusicadmin.Filter.SongFilter
import com.flatcode.littlemusicadmin.Model.Song
import com.flatcode.littlemusicadmin.Unit.CLASS
import com.flatcode.littlemusicadmin.Unit.DATA
import com.flatcode.littlemusicadmin.Unit.VOID
import com.flatcode.littlemusicadmin.databinding.ItemSongBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.scwang.wave.MultiWaveHeader

class SongAdapter(
    private val activity: Activity, var list: ArrayList<Song?>, listener: (Song?, Int) -> Unit,
) : RecyclerView.Adapter<SongAdapter.ViewHolder>(), Filterable {

    private var binding: ItemSongBinding? = null
    var selectedPosition = -1
    private val listener: (Song?, Int) -> Unit
    var filterList: ArrayList<Song?>
    private var filter: SongFilter? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemSongBinding.inflate(LayoutInflater.from(activity), parent, false)
        return ViewHolder(binding!!.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val id = DATA.EMPTY + item!!.id
        val name = DATA.EMPTY + item.name
        val artistId = DATA.EMPTY + item.artistId
        val albumId = DATA.EMPTY + item.albumId
        val categoryId = DATA.EMPTY + item.categoryId
        val nrLoves = DATA.EMPTY + item.lovesCount

        holder.name.text = name
        VOID.dataName(DATA.ARTISTS, artistId, holder.artist)
        VOID.dataName(DATA.ALBUMS, albumId, holder.album)
        VOID.dataName(DATA.CATEGORIES, categoryId, holder.category)
        val duration = VOID.convertDuration(item.duration!!.toLong())
        holder.duration.text = duration
        holder.nrLoves.text = nrLoves

        VOID.isFavorite(holder.favorite, id, DATA.FirebaseUserUid)
        VOID.isLoves(holder.love, id)
        VOID.nrLoves(binding!!.nrLoves, id)

        holder.favorite.setOnClickListener { VOID.checkFavorite(holder.favorite, id) }
        holder.love.setOnClickListener { VOID.checkLove(holder.love, id) }
        IntentData(DATA.ARTISTS, artistId, DATA.ARTIST, holder.artist)
        IntentData(DATA.ALBUMS, albumId, DATA.ALBUM, holder.album)
        IntentData(DATA.CATEGORIES, categoryId, DATA.CATEGORY, holder.category)

        holder.more.setOnClickListener {
            VOID.moreDeleteSong(
                activity, item, DATA.ARTISTS, artistId, DATA.SONGS_COUNT, DATA.CATEGORIES,
                categoryId, DATA.SONGS_COUNT, DATA.ALBUMS, albumId, DATA.SONGS_COUNT
            )
        }
        holder.bind(item, listener, id)
        if (selectedPosition == position) open(holder.wave) else holder.wave.visibility = View.GONE
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

    inner class ViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        var name: TextView
        var artist: TextView
        var album: TextView
        var category: TextView
        var duration: TextView
        var nrLoves: TextView
        var favorite: ImageView
        var love: ImageView
        var more: ImageView
        var card: CardView
        var wave: MultiWaveHeader
        fun bind(getSongs: Song?, listener: (Song?, Int) -> Unit, id: String?) {
            card.setOnClickListener {
                listener(getSongs, adapterPosition)
                VOID.incrementViewCount(id)
                wave.waveHeight = 40
            }
        }

        init {
            favorite = binding!!.favorite
            love = binding!!.love
            more = binding!!.more
            card = binding!!.card
            wave = binding!!.wave
            name = binding!!.name
            artist = binding!!.artist
            album = binding!!.album
            category = binding!!.category
            duration = binding!!.duration
            nrLoves = binding!!.nrLoves
        }
    }

    /*interface RecyclerItemClickListener {
        fun onClickListener(songs: Song?, position: Int)
    }*/

    private fun IntentData(database: String?, dataId: String, type: String?, text: TextView) {
        val reference = FirebaseDatabase.getInstance().getReference(database!!)
        reference.child(dataId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val Name = DATA.EMPTY + snapshot.child(DATA.NAME).value
                val Image = DATA.EMPTY + snapshot.child(DATA.IMAGE).value
                if (type == DATA.ARTIST) text.setOnClickListener {
                    VOID.IntentExtra2(
                        activity, CLASS.ARTIST_SONGS, DATA.ARTIST_ID, dataId, DATA.ARTIST_NAME, Name
                    )
                }
                if (type == DATA.ALBUM) text.setOnClickListener {
                    VOID.IntentExtra3(
                        activity, CLASS.ALBUM_SONGS, DATA.ALBUM_ID, dataId,
                        DATA.ALBUM_NAME, Name, DATA.ALBUM_IMAGE, Image
                    )
                }
                if (type == DATA.CATEGORY) text.setOnClickListener {
                    VOID.IntentExtra2(
                        activity, CLASS.CATEGORY_SONGS, DATA.CATEGORY_ID, dataId,
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