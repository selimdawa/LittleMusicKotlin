package com.flatcode.littlemusic.ui.settings

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemusic.databinding.ItemSettingBinding
import com.flatcode.littlemusic.model.Setting
import com.flatcode.littlemusic.utils.DATA
import com.flatcode.littlemusic.utils.dialogAboutApp
import com.flatcode.littlemusic.utils.dialogLogout
import com.flatcode.littlemusic.utils.findActivity
import com.flatcode.littlemusic.utils.rateApp
import com.flatcode.littlemusic.utils.shareApp

class SettingAdapter(
    private val list: ArrayList<Setting>
) : RecyclerView.Adapter<SettingAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSettingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    class ViewHolder(private val binding: ItemSettingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Setting) {
            val id = item.id ?: ""
            val name = item.name ?: ""
            val image = item.image
            val number = item.number
            val to = item.c

            binding.name.text = name
            binding.image.setImageResource(image)

            if (number != 0) {
                binding.number.visibility = View.VISIBLE
                binding.number.text = number.toString()
            } else {
                binding.number.visibility = View.GONE
            }

            binding.item.setOnClickListener {
                val context = itemView.context
                val activity = context.findActivity()

                when (id) {
                    DATA.ABOUT_APP -> activity?.dialogAboutApp()
                    DATA.LOGOUT -> activity?.dialogLogout()
                    DATA.SHARE_APP -> context.shareApp()
                    DATA.RATE_APP -> context.rateApp()
                    else -> if (to != null) context.startActivity(Intent(context, to))
                }
            }
        }
    }
}