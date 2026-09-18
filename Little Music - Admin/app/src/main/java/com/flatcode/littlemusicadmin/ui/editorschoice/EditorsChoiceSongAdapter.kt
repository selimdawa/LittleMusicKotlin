package com.flatcode.littlemusicadmin.ui.editorschoice

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemusicadmin.model.Song
import com.flatcode.littlemusicadmin.utils.*
import com.flatcode.littlemusicadmin.databinding.ItemEditorsChoiceBinding

class EditorsChoiceSongAdapter(
    private val activity: Activity, var oldId: String?, var list: ArrayList<Song?>, number: Int,
) : RecyclerView.Adapter<EditorsChoiceSongAdapter.ViewHolder>(), Filterable {

    var filterList: ArrayList<Song?>
    private var filter: EditorsChoiceFilter? = null
    var number: Int

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEditorsChoiceBinding.inflate(LayoutInflater.from(activity), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val id = DATA.EMPTY + item!!.id
        val name = DATA.EMPTY + item.name
        val nrViews = DATA.EMPTY + item.viewsCount
        val nrLoves = DATA.EMPTY + item.lovesCount
        val artistId = DATA.EMPTY + item.artistId
        val albumId = DATA.EMPTY + item.albumId
        val categoryId = DATA.EMPTY + item.categoryId

        if (name == DATA.EMPTY) {
            holder.binding.name.visibility = View.GONE
        } else {
            holder.binding.name.visibility = View.VISIBLE
            holder.binding.name.text = name
        }

        holder.binding.nrViews.text = nrViews
        holder.binding.nrLoves.text = nrLoves

        holder.binding.artist.dataName(DATA.ARTISTS, artistId)
        holder.binding.album.dataName(DATA.ALBUMS, albumId)
        holder.binding.category.dataName(DATA.CATEGORIES, categoryId)

        holder.binding.add.setOnClickListener {
            if (oldId != null) {
                activity.addToEditorsChoice(id, number)
                activity.addToEditorsChoice(oldId, 0)
            } else {
                activity.addToEditorsChoice(id, number)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getFilter(): Filter {
        if (filter == null) {
            filter = EditorsChoiceFilter(filterList, this)
        }
        return filter!!
    }

    inner class ViewHolder(val binding: ItemEditorsChoiceBinding) : RecyclerView.ViewHolder(binding.root)

    init {
        filterList = list
        this.number = number
    }
}
