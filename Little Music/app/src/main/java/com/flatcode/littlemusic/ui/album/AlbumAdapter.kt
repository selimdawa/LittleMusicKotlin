package com.flatcode.littlemusic.ui.album

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemusic.databinding.ItemAlbumBinding
import com.flatcode.littlemusic.filter.AlbumFilter
import com.flatcode.littlemusic.model.Album
import com.flatcode.littlemusic.utils.CLASS
import com.flatcode.littlemusic.utils.DATA
import com.flatcode.littlemusic.utils.checkInterested
import com.flatcode.littlemusic.utils.glideImage
import com.flatcode.littlemusic.utils.intentExtra3
import com.flatcode.littlemusic.utils.isInterested
import java.text.MessageFormat

class AlbumAdapter(private val activity: Activity, var list: ArrayList<Album?>) :
    RecyclerView.Adapter<AlbumAdapter.ViewHolder>(), Filterable {

    var filterList: ArrayList<Album?> = list
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
        val interestedCount = DATA.EMPTY + item.interestedCount
        val songsCount = DATA.EMPTY + item.songsCount

        val binding = holder.binding
        binding.image.glideImage(image, false)

        if (name == DATA.EMPTY) {
            binding.name.visibility = View.GONE
        } else {
            binding.name.visibility = View.VISIBLE
            binding.name.text = name
        }

        if (interestedCount == DATA.EMPTY) {
            binding.numberInterested.text = MessageFormat.format("{0}{1}", DATA.EMPTY, DATA.ZERO)
        } else {
            binding.numberInterested.text = interestedCount
        }

        if (songsCount == DATA.EMPTY) {
            binding.numberSongs.text = MessageFormat.format("{0}{1}", DATA.EMPTY, DATA.ZERO)
        } else {
            binding.numberSongs.text = songsCount
        }

        binding.add.isInterested(id, DATA.ALBUMS)
        binding.add.setOnClickListener { binding.add.checkInterested(DATA.ALBUMS, id) }
        binding.item.setOnClickListener {
            activity.intentExtra3(
                CLASS.ALBUM_SONGS, DATA.ALBUM_ID, id,
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

    inner class ViewHolder(val binding: ItemAlbumBinding) : RecyclerView.ViewHolder(binding.root)
}