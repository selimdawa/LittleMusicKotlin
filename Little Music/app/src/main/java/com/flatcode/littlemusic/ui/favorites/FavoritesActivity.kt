package com.flatcode.littlemusic.ui.favorites

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
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
import com.example.jean.jcplayer.model.JcAudio
import com.flatcode.littlemusic.R
import com.flatcode.littlemusic.databinding.ActivityFavoritesBinding
import com.flatcode.littlemusic.ui.album.AlbumSongsActivity
import com.flatcode.littlemusic.ui.artist.ArtistSongsActivity
import com.flatcode.littlemusic.ui.category.CategorySongsActivity
import com.flatcode.littlemusic.ui.song.SongAdapter
import com.flatcode.littlemusic.utils.DATA
import com.flatcode.littlemusic.utils.checkFavorite
import com.flatcode.littlemusic.utils.checkLove
import com.flatcode.littlemusic.utils.openActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber


@AndroidEntryPoint
class FavoritesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoritesBinding
    private val viewModel: FavoritesViewModel by viewModels()
    private var adapter: SongAdapter? = null
    private val jcAudios = ArrayList<JcAudio>()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Timber.i("FavoritesActivity Created")

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

        setupToolbar()
        setupSwitchBar()
        setupRecyclerView()
        observeViewModel()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (DATA.searchStatus) {
                    binding.toolbar.toolbar.visibility = View.VISIBLE
                    binding.toolbar.toolbarSearch.visibility = View.GONE
                    DATA.searchStatus = false
                    binding.toolbar.textSearch.setText(DATA.EMPTY)
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })

        viewModel.getData()
    }

    private fun setupToolbar() {
        binding.toolbar.nameSpace.setText(R.string.favorites)
        binding.toolbar.close.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.toolbar.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        binding.toolbar.search.setOnClickListener {
            binding.toolbar.toolbar.visibility = View.GONE
            binding.toolbar.toolbarSearch.visibility = View.VISIBLE
            DATA.searchStatus = true
        }
        binding.toolbar.textSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                try {
                    adapter!!.filter.filter(s)
                } catch (_: Exception) {
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun setupSwitchBar() {
        binding.switchBar.all.setOnClickListener { viewModel.setType(DATA.TIMESTAMP) }
        binding.switchBar.mostViews.setOnClickListener { viewModel.setType(DATA.VIEWS_COUNT) }
        binding.switchBar.mostLoves.setOnClickListener { viewModel.setType(DATA.LOVES_COUNT) }
        binding.switchBar.name.setOnClickListener { viewModel.setType(DATA.NAME) }
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
                openActivity<ArtistSongsActivity>(
                    extras = arrayOf(
                        DATA.ARTIST_ID to id, DATA.ARTIST_NAME to name
                    )
                )
            },
            onAlbumClick = { id, name, image ->
                openActivity<AlbumSongsActivity>(
                    extras = arrayOf(
                        DATA.ALBUM_ID to id, DATA.ALBUM_NAME to name, DATA.ALBUM_IMAGE to image
                    )
                )
            },
            onCategoryClick = { id, name ->
                openActivity<CategorySongsActivity>(
                    extras = arrayOf(
                        DATA.CATEGORY_ID to id, DATA.CATEGORY_NAME to name
                    )
                )
            })
        binding.recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.songs.collect { songs ->
                        adapter?.setList(songs)
                        binding.toolbar.number.text = getString(R.string.count_format, songs.size)

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
                            binding.player.jcPlayer.initPlaylist(jcAudios, null)
                        } else {
                            binding.recyclerView.visibility = View.GONE
                            binding.emptyText.visibility = View.VISIBLE
                            Toast.makeText(
                                this@FavoritesActivity, "There are no songs!", Toast.LENGTH_SHORT
                            ).show()
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
}