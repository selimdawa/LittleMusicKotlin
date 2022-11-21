package com.flatcode.littlemusic.Adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemusic.Filter.CategoryFilter
import com.flatcode.littlemusic.Modelimport.Category
import com.flatcode.littlemusic.Unit.VOID
import com.flatcode.littlemusic.Unitimport.CLASSv
import com.flatcode.littlemusic.Unitimport.DATAv
import com.flatcode.littlemusic.databinding.ItemCategoryBinding
import java.text.MessageFormat

class CategoryAdapter(private val activity: Activity, var list: ArrayList<Category?>) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>(), Filterable {

    private var binding: ItemCategoryBinding? = null
    var filterList: ArrayList<Category?>
    private var filter: CategoryFilter? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemCategoryBinding.inflate(
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
        val interestedCount = DATAv.EMPTY + item.interestedCount
        val albumCount = DATAv.EMPTY + item.albumsCount
        val songsCount = DATAv.EMPTY + item.songsCount
        VOID.GlideImage(false, activity, image, holder.image)
        if (item.name == DATAv.EMPTY) {
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
        if (albumCount == DATAv.EMPTY) holder.numberAlbums.text =
            MessageFormat.format("{0}{1}", DATAv.EMPTY, DATAv.ZERO) else holder.numberAlbums.text =
            albumCount
        VOID.isInterested(holder.add, id, DATAv.CATEGORIES)
        holder.add.setOnClickListener {
            VOID.checkInterested(
                holder.add,
                DATAv.CATEGORIES,
                id
            )
        }
        holder.item.setOnClickListener {
            VOID.IntentExtra2(
                activity, CLASSv.CATEGORY_SONGS, DATAv.CATEGORY_ID, id, DATAv.CATEGORY_NAME, name
            )
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getFilter(): Filter {
        if (filter == null) {
            filter = CategoryFilter(filterList, this)
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
        var numberAlbums: TextView
        var numberInterested: TextView
        var item: LinearLayout

        init {
            image = binding!!.image
            name = binding!!.name
            add = binding!!.add
            numberSongs = binding!!.numberSongs
            numberAlbums = binding!!.numberAlbums
            numberInterested = binding!!.numberInterested
            item = binding!!.item
        }
    }

    init {
        filterList = list
    }
}