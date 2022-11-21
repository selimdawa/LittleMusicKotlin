package com.flatcode.littlemusic.Adapterimport

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemusic.Filter.AlbumFilter
import com.flatcode.littlemusic.Modelimport.Album
import com.flatcode.littlemusic.Unit.VOID
import com.flatcode.littlemusic.Unitimport.CLASSv
import com.flatcode.littlemusic.Unitimport.DATAv
import com.flatcode.littlemusic.databinding.ItemAlbumBinding
import java.text.MessageFormat

class AlbumAdapter(private val activity: Activity, var list: ArrayList<Album?>) :
    RecyclerView.Adapter<AlbumAdapter.ViewHolder>(), Filterable {

    private var binding: ItemAlbumBinding? = null
    var filterList: ArrayList<Album?>
    private var filter: AlbumFilter? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumAdapter.ViewHolder {
        binding = ItemAlbumBinding.inflate(
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
        val artistId = DATAv.EMPTY + item.artistId
        val categoryId = DATAv.EMPTY + item.categoryId
        val interestedCount = DATAv.EMPTY + item.interestedCount
        val songsCount = DATAv.EMPTY + item.songsCount

        VOID.GlideImage(false, activity, image, holder.image)
        if (name == DATAv.EMPTY) {
            holder.name.visibility = View.GONE
        } else {
            holder.name.visibility = View.VISIBLE
            holder.name.text = name
        }
        if (interestedCount == DATAv.EMPTY) holder.numberInterested.text = MessageFormat.format(
            "{0}{1}",
            DATAv.EMPTY,
            DATAv.ZERO
        ) else holder.numberInterested.text = interestedCount
        if (songsCount == DATAv.EMPTY) holder.numberSongs.text =
            MessageFormat.format("{0}{1}", DATAv.EMPTY, DATAv.ZERO) else holder.numberSongs.text =
            songsCount

        VOID.isInterested(holder.add, id, DATAv.ALBUMS)
        holder.add.setOnClickListener {
            VOID.checkInterested(
                holder.add,
                DATAv.ALBUMS,
                id
            )
        }
        holder.item.setOnClickListener {
            VOID.IntentExtra3(
                activity, CLASSv.ALBUM_SONGS,
                DATAv.ALBUM_ID, id, DATAv.ALBUM_NAME, name, DATAv.ALBUM_IMAGE, image
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

    inner class ViewHolder(view: View?) : RecyclerView.ViewHolder(
        view!!
    ) {
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