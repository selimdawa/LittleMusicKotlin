package com.flatcode.littlemusic.Fragmentimport

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.jean.jcplayer.model.JcAudio
import com.flatcode.littlemusic.Adapter.SongMainAdapter
import com.flatcode.littlemusic.Adapterimport.CategoryHomeAdapter
import com.flatcode.littlemusic.Adapterimport.ImageSliderAdapter
import com.flatcode.littlemusic.Modelimport.Category
import com.flatcode.littlemusic.Modelimport.Song
import com.flatcode.littlemusic.Unit.VOID
import com.flatcode.littlemusic.Unitimport.CLASS
import com.flatcode.littlemusic.Unitimport.DATA
import com.flatcode.littlemusic.databinding.FragmentHomeBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding? = null
    private var list: ArrayList<Song?>? = null
    private var list2: ArrayList<Song?>? = null
    private var list3: ArrayList<Song?>? = null
    private var list4: ArrayList<Song?>? = null
    private var adapter: SongMainAdapter? = null
    private var adapter2: SongMainAdapter? = null
    private var adapter3: SongMainAdapter? = null
    private var adapter4: SongMainAdapter? = null
    private val B_one = false
    private val B_two = true
    private val B_three = true
    private val B_four = true
    var isPlaying = false
    var jcAudios = ArrayList<JcAudio>()
    private var currentSong = 0
    var TotalCounts = 0
    private var categoryList: ArrayList<Category?>? = null
    private var categoryAdapter: CategoryHomeAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(LayoutInflater.from(context), container, false)

        loadCategories()
        binding!!.showMore.setOnClickListener {
            VOID.IntentExtra3(
                context, CLASS.SHOW_MORE,
                DATA.SHOW_MORE_TYPE, DATA.EDITORS_CHOICE, DATA.SHOW_MORE_NAME,
                binding!!.name.text.toString(), DATA.SHOW_MORE_BOOLEAN, DATA.EMPTY + B_one
            )
        }
        binding!!.showMore2.setOnClickListener {
            VOID.IntentExtra3(
                context, CLASS.SHOW_MORE,
                DATA.SHOW_MORE_TYPE, DATA.VIEWS_COUNT, DATA.SHOW_MORE_NAME,
                binding!!.mostViews.text.toString(), DATA.SHOW_MORE_BOOLEAN, DATA.EMPTY + B_two
            )
        }
        binding!!.showMore3.setOnClickListener {
            VOID.IntentExtra3(
                context, CLASS.SHOW_MORE,
                DATA.SHOW_MORE_TYPE, DATA.LOVES_COUNT, DATA.SHOW_MORE_NAME,
                binding!!.name3.text.toString(), DATA.SHOW_MORE_BOOLEAN, DATA.EMPTY + B_three
            )
        }
        binding!!.showMore4.setOnClickListener {
            VOID.IntentExtra3(
                context, CLASS.SHOW_MORE,
                DATA.SHOW_MORE_TYPE, DATA.TIMESTAMP, DATA.SHOW_MORE_NAME,
                binding!!.name4.text.toString(), DATA.SHOW_MORE_BOOLEAN, DATA.EMPTY + B_four
            )
        }

        //RecyclerView Category
        //binding.recyclerCategory.setHasFixedSize(true);
        categoryList = ArrayList()
        categoryAdapter = CategoryHomeAdapter(context, categoryList!!)
        binding!!.recyclerCategory.adapter = categoryAdapter

        //RecyclerView Editor's Choice
        //binding.recyclerView.setHasFixedSize(true);
        list = ArrayList()
        binding!!.recyclerView.adapter = adapter
        adapter = SongMainAdapter(context, list!!, { songs: Song?, position: Int ->
            changeSelectedSong(position, adapter)
            binding!!.player.jcPlayer.playAudio(jcAudios[position])
            changeSelectedSong(-1, adapter2)
            changeSelectedSong(-1, adapter3)
            changeSelectedSong(-1, adapter4)
        }) { songs: Song?, position: Int -> binding!!.player.jcPlayer.pause() }

        //RecyclerView Views Count
        //binding.recyclerView2.setHasFixedSize(true);
        list2 = ArrayList()
        binding!!.recyclerView2.adapter = adapter2
        adapter2 = SongMainAdapter(context, list2!!, { songs: Song?, position: Int ->
            changeSelectedSong(position, adapter2)
            changeSelectedSong(-1, adapter)
            changeSelectedSong(-1, adapter3)
            changeSelectedSong(-1, adapter4)
            binding!!.player.jcPlayer.playAudio(jcAudios[position])
        }) { songs: Song?, position: Int -> binding!!.player.jcPlayer.pause() }

        //RecyclerView Loves Count
        //binding.recyclerView3.setHasFixedSize(true);
        list3 = ArrayList()
        binding!!.recyclerView3.adapter = adapter3
        adapter3 = SongMainAdapter(context, list3!!, { songs: Song?, position: Int ->
            changeSelectedSong(position, adapter3)
            changeSelectedSong(-1, adapter)
            changeSelectedSong(-1, adapter2)
            changeSelectedSong(-1, adapter4)
            binding!!.player.jcPlayer.playAudio(jcAudios[position])
        }) { songs: Song?, position: Int -> binding!!.player.jcPlayer.pause() }

        //RecyclerView New Songs
        //binding.recyclerView4.setHasFixedSize(true);
        list4 = ArrayList()
        binding!!.recyclerView4.adapter = adapter4
        adapter4 = SongMainAdapter(context, list4!!, { songs: Song?, position: Int ->
            changeSelectedSong(position, adapter4)
            changeSelectedSong(-1, adapter)
            changeSelectedSong(-1, adapter2)
            changeSelectedSong(-1, adapter3)
            binding!!.player.jcPlayer.playAudio(jcAudios[position])
        }) { songs: Song?, position: Int -> binding!!.player.jcPlayer.pause() }
        FirebaseDatabase.getInstance().getReference(DATA.SLIDER_SHOW)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val counts = snapshot.childrenCount
                    TotalCounts = counts.toInt()
                    binding!!.imageSlider.sliderAdapter = ImageSliderAdapter(context, TotalCounts)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        return binding!!.root
    }

    private fun start() {
        loadPostEditorsChoice(
            DATA.EDITORS_CHOICE, list, adapter, binding!!.bar,
            binding!!.recyclerView, binding!!.empty
        )
        loadPostBy(
            DATA.VIEWS_COUNT, list2, adapter2, binding!!.bar2,
            binding!!.recyclerView2, binding!!.empty2
        )
        loadPostBy(
            DATA.LOVES_COUNT, list3, adapter3, binding!!.bar3,
            binding!!.recyclerView3, binding!!.empty3
        )
        loadPostBy(
            DATA.TIMESTAMP, list4, adapter4, binding!!.bar4,
            binding!!.recyclerView4, binding!!.empty4
        )
    }

    private fun loadCategories() {
        val ref = FirebaseDatabase.getInstance().getReference(DATA.CATEGORIES)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categoryList!!.clear()
                for (data in snapshot.children) {
                    val category = data.getValue(Category::class.java)
                    categoryList!!.add(category)
                }
                categoryAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun loadPostBy(
        orderBy: String, list: ArrayList<Song?>?, adapter: SongMainAdapter?,
        bar: ProgressBar, recyclerView: RecyclerView, empty: TextView,
    ) {
        changeSelectedSong(-1, adapter)
        val ref: Query = FirebaseDatabase.getInstance().getReference(DATA.SONGS)
        ref.orderByChild(orderBy).limitToLast(DATA.ORDER_MAIN)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    list!!.clear()
                    for (data in snapshot.children) {
                        val item = data.getValue(Song::class.java)!!
                        if (orderBy != DATA.EDITORS_CHOICE) {
                            list.add(item)
                            item.key = (snapshot.key)
                            currentSong = -1
                            isPlaying = true
                            jcAudios.add(JcAudio.createFromURL(item.name!!, item.songLink!!))
                            recyclerView.adapter = adapter
                        }
                    }
                    adapter!!.notifyDataSetChanged()
                    bar.visibility = View.GONE
                    if (list.isNotEmpty()) {
                        recyclerView.visibility = View.VISIBLE
                        empty.visibility = View.GONE
                        if (orderBy != DATA.EDITORS_CHOICE) list.reverse()
                    } else {
                        recyclerView.visibility = View.GONE
                        empty.visibility = View.VISIBLE
                    }
                    if (isPlaying) {
                        binding!!.player.jcPlayer.initPlaylist(jcAudios, null)
                    } else {
                        Toast.makeText(context, "There is no songs!", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun loadPostEditorsChoice(
        orderBy: String, list: ArrayList<Song?>?, adapter: SongMainAdapter?,
        bar: ProgressBar, recyclerView: RecyclerView, empty: TextView,
    ) {
        changeSelectedSong(-1, adapter)
        val ref: Query = FirebaseDatabase.getInstance().getReference(DATA.SONGS)
        ref.orderByChild(orderBy).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list!!.clear()
                for (data in snapshot.children) {
                    val item = data.getValue(Song::class.java)!!
                    if (orderBy == DATA.EDITORS_CHOICE) {
                        if (item.editorsChoice <= 5 && item.editorsChoice > 0) {
                            list.add(item)
                            item.key = (snapshot.key)
                            currentSong = -1
                            isPlaying = true
                            jcAudios.add(JcAudio.createFromURL(item.name!!, item.songLink!!))
                            recyclerView.adapter = adapter
                        }
                    }
                }
                adapter!!.notifyDataSetChanged()
                bar.visibility = View.GONE
                bar.visibility = View.GONE
                if (list.isNotEmpty()) {
                    recyclerView.visibility = View.VISIBLE
                    empty.visibility = View.GONE
                    if (orderBy != DATA.EDITORS_CHOICE) list.reverse()
                } else {
                    recyclerView.visibility = View.GONE
                    empty.visibility = View.VISIBLE
                }
                if (isPlaying) {
                    binding!!.player.jcPlayer.initPlaylist(jcAudios, null)
                } else {
                    Toast.makeText(context, "There is no songs!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun changeSelectedSong(index: Int, adapter: SongMainAdapter?) {
        adapter!!.notifyItemChanged(adapter.selectedPosition)
        currentSong = index
        adapter.selectedPosition = currentSong
        adapter.notifyItemChanged(currentSong)
    }

    override fun onPause() {
        binding!!.player.jcPlayer.pause()
        super.onPause()
    }

    override fun onStop() {
        binding!!.player.jcPlayer.pause()
        super.onStop()
    }

    override fun onResume() {
        start()
        super.onResume()
    }
}