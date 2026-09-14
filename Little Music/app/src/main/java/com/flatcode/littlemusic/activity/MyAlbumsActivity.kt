package com.flatcode.littlemusic.activity

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.littlemusic.adapter.AlbumAdapter
import com.flatcode.littlemusic.model.Album
import com.flatcode.littlemusic.R
import com.flatcode.littlemusic.utils.VOID
import com.flatcode.littlemusic.utils.CLASS
import com.flatcode.littlemusic.utils.DATA
import com.flatcode.littlemusic.databinding.ActivityMyAlbumsBinding
import com.flatcode.littlemusic.viewmodel.MyAlbumsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.MessageFormat

@AndroidEntryPoint
class MyAlbumsActivity : AppCompatActivity() {

    private var binding: ActivityMyAlbumsBinding? = null
    private val viewModel: MyAlbumsViewModel by viewModels()
    private var adapter: AlbumAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyAlbumsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        Timber.i("MyAlbumsActivity Created")

        setupToolbar()
        setupSwitchBar()
        setupRecyclerView()
        observeViewModel()

        viewModel.getData()
    }

    private fun setupToolbar() {
        binding!!.toolbar.nameSpace.setText(R.string.my_albums)
        binding!!.toolbar.close.setOnClickListener { onBackPressed() }
        binding!!.toolbar.back.setOnClickListener { onBackPressed() }

        binding!!.toolbar.search.setOnClickListener {
            binding!!.toolbar.toolbar.visibility = View.GONE
            binding!!.toolbar.toolbarSearch.visibility = View.VISIBLE
            DATA.searchStatus = true
        }

        binding!!.toolbar.textSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                try {
                    adapter!!.filter.filter(s)
                } catch (_: Exception) {}
            }
            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun setupSwitchBar() {
        binding!!.switchBar.explore.setOnClickListener { VOID.Intent1(this, CLASS.ALBUMS) }
        binding!!.switchBar.all.setOnClickListener { viewModel.setType(DATA.TIMESTAMP) }
        binding!!.switchBar.mostSongs.setOnClickListener { viewModel.setType(DATA.SONGS_COUNT) }
        binding!!.switchBar.mostInterested.setOnClickListener { viewModel.setType(DATA.INTERESTED_COUNT) }
        binding!!.switchBar.name.setOnClickListener { viewModel.setType(DATA.NAME) }
    }

    private fun setupRecyclerView() {
        adapter = AlbumAdapter(this, ArrayList())
        binding!!.recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.albums.collect { albums ->
                        adapter?.list?.clear()
                        adapter?.list?.addAll(albums)
                        adapter?.notifyDataSetChanged()
                        binding!!.toolbar.number.text = MessageFormat.format("( {0} )", albums.size)
                        
                        if (albums.isNotEmpty()) {
                            binding!!.recyclerView.visibility = View.VISIBLE
                            binding!!.emptyText.visibility = View.GONE
                        } else {
                            binding!!.recyclerView.visibility = View.GONE
                            binding!!.emptyText.visibility = View.VISIBLE
                        }
                    }
                }
                launch {
                    viewModel.isLoading.collect { isLoading ->
                        binding!!.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        if (DATA.searchStatus) {
            binding!!.toolbar.toolbar.visibility = View.VISIBLE
            binding!!.toolbar.toolbarSearch.visibility = View.GONE
            DATA.searchStatus = false
            binding!!.toolbar.textSearch.setText(DATA.EMPTY)
        } else super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getData()
    }
}