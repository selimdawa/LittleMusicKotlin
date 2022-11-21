package com.flatcode.littlemusicadmin.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemusicadmin.Filter.UserFilter
import com.flatcode.littlemusicadmin.Model.User
import com.flatcode.littlemusicadmin.Unit.CLASSv
import com.flatcode.littlemusicadmin.Unit.DATAv
import com.flatcode.littlemusicadmin.Unit.VOID
import com.flatcode.littlemusicadmin.databinding.ItemUserBinding

class UserAdapter(private val context: Context, var list: ArrayList<User?>) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>(), Filterable {

    private var binding: ItemUserBinding? = null
    var filterList: ArrayList<User?>
    private var filter: UserFilter? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemUserBinding.inflate(
            LayoutInflater.from(
                context
            ), parent, false
        )
        return ViewHolder(binding!!.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val id = item!!.id
        val image = item.profileImage

        VOID.Glide(true, context, image, holder.image)
        if (item.username == DATAv.EMPTY) {
            holder.username.visibility = View.GONE
        } else {
            holder.username.visibility = View.VISIBLE
            holder.username.text = item.username
        }

        holder.item.setOnClickListener {
            VOID.IntentExtra(
                context, CLASSv.PROFILE, DATAv.PROFILE_ID, id
            )
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

    inner class ViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        var image: ImageView
        var username: TextView
        var item: LinearLayout

        init {
            image = binding!!.imageProfile
            username = binding!!.username
            item = binding!!.item
        }
    }

    init {
        filterList = list
    }
}