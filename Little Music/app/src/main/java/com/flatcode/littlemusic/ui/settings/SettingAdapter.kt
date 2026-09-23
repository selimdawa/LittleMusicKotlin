package com.flatcode.littlemusic.ui.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemusic.databinding.ItemSettingBinding
import com.flatcode.littlemusic.model.Setting

class SettingAdapter(
    private val onItemClick: (Setting) -> Unit
) : ListAdapter<Setting, SettingAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSettingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemSettingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Setting) {
            val name = item.name ?: ""
            val image = item.image
            val number = item.number

            binding.name.text = name
            binding.image.setImageResource(image)

            if (number != 0) {
                binding.number.visibility = View.VISIBLE
                binding.number.text = number.toString()
            } else {
                binding.number.visibility = View.GONE
            }

            binding.item.setOnClickListener { onItemClick(item) }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Setting>() {
            override fun areItemsTheSame(oldItem: Setting, newItem: Setting): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Setting, newItem: Setting): Boolean {
                return oldItem == newItem
            }
        }
    }
}