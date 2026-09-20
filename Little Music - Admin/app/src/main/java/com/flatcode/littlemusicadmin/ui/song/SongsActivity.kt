package com.flatcode.littlemusicadmin.ui.song

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.jean.jcplayer.model.JcAudio
import com.flatcode.littlemusicadmin.R
import com.flatcode.littlemusicadmin.databinding.ActivityPageSongSwitchBinding
import com.flatcode.littlemusicadmin.utils.DATA
import com.flatcode.littlemusicadmin.utils.checkFavorite
import com.flatcode.littlemusicadmin.utils.checkLove
import com.flatcode.littlemusicadmin.utils.incrementViewCount
import com.flatcode.littlemusicadmin.utils.moreDelete
import com.flatcode.littlemusicadmin.utils.openActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.MessageFormat

@AndroidEntryPoint
class SongsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPageSongSwitchBinding
    private val activity: Activity = this@SongsActivity
    private var adapter: SongAdapter? = null
    private var jcAudios: ArrayList<JcAudio>? = null
    private val viewModel: SongsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityPageSongSwitchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.nameSpace.setText(R.string.songs)
        binding.toolbar.back.setOnClickListener { finish() }
        binding.toolbar.close.setOnClickListener { finish() }

        binding.toolbar.search.setOnClickListener {
            binding.toolbar.toolbar.visibility = View.GONE
            binding.toolbar.toolbarSearch.visibility = View.VISIBLE
            DATA.searchStatus = true
        }

        binding.toolbar.textSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                viewModel.setSearchQuery(s.toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })

        init()

        binding.switchBar.all.setOnClickListener { viewModel.setOrderBy(DATA.TIMESTAMP) }
        binding.switchBar.mostViews.setOnClickListener { viewModel.setOrderBy(DATA.VIEWS_COUNT) }
        binding.switchBar.mostLoves.setOnClickListener { viewModel.setOrderBy(DATA.LOVES_COUNT) }
        binding.switchBar.name.setOnClickListener { viewModel.setOrderBy(DATA.NAME) }

        observeViewModel()
    }

    private fun init() {
        jcAudios = ArrayList()
        adapter = SongAdapter(
            onItemClick = { song, position ->
                changeSelectedSong(position)
                binding.player.jcPlayer.playAudio(jcAudios!![position])
                binding.player.jcPlayer.visibility = View.VISIBLE
                song.id.incrementViewCount()
            },
            onFavoriteClick = { song, view ->
                view.checkFavorite(song.id)
            },
            onLoveClick = { song, view ->
                view.checkLove(song.id)
            },
            onMoreClick = { song ->
                song.moreDelete(
                    activity, DATA.ARTISTS, song.artistId, DATA.SONGS_COUNT,
                    DATA.CATEGORIES, song.categoryId, DATA.SONGS_COUNT,
                    DATA.ALBUMS, song.albumId, DATA.SONGS_COUNT
                )
            },
            onArtistClick = { artistId ->
                activity.openActivity<ArtistSongsActivity>(
                    extras = arrayOf(DATA.ARTIST_ID to artistId)
                )
            },
            onAlbumClick = { albumId ->
                activity.openActivity<AlbumSongsActivity>(
                    extras = arrayOf(DATA.ALBUM_ID to albumId)
                )
            },
            onCategoryClick = { categoryId ->
                activity.openActivity<CategorySongsActivity>(
                    extras = arrayOf(DATA.CATEGORY_ID to categoryId)
                )
            }
        )
        binding.recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.songs.collectLatest { songs ->
                changeSelectedSong(-1)
                jcAudios!!.clear()
                for (item in songs) {
                    val name = item.name
                    val songLink = item.songLink
                    if (name != null && songLink != null) {
                        jcAudios!!.add(JcAudio.createFromURL(name, songLink))
                    }
                }
                binding.toolbar.number.text = MessageFormat.format("( {0} )", songs.size)
                adapter!!.submitList(songs)

                if (songs.isNotEmpty()) {
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.emptyText.visibility = View.GONE
                    binding.player.jcPlayer.initPlaylist(jcAudios!!, null)
                } else {
                    binding.recyclerView.visibility = View.GONE
                    binding.emptyText.visibility = View.VISIBLE
                    if (!DATA.searchStatus) {
                        Toast.makeText(activity, "There is no songs!", Toast.LENGTH_SHORT).show()
                    }
                }
                Timber.d("Songs updated: ${songs.size}")
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                binding.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
    }

    private fun changeSelectedSong(index: Int) {
        adapter?.let {
            val oldPosition = it.selectedPosition
            it.selectedPosition = index
            if (oldPosition != -1) it.notifyItemChanged(oldPosition)
            if (index != -1) it.notifyItemChanged(index)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (DATA.searchStatus) {
            binding.toolbar.toolbar.visibility = View.VISIBLE
            binding.toolbar.toolbarSearch.visibility = View.GONE
            DATA.searchStatus = false
            binding.toolbar.textSearch.setText(DATA.EMPTY)
            viewModel.setSearchQuery("")
        } else if (DATA.isChange) {
            onResume()
            DATA.isChange = false
        } else {
            super.onBackPressed()
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
}
