package com.flatcode.littlemusic.ui.song

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.jean.jcplayer.model.JcAudio
import com.flatcode.littlemusic.utils.DATA
import com.flatcode.littlemusic.databinding.FragmentMySongsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class mySongsFragment : Fragment() {

    private var binding: FragmentMySongsBinding? = null
    private val viewModel: MySongsViewModel by viewModels()
    private var adapter: SongAdapter? = null
    private val jcAudios = ArrayList<JcAudio>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMySongsBinding.inflate(inflater, container, false)
        Timber.d("mySongsFragment Created")

        setupSwitchBar()
        setupRecyclerView()
        observeViewModel()

        viewModel.getData()

        return binding!!.root
    }

    private fun setupSwitchBar() {
        binding!!.switchBar.apply {
            all.setOnClickListener { viewModel.setType(DATA.TIMESTAMP) }
            mostViews.setOnClickListener { viewModel.setType(DATA.VIEWS_COUNT) }
            mostLoves.setOnClickListener { viewModel.setType(DATA.LOVES_COUNT) }
            name.setOnClickListener { viewModel.setType(DATA.NAME) }
            
            artists.setOnClickListener { viewModel.setDB(DATA.ARTISTS) }
            albums.setOnClickListener { viewModel.setDB(DATA.ALBUMS) }
            categories.setOnClickListener { viewModel.setDB(DATA.CATEGORIES) }
        }
    }

    private fun setupRecyclerView() {
        adapter = SongAdapter(context, ArrayList()) { _, position ->
            changeSelectedSong(position)
            binding!!.player.jcPlayer.playAudio(jcAudios[position])
            binding!!.player.jcPlayer.visibility = View.VISIBLE
        }
        binding!!.recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.songs.collect { songs ->
                        adapter?.list?.clear()
                        adapter?.list?.addAll(songs)
                        adapter?.notifyDataSetChanged()
                        
                        jcAudios.clear()
                        songs.forEach { song ->
                            jcAudios.add(JcAudio.createFromURL(song.name ?: "", song.songLink ?: ""))
                        }

                        if (songs.isNotEmpty()) {
                            binding!!.recyclerView.visibility = View.VISIBLE
                            binding!!.emptyText.visibility = View.GONE
                            binding!!.player.jcPlayer.initPlaylist(jcAudios, null)
                        } else {
                            binding!!.recyclerView.visibility = View.GONE
                            binding!!.emptyText.visibility = View.VISIBLE
                            Toast.makeText(context, "There are no songs!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                launch {
                    viewModel.isLoading.collect { isLoading ->
                        binding!!.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
                    }
                }
            }
        }
    }

    private fun changeSelectedSong(index: Int) {
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

    override fun onResume() {
        super.onResume()
        viewModel.getData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}