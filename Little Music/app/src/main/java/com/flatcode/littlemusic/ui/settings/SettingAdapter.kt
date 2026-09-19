package com.flatcode.littlemusic.ui.settings

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemusic.databinding.ItemSettingBinding
import com.flatcode.littlemusic.model.Setting
import com.flatcode.littlemusic.utils.DATA
import com.flatcode.littlemusic.utils.dialogAboutApp
import com.flatcode.littlemusic.utils.dialogLogout
import com.flatcode.littlemusic.utils.openActivity
import com.flatcode.littlemusic.utils.rateApp
import com.flatcode.littlemusic.utils.shareApp
import java.text.MessageFormat

class SettingAdapter(private val context: Context?, var list: ArrayList<Setting>) :
    RecyclerView.Adapter<SettingAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSettingBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val id = DATA.EMPTY + item.id
        val name = DATA.EMPTY + item.name
        val image = item.image
        val number = item.number
        val to = item.c

        val binding = holder.binding
        binding.name.text = name
        binding.image.setImageResource(image)

        if (number != 0) {
            binding.number.visibility = View.VISIBLE
            binding.number.text = MessageFormat.format("{0}{1}", DATA.EMPTY, number)
        } else {
            binding.number.visibility = View.GONE
        }

        binding.item.setOnClickListener {
            when (id) {
                "6" -> context?.dialogAboutApp()
                "7" -> context?.dialogLogout()
                "8" -> context?.shareApp()
                "9" -> context?.rateApp()
                else -> context?.openActivity<Activity>(c = to)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(val binding: ItemSettingBinding) : RecyclerView.ViewHolder(binding.root)
}