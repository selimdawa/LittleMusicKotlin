package com.flatcode.littlemusicadmin.ui.song

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
import com.flatcode.littlemusicadmin.ui.album.AlbumAdapter
import com.flatcode.littlemusicadmin.model.Album
import com.flatcode.littlemusicadmin.model.Song
import com.flatcode.littlemusicadmin.R
import com.flatcode.littlemusicadmin.utils.*
import com.flatcode.littlemusicadmin.databinding.ActivityArtistSongsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.MessageFormat
import timber.log.Timber

@AndroidEntryPoint
class ArtistSongsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArtistSongsBinding
    var activity: Activity = this@ArtistSongsActivity
    var albumAdapter: AlbumAdapter? = null
    var songList: ArrayList<Song?>? = null
    var songAdapter: SongAdapter? = null
    var isAlbum = true
    var isSong = false
    var jcAudios: ArrayList<JcAudio>? = null
    private var currentSong = 0
    var artistId: String? = null
    var artistName: String? = null
    var artistImage: String? = null
    var artistAbout: String? = null
    private val viewModel: ArtistSongsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityArtistSongsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        artistId = intent.getStringExtra(DATA.ARTIST_ID)
        artistName = intent.getStringExtra(DATA.ARTIST_NAME)
        artistImage = intent.getStringExtra(DATA.ARTIST_IMAGE)
        artistAbout = intent.getStringExtra(DATA.ARTIST_ABOUT)

        viewModel.init(artistId)

        binding.toolbar.nameSpace.text = artistName
        binding.toolbar.back.setOnClickListener { onBackPressed() }
        binding.toolbar.close.setOnClickListener { onBackPressed() }
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

        albumAdapter = AlbumAdapter(
            onItemClick = { album ->
                activity.openActivity<AlbumSongsActivity>(
                    extras = arrayOf(
                        DATA.ALBUM_ID to album.id,
                        DATA.ALBUM_NAME to album.name,
                        DATA.ALBUM_IMAGE to album.image
                    )
                )
            },
            onMoreClick = { album ->
                album.moreDelete(
                    activity, DATA.ARTISTS, album.artistId, DATA.ALBUMS_COUNT,
                    DATA.CATEGORIES, album.categoryId, DATA.ALBUMS_COUNT,
                    null, null, null
                )
            }
        )
        binding.recyclerAlbums.adapter = albumAdapter

        initSongs()

        // Switch to Songs
        binding.switchBarAlbums.songs.setOnClickListener {
            binding.switchBarAlbums.scrollSwitch.visibility = View.GONE
            binding.switchBarSongs.scrollSwitch.visibility = View.VISIBLE
            binding.player.jcPlayer.pause()
            binding.player.jcPlayer.visibility = View.GONE
            isAlbum = false
            isSong = true
            updateVisibility()
            if (DATA.searchStatus) onBackPressed()
        }

        // Albums filters
        binding.switchBarAlbums.aboutTheArtist.setOnClickListener {
            activity.dialogAboutArtist(artistImage, artistName, artistAbout)
        }
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
            if (DATA.searchStatus) onBackPressed()
        }
        binding.switchBarSongs.aboutTheArtist.setOnClickListener {
            activity.dialogAboutArtist(artistImage, artistName, artistAbout)
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

    private fun initSongs() {
        jcAudios = ArrayList()
        songAdapter = SongAdapter(
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
        binding.recyclerSongs.adapter = songAdapter
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.albums.collectLatest { albums ->
                if (isAlbum) {
                    binding.toolbar.number.text = MessageFormat.format("( {0} )", albums.size)
                    albumAdapter!!.submitList(albums)
                    updateVisibility()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.songs.collectLatest { songs ->
                if (isSong) {
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
                    songAdapter!!.submitList(songs)
                    if (songs.isNotEmpty()) {
                        binding.player.jcPlayer.initPlaylist(jcAudios!!, null)
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
            if (albumAdapter!!.itemCount > 0) {
                binding.recyclerAlbums.visibility = View.VISIBLE
                binding.emptyText.visibility = View.GONE
            } else {
                binding.recyclerAlbums.visibility = View.GONE
                binding.emptyText.visibility = View.VISIBLE
            }
        } else {
            binding.recyclerAlbums.visibility = View.GONE
            if (songAdapter!!.itemCount > 0) {
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
        if (songAdapter != null) {
            songAdapter!!.notifyItemChanged(songAdapter!!.selectedPosition)
            currentSong = index
            songAdapter!!.selectedPosition = currentSong
            songAdapter!!.notifyItemChanged(currentSong)
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