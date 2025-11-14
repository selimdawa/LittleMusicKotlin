package com.flatcode.littlemusic.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemusic.Filter.SongMainFilter
import com.flatcode.littlemusic.Modelimport.Song
import com.flatcode.littlemusic.Unit.VOID
import com.flatcode.littlemusic.Unitimport.CLASS
import com.flatcode.littlemusic.Unitimport.DATA
import com.flatcode.littlemusic.databinding.ItemSongHomeBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SongMainAdapter(
    private val context: Context?, var list: ArrayList<Song?>,
    listener: (Song?, Int) -> Unit, listener2: (Song?, Int) -> Unit,
) : RecyclerView.Adapter<SongMainAdapter.ViewHolder>(), Filterable {

    private var binding: ItemSongHomeBinding? = null
    var selectedPosition = -1
    private val listener: (Song?, Int) -> Unit
    private val listener2: (Song?, Int) -> Unit
    var filterList: ArrayList<Song?>
    private var filter: SongMainFilter? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemSongHomeBinding.inflate(LayoutInflater.from(context), parent, false)
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

        holder.bind(item, listener, id, listener2, holder.play, holder.pause)
        if (position == selectedPosition) {
            holder.play.visibility = View.GONE
            holder.pause.visibility = View.VISIBLE
        } else {
            holder.play.visibility = View.VISIBLE
            holder.pause.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getFilter(): Filter {
        if (filter == null) {
            filter = SongMainFilter(filterList, this)
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
        var play: ImageView
        var pause: ImageView
        var card: CardView
        fun bind(
            getSongs: Song?, listener: (Song?, Int) -> Unit, id: String?,
            listener2: (Song?, Int) -> Unit, playBtn: ImageView, pauseBtn: ImageView,
        ) {
            play.setOnClickListener {
                listener(getSongs, adapterPosition)
                VOID.incrementViewCount(id)
            }
            pause.setOnClickListener {
                listener2(getSongs, adapterPosition)
                playBtn.visibility = View.VISIBLE
                pauseBtn.visibility = View.GONE
            }
        }

        init {
            favorite = binding!!.favorite
            love = binding!!.love
            card = binding!!.card
            play = binding!!.play
            pause = binding!!.pause
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

    private fun IntentData(database: String, dataId: String, type: String, text: TextView) {
        val reference = FirebaseDatabase.getInstance().getReference(database)
        reference.child(dataId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val Name = DATA.EMPTY + snapshot.child(DATA.NAME).value
                val Image = DATA.EMPTY + snapshot.child(DATA.IMAGE).value
                if (type == DATA.ARTIST) text.setOnClickListener {
                    VOID.IntentExtra2(
                        context, CLASS.ARTIST_SONGS, DATA.ARTIST_ID, dataId, DATA.ARTIST_NAME, Name)
                }
                if (type == DATA.ALBUM) text.setOnClickListener {
                    VOID.IntentExtra3(
                        context, CLASS.ALBUM_SONGS, DATA.ALBUM_ID, dataId,
                        DATA.ALBUM_NAME, Name, DATA.ALBUM_IMAGE, Image
                    )
                }
                if (type == DATA.CATEGORY) text.setOnClickListener {
                    VOID.IntentExtra2(
                        context, CLASS.CATEGORY_SONGS, DATA.CATEGORY_ID, dataId,
                        DATA.CATEGORY_NAME, Name
                    )
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    init {
        filterList = list
        this.listener = listener
        this.listener2 = listener2
    }
}