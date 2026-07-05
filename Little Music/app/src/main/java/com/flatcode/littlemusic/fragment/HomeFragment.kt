package com.flatcode.littlemusic.fragment

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
import com.flatcode.littlemusic.adapter.SongMainAdapter
import com.flatcode.littlemusic.adapter.CategoryHomeAdapter
import com.flatcode.littlemusic.adapter.ImageSliderAdapter
import com.flatcode.littlemusic.model.Category
import com.flatcode.littlemusic.model.Song
import com.flatcode.littlemusic.utils.VOID
import com.flatcode.littlemusic.utils.CLASS
import com.flatcode.littlemusic.utils.DATA
import com.flatcode.littlemusic.databinding.FragmentHomeBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.selimdawa.autoimageslider.SliderAnimations
import com.selimdawa.autoimageslider.View.animation.type.IndicatorAnimationType

class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding? = null
    private var list = ArrayList<Song?>()
    private var list2 = ArrayList<Song?>()
    private var list3 = ArrayList<Song?>()
    private var list4 = ArrayList<Song?>()
    private var adapter: SongMainAdapter? = null
    private var adapter2: SongMainAdapter? = null
    private var adapter3: SongMainAdapter? = null
    private var adapter4: SongMainAdapter? = null
    private val one = false
    private val two = true
    private val three = true
    private val four = true
    var jcAudios = ArrayList<JcAudio>()
    private var currentSong = 0
    var totalCounts = 0
    private var categoryList = ArrayList<Category?>()
    private var categoryAdapter: CategoryHomeAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        loadCategories()

        binding?.let { b ->
            b.showMore.setOnClickListener {
                VOID.IntentExtra3(
                    context,
                    CLASS.SHOW_MORE,
                    DATA.SHOW_MORE_TYPE,
                    DATA.EDITORS_CHOICE,
                    DATA.SHOW_MORE_NAME,
                    b.name.text.toString(),
                    DATA.SHOW_MORE_BOOLEAN,
                    DATA.EMPTY + one
                )
            }
            b.showMore2.setOnClickListener {
                VOID.IntentExtra3(
                    context,
                    CLASS.SHOW_MORE,
                    DATA.SHOW_MORE_TYPE,
                    DATA.VIEWS_COUNT,
                    DATA.SHOW_MORE_NAME,
                    b.mostViews.text.toString(),
                    DATA.SHOW_MORE_BOOLEAN,
                    DATA.EMPTY + two
                )
            }
            b.showMore3.setOnClickListener {
                VOID.IntentExtra3(
                    context,
                    CLASS.SHOW_MORE,
                    DATA.SHOW_MORE_TYPE,
                    DATA.LOVES_COUNT,
                    DATA.SHOW_MORE_NAME,
                    b.name3.text.toString(),
                    DATA.SHOW_MORE_BOOLEAN,
                    DATA.EMPTY + three
                )
            }
            b.showMore4.setOnClickListener {
                VOID.IntentExtra3(
                    context,
                    CLASS.SHOW_MORE,
                    DATA.SHOW_MORE_TYPE,
                    DATA.TIMESTAMP,
                    DATA.SHOW_MORE_NAME,
                    b.name4.text.toString(),
                    DATA.SHOW_MORE_BOOLEAN,
                    DATA.EMPTY + four
                )
            }

            categoryList = ArrayList()
            categoryAdapter = CategoryHomeAdapter(context, categoryList)
            b.recyclerCategory.adapter = categoryAdapter

            list = ArrayList()
            adapter = SongMainAdapter(context, list, { _, position ->
                changeSelectedSong(position, adapter)
                b.player.jcPlayer.playAudio(jcAudios[position])
                changeSelectedSong(-1, adapter2)
                changeSelectedSong(-1, adapter3)
                changeSelectedSong(-1, adapter4)
            }) { _, _ -> b.player.jcPlayer.pause() }
            b.recyclerView.adapter = adapter

            list2 = ArrayList()
            adapter2 = SongMainAdapter(context, list2, { _, position ->
                changeSelectedSong(position, adapter2)
                changeSelectedSong(-1, adapter)
                changeSelectedSong(-1, adapter3)
                changeSelectedSong(-1, adapter4)
                b.player.jcPlayer.playAudio(jcAudios[position])
            }) { _, _ -> b.player.jcPlayer.pause() }
            b.recyclerView2.adapter = adapter2

            list3 = ArrayList()
            adapter3 = SongMainAdapter(context, list3, { _, position ->
                changeSelectedSong(position, adapter3)
                changeSelectedSong(-1, adapter)
                changeSelectedSong(-1, adapter2)
                changeSelectedSong(-1, adapter4)
                b.player.jcPlayer.playAudio(jcAudios[position])
            }) { _, _ -> b.player.jcPlayer.pause() }
            b.recyclerView3.adapter = adapter3

            list4 = ArrayList()
            adapter4 = SongMainAdapter(context, list4, { _, position ->
                changeSelectedSong(position, adapter4)
                changeSelectedSong(-1, adapter)
                changeSelectedSong(-1, adapter2)
                changeSelectedSong(-1, adapter3)
                b.player.jcPlayer.playAudio(jcAudios[position])
            }) { _, _ -> b.player.jcPlayer.pause() }
            b.recyclerView4.adapter = adapter4

            b.imageSlider.apply {
                setIndicatorAnimation(IndicatorAnimationType.WORM)
                setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
                isAutoCycle = true
                startAutoCycle()

                FirebaseDatabase.getInstance().getReference(DATA.SLIDER_SHOW)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            totalCounts = snapshot.childrenCount.toInt()
                            setSliderAdapter(ImageSliderAdapter(context, totalCounts))
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
            }
        }

        return binding!!.root
    }

    private fun start() {
        binding?.let { b ->
            loadPostBy(DATA.EDITORS_CHOICE, list, adapter, b.bar, b.recyclerView, b.empty)
            loadPostBy(DATA.VIEWS_COUNT, list2, adapter2, b.bar2, b.recyclerView2, b.empty2)
            loadPostBy(DATA.LOVES_COUNT, list3, adapter3, b.bar3, b.recyclerView3, b.empty3)
            loadPostBy(DATA.TIMESTAMP, list4, adapter4, b.bar4, b.recyclerView4, b.empty4)
        }
    }

    private fun loadCategories() {
        FirebaseDatabase.getInstance().getReference(DATA.CATEGORIES)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    categoryList.clear()
                    for (data in snapshot.children) {
                        data.getValue(Category::class.java)?.let { categoryList.add(it) }
                    }
                    categoryAdapter?.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun loadPostBy(
        orderBy: String, list: ArrayList<Song?>, adapter: SongMainAdapter?,
        bar: ProgressBar, recyclerView: RecyclerView, empty: TextView,
    ) {
        changeSelectedSong(-1, adapter)
        val ref: Query = FirebaseDatabase.getInstance().getReference(DATA.SONGS)
        val query = if (orderBy == DATA.EDITORS_CHOICE) {
            ref.orderByChild(orderBy)
        } else {
            ref.orderByChild(orderBy).limitToLast(DATA.ORDER_MAIN)
        }

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                jcAudios.clear()

                for (data in snapshot.children) {
                    val item = data.getValue(Song::class.java) ?: continue
                    item.key = snapshot.key

                    if (orderBy == DATA.EDITORS_CHOICE) {
                        if (item.editorsChoice in 1..5) {
                            list.add(item)
                            jcAudios.add(
                                JcAudio.createFromURL(
                                    item.name ?: "", item.songLink ?: ""
                                )
                            )
                        }
                    } else {
                        list.add(item)
                        jcAudios.add(JcAudio.createFromURL(item.name ?: "", item.songLink ?: ""))
                    }
                }

                if (orderBy != DATA.EDITORS_CHOICE) {
                    list.reverse()
                }

                adapter?.notifyDataSetChanged()
                bar.visibility = View.GONE

                if (list.isNotEmpty()) {
                    recyclerView.visibility = View.VISIBLE
                    empty.visibility = View.GONE
                    binding?.player?.jcPlayer?.initPlaylist(jcAudios, null)
                } else {
                    recyclerView.visibility = View.GONE
                    empty.visibility = View.VISIBLE
                    Toast.makeText(context, "There are no songs!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun changeSelectedSong(index: Int, adapter: SongMainAdapter?) {
        adapter?.let {
            val previousSelected = it.selectedPosition
            it.selectedPosition = index
            currentSong = index
            it.notifyItemChanged(previousSelected)
            it.notifyItemChanged(index)
        }
    }

    override fun onPause() {
        binding?.player?.jcPlayer?.pause()
        super.onPause()
    }

    override fun onStop() {
        binding?.player?.jcPlayer?.pause()
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
        start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}