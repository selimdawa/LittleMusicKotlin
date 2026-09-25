package com.flatcode.littlemusicadmin.ui.album

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import com.flatcode.littlemusicadmin.utils.BaseActivity
import androidx.lifecycle.lifecycleScope
import com.flatcode.littlemusicadmin.R
import com.flatcode.littlemusicadmin.databinding.ActivityAlbumsBinding
import com.flatcode.littlemusicadmin.ui.song.AlbumSongsActivity
import com.flatcode.littlemusicadmin.utils.DATA
import com.flatcode.littlemusicadmin.utils.moreDelete
import com.flatcode.littlemusicadmin.utils.openActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.MessageFormat

@AndroidEntryPoint
class AlbumsActivity : BaseActivity() {

    private lateinit var binding: ActivityAlbumsBinding
    private var adapter: AlbumAdapter? = null
    private val viewModel: AlbumsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlbumsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.nameSpace.setText(R.string.albums)
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

        adapter = AlbumAdapter(onItemClick = { album ->
            openActivity<AlbumSongsActivity>(
                extras = arrayOf(
                    DATA.ALBUM_ID to album.id,
                    DATA.ALBUM_NAME to album.name,
                    DATA.ALBUM_IMAGE to album.image
                )
            )
        }, onMoreClick = { album ->
            album.moreDelete(
                this,
                DATA.ARTISTS,
                album.artistId,
                DATA.ALBUMS_COUNT,
                DATA.CATEGORIES,
                album.categoryId,
                DATA.ALBUMS_COUNT,
                DATA.NULL,
                DATA.NULL,
                DATA.NULL
            )
        })
        binding.recyclerView.adapter = adapter

        binding.switchBar.all.setOnClickListener { viewModel.setOrderBy(DATA.TIMESTAMP) }
        binding.switchBar.mostSongs.setOnClickListener { viewModel.setOrderBy(DATA.SONGS_COUNT) }
        binding.switchBar.mostInterested.setOnClickListener { viewModel.setOrderBy(DATA.INTERESTED_COUNT) }
        binding.switchBar.name.setOnClickListener { viewModel.setOrderBy(DATA.NAME) }

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.albums.collectLatest { albums ->
                binding.toolbar.number.text = MessageFormat.format("( {0} )", albums.size)
                adapter!!.submitList(albums)

                if (albums.isNotEmpty()) {
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.emptyText.visibility = View.GONE
                } else {
                    binding.recyclerView.visibility = View.GONE
                    binding.emptyText.visibility = View.VISIBLE
                }
                Timber.d("Albums updated: ${albums.size}")
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                binding.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
    }


}
