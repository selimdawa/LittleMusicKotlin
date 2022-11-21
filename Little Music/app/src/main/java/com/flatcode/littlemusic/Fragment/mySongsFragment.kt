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
import com.flatcode.littlemusic.Unitimport.DATAv
import com.flatcode.littlemusic.databinding.FragmentMySongsBinding
import com.google.firebase.database.*

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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMySongsBinding.inflate(
            LayoutInflater.from(
                context
            ), container, false
        )
        type = DATAv.TIMESTAMP
        DB = DATAv.ARTISTS
        init()
        binding!!.switchBar.all.setOnClickListener {
            type = DATAv.TIMESTAMP
            init()
            getData(type, DB)
        }
        binding!!.switchBar.mostViews.setOnClickListener {
            type = DATAv.VIEWS_COUNT
            init()
            getData(type, DB)
        }
        binding!!.switchBar.mostLoves.setOnClickListener {
            type = DATAv.LOVES_COUNT
            init()
            getData(type, DB)
        }
        binding!!.switchBar.name.setOnClickListener {
            type = DATAv.NAME
            init()
            getData(type, DB)
        }
        //Switch Type
        binding!!.switchBar.artists.setOnClickListener {
            type = DATAv.NAME
            init()
            DB = DATAv.ARTISTS
            getData(type, DB)
        }
        binding!!.switchBar.albums.setOnClickListener {
            type = DATAv.NAME
            init()
            DB = DATAv.ALBUMS
            getData(type, DB)
        }
        binding!!.switchBar.categories.setOnClickListener {
            type = DATAv.NAME
            init()
            DB = DATAv.CATEGORIES
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
        val reference = FirebaseDatabase.getInstance().getReference(DATAv.INTERESTED)
            .child(DATAv.FirebaseUserUid)
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
        val ref: Query = FirebaseDatabase.getInstance().getReference(DATAv.SONGS)
        ref.orderByChild(orderBy!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                list!!.clear()
                for (snapshot in dataSnapshot.children) {
                    val song = snapshot.getValue(Song::class.java)
                    for (id in check!!) {
                        assert(song != null)
                        if (song!!.id != null) {
                            if (typeDB == DATAv.ARTISTS) {
                                if (song.artistId == id) {
                                    list!!.add(song)
                                    song.key = (snapshot.key)
                                    currentSong = -1
                                    isPlaying = true
                                    jcAudios!!.add(
                                        JcAudio.createFromURL(
                                            song.name!!,
                                            song.songLink!!
                                        )
                                    )
                                    binding!!.recyclerView.adapter = adapter
                                }
                            } else if (typeDB == DATAv.ALBUMS) {
                                if (song.albumId == id) {
                                    list!!.add(song)
                                    song.key = (snapshot.key)
                                    currentSong = -1
                                    isPlaying = true
                                    jcAudios!!.add(
                                        JcAudio.createFromURL(
                                            song.name!!,
                                            song.songLink!!
                                        )
                                    )
                                    binding!!.recyclerView.adapter = adapter
                                }
                            } else if (typeDB == DATAv.CATEGORIES) {
                                if (song.categoryId == id) {
                                    list!!.add(song)
                                    song.key = (snapshot.key)
                                    currentSong = -1
                                    isPlaying = true
                                    jcAudios!!.add(
                                        JcAudio.createFromURL(
                                            song.name!!,
                                            song.songLink!!
                                        )
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
        getData(type, DATAv.ARTISTS)
        super.onResume()
    }

    companion object {
        private var DB: String? = null
    }
}