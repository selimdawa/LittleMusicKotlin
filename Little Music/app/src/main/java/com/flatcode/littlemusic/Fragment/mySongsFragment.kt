package com.flatcode.littlemusic.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.jean.jcplayer.model.JcAudio
import com.flatcode.littlemusic.Adapterimport.SongAdapter
import com.flatcode.littlemusic.Modelimport.Song
import com.flatcode.littlemusic.Unitimport.DATA
import com.flatcode.littlemusic.databinding.FragmentMySongsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class mySongsFragment : Fragment() {

    private var binding: FragmentMySongsBinding? = null
    var check: MutableList<String?>? = null
    var list: ArrayList<Song?>? = null
    var adapter: SongAdapter? = null
    var isPlaying = false
    var jcAudios: ArrayList<JcAudio>? = null
    private var currentSong = 0
    private var type: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMySongsBinding.inflate(LayoutInflater.from(context), container, false)

        type = DATA.TIMESTAMP
        DB = DATA.ARTISTS

        init()
        binding!!.switchBar.all.setOnClickListener {
            type = DATA.TIMESTAMP
            init()
            getData(type, DB)
        }
        binding!!.switchBar.mostViews.setOnClickListener {
            type = DATA.VIEWS_COUNT
            init()
            getData(type, DB)
        }
        binding!!.switchBar.mostLoves.setOnClickListener {
            type = DATA.LOVES_COUNT
            init()
            getData(type, DB)
        }
        binding!!.switchBar.name.setOnClickListener {
            type = DATA.NAME
            init()
            getData(type, DB)
        }
        //Switch Type
        binding!!.switchBar.artists.setOnClickListener {
            type = DATA.NAME
            init()
            DB = DATA.ARTISTS
            getData(type, DB)
        }
        binding!!.switchBar.albums.setOnClickListener {
            type = DATA.NAME
            init()
            DB = DATA.ALBUMS
            getData(type, DB)
        }
        binding!!.switchBar.categories.setOnClickListener {
            type = DATA.NAME
            init()
            DB = DATA.CATEGORIES
            getData(type, DB)
        }
        return binding!!.root
    }

    private fun init() {
        //binding.recyclerView.setHasFixedSize(true);
        list = ArrayList()
        jcAudios = ArrayList()
        binding!!.recyclerView.adapter = adapter
        adapter = SongAdapter(context, list!!) { songs: Song?, position: Int ->
            changeSelectedSong(position)
            binding!!.player.jcPlayer.playAudio(jcAudios!![position])
            binding!!.player.jcPlayer.visibility = View.VISIBLE
        }
    }

    private fun getData(orderBy: String?, typeDB: String?) {
        check = ArrayList()
        val reference = FirebaseDatabase.getInstance().getReference(DATA.INTERESTED)
            .child(DATA.FirebaseUserUid)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                (check as ArrayList<String?>).clear()
                for (snapshot in dataSnapshot.child(typeDB!!).children) (check as ArrayList<String?>).add(
                    snapshot.key
                )
                getItems(orderBy, typeDB)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun getItems(orderBy: String?, typeDB: String?) {
        changeSelectedSong(-1)
        val ref: Query = FirebaseDatabase.getInstance().getReference(DATA.SONGS)
        ref.orderByChild(orderBy!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                list!!.clear()
                for (snapshot in dataSnapshot.children) {
                    val song = snapshot.getValue(Song::class.java)
                    for (id in check!!) {
                        assert(song != null)
                        if (song!!.id != null) {
                            if (typeDB == DATA.ARTISTS) {
                                if (song.artistId == id) {
                                    list!!.add(song)
                                    song.key = (snapshot.key)
                                    currentSong = -1
                                    isPlaying = true
                                    jcAudios!!.add(
                                        JcAudio.createFromURL(song.name!!, song.songLink!!)
                                    )
                                    binding!!.recyclerView.adapter = adapter
                                }
                            } else if (typeDB == DATA.ALBUMS) {
                                if (song.albumId == id) {
                                    list!!.add(song)
                                    song.key = (snapshot.key)
                                    currentSong = -1
                                    isPlaying = true
                                    jcAudios!!.add(
                                        JcAudio.createFromURL(song.name!!, song.songLink!!)
                                    )
                                    binding!!.recyclerView.adapter = adapter
                                }
                            } else if (typeDB == DATA.CATEGORIES) {
                                if (song.categoryId == id) {
                                    list!!.add(song)
                                    song.key = (snapshot.key)
                                    currentSong = -1
                                    isPlaying = true
                                    jcAudios!!.add(
                                        JcAudio.createFromURL(song.name!!, song.songLink!!)
                                    )
                                    binding!!.recyclerView.adapter = adapter
                                }
                            }
                        }
                    }
                }
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
                    Toast.makeText(context, "There is no songs!", Toast.LENGTH_SHORT).show()
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

    override fun onPause() {
        binding!!.player.jcPlayer.pause()
        super.onPause()
    }

    override fun onStop() {
        binding!!.player.jcPlayer.pause()
        super.onStop()
    }

    override fun onResume() {
        getData(type, DATA.ARTISTS)
        super.onResume()
    }

    companion object {
        private var DB: String? = null
    }
}