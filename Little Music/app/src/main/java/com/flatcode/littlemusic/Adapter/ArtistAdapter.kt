package com.flatcode.littlemusic.Adapterimport

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemusic.Filterimport.ArtistFilter
import com.flatcode.littlemusic.Modelimport.Artist
import com.flatcode.littlemusic.Unit.VOID
import com.flatcode.littlemusic.Unitimport.CLASS
import com.flatcode.littlemusic.Unitimport.DATA
import com.flatcode.littlemusic.databinding.ItemArtistBinding
import java.text.MessageFormat

class ArtistAdapter(private val activity: Activity, var list: ArrayList<Artist?>) :
    RecyclerView.Adapter<ArtistAdapter.ViewHolder>(), Filterable {

    private var binding: ItemArtistBinding? = null
    var filterList: ArrayList<Artist?>
    private var filter: ArtistFilter? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistAdapter.ViewHolder {
        binding = ItemArtistBinding.inflate(LayoutInflater.from(activity), parent, false)
        return ViewHolder(binding!!.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val id = DATA.EMPTY + item!!.id
        val name = DATA.EMPTY + item.name
        val image = DATA.EMPTY + item.image
        val aboutTheArtist = DATA.EMPTY + item.aboutTheArtist
        val interestedCount = DATA.EMPTY + item.interestedCount
        val albumCount = DATA.EMPTY + item.albumsCount
        val songsCount = DATA.EMPTY + item.songsCount

        VOID.GlideImage(true, activity, image, holder.image)

        if (item.name == DATA.EMPTY) {
            holder.name.visibility = View.GONE
        } else {
            holder.name.visibility = View.VISIBLE
            holder.name.text = name
        }

        if (albumCount == DATA.EMPTY) holder.numberAlbums.text =
            MessageFormat.format("{0}{1}", DATA.EMPTY, DATA.ZERO) else holder.numberAlbums.text =
            albumCount

        if (songsCount == DATA.EMPTY) holder.numberSongs.text =
            MessageFormat.format("{0}{1}", DATA.EMPTY, DATA.ZERO) else holder.numberSongs.text =
            songsCount

        VOID.isInterested(holder.add, id, DATA.ARTISTS)
        holder.add.setOnClickListener { VOID.checkInterested(holder.add, DATA.ARTISTS, id) }

        holder.item.setOnClickListener {
            VOID.IntentExtra4(
                activity, CLASS.ARTIST_SONGS, DATA.ARTIST_ID, id, DATA.ARTIST_NAME,
                name, DATA.ARTIST_IMAGE, image, DATA.ARTIST_ABOUT, aboutTheArtist
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

    inner class ViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        var image: ImageView
        var add: ImageView
        var name: TextView
        var numberSongs: TextView
        var numberAlbums: TextView
        var item: LinearLayout

        init {
            image = binding!!.image
            name = binding!!.name
            add = binding!!.add
            numberSongs = binding!!.numberSongs
            numberAlbums = binding!!.numberAlbums
            item = binding!!.item
        }
    }

    init {
        filterList = list
    }
}