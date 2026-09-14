package com.flatcode.littlemusic.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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
import com.flatcode.littlemusic.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.selimdawa.autoimageslider.SliderAnimations
import io.selimdawa.autoimageslider.view.model.IndicatorAnimationType
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding? = null
    private val viewModel: HomeViewModel by viewModels()

    private var adapter: SongMainAdapter? = null
    private var adapter2: SongMainAdapter? = null
    private var adapter3: SongMainAdapter? = null
    private var adapter4: SongMainAdapter? = null

    private val one = false
    private val two = true
    private val three = true
    private val four = true

    private val jcAudios = ArrayList<JcAudio>()
    private var categoryAdapter: CategoryHomeAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        Timber.d("HomeFragment Created")

        setupAdapters()
        setupClickListeners()
        setupImageSlider()
        observeViewModel()

        viewModel.loadCategories()
        viewModel.loadSliderCount()
        viewModel.loadSongs()

        return binding!!.root
    }

    private fun setupAdapters() {
        binding?.let { b ->
            categoryAdapter = CategoryHomeAdapter(context, arrayListOf())
            b.recyclerCategory.adapter = categoryAdapter

            adapter = SongMainAdapter(context, arrayListOf(), { _, position ->
                changeSelectedSong(position, adapter)
                b.player.jcPlayer.playAudio(jcAudios[position])
                changeSelectedSong(-1, adapter2)
                changeSelectedSong(-1, adapter3)
                changeSelectedSong(-1, adapter4)
            }) { _, _ -> b.player.jcPlayer.pause() }
            b.recyclerView.adapter = adapter

            adapter2 = SongMainAdapter(context, arrayListOf(), { _, position ->
                changeSelectedSong(position, adapter2)
                changeSelectedSong(-1, adapter)
                changeSelectedSong(-1, adapter3)
                changeSelectedSong(-1, adapter4)
                b.player.jcPlayer.playAudio(jcAudios[position])
            }) { _, _ -> b.player.jcPlayer.pause() }
            b.recyclerView2.adapter = adapter2

            adapter3 = SongMainAdapter(context, arrayListOf(), { _, position ->
                changeSelectedSong(position, adapter3)
                changeSelectedSong(-1, adapter)
                changeSelectedSong(-1, adapter2)
                changeSelectedSong(-1, adapter4)
                b.player.jcPlayer.playAudio(jcAudios[position])
            }) { _, _ -> b.player.jcPlayer.pause() }
            b.recyclerView3.adapter = adapter3

            adapter4 = SongMainAdapter(context, arrayListOf(), { _, position ->
                changeSelectedSong(position, adapter4)
                changeSelectedSong(-1, adapter)
                changeSelectedSong(-1, adapter2)
                changeSelectedSong(-1, adapter3)
                b.player.jcPlayer.playAudio(jcAudios[position])
            }) { _, _ -> b.player.jcPlayer.pause() }
            b.recyclerView4.adapter = adapter4
        }
    }

    private fun setupClickListeners() {
        binding?.let { b ->
            b.showMore.setOnClickListener {
                VOID.IntentExtra3(
                    context, CLASS.SHOW_MORE, DATA.SHOW_MORE_TYPE, DATA.EDITORS_CHOICE,
                    DATA.SHOW_MORE_NAME, b.name.text.toString(), DATA.SHOW_MORE_BOOLEAN, DATA.EMPTY + one
                )
            }
            b.showMore2.setOnClickListener {
                VOID.IntentExtra3(
                    context, CLASS.SHOW_MORE, DATA.SHOW_MORE_TYPE, DATA.VIEWS_COUNT,
                    DATA.SHOW_MORE_NAME, b.mostViews.text.toString(), DATA.SHOW_MORE_BOOLEAN, DATA.EMPTY + two
                )
            }
            b.showMore3.setOnClickListener {
                VOID.IntentExtra3(
                    context, CLASS.SHOW_MORE, DATA.SHOW_MORE_TYPE, DATA.LOVES_COUNT,
                    DATA.SHOW_MORE_NAME, b.name3.text.toString(), DATA.SHOW_MORE_BOOLEAN, DATA.EMPTY + three
                )
            }
            b.showMore4.setOnClickListener {
                VOID.IntentExtra3(
                    context, CLASS.SHOW_MORE, DATA.SHOW_MORE_TYPE, DATA.TIMESTAMP,
                    DATA.SHOW_MORE_NAME, b.name4.text.toString(), DATA.SHOW_MORE_BOOLEAN, DATA.EMPTY + four
                )
            }
        }
    }

    private fun setupImageSlider() {
        binding?.imageSlider?.apply {
            setIndicatorAnimation(IndicatorAnimationType.WORM)
            setSliderTransformAnimation(SliderAnimations.SIMPLE)
            isAutoCycle = true
            startAutoCycle()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.categories.collect { categories ->
                        categoryAdapter?.list?.clear()
                        categoryAdapter?.list?.addAll(categories)
                        categoryAdapter?.notifyDataSetChanged()
                    }
                }
                launch {
                    viewModel.sliderCount.collect { count ->
                        if (count > 0) {
                            binding?.imageSlider?.setSliderAdapter(ImageSliderAdapter(context, count))
                        }
                    }
                }
                launch {
                    viewModel.editorsChoiceSongs.collect { songs ->
                        updateSongList(songs, adapter, binding?.bar, binding?.recyclerView, binding?.empty)
                    }
                }
                launch {
                    viewModel.mostViewedSongs.collect { songs ->
                        updateSongList(songs, adapter2, binding?.bar2, binding?.recyclerView2, binding?.empty2)
                    }
                }
                launch {
                    viewModel.mostLovedSongs.collect { songs ->
                        updateSongList(songs, adapter3, binding?.bar3, binding?.recyclerView3, binding?.empty3)
                    }
                }
                launch {
                    viewModel.latestSongs.collect { songs ->
                        updateSongList(songs, adapter4, binding?.bar4, binding?.recyclerView4, binding?.empty4)
                    }
                }
            }
        }
    }

    private fun updateSongList(
        songs: List<Song>, adapter: SongMainAdapter?, bar: ProgressBar?,
        recyclerView: RecyclerView?, empty: TextView?,
    ) {
        adapter?.list?.clear()
        adapter?.list?.addAll(songs)
        adapter?.notifyDataSetChanged()
        bar?.visibility = View.GONE

        if (songs.isNotEmpty()) {
            recyclerView?.visibility = View.VISIBLE
            empty?.visibility = View.GONE

            jcAudios.clear()
            songs.forEach { song ->
                jcAudios.add(JcAudio.createFromURL(song.name ?: "", song.songLink ?: ""))
            }
            binding?.player?.jcPlayer?.initPlaylist(jcAudios, null)
        } else {
            recyclerView?.visibility = View.GONE
            empty?.visibility = View.VISIBLE
        }
    }

    private fun changeSelectedSong(index: Int, adapter: SongMainAdapter?) {
        adapter?.let {
            val previousSelected = it.selectedPosition
            it.selectedPosition = index
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

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}