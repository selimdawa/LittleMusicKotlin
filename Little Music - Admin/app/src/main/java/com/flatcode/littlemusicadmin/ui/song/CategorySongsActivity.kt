package com.flatcode.littlemusicadmin.ui.song

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.jean.jcplayer.model.JcAudio
import com.flatcode.littlemusicadmin.databinding.ActivityCategorySongsBinding
import com.flatcode.littlemusicadmin.ui.album.AlbumAdapter
import com.flatcode.littlemusicadmin.utils.DATA
import com.flatcode.littlemusicadmin.utils.checkFavorite
import com.flatcode.littlemusicadmin.utils.checkLove
import com.flatcode.littlemusicadmin.utils.moreDelete
import com.flatcode.littlemusicadmin.utils.openActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.MessageFormat

@AndroidEntryPoint
class CategorySongsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategorySongsBinding
    private val activity: Activity = this@CategorySongsActivity
    private lateinit var albumAdapter: AlbumAdapter
    private lateinit var songAdapter: SongAdapter
    private var isAlbum = true
    private var isSong = false
    private var jcAudios: ArrayList<JcAudio> = ArrayList()
    private var currentSong = 0
    private var categoryId: String? = null
    private var categoryName: String? = null
    private val viewModel: CategorySongsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityCategorySongsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        categoryId = intent.getStringExtra(DATA.CATEGORY_ID)
        categoryName = intent.getStringExtra(DATA.CATEGORY_NAME)

        viewModel.init(categoryId)

        binding.toolbar.nameSpace.text = categoryName
        binding.toolbar.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.toolbar.close.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (DATA.searchStatus) {
                    binding.toolbar.toolbar.visibility = View.VISIBLE
                    binding.toolbar.toolbarSearch.visibility = View.GONE
                    DATA.searchStatus = false
                    binding.toolbar.textSearch.setText(DATA.EMPTY)
                    viewModel.setSearchQuery(DATA.EMPTY)
                } else if (DATA.isChange) {
                    onResume()
                    DATA.isChange = false
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })

        binding.switchBarAlbums.scrollSwitch.visibility = View.VISIBLE

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

        initAdapters()

        // Switch to Songs
        binding.switchBarAlbums.songs.setOnClickListener {
            binding.switchBarAlbums.scrollSwitch.visibility = View.GONE
            binding.switchBarSongs.scrollSwitch.visibility = View.VISIBLE
            binding.player.jcPlayer.pause()
            binding.player.jcPlayer.visibility = View.GONE
            isAlbum = false
            isSong = true
            updateVisibility()
            if (DATA.searchStatus) onBackPressedDispatcher.onBackPressed()
        }

        // Albums filters
        binding.switchBarAlbums.all.setOnClickListener {
            viewModel.setOrderBy(DATA.TIMESTAMP)
        }
        binding.switchBarAlbums.mostSongs.setOnClickListener {
            viewModel.setOrderBy(DATA.SONGS_COUNT)
        }
        binding.switchBarAlbums.mostInterested.setOnClickListener {
            viewModel.setOrderBy(DATA.INTERESTED_COUNT)
        }
        binding.switchBarAlbums.name.setOnClickListener {
            viewModel.setOrderBy(DATA.NAME)
        }

        // Switch to Albums
        binding.switchBarSongs.albums.setOnClickListener {
            binding.switchBarSongs.scrollSwitch.visibility = View.GONE
            binding.switchBarAlbums.scrollSwitch.visibility = View.VISIBLE
            isAlbum = true
            isSong = false
            updateVisibility()
            if (DATA.searchStatus) onBackPressedDispatcher.onBackPressed()
        }
        binding.switchBarSongs.all.setOnClickListener {
            viewModel.setOrderBy(DATA.TIMESTAMP)
        }
        binding.switchBarSongs.mostViews.setOnClickListener {
            viewModel.setOrderBy(DATA.VIEWS_COUNT)
        }
        binding.switchBarSongs.mostLoves.setOnClickListener {
            viewModel.setOrderBy(DATA.LOVES_COUNT)
        }
        binding.switchBarSongs.name.setOnClickListener {
            viewModel.setOrderBy(DATA.NAME)
        }

        observeViewModel()
    }

    private fun initAdapters() {
        albumAdapter = AlbumAdapter(onItemClick = { album ->
            openActivity<AlbumSongsActivity>(
                extras = arrayOf(
                    DATA.ALBUM_ID to album.id,
                    DATA.ALBUM_NAME to album.name,
                    DATA.ALBUM_IMAGE to album.image
                )
            )
        }, onMoreClick = { album ->
            album.moreDelete(activity, null, null, null, null, null, null, null, null, null)
        })
        binding.recyclerAlbums.adapter = albumAdapter

        songAdapter = SongAdapter(
            onItemClick = { _, position ->
            changeSelectedSong(position)
            binding.player.jcPlayer.playAudio(jcAudios[position])
            binding.player.jcPlayer.visibility = View.VISIBLE
        },
            onFavoriteClick = { song, imageView -> imageView.checkFavorite(song.id) },
            onLoveClick = { song, imageView -> imageView.checkLove(song.id) },
            onMoreClick = { song ->
                song.moreDelete(activity, null, null, null, null, null, null, null, null, null)
            },
            onArtistClick = { artistId ->
                openActivity<ArtistSongsActivity>(extras = arrayOf(DATA.ARTIST_ID to artistId))
            },
            onAlbumClick = { albumId ->
                openActivity<AlbumSongsActivity>(extras = arrayOf(DATA.ALBUM_ID to albumId))
            },
            onCategoryClick = { /* Already in CategorySongsActivity */ })
        binding.recyclerSongs.adapter = songAdapter
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.albums.collectLatest { albums ->
                if (isAlbum) {
                    binding.toolbar.number.text = MessageFormat.format("( {0} )", albums.size)
                    albumAdapter.submitList(albums)
                    updateVisibility()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.songs.collectLatest { songs ->
                if (isSong) {
                    changeSelectedSong(-1)
                    jcAudios.clear()
                    for (item in songs) {
                        val name = item.name
                        val songLink = item.songLink
                        if (name != null && songLink != null) {
                            jcAudios.add(JcAudio.createFromURL(name, songLink))
                        }
                    }
                    binding.toolbar.number.text = MessageFormat.format("( {0} )", songs.size)
                    songAdapter.submitList(songs)
                    if (songs.isNotEmpty()) {
                        binding.player.jcPlayer.initPlaylist(jcAudios, null)
                    }
                    updateVisibility()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                binding.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
    }

    private fun updateVisibility() {
        if (isAlbum) {
            binding.recyclerSongs.visibility = View.GONE
            if (albumAdapter.itemCount > 0) {
                binding.recyclerAlbums.visibility = View.VISIBLE
                binding.emptyText.visibility = View.GONE
            } else {
                binding.recyclerAlbums.visibility = View.GONE
                binding.emptyText.visibility = View.VISIBLE
            }
        } else {
            binding.recyclerAlbums.visibility = View.GONE
            if (songAdapter.itemCount > 0) {
                binding.recyclerSongs.visibility = View.VISIBLE
                binding.emptyText.visibility = View.GONE
            } else {
                binding.recyclerSongs.visibility = View.GONE
                binding.emptyText.visibility = View.VISIBLE
                Toast.makeText(activity, "There is no songs!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun changeSelectedSong(index: Int) {
        songAdapter.notifyItemChanged(songAdapter.selectedPosition)
        currentSong = index
        songAdapter.selectedPosition = currentSong
        songAdapter.notifyItemChanged(currentSong)
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