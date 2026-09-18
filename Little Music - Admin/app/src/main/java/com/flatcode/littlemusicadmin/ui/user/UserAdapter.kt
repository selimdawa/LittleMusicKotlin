package com.flatcode.littlemusicadmin.ui.user

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemusicadmin.ui.profile.ProfileActivity
import com.flatcode.littlemusicadmin.model.User
import com.flatcode.littlemusicadmin.utils.*
import com.flatcode.littlemusicadmin.databinding.ItemUserBinding

class UserAdapter(private val context: Context, var list: ArrayList<User?>) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>(), Filterable {

    var filterList: ArrayList<User?>
    private var filter: UserFilter? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val id = item!!.id
        val image = item.profileImage

        holder.binding.imageProfile.glide(true, image)

        if (item.username == DATA.EMPTY) {
            holder.binding.username.visibility = View.GONE
        } else {
            holder.binding.username.visibility = View.VISIBLE
            holder.binding.username.text = item.username
        }

        holder.binding.item.setOnClickListener {
            context.openActivity(ProfileActivity::class.java, DATA.PROFILE_ID, id)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getFilter(): Filter {
        if (filter == null) {
            filter = UserFilter(filterList, this)
        }
        return filter!!
    }

    inner class ViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root)

    init {
        filterList = list
    }
}
