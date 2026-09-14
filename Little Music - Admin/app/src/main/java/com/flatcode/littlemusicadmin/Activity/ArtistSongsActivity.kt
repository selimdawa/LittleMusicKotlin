package com.flatcode.littlemusicadmin.Activity

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.jean.jcplayer.model.JcAudio
import com.flatcode.littlemusicadmin.Adapter.AlbumAdapter
import com.flatcode.littlemusicadmin.Adapter.SongAdapter
import com.flatcode.littlemusicadmin.Model.Album
import com.flatcode.littlemusicadmin.Model.Song
import com.flatcode.littlemusicadmin.Unit.DATA
import com.flatcode.littlemusicadmin.Unit.VOID
import com.flatcode.littlemusicadmin.ViewModel.ArtistSongsViewModel
import com.flatcode.littlemusicadmin.databinding.ActivityArtistSongsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.MessageFormat
import timber.log.Timber

@AndroidEntryPoint
class ArtistSongsActivity : AppCompatActivity() {

    private var binding: ActivityArtistSongsBinding? = null
    var activity: Activity = this@ArtistSongsActivity
    var albumList: ArrayList<Album?>? = null
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
        super.onCreate(savedInstanceState)
        binding = ActivityArtistSongsBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        artistId = intent.getStringExtra(DATA.ARTIST_ID)
        artistName = intent.getStringExtra(DATA.ARTIST_NAME)
        artistImage = intent.getStringExtra(DATA.ARTIST_IMAGE)
        artistAbout = intent.getStringExtra(DATA.ARTIST_ABOUT)

        viewModel.init(artistId)

        binding!!.toolbar.nameSpace.text = artistName
        binding!!.toolbar.back.setOnClickListener { onBackPressed() }
        binding!!.toolbar.close.setOnClickListener { onBackPressed() }
        binding!!.switchBarAlbums.scrollSwitch.visibility = View.VISIBLE

        binding!!.toolbar.search.setOnClickListener {
            binding!!.toolbar.toolbar.visibility = View.GONE
            binding!!.toolbar.toolbarSearch.visibility = View.VISIBLE
            DATA.searchStatus = true
        }

        binding!!.toolbar.textSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                try {
                    if (isAlbum) albumAdapter!!.filter.filter(s)
                    else if (isSong) songAdapter!!.filter.filter(s)
                } catch (e: Exception) {
                    Timber.e(e, "Error filtering")
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        albumList = ArrayList()
        albumAdapter = AlbumAdapter(activity, albumList!!)
        binding!!.recyclerAlbums.adapter = albumAdapter

        initSongs()

        // Switch to Songs
        binding!!.switchBarAlbums.songs.setOnClickListener {
            binding!!.switchBarAlbums.scrollSwitch.visibility = View.GONE
            binding!!.switchBarSongs.scrollSwitch.visibility = View.VISIBLE
            binding!!.player.jcPlayer.pause()
            binding!!.player.jcPlayer.visibility = View.GONE
            isAlbum = false
            isSong = true
            updateVisibility()
            if (DATA.searchStatus) onBackPressed()
        }

        // Albums filters
        binding!!.switchBarAlbums.aboutTheArtist.setOnClickListener {
            VOID.dialogAboutArtist(activity, artistImage, artistName, artistAbout)
        }
        binding!!.switchBarAlbums.all.setOnClickListener {
            viewModel.setOrderBy(DATA.TIMESTAMP)
        }
        binding!!.switchBarAlbums.mostSongs.setOnClickListener {
            viewModel.setOrderBy(DATA.SONGS_COUNT)
        }
        binding!!.switchBarAlbums.mostInterested.setOnClickListener {
            viewModel.setOrderBy(DATA.INTERESTED_COUNT)
        }
        binding!!.switchBarAlbums.name.setOnClickListener {
            viewModel.setOrderBy(DATA.NAME)
        }

        // Switch to Albums
        binding!!.switchBarSongs.albums.setOnClickListener {
            binding!!.switchBarSongs.scrollSwitch.visibility = View.GONE
            binding!!.switchBarAlbums.scrollSwitch.visibility = View.VISIBLE
            isAlbum = true
            isSong = false
            updateVisibility()
            if (DATA.searchStatus) onBackPressed()
        }
        binding!!.switchBarSongs.aboutTheArtist.setOnClickListener {
            VOID.dialogAboutArtist(activity, artistImage, artistName, artistAbout)
        }
        binding!!.switchBarSongs.all.setOnClickListener {
            viewModel.setOrderBy(DATA.TIMESTAMP)
        }
        binding!!.switchBarSongs.mostViews.setOnClickListener {
            viewModel.setOrderBy(DATA.VIEWS_COUNT)
        }
        binding!!.switchBarSongs.mostLoves.setOnClickListener {
            viewModel.setOrderBy(DATA.LOVES_COUNT)
        }
        binding!!.switchBarSongs.name.setOnClickListener {
            viewModel.setOrderBy(DATA.NAME)
        }

        observeViewModel()
    }

    private fun initSongs() {
        songList = ArrayList()
        jcAudios = ArrayList()
        songAdapter = SongAdapter(activity, songList!!) { _, position: Int ->
            changeSelectedSong(position)
            binding!!.player.jcPlayer.playAudio(jcAudios!![position])
            binding!!.player.jcPlayer.visibility = View.VISIBLE
        }
        binding!!.recyclerSongs.adapter = songAdapter
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.albums.collectLatest { albums ->
                if (isAlbum) {
                    albumList!!.clear()
                    albumList!!.addAll(albums)
                    binding!!.toolbar.number.text = MessageFormat.format("( {0} )", albums.size)
                    albumAdapter!!.notifyDataSetChanged()
                    updateVisibility()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.songs.collectLatest { songs ->
                if (isSong) {
                    changeSelectedSong(-1)
                    songList!!.clear()
                    jcAudios!!.clear()
                    for (item in songs) {
                        songList!!.add(item)
                        if (item.name != null && item.songLink != null) {
                            jcAudios!!.add(JcAudio.createFromURL(item.name, item.songLink))
                        }
                    }
                    binding!!.toolbar.number.text = MessageFormat.format("( {0} )", songs.size)
                    songAdapter!!.notifyDataSetChanged()
                    if (songs.isNotEmpty()) {
                        binding!!.player.jcPlayer.initPlaylist(jcAudios!!, null)
                    }
                    updateVisibility()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                binding!!.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
    }

    private fun updateVisibility() {
        if (isAlbum) {
            binding!!.recyclerSongs.visibility = View.GONE
            if (albumList!!.isNotEmpty()) {
                binding!!.recyclerAlbums.visibility = View.VISIBLE
                binding!!.emptyText.visibility = View.GONE
            } else {
                binding!!.recyclerAlbums.visibility = View.GONE
                binding!!.emptyText.visibility = View.VISIBLE
            }
        } else {
            binding!!.recyclerAlbums.visibility = View.GONE
            if (songList!!.isNotEmpty()) {
                binding!!.recyclerSongs.visibility = View.VISIBLE
                binding!!.emptyText.visibility = View.GONE
            } else {
                binding!!.recyclerSongs.visibility = View.GONE
                binding!!.emptyText.visibility = View.VISIBLE
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
            binding!!.toolbar.toolbar.visibility = View.VISIBLE
            binding!!.toolbar.toolbarSearch.visibility = View.GONE
            DATA.searchStatus = false
            binding!!.toolbar.textSearch.setText(DATA.EMPTY)
        } else if (DATA.isChange) {
            onResume()
            DATA.isChange = false
        } else super.onBackPressed()
    }

    override fun onPause() {
        binding!!.player.jcPlayer.pause()
        super.onPause()
    }

    override fun onStop() {
        binding!!.player.jcPlayer.pause()
        super.onStop()
    }
}