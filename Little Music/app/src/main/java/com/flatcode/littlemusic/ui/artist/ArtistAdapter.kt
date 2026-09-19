package com.flatcode.littlemusic.ui.artist

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemusic.databinding.ItemArtistBinding
import com.flatcode.littlemusic.filter.ArtistFilter
import com.flatcode.littlemusic.model.Artist
import com.flatcode.littlemusic.utils.DATA
import com.flatcode.littlemusic.utils.checkInterested
import com.flatcode.littlemusic.utils.glideImage
import com.flatcode.littlemusic.utils.intentExtra4
import com.flatcode.littlemusic.utils.isInterested
import java.text.MessageFormat

class ArtistAdapter(private val activity: Activity, var list: ArrayList<Artist?>) :
    RecyclerView.Adapter<ArtistAdapter.ViewHolder>(), Filterable {

    var filterList: ArrayList<Artist?> = list
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
        val albumCount = DATA.EMPTY + item.albumsCount
        val songsCount = DATA.EMPTY + item.songsCount

        val binding = holder.binding
        binding.image.glideImage(image, true)

        if (name == DATA.EMPTY) {
            binding.name.visibility = View.GONE
        } else {
            binding.name.visibility = View.VISIBLE
            binding.name.text = name
        }

        if (albumCount == DATA.EMPTY) {
            binding.numberAlbums.text = MessageFormat.format("{0}{1}", DATA.EMPTY, DATA.ZERO)
        } else {
            binding.numberAlbums.text = albumCount
        }

        if (songsCount == DATA.EMPTY) {
            binding.numberSongs.text = MessageFormat.format("{0}{1}", DATA.EMPTY, DATA.ZERO)
        } else {
            binding.numberSongs.text = songsCount
        }

        binding.add.isInterested(id, DATA.ARTISTS)
        binding.add.setOnClickListener { binding.add.checkInterested(DATA.ARTISTS, id) }

        binding.item.setOnClickListener {
            activity.intentExtra4(
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
}