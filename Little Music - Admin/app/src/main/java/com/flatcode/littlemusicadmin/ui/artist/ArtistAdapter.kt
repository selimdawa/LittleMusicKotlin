package com.flatcode.littlemusicadmin.ui.artist

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemusicadmin.ui.song.ArtistSongsActivity
import com.flatcode.littlemusicadmin.model.Artist
import com.flatcode.littlemusicadmin.utils.*
import com.flatcode.littlemusicadmin.databinding.ItemArtistBinding
import java.text.MessageFormat

class ArtistAdapter(private val activity: Activity, var list: ArrayList<Artist?>) :

    RecyclerView.Adapter<ArtistAdapter.ViewHolder>(), Filterable {
    var filterList: ArrayList<Artist?>
    private var filter: ArtistFilter? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemArtistBinding.inflate(LayoutInflater.from(activity), parent, false)
        return ViewHolder(binding)
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

        holder.binding.image.glide(true, image)

        if (item.name == DATA.EMPTY) {
            holder.binding.name.visibility = View.GONE
        } else {
            holder.binding.name.visibility = View.VISIBLE
            holder.binding.name.text = name
        }

        if (albumCount == DATA.EMPTY) holder.binding.numberAlbums.text =
            MessageFormat.format("{0}{1}", DATA.EMPTY, DATA.ZERO) else holder.binding.numberAlbums.text =
            albumCount

        if (songsCount == DATA.EMPTY) holder.binding.numberSongs.text =
            MessageFormat.format("{0}{1}", DATA.EMPTY, DATA.ZERO) else holder.binding.numberSongs.text =
            songsCount

        holder.binding.more.setOnClickListener {
            item.moreDelete(
                activity, DATA.NULL, DATA.NULL, DATA.NULL, DATA.NULL, DATA.NULL, DATA.NULL,
                DATA.NULL, DATA.NULL, DATA.NULL
            )
        }
        holder.binding.item.setOnClickListener {
            activity.openActivity(
                ArtistSongsActivity::class.java, DATA.ARTIST_ID, id, DATA.ARTIST_NAME,
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

    inner class ViewHolder(val binding: ItemArtistBinding) : RecyclerView.ViewHolder(binding.root)

    init {
        filterList = list
    }
}
