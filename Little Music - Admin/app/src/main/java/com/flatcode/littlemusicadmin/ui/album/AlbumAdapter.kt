package com.flatcode.littlemusicadmin.ui.album

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemusicadmin.ui.song.AlbumSongsActivity
import com.flatcode.littlemusicadmin.filter.AlbumFilter
import com.flatcode.littlemusicadmin.model.Album
import com.flatcode.littlemusicadmin.utils.*
import com.flatcode.littlemusicadmin.databinding.ItemAlbumBinding
import java.text.MessageFormat

class AlbumAdapter(private val activity: Activity, var list: ArrayList<Album?>) :
    RecyclerView.Adapter<AlbumAdapter.ViewHolder>(), Filterable {

    var filterList: ArrayList<Album?>
    private var filter: AlbumFilter? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAlbumBinding.inflate(LayoutInflater.from(activity), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val id = DATA.EMPTY + item!!.id
        val name = DATA.EMPTY + item.name
        val image = DATA.EMPTY + item.image
        val artistId = DATA.EMPTY + item.artistId
        val categoryId = DATA.EMPTY + item.categoryId
        val interestedCount = DATA.EMPTY + item.interestedCount
        val songsCount = DATA.EMPTY + item.songsCount

        holder.binding.image.glide(false, image)

        if (name == DATA.EMPTY) {
            holder.binding.name.visibility = View.GONE
        } else {
            holder.binding.name.visibility = View.VISIBLE
            holder.binding.name.text = name
        }

        if (interestedCount == DATA.EMPTY) holder.binding.numberInterested.text = MessageFormat.format(
            "{0}{1}", DATA.EMPTY, DATA.ZERO
        ) else holder.binding.numberInterested.text = interestedCount

        if (songsCount == DATA.EMPTY) holder.binding.numberSongs.text =
            MessageFormat.format("{0}{1}", DATA.EMPTY, DATA.ZERO) else holder.binding.numberSongs.text =
            songsCount

        holder.binding.more.setOnClickListener {
            item.moreDelete(
                activity, DATA.ARTISTS, artistId, DATA.ALBUMS_COUNT,
                DATA.CATEGORIES, categoryId, DATA.ALBUMS_COUNT, DATA.NULL, DATA.NULL, DATA.NULL
            )
        }
        holder.binding.item.setOnClickListener {
            activity.intentExtra3(
                AlbumSongsActivity::class.java,
                DATA.ALBUM_ID, id, DATA.ALBUM_NAME, name, DATA.ALBUM_IMAGE, image
            )
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getFilter(): Filter {
        if (filter == null) {
            filter = AlbumFilter(filterList, this)
        }
        return filter!!
    }

    inner class ViewHolder(val binding: ItemAlbumBinding) : RecyclerView.ViewHolder(binding.root)

    init {
        filterList = list
    }
}