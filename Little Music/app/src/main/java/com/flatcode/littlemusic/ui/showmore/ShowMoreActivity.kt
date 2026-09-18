package com.flatcode.littlemusic.ui.showmore

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
import androidx.recyclerview.widget.RecyclerView
import com.example.jean.jcplayer.model.JcAudio
import com.flatcode.littlemusic.ui.song.SongAdapter
import com.flatcode.littlemusic.utils.DATA
import com.flatcode.littlemusic.databinding.ActivityShowMoreBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.MessageFormat

@AndroidEntryPoint
class ShowMoreActivity : AppCompatActivity() {

    private var binding: ActivityShowMoreBinding? = null
    private val viewModel: ShowMoreViewModel by viewModels()
    private var adapter: SongAdapter? = null
    private val jcAudios = ArrayList<JcAudio>()
    
    private var type: String? = null
    private var name: String? = null
    private var isReverse: String? = null
    private var recyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityShowMoreBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        Timber.i("ShowMoreActivity Created")

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

        type = intent.getStringExtra(DATA.SHOW_MORE_TYPE)
        name = intent.getStringExtra(DATA.SHOW_MORE_NAME)
        isReverse = intent.getStringExtra(DATA.SHOW_MORE_BOOLEAN)

        setupToolbar()
        setupRecyclerView()
        observeViewModel()

        viewModel.getData(type)
    }

    private fun setupToolbar() {
        binding!!.toolbar.nameSpace.text = name
        binding!!.toolbar.back.setOnClickListener { onBackPressed() }
        binding!!.toolbar.close.setOnClickListener { onBackPressed() }

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
                } catch (e: Exception) {}
            }
            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun setupRecyclerView() {
        if (isReverse == "true") {
            recyclerView = binding!!.recyclerViewReverse
        } else {
            recyclerView = binding!!.recyclerView
        }
        
        adapter = SongAdapter(this, ArrayList()) { _, position ->
            changeSelectedSong(position)
            binding!!.player.jcPlayer.playAudio(jcAudios[position])
            binding!!.player.jcPlayer.visibility = View.VISIBLE
        }
        recyclerView!!.adapter = adapter
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
                            recyclerView!!.visibility = View.VISIBLE
                            binding!!.emptyText.visibility = View.GONE
                            binding!!.player.jcPlayer.initPlaylist(jcAudios, null)
                        } else {
                            recyclerView!!.visibility = View.GONE
                            binding!!.emptyText.visibility = View.VISIBLE
                            Toast.makeText(this@ShowMoreActivity, "There are no songs!", Toast.LENGTH_SHORT).show()
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

    override fun onBackPressed() {
        if (DATA.searchStatus) {
            binding!!.toolbar.toolbar.visibility = View.VISIBLE
            binding!!.toolbar.toolbarSearch.visibility = View.GONE
            DATA.searchStatus = false
            binding!!.toolbar.textSearch.setText(DATA.EMPTY)
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

    override fun onResume() {
        super.onResume()
        viewModel.getData(type)
    }
}