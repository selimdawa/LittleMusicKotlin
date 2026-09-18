package com.flatcode.littlemusicadmin.ui.song

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.jean.jcplayer.model.JcAudio
import com.flatcode.littlemusicadmin.model.Song
import com.flatcode.littlemusicadmin.R
import com.flatcode.littlemusicadmin.utils.DATA
import com.flatcode.littlemusicadmin.databinding.ActivityPageSongSwitchBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.MessageFormat
import timber.log.Timber

@AndroidEntryPoint
class SongsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPageSongSwitchBinding
    var activity: Activity = this@SongsActivity
    var list: ArrayList<Song?>? = null
    var adapter: SongAdapter? = null
    var jcAudios: ArrayList<JcAudio>? = null
    private var currentSong = 0
    private val viewModel: SongsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityPageSongSwitchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.nameSpace.setText(R.string.songs)
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
                    adapter!!.filter.filter(s)
                } catch (e: Exception) {
                    Timber.e(e, "Error filtering songs")
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        init()

        binding.switchBar.all.setOnClickListener {
            viewModel.setOrderBy(DATA.TIMESTAMP)
        }
        binding.switchBar.mostViews.setOnClickListener {
            viewModel.setOrderBy(DATA.VIEWS_COUNT)
        }
        binding.switchBar.mostLoves.setOnClickListener {
            viewModel.setOrderBy(DATA.LOVES_COUNT)
        }
        binding.switchBar.name.setOnClickListener {
            viewModel.setOrderBy(DATA.NAME)
        }

        observeViewModel()
    }

    private fun init() {
        list = ArrayList()
        jcAudios = ArrayList()
        adapter = SongAdapter(activity, list!!) { _, position: Int ->
            changeSelectedSong(position)
            binding.player.jcPlayer.playAudio(jcAudios!![position])
            binding.player.jcPlayer.visibility = View.VISIBLE
        }
        binding.recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.songs.collectLatest { songs ->
                changeSelectedSong(-1)
                list!!.clear()
                jcAudios!!.clear()
                var i = 0
                for (item in songs) {
                    list!!.add(item)
                    val name = item.name
                    val songLink = item.songLink
                    if (name != null && songLink != null) {
                        jcAudios!!.add(JcAudio.createFromURL(name, songLink))
                    }
                    i++
                }
                binding.toolbar.number.text = MessageFormat.format("( {0} )", i)
                adapter!!.notifyDataSetChanged()

                if (list!!.isNotEmpty()) {
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.emptyText.visibility = View.GONE
                    binding.player.jcPlayer.initPlaylist(jcAudios!!, null)
                } else {
                    binding.recyclerView.visibility = View.GONE
                    binding.emptyText.visibility = View.VISIBLE
                    Toast.makeText(activity, "There is no songs!", Toast.LENGTH_SHORT).show()
                }
                Timber.d("Songs updated: ${songs.size}")
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                binding.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
    }

    fun changeSelectedSong(index: Int) {
        if (adapter != null) {
            adapter!!.notifyItemChanged(adapter!!.selectedPosition)
            currentSong = index
            adapter!!.selectedPosition = currentSong
            adapter!!.notifyItemChanged(currentSong)
        }
    }

    override fun onBackPressed() {
        if (DATA.searchStatus) {
            binding.toolbar.toolbar.visibility = View.VISIBLE
            binding.toolbar.toolbarSearch.visibility = View.GONE
            DATA.searchStatus = false
            binding.toolbar.textSearch.setText(DATA.EMPTY)
        } else if (DATA.isChange) {
            onResume()
            DATA.isChange = false
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
    }

    override fun onRestart() {
        super.onRestart()
    }
}