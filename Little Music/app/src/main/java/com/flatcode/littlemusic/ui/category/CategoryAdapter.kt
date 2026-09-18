package com.flatcode.littlemusic.ui.category

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemusic.databinding.ItemCategoryBinding
import com.flatcode.littlemusic.filter.CategoryFilter
import com.flatcode.littlemusic.model.Category
import com.flatcode.littlemusic.utils.CLASS
import com.flatcode.littlemusic.utils.DATA
import com.flatcode.littlemusic.utils.checkInterested
import com.flatcode.littlemusic.utils.glideImage
import com.flatcode.littlemusic.utils.intentExtra2
import com.flatcode.littlemusic.utils.isInterested
import java.text.MessageFormat

class CategoryAdapter(private val activity: Activity, var list: ArrayList<Category?>) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>(), Filterable {

    var filterList: ArrayList<Category?> = list
    private var filter: CategoryFilter? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(activity), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val id = DATA.EMPTY + item!!.id
        val name = DATA.EMPTY + item.name
        val image = DATA.EMPTY + item.image
        val interestedCount = DATA.EMPTY + item.interestedCount
        val albumCount = DATA.EMPTY + item.albumsCount
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

        if (albumCount == DATA.EMPTY) {
            binding.numberAlbums.text = MessageFormat.format("{0}{1}", DATA.EMPTY, DATA.ZERO)
        } else {
            binding.numberAlbums.text = albumCount
        }

        binding.add.isInterested(id, DATA.CATEGORIES)
        binding.add.setOnClickListener { binding.add.checkInterested(DATA.CATEGORIES, id) }

        binding.item.setOnClickListener {
            activity.intentExtra2(
                CLASS.CATEGORY_SONGS, DATA.CATEGORY_ID, id, DATA.CATEGORY_NAME, name
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

    inner class ViewHolder(val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root)
}