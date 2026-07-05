package com.flatcode.littlemusic.activity

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.jean.jcplayer.model.JcAudio
import com.flatcode.littlemusic.adapter.SongAdapter
import com.flatcode.littlemusic.model.Song
import com.flatcode.littlemusic.utils.VOID
import com.flatcode.littlemusic.utils.DATA
import com.flatcode.littlemusic.utils.THEME
import com.flatcode.littlemusic.databinding.ActivityAlbumSongsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import java.text.MessageFormat

class AlbumSongsActivity : AppCompatActivity() {

    private var binding: ActivityAlbumSongsBinding? = null
    private val activity: Activity = this

    private val list = ArrayList<Song?>()
    private var adapter: SongAdapter? = null
    private var isPlaying = false
    private val jcAudios = ArrayList<JcAudio>()
    private var currentSong = 0
    private var albumId: String? = null
    private var albumName: String? = null
    private var albumImage: String? = null
    private var type: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        THEME.setThemeOfApp(activity)
        super.onCreate(savedInstanceState)
        binding = ActivityAlbumSongsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        albumId = intent.getStringExtra(DATA.ALBUM_ID)
        albumName = intent.getStringExtra(DATA.ALBUM_NAME)
        albumImage = intent.getStringExtra(DATA.ALBUM_IMAGE)

        type = DATA.TIMESTAMP

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
            VOID.GlideImage(false, activity, albumImage, b.image)
            VOID.GlideBlur(false, activity, albumImage, b.imageBlur, 50)

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
                override fun beforeTextChanged(
                    s: CharSequence, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    try {
                        adapter?.filter?.filter(s)
                    } catch (_: Exception) {
                        // Suppressed
                    }
                }

                override fun afterTextChanged(s: Editable) {}
            })

            b.switchBar.all.setOnClickListener { switchType(DATA.TIMESTAMP) }
            b.switchBar.mostViews.setOnClickListener { switchType(DATA.VIEWS_COUNT) }
            b.switchBar.mostLoves.setOnClickListener { switchType(DATA.LOVES_COUNT) }
            b.switchBar.name.setOnClickListener { switchType(DATA.NAME) }
        }
        init()
    }

    private fun switchType(newType: String) {
        type = newType
        init()
        getData(type)
    }

    private fun init() {
        list.clear()
        jcAudios.clear()

        adapter = SongAdapter(activity, list) { _, position ->
            changeSelectedSong(position)
            binding?.player?.jcPlayer?.playAudio(jcAudios[position])
            binding?.player?.jcPlayer?.visibility = View.VISIBLE
        }
        binding!!.recyclerView.adapter = adapter
    }

    private fun getData(orderBy: String?) {
        if (orderBy == null) return
        changeSelectedSong(-1)

        val ref: Query = FirebaseDatabase.getInstance().getReference(DATA.SONGS)
        ref.orderByChild(orderBy).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                list.clear()
                jcAudios.clear()
                var songCount = 0

                for (data in dataSnapshot.children) {
                    val item = data.getValue(Song::class.java) ?: continue
                    if (item.id != null && item.albumId == albumId) {
                        list.add(item)
                        item.key = data.key
                        currentSong = -1
                        isPlaying = true
                        jcAudios.add(JcAudio.createFromURL(item.name ?: "", item.songLink ?: ""))
                        songCount++
                    }
                }

                binding?.let { b ->
                    b.toolbar.number.text = MessageFormat.format("( {0} )", songCount)
                    adapter?.notifyDataSetChanged()
                    b.progress.visibility = View.GONE

                    if (list.isNotEmpty()) {
                        b.recyclerView.visibility = View.VISIBLE
                        b.emptyText.visibility = View.GONE
                        b.player.jcPlayer.initPlaylist(jcAudios, null)
                    } else {
                        b.recyclerView.visibility = View.GONE
                        b.emptyText.visibility = View.VISIBLE
                        Toast.makeText(activity, "There are no songs!", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun changeSelectedSong(index: Int) {
        adapter?.let {
            val previousSelected = it.selectedPosition
            it.selectedPosition = index
            currentSong = index
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

    override fun onRestart() {
        super.onRestart()
        getData(type)
    }

    override fun onResume() {
        super.onResume()
        getData(type)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}