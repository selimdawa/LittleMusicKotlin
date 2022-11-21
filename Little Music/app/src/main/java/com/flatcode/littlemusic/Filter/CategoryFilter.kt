package com.flatcode.littlemusic.Filter

import android.widget.Filter
import com.flatcode.littlemusic.Adapter.CategoryAdapter
import com.flatcode.littlemusic.Modelimport.Category
import java.util.*

class CategoryFilter(var list: ArrayList<Category?>, var adapter: CategoryAdapter) : Filter() {
    override fun performFiltering(constraint: CharSequence): FilterResults {

        var constraint: CharSequence? = constraint
        val results = FilterResults()
        if (constraint != null && constraint.length > 0) {
            constraint = constraint.toString().uppercase(Locale.getDefault())
            val filter = ArrayList<Category?>()
            for (i in list.indices) {
                if (list[i]!!.name!!.uppercase(Locale.getDefault()).contains(constraint)) {
                    filter.add(list[i])
                }
            }
            results.count = filter.size
            results.values = filter
        } else {
            results.count = list.size
            results.values = list
        }
        return results
    }

    override fun publishResults(constraint: CharSequence, results: FilterResults) {
        adapter.list = (results.values as ArrayList<Category?>)
        adapter.notifyDataSetChanged()
    }
}