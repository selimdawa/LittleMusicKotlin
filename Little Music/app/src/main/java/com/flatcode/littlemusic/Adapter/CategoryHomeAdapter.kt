package com.flatcode.littlemusic.Adapterimport

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemusic.Modelimport.Category
import com.flatcode.littlemusic.Unit.VOID
import com.flatcode.littlemusic.Unitimport.CLASS
import com.flatcode.littlemusic.Unitimport.DATA
import com.flatcode.littlemusic.databinding.ItemCategoryHomeBinding

class CategoryHomeAdapter(private val context: Context?, var list: ArrayList<Category?>) :
    RecyclerView.Adapter<CategoryHomeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemCategoryHomeBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding!!.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val id = item!!.id
        val name = item.name
        val image = item.image

        VOID.GlideImage(false, context, image, binding!!.image)

        holder.image.setOnClickListener {
            VOID.IntentExtra2(
                context, CLASS.CATEGORY_SONGS, DATA.CATEGORY_ID, id, DATA.CATEGORY_NAME, name
            )
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        var image: ImageView

        init {
            image = binding!!.image
        }
    }

    companion object {
        private var binding: ItemCategoryHomeBinding? = null
    }
}