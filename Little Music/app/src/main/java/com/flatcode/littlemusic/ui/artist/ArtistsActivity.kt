package com.flatcode.littlemusic.ui.artist

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.littlemusic.R
import com.flatcode.littlemusic.databinding.ActivityArtistsBinding
import com.flatcode.littlemusic.utils.DATA
import com.flatcode.littlemusic.utils.checkInterested
import com.flatcode.littlemusic.utils.openActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber


@AndroidEntryPoint
class ArtistsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArtistsBinding
    private val viewModel: ArtistsViewModel by viewModels()
    private var adapter: ArtistAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityArtistsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Timber.i("ArtistsActivity Created")

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(
                top = insets.top, bottom = insets.bottom
            )
            windowInsets
        }

        setupToolbar()
        setupRecyclerView()
        setupSwitchBar()
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
        binding.toolbar.nameSpace.setText(R.string.artists)
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
                    //None
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun setupRecyclerView() {
        adapter = ArtistAdapter(onItemClick = { artist ->
            openActivity<ArtistSongsActivity>(
                extras = arrayOf(
                    DATA.ARTIST_ID to artist.id,
                    DATA.ARTIST_NAME to artist.name,
                    DATA.ARTIST_IMAGE to artist.image,
                    DATA.ARTIST_ABOUT to artist.aboutTheArtist
                )
            )
        }, onInterestedClick = { artist, view ->
            (view as? ImageView)?.checkInterested(
                DATA.ARTISTS, artist.id
            )
        })
        binding.recyclerView.adapter = adapter
    }

    private fun setupSwitchBar() {
        binding.switchBar.all.setOnClickListener { viewModel.setType(DATA.TIMESTAMP) }
        binding.switchBar.mostSongs.setOnClickListener { viewModel.setType(DATA.SONGS_COUNT) }
        binding.switchBar.mostAlbums.setOnClickListener { viewModel.setType(DATA.ALBUMS_COUNT) }
        binding.switchBar.mostInterested.setOnClickListener { viewModel.setType(DATA.INTERESTED_COUNT) }
        binding.switchBar.name.setOnClickListener { viewModel.setType(DATA.NAME) }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.artists.collect { artists ->
                        adapter?.setList(artists)
                        binding.toolbar.number.text = getString(R.string.count_format, artists.size)

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


    override fun onResume() {
        super.onResume()
        viewModel.getData()
    }
}