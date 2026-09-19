package com.flatcode.littlemusic.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemusic.databinding.ItemCategoryMainBinding
import com.flatcode.littlemusic.model.Category
import com.flatcode.littlemusic.ui.category.CategorySongsActivity
import com.flatcode.littlemusic.utils.DATA
import com.flatcode.littlemusic.utils.glideBlur
import com.flatcode.littlemusic.utils.glideImage
import com.flatcode.littlemusic.utils.intentExtra2

class CategoryMainAdapter(private val context: Context?, var list: ArrayList<Category?>) :
    RecyclerView.Adapter<CategoryMainAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryMainBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val id = DATA.EMPTY + item!!.id
        val name = DATA.EMPTY + item.name
        val image = DATA.EMPTY + item.image

        val binding = holder.binding
        binding.image.glideImage(image, false)
        binding.imageBlur.glideBlur(image, 50, false)

        if (name == DATA.EMPTY) {
            binding.name.visibility = View.GONE
        } else {
            binding.name.visibility = View.VISIBLE
            binding.name.text = name
        }

        binding.card.setOnClickListener {
            context?.intentExtra2(
                CategorySongsActivity::class.java, DATA.CATEGORY_ID, id, DATA.CATEGORY_NAME, name
            )
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(val binding: ItemCategoryMainBinding) : RecyclerView.ViewHolder(binding.root)
}