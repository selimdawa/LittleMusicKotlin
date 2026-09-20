package com.flatcode.littlemusic.ui.category

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import android.widget.ImageView
import com.example.jean.jcplayer.model.JcAudio
import com.flatcode.littlemusic.ui.album.AlbumAdapter
import com.flatcode.littlemusic.ui.album.AlbumSongsActivity
import com.flatcode.littlemusic.ui.artist.ArtistSongsActivity
import com.flatcode.littlemusic.ui.song.SongAdapter
import com.flatcode.littlemusic.utils.checkFavorite
import com.flatcode.littlemusic.utils.checkLove
import com.flatcode.littlemusic.utils.openActivity
import com.flatcode.littlemusic.model.Song
import com.flatcode.littlemusic.utils.DATA
import com.flatcode.littlemusic.utils.checkInterested
import com.flatcode.littlemusic.utils.isInterested
import com.flatcode.littlemusic.databinding.ActivityCategorySongsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.MessageFormat

@AndroidEntryPoint
class CategorySongsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategorySongsBinding
    private val viewModel: CategorySongsViewModel by viewModels()

    private var albumAdapter: AlbumAdapter? = null
    private var songAdapter: SongAdapter? = null
    private val jcAudios = ArrayList<JcAudio>()

    private var isAlbum = true
    private var isSong = false

    private var categoryId: String? = null
    private var categoryName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityCategorySongsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Timber.i("CategorySongsActivity Created")

        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar.root) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = insets.top
            }
            windowInsets
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.player.root) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(bottom = insets.bottom)
            windowInsets
        }

        categoryId = intent.getStringExtra(DATA.CATEGORY_ID)
        categoryName = intent.getStringExtra(DATA.CATEGORY_NAME)

        setupToolbar()
        setupSwitchBars()
        setupRecyclerViews()
        observeViewModel()

        viewModel.getAlbums(categoryId)
    }

    private fun setupToolbar() {
        binding.toolbar.nameSpace.text = categoryName
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
                try {
                    if (isAlbum) albumAdapter!!.filter.filter(s) else if (isSong) songAdapter!!.filter.filter(s)
                } catch (e: Exception) {}
            }
            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun setupSwitchBars() {
        binding.switchBarAlbums.scrollSwitch.visibility = View.VISIBLE

        binding.switchBarSongs.interest.isInterested(categoryId, DATA.CATEGORIES)
        binding.switchBarSongs.add.setOnClickListener { binding.switchBarSongs.interest.checkInterested(DATA.CATEGORIES, categoryId) }

        binding.switchBarAlbums.interest.isInterested(categoryId, DATA.CATEGORIES)
        binding.switchBarAlbums.add.setOnClickListener { binding.switchBarAlbums.interest.checkInterested(DATA.CATEGORIES, categoryId) }

        binding.switchBarAlbums.songs.setOnClickListener {
            isAlbum = false
            isSong = true
            binding.switchBarAlbums.scrollSwitch.visibility = View.GONE
            binding.switchBarSongs.scrollSwitch.visibility = View.VISIBLE
            binding.player.jcPlayer.pause()
            binding.player.jcPlayer.visibility = View.GONE
            viewModel.getSongs(categoryId)
            if (DATA.searchStatus) onBackPressed()
        }

        binding.switchBarAlbums.apply {
            all.setOnClickListener { viewModel.setType(DATA.TIMESTAMP, categoryId!!, true) }
            mostSongs.setOnClickListener { viewModel.setType(DATA.SONGS_COUNT, categoryId!!, true) }
            mostInterested.setOnClickListener { viewModel.setType(DATA.INTERESTED_COUNT, categoryId!!, true) }
            name.setOnClickListener { viewModel.setType(DATA.NAME, categoryId!!, true) }
        }

        binding.switchBarSongs.albums.setOnClickListener {
            isAlbum = true
            isSong = false
            binding.switchBarSongs.scrollSwitch.visibility = View.GONE
            binding.switchBarAlbums.scrollSwitch.visibility = View.VISIBLE
            viewModel.getAlbums(categoryId)
            if (DATA.searchStatus) onBackPressed()
        }

        binding.switchBarSongs.apply {
            all.setOnClickListener { viewModel.setType(DATA.TIMESTAMP, categoryId!!, false) }
            mostViews.setOnClickListener { viewModel.setType(DATA.VIEWS_COUNT, categoryId!!, false) }
            mostLoves.setOnClickListener { viewModel.setType(DATA.LOVES_COUNT, categoryId!!, false) }
            name.setOnClickListener { viewModel.setType(DATA.NAME, categoryId!!, false) }
        }
    }

    private fun setupRecyclerViews() {
        albumAdapter = AlbumAdapter(
            onItemClick = { album ->
                openActivity<AlbumSongsActivity>(
                    extras = arrayOf(DATA.ALBUM_ID to album.id, DATA.ALBUM_NAME to album.name, DATA.ALBUM_IMAGE to album.image)
                )
            },
            onInterestedClick = { album, view -> (view as? ImageView)?.checkInterested(DATA.ALBUMS, album.id) }
        )
        binding.recyclerAlbums.adapter = albumAdapter

        songAdapter = SongAdapter(
            onItemClick = { _, position ->
                changeSelectedSong(position)
                binding.player.jcPlayer.playAudio(jcAudios[position])
                binding.player.jcPlayer.visibility = View.VISIBLE
            },
            onFavoriteClick = { song, view -> (view as? ImageView)?.checkFavorite(song.id) },
            onLoveClick = { song, view -> (view as? ImageView)?.checkLove(song.id) },
            onArtistClick = { id, name ->
                openActivity<ArtistSongsActivity>(extras = arrayOf(DATA.ARTIST_ID to id, DATA.ARTIST_NAME to name))
            },
            onAlbumClick = { id, name, image ->
                openActivity<AlbumSongsActivity>(extras = arrayOf(DATA.ALBUM_ID to id, DATA.ALBUM_NAME to name, DATA.ALBUM_IMAGE to image))
            },
            onCategoryClick = { id, name ->
                openActivity<CategorySongsActivity>(extras = arrayOf(DATA.CATEGORY_ID to id, DATA.CATEGORY_NAME to name))
            }
        )
        binding.recyclerSongs.adapter = songAdapter
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.albums.collect { albums ->
                        if (isAlbum) {
                            albumAdapter?.setList(albums)
                            binding.toolbar.number.text = MessageFormat.format("( {0} )", albums.size)
                            binding.progress.visibility = View.GONE
                            if (albums.isNotEmpty()) {
                                binding.recyclerAlbums.visibility = View.VISIBLE
                                binding.emptyText.visibility = View.GONE
                            } else {
                                binding.recyclerAlbums.visibility = View.GONE
                                binding.emptyText.visibility = View.VISIBLE
                            }
                        }
                    }
                }
                launch {
                    viewModel.songs.collect { songs ->
                        if (isSong) {
                            songAdapter?.setList(songs)
                            binding.toolbar.number.text = MessageFormat.format("( {0} )", songs.size)

                            jcAudios.clear()
                            songs.forEach { song ->
                                jcAudios.add(JcAudio.createFromURL(song.name ?: "", song.songLink ?: ""))
                            }

                            binding.progress.visibility = View.GONE
                            if (songs.isNotEmpty()) {
                                binding.recyclerSongs.visibility = View.VISIBLE
                                binding.emptyText.visibility = View.GONE
                                binding.player.jcPlayer.initPlaylist(jcAudios, null)
                            } else {
                                binding.recyclerSongs.visibility = View.GONE
                                binding.emptyText.visibility = View.VISIBLE
                                Toast.makeText(this@CategorySongsActivity, "There are no songs!", Toast.LENGTH_SHORT).show()
                            }
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
        songAdapter?.let {
            val previousSelected = it.selectedPosition
            it.selectedPosition = index
            it.notifyItemChanged(previousSelected)
            it.notifyItemChanged(index)
        }
    }

    override fun onBackPressed() {
        if (DATA.searchStatus) {
            binding.toolbar.toolbar.visibility = View.VISIBLE
            binding.toolbar.toolbarSearch.visibility = View.GONE
            DATA.searchStatus = false
            binding.toolbar.textSearch.setText(DATA.EMPTY)
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

    override fun onResume() {
        super.onResume()
        if (isAlbum) viewModel.getAlbums(categoryId) else viewModel.getSongs(categoryId)
    }
}