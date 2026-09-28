package com.flatcode.littlemusic.ui.song

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.jean.jcplayer.JcPlayerManagerListener
import com.example.jean.jcplayer.general.JcStatus
import com.example.jean.jcplayer.model.JcAudio
import com.flatcode.littlemusic.databinding.FragmentMySongsBinding
import com.flatcode.littlemusic.ui.album.AlbumSongsActivity
import com.flatcode.littlemusic.ui.artist.ArtistSongsActivity
import com.flatcode.littlemusic.ui.category.CategorySongsActivity
import com.flatcode.littlemusic.utils.DATA
import com.flatcode.littlemusic.utils.checkFavorite
import com.flatcode.littlemusic.utils.checkLove
import com.flatcode.littlemusic.utils.openActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MySongsFragment : Fragment() {

    private var _binding: FragmentMySongsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MySongsViewModel by viewModels()
    private var adapter: SongAdapter? = null
    private val jcAudios = ArrayList<JcAudio>()

    private val jcPlayerListener = object : JcPlayerManagerListener {
        override fun onPreparedAudio(status: JcStatus) {
            val index = jcAudios.indexOf(status.jcAudio)
            if (index != -1) changeSelectedSong(index)
        }
        override fun onCompletedAudio() {}
        override fun onPaused(status: JcStatus) {}
        override fun onContinueAudio(status: JcStatus) {
            val index = jcAudios.indexOf(status.jcAudio)
            if (index != -1) changeSelectedSong(index)
        }
        override fun onPlaying(status: JcStatus) {}
        override fun onTimeChanged(status: JcStatus) {}
        override fun onStopped(status: JcStatus) {}
        override fun onJcpError(throwable: Throwable) {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMySongsBinding.inflate(inflater, container, false)
        Timber.d("MySongsFragment Created")

        setupSwitchBar()
        setupRecyclerView()
        observeViewModel()

        viewModel.getData()

        return binding.root
    }

    private fun setupSwitchBar() {
        binding.switchBar.apply {
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
        adapter = SongAdapter(
            onItemClick = { _, position ->
            changeSelectedSong(position)
            binding.player.jcPlayer.playAudio(jcAudios[position])
            binding.player.jcPlayer.visibility = View.VISIBLE
        },
            onFavoriteClick = { song, view -> (view as? ImageView)?.checkFavorite(song.id) },
            onLoveClick = { song, view -> (view as? ImageView)?.checkLove(song.id) },
            onArtistClick = { id, name ->
                context?.openActivity<ArtistSongsActivity>(
                    extras = arrayOf(
                        DATA.ARTIST_ID to id, DATA.ARTIST_NAME to name
                    )
                )
            },
            onAlbumClick = { id, name, image ->
                context?.openActivity<AlbumSongsActivity>(
                    extras = arrayOf(
                        DATA.ALBUM_ID to id, DATA.ALBUM_NAME to name, DATA.ALBUM_IMAGE to image
                    )
                )
            },
            onCategoryClick = { id, name ->
                context?.openActivity<CategorySongsActivity>(
                    extras = arrayOf(
                        DATA.CATEGORY_ID to id, DATA.CATEGORY_NAME to name
                    )
                )
            })
        binding.recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.songs.collect { songs ->
                        adapter?.setList(songs)

                        jcAudios.clear()
                        songs.forEach { song ->
                            jcAudios.add(
                                JcAudio.createFromURL(
                                    song.name ?: "", song.songLink ?: ""
                                )
                            )
                        }

                        if (songs.isNotEmpty()) {
                            binding.recyclerView.visibility = View.VISIBLE
                            binding.emptyText.visibility = View.GONE
                            binding.player.jcPlayer.initPlaylist(jcAudios, jcPlayerListener)
                        } else {
                            binding.recyclerView.visibility = View.GONE
                            binding.emptyText.visibility = View.VISIBLE
                        }
                    }
                }
                launch {
                    viewModel.isLoading.collect { isLoading ->
                        binding.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
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
        binding.player.jcPlayer.pause()
        super.onPause()
    }

    override fun onStop() {
        binding.player.jcPlayer.pause()
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}