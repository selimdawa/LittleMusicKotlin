package com.flatcode.littlemusicadmin.Activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.flatcode.littlemusicadmin.Adapter.MainAdapter
import com.flatcode.littlemusicadmin.Model.Main
import com.flatcode.littlemusicadmin.Model.Song
import com.flatcode.littlemusicadmin.Model.User
import com.flatcode.littlemusicadmin.R
import com.flatcode.littlemusicadmin.Unit.CLASS
import com.flatcode.littlemusicadmin.Unit.DATA
import com.flatcode.littlemusicadmin.Unit.THEME
import com.flatcode.littlemusicadmin.Unit.VOID
import com.flatcode.littlemusicadmin.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity(), OnSharedPreferenceChangeListener {
    private var binding: ActivityMainBinding? = null

    var list: MutableList<Main>? = null
    var adapter: MainAdapter? = null
    var context: Context = this@MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        PreferenceManager.getDefaultSharedPreferences(baseContext)
            .registerOnSharedPreferenceChangeListener(this)
        THEME.setThemeOfApp(context)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        // Color Mode ----------------------------- Start
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        // Color Mode -------------------------------- End
        val sharedPreferences = PreferenceManager
            .getDefaultSharedPreferences(baseContext)
        if (sharedPreferences.getString(DATA.COLOR_OPTION, "ONE") == "ONE") {
            binding!!.toolbar.mode.setBackgroundResource(R.drawable.sun)
        } else if (sharedPreferences.getString(DATA.COLOR_OPTION, "NIGHT_ONE") == "NIGHT_ONE") {
            binding!!.toolbar.mode.setBackgroundResource(R.drawable.moon)
        }
        binding!!.toolbar.image.setOnClickListener {
            VOID.IntentExtra(context, CLASS.PROFILE, DATA.PROFILE_ID, DATA.FirebaseUserUid)
        }

        //binding.recyclerView.setHasFixedSize(true);
        list = ArrayList()
        adapter = MainAdapter(context, list as ArrayList<Main>)
        binding!!.recyclerView.adapter = adapter
    }

    var U = 0
    var SO = 0
    var EC = 0
    var CA = 0
    var SL = 0
    var AL = 0
    var AR = 0
    var FA = 0
    private fun nrItems() {
        val reference = FirebaseDatabase.getInstance().getReference(DATA.USERS)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                U = 0
                for (data in dataSnapshot.children) {
                    val item = data.getValue(User::class.java)!!
                    if (item.id != null && item.id != DATA.FirebaseUserUid) U++
                }
                nrSongs()
            }

            private fun nrSongs() {
                val reference = FirebaseDatabase.getInstance().getReference(DATA.SONGS)
                reference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        SO = 0
                        EC = 0
                        for (data in dataSnapshot.children) {
                            val item = data.getValue(Song::class.java)!!
                            if (item.id != null) {
                                SO++
                                if (item.editorsChoice != 0) if (item.publisher == DATA.FirebaseUserUid) EC++
                            }
                        }
                        nrCategories()
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            }

            private fun nrCategories() {
                val reference = FirebaseDatabase.getInstance().getReference(DATA.CATEGORIES)
                reference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        CA = 0
                        for (data in dataSnapshot.children) {
                            val item = data.getValue(Song::class.java)!!
                            if (item.id != null) if (item.publisher == DATA.FirebaseUserUid) CA++
                        }
                        nrSliderShow()
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            }

            private fun nrSliderShow() {
                val reference = FirebaseDatabase.getInstance().getReference(DATA.SLIDER_SHOW)
                reference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        SL = 0
                        SL = dataSnapshot.childrenCount.toInt()
                        nrAlbums()
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            }

            private fun nrAlbums() {
                val reference = FirebaseDatabase.getInstance().getReference(DATA.ALBUMS)
                reference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        AL = 0
                        AL = dataSnapshot.childrenCount.toInt()
                        nrArtists()
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            }

            private fun nrArtists() {
                val reference = FirebaseDatabase.getInstance().getReference(DATA.ARTISTS)
                reference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        AR = 0
                        AR = dataSnapshot.childrenCount.toInt()
                        nrFavorites()
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            }

            private fun nrFavorites() {
                val reference = FirebaseDatabase.getInstance().getReference(DATA.FAVORITES)
                    .child(DATA.FirebaseUserUid)
                reference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        FA = 0
                        FA = dataSnapshot.childrenCount.toInt()
                        IdeaPosts(U, SO, EC, CA, SL, AL, AR, FA)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun userInfo() {
        val reference =
            FirebaseDatabase.getInstance().getReference(DATA.USERS).child(DATA.FirebaseUserUid)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)!!
                VOID.Glide(true, context, user.profileImage, binding!!.toolbar.image)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun IdeaPosts(
        users: Int, songs: Int, editorsChoice: Int, categories: Int, sliderShow: Int,
        albums: Int, artists: Int, favorites: Int,
    ) {
        list!!.clear()
        val item1 = Main(R.drawable.ic_person, "Users", users, CLASS.USERS)
        val item2 = Main(R.drawable.ic_add, "Add Song", 0, CLASS.SONG_ADD)
        val item3 = Main(R.drawable.ic_music, "Songs", songs, CLASS.SONGS)
        val item4 =
            Main(R.drawable.ic_users, "Editors Choice", editorsChoice, CLASS.EDITORS_CHOICE)
        val item5 = Main(R.drawable.ic_add_category, "Add Category", 0, CLASS.CATEGORY_ADD)
        val item6 = Main(R.drawable.ic_category_gray, "Categories", categories, CLASS.CATEGORIES)
        val item7 = Main(R.drawable.ic_slider, "Slider Show", sliderShow, CLASS.SLIDER_SHOW)
        val item8 = Main(R.drawable.ic_adds, "Add Album", 0, CLASS.ALBUM_ADD)
        val item9 = Main(R.drawable.ic_album, "Albums", albums, CLASS.ALBUMS)
        val item10 = Main(R.drawable.ic__add, "Add Artist", 0, CLASS.ARTIST_ADD)
        val item11 = Main(R.drawable.ic_mic, "Artists", artists, CLASS.ARTISTS)
        val item12 = Main(R.drawable.ic_star_selected, "Favorites", favorites, CLASS.FAVORITES)
        val item13 = Main(R.drawable.ic_privacy_policy, "Privacy Policy", 0, CLASS.PRIVACY_POLICY)
        list!!.add(item1)
        list!!.add(item2)
        list!!.add(item3)
        list!!.add(item4)
        list!!.add(item5)
        list!!.add(item6)
        list!!.add(item7)
        list!!.add(item8)
        list!!.add(item9)
        list!!.add(item10)
        list!!.add(item11)
        list!!.add(item12)
        list!!.add(item13)
        adapter!!.notifyDataSetChanged()
        binding!!.bar.visibility = View.GONE
        binding!!.recyclerView.visibility = View.VISIBLE
    }

    // Color Mode ----------------------------- Start
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == "color_option") {
            recreate()
        }
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SETTINGS_CODE) {
            recreate()
        }
    }

    // Color Mode -------------------------------- End
    override fun onResume() {
        userInfo()
        nrItems()
        super.onResume()
    }

    companion object {
        private const val SETTINGS_CODE = 234
    }
}