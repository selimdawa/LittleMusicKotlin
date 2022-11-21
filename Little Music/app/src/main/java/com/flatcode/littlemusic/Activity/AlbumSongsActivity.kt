package com.flatcode.littlemusic.Activity

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.jean.jcplayer.model.JcAudio
import com.flatcode.littlemusic.Adapterimport.SongAdapter
import com.flatcode.littlemusic.Modelimport.Song
import com.flatcode.littlemusic.Unit.VOID
import com.flatcode.littlemusic.Unitimport.DATAv
import com.flatcode.littlemusic.Unitimport.THEME
import com.flatcode.littlemusic.databinding.ActivityAlbumSongsBinding
import com.google.firebase.database.*
import java.text.MessageFormat

class AlbumSongsActivity : AppCompatActivity() {

    private var binding: ActivityAlbumSongsBinding? = null
    var activity: Activity = this@AlbumSongsActivity
    var list: ArrayList<Song?>? = null
    var adapter: SongAdapter? = null
    var isPlaying = false
    var jcAudios: ArrayList<JcAudio>? = null
    private var currentSong = 0
    var albumId: String? = null
    var albumName: String? = null
    var albumImage: String? = null
    var type: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        THEME.setThemeOfApp(activity)
        super.onCreate(savedInstanceState)
        binding = ActivityAlbumSongsBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        albumId = intent.getStringExtra(DATAv.ALBUM_ID)
        albumName = intent.getStringExtra(DATAv.ALBUM_NAME)
        albumImage = intent.getStringExtra(DATAv.ALBUM_IMAGE)

        type = DATAv.TIMESTAMP
        VOID.GlideImage(false, activity, albumImage, binding!!.image)
        VOID.GlideBlur(false, activity, albumImage, binding!!.imageBlur, 50)

        binding!!.toolbar.nameSpace.text = albumName
        binding!!.toolbar.search.setOnClickListener {
            binding!!.toolbar.toolbar.visibility = View.GONE
            binding!!.toolbar.toolbarSearch.visibility = View.VISIBLE
            DATAv.searchStatus = true
        }
        binding!!.toolbar.close.setOnClickListener { onBackPressed() }
        binding!!.toolbar.textSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                try {
                    adapter!!.filter.filter(s)
                } catch (e: Exception) {
                    //None
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        binding!!.toolbar.back.setOnClickListener { onBackPressed() }
        init()
        binding!!.switchBar.all.setOnClickListener {
            type = DATAv.TIMESTAMP
            init()
            getData(type)
        }
        binding!!.switchBar.mostViews.setOnClickListener {
            type = DATAv.VIEWS_COUNT
            init()
            getData(type)
        }
        binding!!.switchBar.mostLoves.setOnClickListener {
            type = DATAv.LOVES_COUNT
            init()
            getData(type)
        }
        binding!!.switchBar.name.setOnClickListener {
            type = DATAv.NAME
            init()
            getData(type)
        }
    }

    private fun init() {
        //binding.recyclerView.setHasFixedSize(true);
        list = ArrayList()
        jcAudios = ArrayList()
        binding!!.recyclerView.adapter = adapter
        adapter = SongAdapter(activity, list!!) { songs: Song?, position: Int ->
            changeSelectedSong(position)
            binding!!.player.jcPlayer.playAudio(jcAudios!![position])
            binding!!.player.jcPlayer.visibility = View.VISIBLE
        }
    }

    private fun getData(orderBy: String?) {
        changeSelectedSong(-1)
        val ref: Query = FirebaseDatabase.getInstance().getReference(DATAv.SONGS)
        ref.orderByChild(orderBy!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                list!!.clear()
                var i = 0
                for (data in dataSnapshot.children) {
                    val item = data.getValue(Song::class.java)!!
                    if (item.id != null) {
                        if (item.albumId == albumId) {
                            list!!.add(item)
                            item.key = (data.key)
                            currentSong = -1
                            isPlaying = true
                            jcAudios!!.add(
                                JcAudio.createFromURL(
                                    item.name!!,
                                    item.songLink!!
                                )
                            )
                            i++
                            binding!!.toolbar.number.text = MessageFormat.format("( {0} )", i)
                            binding!!.recyclerView.adapter = adapter
                        }
                    }
                }
                binding!!.toolbar.number.text = MessageFormat.format("( {0} )", i)
                adapter!!.notifyDataSetChanged()
                binding!!.progress.visibility = View.GONE
                if (list!!.isNotEmpty()) {
                    binding!!.recyclerView.visibility = View.VISIBLE
                    binding!!.emptyText.visibility = View.GONE
                } else {
                    binding!!.recyclerView.visibility = View.GONE
                    binding!!.emptyText.visibility = View.VISIBLE
                }
                if (isPlaying) {
                    binding!!.player.jcPlayer.initPlaylist(jcAudios!!, null)
                } else {
                    Toast.makeText(activity, "There is no songs!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun changeSelectedSong(index: Int) {
        adapter!!.notifyItemChanged(adapter!!.selectedPosition)
        currentSong = index
        adapter!!.selectedPosition = currentSong
        adapter!!.notifyItemChanged(currentSong)
    }

    override fun onBackPressed() {
        if (DATAv.searchStatus) {
            binding!!.toolbar.toolbar.visibility = View.VISIBLE
            binding!!.toolbar.toolbarSearch.visibility = View.GONE
            DATAv.searchStatus = false
            binding!!.toolbar.textSearch.setText(DATAv.EMPTY)
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

    override fun onRestart() {
        getData(type)
        super.onRestart()
    }

    override fun onResume() {
        getData(type)
        super.onResume()
    }
}