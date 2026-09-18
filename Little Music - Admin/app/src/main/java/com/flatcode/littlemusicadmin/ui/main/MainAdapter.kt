package com.flatcode.littlemusicadmin.ui.main

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemusicadmin.model.Main
import com.flatcode.littlemusicadmin.R
import com.flatcode.littlemusicadmin.utils.DATA
import com.flatcode.littlemusicadmin.databinding.ItemMainBinding
import java.text.MessageFormat

class MainAdapter(private val context: Context, var list: List<Main>) :
    RecyclerView.Adapter<MainAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMainBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = list[position]
        val image = model.image
        val number = model.number
        val name = model.title
        //String id = list.getId();
        val c = model.c

        if (image != 0) {
            holder.binding.image.setImageResource(image)
        } else {
            holder.binding.image.setImageResource(R.drawable.ic_load)
        }

        if (number != 0) {
            holder.binding.number.visibility = View.VISIBLE
            holder.binding.number.text = MessageFormat.format("{0}{1}", DATA.EMPTY, number)
        } else {
            holder.binding.number.visibility = View.GONE
        }

        holder.binding.name.text = name
        holder.itemView.setOnClickListener {
            val intent = Intent(context, c)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(val binding: ItemMainBinding) : RecyclerView.ViewHolder(binding.root)
}