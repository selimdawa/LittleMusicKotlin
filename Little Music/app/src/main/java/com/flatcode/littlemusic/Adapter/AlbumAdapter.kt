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
import com.flatcode.littlemusic.Filter.AlbumFilter
import com.flatcode.littlemusic.Modelimport.Album
import com.flatcode.littlemusic.Unit.VOID
import com.flatcode.littlemusic.Unitimport.CLASS
import com.flatcode.littlemusic.Unitimport.DATA
import com.flatcode.littlemusic.databinding.ItemAlbumBinding
import java.text.MessageFormat

class AlbumAdapter(private val activity: Activity, var list: ArrayList<Album?>) :
    RecyclerView.Adapter<AlbumAdapter.ViewHolder>(), Filterable {

    private var binding: ItemAlbumBinding? = null
    var filterList: ArrayList<Album?>
    private var filter: AlbumFilter? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumAdapter.ViewHolder {
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

        VOID.GlideImage(false, activity, image, holder.image)

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

        VOID.isInterested(holder.add, id, DATA.ALBUMS)
        holder.add.setOnClickListener { VOID.checkInterested(holder.add, DATA.ALBUMS, id) }
        holder.item.setOnClickListener {
            VOID.IntentExtra3(
                activity, CLASS.ALBUM_SONGS, DATA.ALBUM_ID, id,
                DATA.ALBUM_NAME, name, DATA.ALBUM_IMAGE, image
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
        var add: ImageView
        var name: TextView
        var numberSongs: TextView
        var numberInterested: TextView
        var item: LinearLayout

        init {
            image = binding!!.image
            name = binding!!.name
            add = binding!!.add
            numberSongs = binding!!.numberSongs
            numberInterested = binding!!.numberInterested
            item = binding!!.item
        }
    }

    init {
        filterList = list
    }
}