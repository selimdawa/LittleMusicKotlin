package com.flatcode.littlemusicadmin.Adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemusicadmin.Filter.EditorsChoiceFilter
import com.flatcode.littlemusicadmin.Model.Song
import com.flatcode.littlemusicadmin.Unit.DATAv
import com.flatcode.littlemusicadmin.Unit.VOID
import com.flatcode.littlemusicadmin.databinding.ItemEditorsChoiceBinding

class EditorsChoiceSongAdapter(
    private val activity: Activity,
    var oldId: String?,
    var list: ArrayList<Song?>,
    number: Int,
) : RecyclerView.Adapter<EditorsChoiceSongAdapter.ViewHolder>(), Filterable {

    private var binding: ItemEditorsChoiceBinding? = null
    var filterList: ArrayList<Song?>
    private var filter: EditorsChoiceFilter? = null
    var number: Int

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemEditorsChoiceBinding.inflate(
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
        val nrViews = DATAv.EMPTY + item.viewsCount
        val nrLoves = DATAv.EMPTY + item.lovesCount
        val artistId = DATAv.EMPTY + item.artistId
        val albumId = DATAv.EMPTY + item.albumId
        val categoryId = DATAv.EMPTY + item.categoryId

        if (name == DATAv.EMPTY) {
            holder.name.visibility = View.GONE
        } else {
            holder.name.visibility = View.VISIBLE
            holder.name.text = name
        }
        holder.nrViews.text = nrViews
        holder.nrLoves.text = nrLoves
        VOID.dataName(DATAv.ARTISTS, artistId, holder.artist)
        VOID.dataName(DATAv.ALBUMS, albumId, holder.album)
        VOID.dataName(DATAv.CATEGORIES, categoryId, holder.category)

        holder.add.setOnClickListener {
            if (oldId != null) {
                VOID.addToEditorsChoice(activity, activity, id, number)
                VOID.addToEditorsChoice(activity, activity, oldId, 0)
            } else {
                VOID.addToEditorsChoice(activity, activity, id, number)
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

    inner class ViewHolder(view: View?) : RecyclerView.ViewHolder(
        view!!
    ) {
        var add: ImageView
        var name: TextView
        var nrViews: TextView
        var nrLoves: TextView
        var artist: TextView
        var album: TextView
        var category: TextView
        var item: LinearLayout

        init {
            name = binding!!.name
            nrViews = binding!!.nrViews
            nrLoves = binding!!.nrLoves
            add = binding!!.add
            artist = binding!!.artist
            album = binding!!.album
            category = binding!!.category
            item = binding!!.item
        }
    }

    init {
        filterList = list
        this.number = number
    }
}