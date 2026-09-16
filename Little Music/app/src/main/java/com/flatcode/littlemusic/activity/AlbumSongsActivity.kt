package com.flatcode.littlemusic.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
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
import com.flatcode.littlemusic.adapter.SongAdapter
import com.flatcode.littlemusic.model.Song
import com.flatcode.littlemusic.utils.VOID
import com.flatcode.littlemusic.utils.DATA
import com.flatcode.littlemusic.databinding.ActivityAlbumSongsBinding
import com.flatcode.littlemusic.viewmodel.AlbumSongsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.MessageFormat

@AndroidEntryPoint
class AlbumSongsActivity : AppCompatActivity() {

    private var binding: ActivityAlbumSongsBinding? = null
    private val viewModel: AlbumSongsViewModel by viewModels()
    private var adapter: SongAdapter? = null
    private val jcAudios = ArrayList<JcAudio>()
    private var albumId: String? = null
    private var albumName: String? = null
    private var albumImage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityAlbumSongsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        Timber.i("AlbumSongsActivity Created")

        ViewCompat.setOnApplyWindowInsetsListener(binding!!.toolbar.root) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = insets.top
            }
            windowInsets
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding!!.player.root) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(bottom = insets.bottom)
            windowInsets
        }

        albumId = intent.getStringExtra(DATA.ALBUM_ID)
        albumName = intent.getStringExtra(DATA.ALBUM_NAME)
        albumImage = intent.getStringExtra(DATA.ALBUM_IMAGE)

        setupToolbar()
        setupSwitchBar()
        setupRecyclerView()
        observeViewModel()

        viewModel.getData(albumId)
    }

    private fun setupToolbar() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (DATA.searchStatus) {
                    binding?.let { b ->
                        b.toolbar.toolbar.visibility = View.VISIBLE
                        b.toolbar.toolbarSearch.visibility = View.GONE
                        DATA.searchStatus = false
                        b.toolbar.textSearch.setText(DATA.EMPTY)
                    }
                } else {
                    finish()
                }
            }
        })

        binding?.let { b ->
            VOID.GlideImage(false, this, albumImage, b.image)
            VOID.GlideBlur(false, this, albumImage, b.imageBlur, 50)

            b.toolbar.nameSpace.text = albumName
            b.toolbar.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
            b.toolbar.close.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

            b.toolbar.search.setOnClickListener {
                b.toolbar.toolbar.visibility = View.GONE
                b.toolbar.toolbarSearch.visibility = View.VISIBLE
                DATA.searchStatus = true
            }

            VOID.isInterested(b.switchBar.interest, albumId, DATA.ALBUMS)
            b.switchBar.add.setOnClickListener {
                VOID.checkInterested(b.switchBar.interest, DATA.ALBUMS, albumId)
            }

            b.toolbar.textSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    try {
                        adapter?.filter?.filter(s)
                    } catch (_: Exception) {}
                }
                override fun afterTextChanged(s: Editable) {}
            })
        }
    }

    private fun setupSwitchBar() {
        binding?.let { b ->
            b.switchBar.all.setOnClickListener { viewModel.setType(DATA.TIMESTAMP, albumId!!) }
            b.switchBar.mostViews.setOnClickListener { viewModel.setType(DATA.VIEWS_COUNT, albumId!!) }
            b.switchBar.mostLoves.setOnClickListener { viewModel.setType(DATA.LOVES_COUNT, albumId!!) }
            b.switchBar.name.setOnClickListener { viewModel.setType(DATA.NAME, albumId!!) }
        }
    }

    private fun setupRecyclerView() {
        adapter = SongAdapter(this, ArrayList()) { _, position ->
            changeSelectedSong(position)
            binding?.player?.jcPlayer?.playAudio(jcAudios[position])
            binding?.player?.jcPlayer?.visibility = View.VISIBLE
        }
        binding!!.recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.songs.collect { songs ->
                        adapter?.list?.clear()
                        adapter?.list?.addAll(songs)
                        adapter?.notifyDataSetChanged()
                        binding!!.toolbar.number.text = MessageFormat.format("( {0} )", songs.size)
                        
                        jcAudios.clear()
                        songs.forEach { song ->
                            jcAudios.add(JcAudio.createFromURL(song.name ?: "", song.songLink ?: ""))
                        }

                        if (songs.isNotEmpty()) {
                            binding!!.recyclerView.visibility = View.VISIBLE
                            binding!!.emptyText.visibility = View.GONE
                            binding!!.player.jcPlayer.initPlaylist(jcAudios, null)
                        } else {
                            binding!!.recyclerView.visibility = View.GONE
                            binding!!.emptyText.visibility = View.VISIBLE
                            Toast.makeText(this@AlbumSongsActivity, "There are no songs!", Toast.LENGTH_SHORT).show()
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

    private fun changeSelectedSong(index: Int) {
        adapter?.let {
            val previousSelected = it.selectedPosition
            it.selectedPosition = index
            it.notifyItemChanged(previousSelected)
            it.notifyItemChanged(index)
        }
    }

    override fun onPause() {
        binding?.player?.jcPlayer?.pause()
        super.onPause()
    }

    override fun onStop() {
        binding?.player?.jcPlayer?.pause()
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getData(albumId)
    }
}