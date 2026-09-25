package com.flatcode.littlemusic.ui.profile

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.flatcode.littlemusic.utils.BaseActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.littlemusic.R
import com.flatcode.littlemusic.utils.DATA
import com.flatcode.littlemusic.utils.loadImage
import com.flatcode.littlemusic.utils.openActivity
import com.flatcode.littlemusic.databinding.ActivityProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class ProfileActivity : BaseActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val viewModel: ProfileViewModel by viewModels()
    private var profileId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Timber.i("ProfileActivity Created")

        profileId = intent.getStringExtra(DATA.PROFILE_ID)

        if (profileId == DATA.FirebaseUserUid) {
            binding.edit.visibility = View.VISIBLE
            binding.edit.setImageResource(R.drawable.ic_edit_white)
            binding.edit.setOnClickListener { this.openActivity<ProfileEditActivity>() }
        }
        binding.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.username.collect { username ->
                        binding.username.text = username
                    }
                }
                launch {
                    viewModel.profileImage.collect { profileImage ->
                        binding.profile.loadImage(profileImage, true)
                    }
                }
                launch {
                    viewModel.favoritesCount.collect { count ->
                        binding.numberFavorites.text = "$count"
                    }
                }
                launch {
                    viewModel.albumsCount.collect { count ->
                        binding.numberAlbums.text = "$count"
                    }
                }
                launch {
                    viewModel.artistsCount.collect { count ->
                        binding.numberArtists.text = "$count"
                    }
                }
                launch {
                    viewModel.categoriesCount.collect { count ->
                        binding.numberCategories.text = "$count"
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        profileId?.let {
            viewModel.loadUserInfo(it)
            viewModel.loadCounts(it)
        }
    }
}