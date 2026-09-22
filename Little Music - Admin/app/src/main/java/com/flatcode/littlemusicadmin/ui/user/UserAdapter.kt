package com.flatcode.littlemusicadmin.ui.user

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.core.view.isVisible
import com.flatcode.littlemusicadmin.databinding.ItemUserBinding
import com.flatcode.littlemusicadmin.model.User
import com.flatcode.littlemusicadmin.utils.loadImage

class UserAdapter(private val onItemClick: (User) -> Unit) :
    ListAdapter<User, UserAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position) ?: return
        holder.bind(item, onItemClick)
    }

    class ViewHolder(private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: User, onItemClick: (User) -> Unit) {
            binding.imageProfile.loadImage(true, item.profileImage)
            binding.username.isVisible = !item.username.isNullOrEmpty()
            binding.username.text = item.username

            binding.item.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean =
                oldItem.username == newItem.username &&
                        oldItem.profileImage == newItem.profileImage &&
                        oldItem.email == newItem.email &&
                        oldItem.timestamp == newItem.timestamp &&
                        oldItem.version == newItem.version
        }
    }
}
