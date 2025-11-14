package com.flatcode.littlemusicadmin.Adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemusicadmin.Filter.AlbumFilter
import com.flatcode.littlemusicadmin.Model.Album
import com.flatcode.littlemusicadmin.Unit.CLASS
import com.flatcode.littlemusicadmin.Unit.DATA
import com.flatcode.littlemusicadmin.Unit.VOID
import com.flatcode.littlemusicadmin.databinding.ItemAlbumBinding
import java.text.MessageFormat

class AlbumAdapter(private val activity: Activity, var list: ArrayList<Album?>) :
    RecyclerView.Adapter<AlbumAdapter.ViewHolder>(), Filterable {

    private var binding: ItemAlbumBinding? = null
    var filterList: ArrayList<Album?>
    private var filter: AlbumFilter? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemAlbumBinding.inflate(LayoutInflater.from(activity), parent, false)
        return ViewHolder(binding!!.root)
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

        VOID.Glide(false, activity, image, holder.image)

        if (name == DATA.EMPTY) {
            holder.name.visibility = View.GONE
        } else {
            holder.name.visibility = View.VISIBLE
            holder.name.text = name
        }

        if (interestedCount == DATA.EMPTY) holder.numberInterested.text = MessageFormat.format(
            "{0}{1}", DATA.EMPTY, DATA.ZERO
        ) else holder.numberInterested.text = interestedCount

        if (songsCount == DATA.EMPTY) holder.numberSongs.text =
            MessageFormat.format("{0}{1}", DATA.EMPTY, DATA.ZERO) else holder.numberSongs.text =
            songsCount

        holder.more.setOnClickListener {
            VOID.moreDeleteAlbum(
                activity, item, DATA.ARTISTS, artistId, DATA.ALBUMS_COUNT,
                DATA.CATEGORIES, categoryId, DATA.ALBUMS_COUNT, DATA.NULL, DATA.NULL, DATA.NULL
            )
        }
        holder.item.setOnClickListener {
            VOID.IntentExtra3(
                activity, CLASS.ALBUM_SONGS,
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

    inner class ViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        var image: ImageView
        var more: ImageButton
        var name: TextView
        var numberSongs: TextView
        var numberInterested: TextView
        var item: LinearLayout

        init {
            image = binding!!.image
            name = binding!!.name
            more = binding!!.more
            numberSongs = binding!!.numberSongs
            numberInterested = binding!!.numberInterested
            item = binding!!.item
        }
    }

    init {
        filterList = list
    }
}