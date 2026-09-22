package com.flatcode.littlemusicadmin.ui.artist
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.flatcode.littlemusicadmin.R
import com.flatcode.littlemusicadmin.databinding.ActivityArtistsBinding
import com.flatcode.littlemusicadmin.ui.song.ArtistSongsActivity
import com.flatcode.littlemusicadmin.utils.DATA
import com.flatcode.littlemusicadmin.utils.moreDelete
import com.flatcode.littlemusicadmin.utils.openActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.MessageFormat

@AndroidEntryPoint
class ArtistsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArtistsBinding
    private var adapter: ArtistAdapter? = null
    private val viewModel: ArtistsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityArtistsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.nameSpace.setText(R.string.artists)
        binding.toolbar.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.toolbar.close.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
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
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })

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
            onMoreClick = { artist ->
                artist.moreDelete(
                    this, DATA.NULL, DATA.NULL, DATA.NULL,
                    DATA.NULL, DATA.NULL, DATA.NULL,
                    DATA.NULL, DATA.NULL, DATA.NULL
                )
            }
        )
        binding.recyclerView.adapter = adapter

        binding.switchBar.all.setOnClickListener { viewModel.setOrderBy(DATA.TIMESTAMP) }
        binding.switchBar.mostSongs.setOnClickListener { viewModel.setOrderBy(DATA.SONGS_COUNT) }
        binding.switchBar.mostAlbums.setOnClickListener { viewModel.setOrderBy(DATA.ALBUMS_COUNT) }
        binding.switchBar.mostInterested.setOnClickListener { viewModel.setOrderBy(DATA.INTERESTED_COUNT) }
        binding.switchBar.name.setOnClickListener { viewModel.setOrderBy(DATA.NAME) }

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.artists.collectLatest { artists ->
                binding.toolbar.number.text = MessageFormat.format("( {0} )", artists.size)
                adapter!!.submitList(artists)

                if (artists.isNotEmpty()) {
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.emptyText.visibility = View.GONE
                } else {
                    binding.recyclerView.visibility = View.GONE
                    binding.emptyText.visibility = View.VISIBLE
                }
                Timber.d("Artists updated: ${artists.size}")
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                binding.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
    }


}