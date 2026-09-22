package com.flatcode.littlemusicadmin.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.flatcode.littlemusicadmin.ui.user.UsersActivity
import com.flatcode.littlemusicadmin.ui.song.SongAddActivity
import com.flatcode.littlemusicadmin.ui.song.SongsActivity
import com.flatcode.littlemusicadmin.ui.editorschoice.EditorsChoiceActivity
import com.flatcode.littlemusicadmin.ui.category.CategoryAddActivity
import com.flatcode.littlemusicadmin.ui.category.CategoriesActivity
import com.flatcode.littlemusicadmin.ui.others.SliderShowActivity
import com.flatcode.littlemusicadmin.ui.album.AlbumAddActivity
import com.flatcode.littlemusicadmin.ui.album.AlbumsActivity
import com.flatcode.littlemusicadmin.ui.artist.ArtistAddActivity
import com.flatcode.littlemusicadmin.ui.artist.ArtistsActivity
import com.flatcode.littlemusicadmin.ui.others.FavoritesActivity
import com.flatcode.littlemusicadmin.ui.others.PrivacyPolicyActivity
import com.flatcode.littlemusicadmin.ui.profile.ProfileActivity
import com.flatcode.littlemusicadmin.model.Main
import com.flatcode.littlemusicadmin.R
import com.flatcode.littlemusicadmin.utils.*
import com.flatcode.littlemusicadmin.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels()
    private var list: MutableList<Main>? = null
    private var adapter: MainAdapter? = null
    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.image.setOnClickListener {
            mContext.openActivity<ProfileActivity>(extras = arrayOf(DATA.PROFILE_ID to DATA.FirebaseUserUid))
        }

        list = ArrayList()
        adapter = MainAdapter { model ->
            if (model.c != null) {
                mContext.startActivity(Intent(mContext, model.c))
            }
        }
        binding.recyclerView.adapter = adapter

        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userInfo.collectLatest { user ->
                user?.let {
                    binding.toolbar.image.loadImage(true, it.profileImage)
                    Timber.d("User info updated: ${it.username}")
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.counts.collectLatest { counts ->
                ideaPosts(
                    counts.users, counts.songs, counts.editorsChoice, counts.categories,
                    counts.sliderShow, counts.albums, counts.artists, counts.favorites
                )
                Timber.d("Counts updated: $counts")
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                binding.bar.visibility = if (isLoading) View.VISIBLE else View.GONE
                binding.recyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
            }
        }
    }

    private fun ideaPosts(
        users: Int, songs: Int, editorsChoice: Int, categories: Int, sliderShow: Int,
        albums: Int, artists: Int, favorites: Int,
    ) {
        val list = mutableListOf<Main>()
        list.add(Main(R.drawable.ic_person, "Users", users, UsersActivity::class.java))
        list.add(Main(R.drawable.ic_add, "Add Song", 0, SongAddActivity::class.java))
        list.add(Main(R.drawable.ic_music, "Songs", songs, SongsActivity::class.java))
        list.add(Main(R.drawable.ic_users, "Editors Choice", editorsChoice, EditorsChoiceActivity::class.java))
        list.add(Main(R.drawable.ic_add_category, "Add Category", 0, CategoryAddActivity::class.java))
        list.add(Main(R.drawable.ic_category_gray, "Categories", categories, CategoriesActivity::class.java))
        list.add(Main(R.drawable.ic_slider, "Slider Show", sliderShow, SliderShowActivity::class.java))
        list.add(Main(R.drawable.ic_adds, "Add Album", 0, AlbumAddActivity::class.java))
        list.add(Main(R.drawable.ic_album, "Albums", albums, AlbumsActivity::class.java))
        list.add(Main(R.drawable.ic__add, "Add Artist", 0, ArtistAddActivity::class.java))
        list.add(Main(R.drawable.ic_mic, "Artists", artists, ArtistsActivity::class.java))
        list.add(Main(R.drawable.ic_star_selected, "Favorites", favorites, FavoritesActivity::class.java))
        list.add(Main(R.drawable.ic_privacy_policy, "Privacy Policy", 0, PrivacyPolicyActivity::class.java))

        adapter!!.submitList(list)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
