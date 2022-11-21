package com.flatcode.littlemusic.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemusic.Modelimport.Setting
import com.flatcode.littlemusic.Unit.VOID
import com.flatcode.littlemusic.Unitimport.DATAv
import com.flatcode.littlemusic.databinding.ItemSettingBinding
import java.text.MessageFormat

class SettingAdapter(private val context: Context?, private val list: ArrayList<Setting>) :
    RecyclerView.Adapter<SettingAdapter.ViewHolder>() {

    private var binding: ItemSettingBinding? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemSettingBinding.inflate(
            LayoutInflater.from(
                context
            ), parent, false
        )
        return ViewHolder(binding!!.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val id = DATAv.EMPTY + item.id
        val name = DATAv.EMPTY + item.name
        val image = item.image
        val number = item.number
        val to = item.c

        holder.name.text = name
        holder.image.setImageResource(image)
        if (number != 0) {
            holder.number.visibility = View.VISIBLE
            holder.number.text = MessageFormat.format("{0}{1}", DATAv.EMPTY, number)
        } else {
            holder.number.visibility = View.GONE
        }

        holder.item.setOnClickListener {
            when (id) {
                "6" -> VOID.dialogAboutApp(
                    context
                )
                "7" -> VOID.dialogLogout(context)
                "8" -> VOID.shareApp(context)
                "9" -> VOID.rateApp(context)
                else -> VOID.Intent1(context, to)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        var image: ImageView
        var name: TextView
        var number: TextView
        var item: LinearLayout

        init {
            image = binding!!.image
            number = binding!!.number
            name = binding!!.name
            item = binding!!.item
        }
    }
}