package com.flatcode.littlemusic.Activity

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.jean.jcplayer.model.JcAudio
import com.flatcode.littlemusic.Adapterimport.AlbumAdapter
import com.flatcode.littlemusic.Adapterimport.SongAdapter
import com.flatcode.littlemusic.Modelimport.Album
import com.flatcode.littlemusic.Modelimport.Song
import com.flatcode.littlemusic.Unit.VOID
import com.flatcode.littlemusic.Unitimport.DATA
import com.flatcode.littlemusic.Unitimport.THEME
import com.flatcode.littlemusic.databinding.ActivityCategorySongsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import java.text.MessageFormat

class CategorySongsActivity : AppCompatActivity() {

    private var binding: ActivityCategorySongsBinding? = null
    var activity: Activity = this@CategorySongsActivity
    var albumList: ArrayList<Album?>? = null
    var albumAdapter: AlbumAdapter? = null
    var songList: ArrayList<Song?>? = null
    var songAdapter: SongAdapter? = null
    var isPlaying = false
    var isAlbum = true
    var isSong = false
    var jcAudios: ArrayList<JcAudio>? = null
    private var currentSong = 0
    var categoryId: String? = null
    var categoryName: String? = null
    var type: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        THEME.setThemeOfApp(activity)
        super.onCreate(savedInstanceState)
        binding = ActivityCategorySongsBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        categoryId = intent.getStringExtra(DATA.CATEGORY_ID)
        categoryName = intent.getStringExtra(DATA.CATEGORY_NAME)

        binding!!.toolbar.nameSpace.text = categoryName
        binding!!.toolbar.back.setOnClickListener { onBackPressed() }
        binding!!.toolbar.close.setOnClickListener { onBackPressed() }
        binding!!.switchBarAlbums.scrollSwitch.visibility = View.VISIBLE
        type = DATA.TIMESTAMP

        binding!!.toolbar.search.setOnClickListener {
            binding!!.toolbar.toolbar.visibility = View.GONE
            binding!!.toolbar.toolbarSearch.visibility = View.VISIBLE
            DATA.searchStatus = true
        }

        VOID.isInterested(binding!!.switchBarSongs.interest, categoryId, DATA.CATEGORIES)
        binding!!.switchBarSongs.add.setOnClickListener {
            VOID.checkInterested(binding!!.switchBarSongs.interest, DATA.CATEGORIES, categoryId)
        }

        VOID.isInterested(binding!!.switchBarAlbums.interest, categoryId, DATA.CATEGORIES)
        binding!!.switchBarAlbums.add.setOnClickListener {
            VOID.checkInterested(binding!!.switchBarAlbums.interest, DATA.CATEGORIES, categoryId)
        }

        binding!!.toolbar.textSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                try {
                    if (isAlbum) albumAdapter!!.filter.filter(s) else if (isSong) songAdapter!!.filter.filter(
                        s
                    )
                } catch (e: Exception) {
                    //None
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        //binding.recyclerView.setHasFixedSize(true);
        albumList = ArrayList()
        albumAdapter = AlbumAdapter(activity, albumList!!)
        binding!!.recyclerAlbums.adapter = albumAdapter

        init()

        binding!!.switchBarAlbums.songs.setOnClickListener {
            binding!!.switchBarAlbums.scrollSwitch.visibility = View.GONE
            binding!!.switchBarSongs.scrollSwitch.visibility = View.VISIBLE
            binding!!.player.jcPlayer.pause()
            binding!!.player.jcPlayer.visibility = View.GONE
            albumList!!.clear()
            init()
            getSongs(type)
            isAlbum = false
            isSong = true
            if (DATA.searchStatus) onBackPressed()
        }
        //Albums
        binding!!.switchBarAlbums.all.setOnClickListener {
            type = DATA.TIMESTAMP
            getAlbums(type)
        }
        binding!!.switchBarAlbums.mostSongs.setOnClickListener {
            type = DATA.SONGS_COUNT
            getAlbums(type)
        }
        binding!!.switchBarAlbums.mostInterested.setOnClickListener {
            type = DATA.INTERESTED_COUNT
            getAlbums(type)
        }
        binding!!.switchBarAlbums.name.setOnClickListener {
            type = DATA.NAME
            getAlbums(type)
        }
        //Songs
        binding!!.switchBarSongs.albums.setOnClickListener {
            binding!!.switchBarSongs.scrollSwitch.visibility = View.GONE
            binding!!.switchBarAlbums.scrollSwitch.visibility = View.VISIBLE
            songList!!.clear()
            getAlbums(type)
            isAlbum = true
            isSong = false
            if (DATA.searchStatus) onBackPressed()
        }
        binding!!.switchBarSongs.all.setOnClickListener {
            type = DATA.TIMESTAMP
            init()
            getSongs(type)
        }
        binding!!.switchBarSongs.mostViews.setOnClickListener {
            type = DATA.VIEWS_COUNT
            init()
            getSongs(type)
        }
        binding!!.switchBarSongs.mostLoves.setOnClickListener {
            type = DATA.LOVES_COUNT
            init()
            getSongs(type)
        }
        binding!!.switchBarSongs.name.setOnClickListener {
            type = DATA.NAME
            init()
            getSongs(type)
        }
        getAlbums(type)
    }

    private fun init() {
        //binding.recyclerView.setHasFixedSize(true);
        songList = ArrayList()
        jcAudios = ArrayList()
        binding!!.recyclerSongs.adapter = songAdapter

        songAdapter = SongAdapter(activity, songList!!) { songs: Song?, position: Int ->
            changeSelectedSong(position)
            binding!!.player.jcPlayer.playAudio(jcAudios!![position])
            binding!!.player.jcPlayer.visibility = View.VISIBLE
        }
    }

    private fun getAlbums(orderBy: String?) {
        binding!!.recyclerSongs.visibility = View.GONE
        val ref: Query = FirebaseDatabase.getInstance().getReference(DATA.ALBUMS)
        ref.orderByChild(orderBy!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                albumList!!.clear()
                var i = 0
                for (data in dataSnapshot.children) {
                    val item = data.getValue(Album::class.java)!!
                    if (item.categoryId == categoryId) {
                        albumList!!.add(item)
                        i++
                    }
                }
                albumList!!.reverse()
                binding!!.toolbar.number.text = MessageFormat.format("( {0} )", i)
                albumAdapter!!.notifyDataSetChanged()
                binding!!.progress.visibility = View.GONE
                if (albumList!!.isNotEmpty()) {
                    binding!!.recyclerAlbums.visibility = View.VISIBLE
                    binding!!.emptyText.visibility = View.GONE
                } else {
                    binding!!.recyclerAlbums.visibility = View.GONE
                    binding!!.emptyText.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun getSongs(orderBy: String?) {
        changeSelectedSong(-1)
        binding!!.recyclerAlbums.visibility = View.GONE
        val ref: Query = FirebaseDatabase.getInstance().getReference(DATA.SONGS)
        ref.orderByChild(orderBy!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                songList!!.clear()
                var i = 0
                for (data in dataSnapshot.children) {
                    val item = data.getValue(Song::class.java)!!
                    if (item.id != null) {
                        if (item.categoryId == categoryId) {
                            songList!!.add(item)
                            item.key = (data.key)
                            currentSong = -1
                            isPlaying = true
                            jcAudios!!.add(JcAudio.createFromURL(item.name!!, item.songLink!!))
                            i++
                            binding!!.toolbar.number.text = MessageFormat.format("( {0} )", i)
                            binding!!.recyclerSongs.adapter = songAdapter
                        }
                    }
                }
                binding!!.toolbar.number.text = MessageFormat.format("( {0} )", i)
                songAdapter!!.notifyDataSetChanged()
                binding!!.progress.visibility = View.GONE
                if (songList!!.isNotEmpty()) {
                    binding!!.recyclerSongs.visibility = View.VISIBLE
                    binding!!.emptyText.visibility = View.GONE
                } else {
                    binding!!.recyclerSongs.visibility = View.GONE
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
        songAdapter!!.notifyItemChanged(songAdapter!!.selectedPosition)
        currentSong = index
        songAdapter!!.selectedPosition = currentSong
        songAdapter!!.notifyItemChanged(currentSong)
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

    override fun onRestart() {
        getAlbums(type)
        super.onRestart()
    }

    override fun onResume() {
        getAlbums(type)
        super.onResume()
    }
}