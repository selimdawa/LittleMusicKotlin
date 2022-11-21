package com.flatcode.littlemusicadmin.Activity

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.flatcode.littlemusicadmin.Adapter.EditorsChoiceSongAdapter
import com.flatcode.littlemusicadmin.Model.Song
import com.flatcode.littlemusicadmin.R
import com.flatcode.littlemusicadmin.Unit.DATAv
import com.flatcode.littlemusicadmin.Unit.THEME
import com.flatcode.littlemusicadmin.databinding.ActivityEditorsChoiceAddBinding
import com.google.firebase.database.*
import java.text.MessageFormat

class EditorsChoiceAddActivity : AppCompatActivity() {

    private var binding: ActivityEditorsChoiceAddBinding? = null
    var activity: Activity = this@EditorsChoiceAddActivity
    var item: MutableList<String?>? = null
    var list: ArrayList<Song?>? = null
    var adapter: EditorsChoiceSongAdapter? = null
    var editorsChoiceId: String? = null
    var type: String? = null
    var oldId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        THEME.setThemeOfApp(activity)
        super.onCreate(savedInstanceState)
        binding = ActivityEditorsChoiceAddBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        val intent = intent
        editorsChoiceId = intent.getStringExtra(DATAv.EDITORS_CHOICE_ID)
        oldId = intent.getStringExtra(DATAv.OLD_ID)
        val id = editorsChoiceId!!.toInt()
        binding!!.toolbar.nameSpace.setText(R.string.editors_choice)
        binding!!.toolbar.back.setOnClickListener { onBackPressed() }
        type = DATAv.TIMESTAMP
        binding!!.toolbar.search.setOnClickListener {
            binding!!.toolbar.toolbar.visibility = View.GONE
            binding!!.toolbar.toolbarSearch.visibility = View.VISIBLE
            DATAv.searchStatus = true
        }
        binding!!.toolbar.close.setOnClickListener { onBackPressed() }

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
        binding!!.recyclerView.setHasFixedSize(true)
        list = ArrayList()
        adapter = EditorsChoiceSongAdapter(activity, oldId, list!!, id)
        binding!!.recyclerView.adapter = adapter
        binding!!.all.setOnClickListener {
            type = DATAv.TIMESTAMP
            getData(type)
        }
        binding!!.name.setOnClickListener {
            type = DATAv.NAME
            getData(type)
        }
        binding!!.mostViews.setOnClickListener {
            type = DATAv.VIEWS_COUNT
            getData(type)
        }
        binding!!.mostLoves.setOnClickListener {
            type = DATAv.LOVES_COUNT
            getData(type)
        }
        binding!!.favorites.setOnClickListener {
            type = DATAv.NAME
            getFavorites(type)
        }
        binding!!.toolbar.back.setOnClickListener { onBackPressed() }
        getData(type)
    }

    private fun getData(orderBy: String?) {
        val ref = FirebaseDatabase.getInstance().getReference(DATAv.SONGS)
        ref.orderByChild(orderBy!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                list!!.clear()
                var i = 0
                for (data in dataSnapshot.children) {
                    val item = data.getValue(Song::class.java)!!
                    if (item.id != null) if (item.editorsChoice == 0) {
                        list!!.add(item)
                        i++
                    }
                }
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

    private fun getFavorites(orderBy: String?) {
        item = ArrayList()
        val reference = FirebaseDatabase.getInstance().getReference(DATAv.FAVORITES)
            .child(DATAv.FirebaseUserUid)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                (item as ArrayList<String?>).clear()
                for (snapshot in dataSnapshot.children) {
                    (item as ArrayList<String?>).add(snapshot.key)
                }
                getItems(orderBy)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun getItems(orderBy: String?) {
        val ref: Query = FirebaseDatabase.getInstance().getReference(DATAv.SONGS)
        ref.orderByChild(orderBy!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                list!!.clear()
                for (snapshot in dataSnapshot.children) {
                    val song = snapshot.getValue(Song::class.java)
                    for (id in item!!) {
                        assert(song != null)
                        if (song!!.id != null) if (song.id == id) if (song.editorsChoice == 0) list!!.add(
                            song
                        )
                    }
                }
                binding!!.progress.visibility = View.GONE
                if (list!!.isNotEmpty()) {
                    binding!!.recyclerView.visibility = View.VISIBLE
                    binding!!.emptyText.visibility = View.GONE
                } else {
                    binding!!.recyclerView.visibility = View.GONE
                    binding!!.emptyText.visibility = View.VISIBLE
                }
                adapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    override fun onBackPressed() {
        if (DATAv.searchStatus) {
            binding!!.toolbar.toolbar.visibility = View.VISIBLE
            binding!!.toolbar.toolbarSearch.visibility = View.GONE
            DATAv.searchStatus = false
            binding!!.toolbar.textSearch.setText(DATAv.EMPTY)
        } else super.onBackPressed()
    }
}