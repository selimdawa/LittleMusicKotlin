package com.flatcode.littlemusicadmin.Activity

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.flatcode.littlemusicadmin.Adapter.ArtistAdapter
import com.flatcode.littlemusicadmin.Model.Artist
import com.flatcode.littlemusicadmin.R
import com.flatcode.littlemusicadmin.Unit.DATA
import com.flatcode.littlemusicadmin.Unit.THEME
import com.flatcode.littlemusicadmin.databinding.ActivityArtistsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import java.text.MessageFormat

class ArtistsActivity : AppCompatActivity() {

    private var binding: ActivityArtistsBinding? = null
    var activity: Activity = this@ArtistsActivity
    var list: ArrayList<Artist?>? = null
    var adapter: ArtistAdapter? = null
    var type: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        THEME.setThemeOfApp(activity)
        super.onCreate(savedInstanceState)
        binding = ActivityArtistsBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        binding!!.toolbar.nameSpace.setText(R.string.artists)
        binding!!.toolbar.back.setOnClickListener { onBackPressed() }
        binding!!.toolbar.close.setOnClickListener { onBackPressed() }
        type = DATA.TIMESTAMP

        binding!!.toolbar.search.setOnClickListener {
            binding!!.toolbar.toolbar.visibility = View.GONE
            binding!!.toolbar.toolbarSearch.visibility = View.VISIBLE
            DATA.searchStatus = true
        }

        binding!!.toolbar.textSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                try {
                    adapter!!.filter.filter(s)
                } catch (e: Exception) {
                    //None
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        //binding.recyclerView.setHasFixedSize(true);
        list = ArrayList()
        adapter = ArtistAdapter(activity, list!!)
        binding!!.recyclerView.adapter = adapter

        binding!!.switchBar.all.setOnClickListener {
            type = DATA.TIMESTAMP
            getData(type)
        }
        binding!!.switchBar.mostSongs.setOnClickListener {
            type = DATA.SONGS_COUNT
            getData(type)
        }
        binding!!.switchBar.mostAlbums.setOnClickListener {
            type = DATA.ALBUMS_COUNT
            getData(type)
        }
        binding!!.switchBar.mostInterested.setOnClickListener {
            type = DATA.INTERESTED_COUNT
            getData(type)
        }
        binding!!.switchBar.name.setOnClickListener {
            type = DATA.NAME
            getData(type)
        }
        getData(type)
    }

    private fun getData(orderBy: String?) {
        val ref: Query = FirebaseDatabase.getInstance().getReference(DATA.ARTISTS)
        ref.orderByChild(orderBy!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                list!!.clear()
                var i = 0
                for (data in dataSnapshot.children) {
                    val item = data.getValue(Artist::class.java)!!
                    list!!.add(item)
                    i++
                }
                list!!.reverse()
                binding!!.toolbar.number.text = MessageFormat.format("( {0} )", i)
                adapter!!.notifyDataSetChanged()
                binding!!.progress.visibility = View.GONE
                if (list!!.isNotEmpty()) {
                    binding!!.recyclerView.visibility = View.VISIBLE
                    binding!!.emptyText.visibility = View.GONE
                } else {
                    binding!!.recyclerView.visibility = View.GONE
                    binding!!.emptyText.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    override fun onBackPressed() {
        if (DATA.searchStatus) {
            binding!!.toolbar.toolbar.visibility = View.VISIBLE
            binding!!.toolbar.toolbarSearch.visibility = View.GONE
            DATA.searchStatus = false
            binding!!.toolbar.textSearch.setText(DATA.EMPTY)
        } else if (DATA.isChange) {
            onResume()
            DATA.isChange = false
        } else super.onBackPressed()
    }

    override fun onRestart() {
        getData(type)
        super.onRestart()
    }

    override fun onResume() {
        getData(type)
        super.onResume()
    }
}