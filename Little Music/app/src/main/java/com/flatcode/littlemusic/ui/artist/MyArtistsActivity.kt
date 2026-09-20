package com.flatcode.littlemusic.ui.artist

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.littlemusic.R
import com.flatcode.littlemusic.utils.DATA
import android.widget.ImageView
import com.flatcode.littlemusic.utils.checkInterested
import com.flatcode.littlemusic.utils.openActivity
import com.flatcode.littlemusic.databinding.ActivityMyArtistsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.MessageFormat

@AndroidEntryPoint
class MyArtistsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyArtistsBinding
    private val viewModel: MyArtistsViewModel by viewModels()
    private var adapter: ArtistAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityMyArtistsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Timber.i("MyArtistsActivity Created")

        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar.root) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = insets.top
            }
            windowInsets
        }

        setupToolbar()
        setupSwitchBar()
        setupRecyclerView()
        observeViewModel()

        viewModel.getData()
    }

    private fun setupToolbar() {
        binding.toolbar.nameSpace.setText(R.string.my_artists)
        binding.toolbar.close.setOnClickListener { onBackPressed() }
        binding.toolbar.back.setOnClickListener { onBackPressed() }

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
                } catch (e: Exception) {}
            }
            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun setupSwitchBar() {
        binding.switchBar.explore.setOnClickListener { this.openActivity<ArtistsActivity>() }
        binding.switchBar.all.setOnClickListener { viewModel.setType(DATA.TIMESTAMP) }
        binding.switchBar.mostSongs.setOnClickListener { viewModel.setType(DATA.SONGS_COUNT) }
        binding.switchBar.mostAlbums.setOnClickListener { viewModel.setType(DATA.ALBUMS_COUNT) }
        binding.switchBar.mostInterested.setOnClickListener { viewModel.setType(DATA.INTERESTED_COUNT) }
        binding.switchBar.name.setOnClickListener { viewModel.setType(DATA.NAME) }
    }

    private fun setupRecyclerView() {
        adapter = ArtistAdapter(
            onItemClick = { artist ->
                openActivity<ArtistSongsActivity>(
                    extras = arrayOf(
                        DATA.ARTIST_ID to artist.id,
                        DATA.ARTIST_NAME to artist.name,
                        DATA.ARTIST_IMAGE to artist.image,
                        DATA.ARTIST_ABOUT to artist.aboutTheArtist
                    )
                )
            },
            onInterestedClick = { artist, view -> (view as? ImageView)?.checkInterested(DATA.ARTISTS, artist.id) }
        )
        binding.recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.artists.collect { artists ->
                        adapter?.setList(artists)
                        binding.toolbar.number.text = MessageFormat.format("( {0} )", artists.size)
                        
                        if (artists.isNotEmpty()) {
                            binding.recyclerView.visibility = View.VISIBLE
                            binding.emptyText.visibility = View.GONE
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

    override fun onBackPressed() {
        if (DATA.searchStatus) {
            binding.toolbar.toolbar.visibility = View.VISIBLE
            binding.toolbar.toolbarSearch.visibility = View.GONE
            DATA.searchStatus = false
            binding.toolbar.textSearch.setText(DATA.EMPTY)
        } else super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getData()
    }
}