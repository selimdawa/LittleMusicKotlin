package com.flatcode.littlemusicadmin.Adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemusicadmin.Filter.ArtistFilter
import com.flatcode.littlemusicadmin.Model.Artist
import com.flatcode.littlemusicadmin.Unit.CLASSv
import com.flatcode.littlemusicadmin.Unit.DATAv
import com.flatcode.littlemusicadmin.Unit.VOID
import com.flatcode.littlemusicadmin.databinding.ItemArtistBinding
import java.text.MessageFormat

class ArtistAdapter(private val activity: Activity, var list: ArrayList<Artist?>) :

    RecyclerView.Adapter<ArtistAdapter.ViewHolder>(), Filterable {
    private var binding: ItemArtistBinding? = null
    var filterList: ArrayList<Artist?>
    private var filter: ArtistFilter? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemArtistBinding.inflate(
            LayoutInflater.from(
                activity
            ), parent, false
        )
        return ViewHolder(binding!!.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val id = DATAv.EMPTY + item!!.id
        val name = DATAv.EMPTY + item.name
        val image = DATAv.EMPTY + item.image
        val aboutTheArtist = DATAv.EMPTY + item.aboutTheArtist
        val interestedCount = DATAv.EMPTY + item.interestedCount
        val albumCount = DATAv.EMPTY + item.albumsCount
        val songsCount = DATAv.EMPTY + item.songsCount

        VOID.Glide(true, activity, image, holder.image)

        if (item.name == DATAv.EMPTY) {
            holder.name.visibility = View.GONE
        } else {
            holder.name.visibility = View.VISIBLE
            holder.name.text = name
        }
        if (albumCount == DATAv.EMPTY) holder.numberAlbums.text =
            MessageFormat.format("{0}{1}", DATAv.EMPTY, DATAv.ZERO) else holder.numberAlbums.text =
            albumCount
        if (songsCount == DATAv.EMPTY) holder.numberSongs.text =
            MessageFormat.format("{0}{1}", DATAv.EMPTY, DATAv.ZERO) else holder.numberSongs.text =
            songsCount

        holder.more.setOnClickListener {
            VOID.moreDeleteArtist(
                activity,
                item,
                DATAv.NULL,
                DATAv.NULL,
                DATAv.NULL,
                DATAv.NULL,
                DATAv.NULL,
                DATAv.NULL,
                DATAv.NULL,
                DATAv.NULL,
                DATAv.NULL
            )
        }
        holder.item.setOnClickListener {
            VOID.IntentExtra4(
                activity, CLASSv.ARTIST_SONGS, DATAv.ARTIST_ID, id, DATAv.ARTIST_NAME,
                name, DATAv.ARTIST_IMAGE, image, DATAv.ARTIST_ABOUT, aboutTheArtist
            )
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getFilter(): Filter {
        if (filter == null) {
            filter = ArtistFilter(filterList, this)
        }
        return filter!!
    }

    inner class ViewHolder(view: View?) : RecyclerView.ViewHolder(
        view!!
    ) {
        var image: ImageView
        var more: ImageButton
        var name: TextView
        var numberSongs: TextView
        var numberAlbums: TextView
        var item: LinearLayout

        init {
            image = binding!!.image
            name = binding!!.name
            more = binding!!.more
            numberSongs = binding!!.numberSongs
            numberAlbums = binding!!.numberAlbums
            item = binding!!.item
        }
    }

    init {
        filterList = list
    }
}