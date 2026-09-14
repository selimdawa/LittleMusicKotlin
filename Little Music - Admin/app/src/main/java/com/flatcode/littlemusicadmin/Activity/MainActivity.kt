package com.flatcode.littlemusicadmin.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.flatcode.littlemusicadmin.Adapter.MainAdapter
import com.flatcode.littlemusicadmin.Model.Main
import com.flatcode.littlemusicadmin.Model.Song
import com.flatcode.littlemusicadmin.Model.User
import com.flatcode.littlemusicadmin.R
import com.flatcode.littlemusicadmin.Unit.CLASS
import com.flatcode.littlemusicadmin.Unit.DATA
import com.flatcode.littlemusicadmin.Unit.VOID
import com.flatcode.littlemusicadmin.databinding.ActivityMainBinding
import androidx.activity.viewModels
import com.flatcode.littlemusicadmin.ViewModel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    private val viewModel: MainViewModel by viewModels()
    var list: MutableList<Main>? = null
    var adapter: MainAdapter? = null
    var context: Context = this@MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        binding!!.toolbar.image.setOnClickListener {
            VOID.IntentExtra(context, CLASS.PROFILE, DATA.PROFILE_ID, DATA.FirebaseUserUid)
        }

        list = ArrayList()
        adapter = MainAdapter(context, list as ArrayList<Main>)
        binding!!.recyclerView.adapter = adapter

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.userInfo.collectLatest { user ->
                user?.let {
                    VOID.Glide(true, context, it.profileImage, binding!!.toolbar.image)
                    Timber.d("User info updated: ${it.username}")
                }
            }
        }

        lifecycleScope.launch {
            viewModel.counts.collectLatest { counts ->
                IdeaPosts(
                    counts.users, counts.songs, counts.editorsChoice, counts.categories,
                    counts.sliderShow, counts.albums, counts.artists, counts.favorites
                )
                Timber.d("Counts updated: $counts")
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                binding!!.bar.visibility = if (isLoading) View.VISIBLE else View.GONE
                binding!!.recyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
            }
        }
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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onResume() {
        super.onResume()
    }

    companion object {
    }
}