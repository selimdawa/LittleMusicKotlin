package com.flatcode.littlemusicadmin.ui.others

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.jean.jcplayer.model.JcAudio
import com.flatcode.littlemusicadmin.R
import com.flatcode.littlemusicadmin.databinding.ActivityPageSongSwitchBinding
import com.flatcode.littlemusicadmin.model.Song
import com.flatcode.littlemusicadmin.ui.song.AlbumSongsActivity
import com.flatcode.littlemusicadmin.ui.song.ArtistSongsActivity
import com.flatcode.littlemusicadmin.ui.song.CategorySongsActivity
import com.flatcode.littlemusicadmin.ui.song.SongAdapter
import com.flatcode.littlemusicadmin.utils.DATA
import com.flatcode.littlemusicadmin.utils.checkFavorite
import com.flatcode.littlemusicadmin.utils.checkLove
import com.flatcode.littlemusicadmin.utils.incrementViewCount
import com.flatcode.littlemusicadmin.utils.moreDelete
import com.flatcode.littlemusicadmin.utils.openActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.MessageFormat
import timber.log.Timber

@AndroidEntryPoint
class FavoritesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPageSongSwitchBinding
    private val activity: Activity = this@FavoritesActivity
    var adapter: SongAdapter? = null
    var jcAudios: ArrayList<JcAudio>? = null
    private var currentSong = 0
    private val viewModel: FavoritesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityPageSongSwitchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.nameSpace.setText(R.string.favorites)
        binding.toolbar.back.setOnClickListener { onBackPressed() }
        binding.toolbar.close.setOnClickListener { onBackPressed() }

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
        binding.switchBar.all.setOnClickListener {
            viewModel.setOrderBy(DATA.TIMESTAMP)
        }
        binding.switchBar.mostViews.setOnClickListener {
            viewModel.setOrderBy(DATA.VIEWS_COUNT)
        }
        binding.switchBar.mostLoves.setOnClickListener {
            viewModel.setOrderBy(DATA.LOVES_COUNT)
        }
        binding.switchBar.name.setOnClickListener {
            viewModel.setOrderBy(DATA.NAME)
        }

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
            viewModel.favorites.collectLatest { songs ->
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
                    Toast.makeText(activity, "There is no songs!", Toast.LENGTH_SHORT).show()
                }
                Timber.d("Favorites updated: ${songs.size}")
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                binding.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
    }

    fun changeSelectedSong(index: Int) {
        if (adapter != null) {
            adapter!!.notifyItemChanged(adapter!!.selectedPosition)
            currentSong = index
            adapter!!.selectedPosition = currentSong
            adapter!!.notifyItemChanged(currentSong)
        }
    }

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
        } else super.onBackPressed()
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
